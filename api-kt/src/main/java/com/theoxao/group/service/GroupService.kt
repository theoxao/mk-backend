package com.theoxao.group.service

import com.theoxao.commons.oss.OssService
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.group.UserGroup
import com.theoxao.group.dto.GroupActivityDTO
import com.theoxao.group.dto.GroupDTO
import com.theoxao.group.enums.ActivityEnum
import com.theoxao.group.model.Group
import com.theoxao.group.model.GroupActivity
import com.theoxao.group.model.Member
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.*

/**
 * Created by theo on 2018/12/18
 */
@Service
class GroupService(private val mongoTemplate: ReactiveMongoTemplate,
                   private val postService: PostService,
                   private val reactiveMongoOperations: ReactiveMongoOperations,
                   private val ossService: OssService) {

    companion object {
        const val GROUP_DIR = "group_image"
    }

    @Value("\${oss.prefix}")
    var prefix: String = ""

    fun create(group: Group, principal: Principal, imageFile: MultipartFile?): Mono<RestResponse<GroupDTO>> {
        val id = ObjectId()
        group.id = id
        imageFile?.let {
            GlobalScope.launch {
                ossService.upload(it.bytes, "${id.toHexString()}.${FilenameUtils.getExtension(imageFile.originalFilename)}", GROUP_DIR)
            }
            group.image = "$prefix$GROUP_DIR/${id.toHexString()}.${FilenameUtils.getExtension(imageFile.originalFilename)}"
        }
        return reactiveMongoOperations.inTransaction().execute { template ->
            template.save(group)
                    .doOnNext {
                        template.save(UserGroup(group.creatorId, it.id!!.toHexString(), true)).subscribe()
                    }.doOnNext {
                        val record = GroupActivity(it.id?.toHexString(), group.creatorId, principal.displayName, principal.avatarUrl, ActivityEnum.CREATE_GROUP.code)
                        template.save(record).subscribe()
                    }.doOnError {
                        throw RuntimeException(it.message)
                    }.map {
                        RestResponse<GroupDTO>().ok().withData(GroupDTO.fromEntity(it))
                    }
        }.toMono()
    }

    suspend fun groupList(userId: String): Mono<RestResponse<List<GroupDTO>>> {
        return mongoTemplate.find(Query.query(Criteria.where("userId").`is`(userId)).with(Sort(Sort.Direction.DESC, "_id")), UserGroup::class.java)
                .flatMap {
                    mongoTemplate.find(Query.query(Criteria.where("_id").`is`(it.groupId)), Group::class.java)
                            .map { record ->
                                val result = GroupDTO.fromEntity(record)
                                result.owner = userId == record.creatorId
                                result
                            }
                }.collectList().map { record -> RestResponse<List<GroupDTO>>().ok().withData(record) }
    }

    fun editGroup(id: String, name: String?, remark: String?, userId: String): Mono<RestResponse<Any>> {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(id)).addCriteria(Criteria.where("creatorId").`is`(userId)), Group::class.java)
                .flatMap {
                    if (StringUtils.isBlank(name) && StringUtils.isBlank(remark))
                        throw RuntimeException("提交信息不能都为空")
                    val update = Update()
                    if (StringUtils.isNotBlank(name))
                        update.set("name", name)
                    if (StringUtils.isNotBlank(remark))
                        update.set("remark", remark)
                    mongoTemplate.updateFirst(Query.query(Criteria.where("_id").`is`(id)), update, Group::class.java)
                }.map<RestResponse<Any>> {
                    Assert.isTrue(it.modifiedCount > 0, "更新失败")
                    RestResponse.success()
                }
                .defaultIfEmpty(RestResponse.notFound())
    }

    fun findById(id: String, userId: String): Mono<RestResponse<GroupDTO>> {
        return mongoTemplate.findById(ObjectId(id), Group::class.java)
                .map {
                    val record = GroupDTO.fromEntity(it)
                    record.owner = userId == it.creatorId
                    record
                }.map {
                    RestResponse<GroupDTO>().ok().withData(it)
                }.defaultIfEmpty(RestResponse.notFound())
    }

    fun findMembers(id: String, userId: String): Mono<RestResponse<List<Member>>> {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(id)), Group::class.java)
                .flatMapIterable {
                    val members = it.members
                    if (!members.map { record -> record.userId }.contains(userId))
                        throw RuntimeException("不是该小组成员")
                    members.forEach { m ->
                        m.owner = m.userId == it.creatorId
                    }
                    members
                }.collectList().map { record -> RestResponse<List<Member>>().ok().withData(record) }
                .defaultIfEmpty(RestResponse.notFound())
    }

    fun editName(userId: String, groupId: String, name: String?): Mono<RestResponse<Any>> {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(groupId)), Group::class.java)
                .flatMapIterable {
                    it.members
                }.map {
                    if (it.userId == userId)
                        it.displayName = name
                    it
                }.collectList().flatMap {
                    val update = Update()
                    update.set("members", it)
                    mongoTemplate.updateFirst(Query.query(Criteria.where("_id").`is`(groupId)), update, Group::class.java)
                }.map<RestResponse<Any>> {
                    Assert.isTrue(it.modifiedCount > 0, "更新失败")
                    RestResponse.success()
                }.defaultIfEmpty(RestResponse.notFound())
    }

    fun quit(principal: Principal, groupId: String): Mono<RestResponse<Any>> {

        return reactiveMongoOperations.inTransaction().execute { template ->
            template.findOne(Query.query(Criteria.where("_id").`is`(groupId)), Group::class.java)
                    .flatMapIterable {
                        it.members
                    }.filter {
                        it.userId != principal.id
                    }.collectList().flatMap {
                        val update = Update()
                        update.set("members", it)
                        template.updateFirst(Query.query(Criteria.where("_id").`is`(groupId)), update, Group::class.java)
                    }.map<RestResponse<Any>> {
                        Assert.isTrue(it.modifiedCount > 0, "更新失败")
                        RestResponse.success()
                    }.doOnNext {
                        template.remove(Query.query(Criteria.where("userId").`is`(principal.id).and("groupId").`is`(groupId)), UserGroup::class.java).subscribe()
                    }.doOnNext {
                        val record = GroupActivity(groupId, principal.id, principal.displayName, principal.avatarUrl, ActivityEnum.QUIT_GROUP.code)
                        template.save(record).subscribe()
                    }
        }.toMono()
    }

    fun joinGroup(member: Member, groupId: String, principal: Principal): Mono<RestResponse<Any>> {
        return reactiveMongoOperations.inTransaction().execute { template ->
            template.findOne(Query.query(Criteria.where("_id").`is`(groupId)), Group::class.java)
                    .flatMapIterable {
                        it.members
                    }.map {
                        if (it.userId == member.userId)
                            throw RuntimeException("已经在小组")
                        it
                    }.concatWithValues(member).collectList().flatMap {
                        val update = Update()
                        update.set("members", it)
                        template.updateFirst(Query.query(Criteria.where("_id").`is`(groupId)), update, Group::class.java)
                    }.doOnNext {
                        template.save(UserGroup(member.userId, groupId, false)).subscribe()
                    }.map<RestResponse<Any>> {
                        Assert.isTrue(it.modifiedCount > 0, "更新失败")
                        RestResponse.success()
                    }.doOnNext {
                        val record = GroupActivity(groupId, principal.id, principal.displayName, principal.avatarUrl, ActivityEnum.JOIN_GROUP.code)
                        template.save(record)
                    }.defaultIfEmpty(RestResponse.notFound("小组不存在"))

        }.toMono()
    }

    fun activities(groupId: String): Mono<RestResponse<List<GroupActivityDTO>>> {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(groupId)), Group::class.java)
                .flatMap { _ ->
                    mongoTemplate.find(Query.query(Criteria.where("groupId").`is`(groupId)).with(Sort(Sort.Direction.DESC, "createAt")), GroupActivity::class.java)
                            .map {
                                GroupActivityDTO.fromEntity(it)
                            }.collectList().map {
                                RestResponse<List<GroupActivityDTO>>().ok().withData(it)
                            }.defaultIfEmpty(RestResponse<List<GroupActivityDTO>>().ok().withData(Collections.emptyList()))
                }.defaultIfEmpty(RestResponse.notFound())


    }

}

