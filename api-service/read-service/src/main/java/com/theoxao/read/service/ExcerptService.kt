package com.theoxao.read.service

import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.group.Post
import com.theoxao.group.UserGroup
import com.theoxao.read.dto.ExcerptDTO
import com.theoxao.read.model.Excerpt
import org.apache.commons.lang3.StringUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

/**
 * Created by theo on 2018/11/13
 */
@Service
class ExcerptService(private val mongoTemplate: ReactiveMongoTemplate, private val reactiveMongoOperations: ReactiveMongoOperations) {

    fun selectByPage(id: String, bookId: String, pageRequest: PageRequest): Mono<RestResponse<List<ExcerptDTO>>> {
        return if (pageRequest.pageNumber == 0)
            mongoTemplate.find(Query.query(Criteria.where("userId").`is`(id)).addCriteria(Criteria.where("refBook").`is`(bookId)), Excerpt::class.java)
                    .map { ExcerptDTO.fromEntity(it) }.collectList().map { RestResponse<List<ExcerptDTO>>().ok().withData(it) }.switchIfEmpty(Mono.just(RestResponse.notFound()))
        else
            mongoTemplate.find(Query.query(Criteria.where("userId").`is`(id)).addCriteria(Criteria.where("refBook").`is`(bookId)).skip(pageRequest.offset - pageRequest.pageSize).limit(pageRequest.pageSize), Excerpt::class.java)
                    .map { ExcerptDTO.fromEntity(it) }.collectList().map { RestResponse<List<ExcerptDTO>>().ok().withData(it) }.switchIfEmpty(Mono.just(RestResponse.notFound()))

    }

    fun findById(userId: String, id: String): Mono<RestResponse<ExcerptDTO>> {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(id)), Excerpt::class.java)
                .map {
                    RestResponse<ExcerptDTO>().ok().withData(ExcerptDTO.fromEntity(it))
                }.defaultIfEmpty(RestResponse.notFound())
    }

    fun updateById(id: String, record: Excerpt): Mono<RestResponse<Any>> {
        val update = Update()
        if (StringUtils.isNotBlank(record.content) && record.images.isNotEmpty()) {
            throw RuntimeException("无更新数据")
        }
        if (StringUtils.isNotBlank(record.content)) {
            update.set("content", record.content)
        }
        if (record.images.isNotEmpty()) {
            update.set("images", record.images)
        }
        val result = mongoTemplate.updateFirst(Query.query(Criteria.where("_id").`is`(id)).addCriteria(Criteria.where("userId").`is`(record.userId)), update, Excerpt::class.java)
        return result.map<RestResponse<Any>> {
            Assert.isTrue(it.modifiedCount > 0, "更新失败")
            RestResponse.success()
        }

    }

    fun removeRecord(userId: String, id: String): Mono<RestResponse<Any>> {
        return reactiveMongoOperations.inTransaction().execute { template ->
            template.remove(Query.query(Criteria.where("_id").`is`(id)).addCriteria(Criteria.where("userId").`is`(userId)), Excerpt::class.java)
                    .map<RestResponse<Any>> {
                        Assert.isTrue(it.deletedCount > 0, "删除失败")
                        RestResponse.success()
                    }.doOnNext {
                        template.remove(Query.query(Criteria.where("excerptId").`is`(id).and("userId").`is`(userId)), Post::class.java).subscribe()
                    }.doOnError {
                        throw it
                    }
        }.toMono()
    }


    //TODO  transaction
    fun add(record: Excerpt, principal: Principal, share: Boolean): Mono<RestResponse<ExcerptDTO>> {

        return reactiveMongoOperations.inTransaction().execute { template ->
            template.save(record).map {
                ExcerptDTO.fromEntity(it)
            }.doOnNext {
                if (share)
                    template.find(Query.query(Criteria.where("userId").`is`(it.userId)), UserGroup::class.java).map { group ->
                        val post = Post()
                        post.content = it.content
                        post.userId = it.userId
                        post.nickName = principal.displayName
                        post.avatarUrl = principal.avatarUrl
                        post.refBook = it.refBook
                        post.images = it.images
                        post.groupId = group.groupId
                        post.excerptId = it.id
                        post
                    }.collectList().doOnNext { list ->
                        template.insertAll(list).subscribe()
                    }.subscribe()
            }.map {
                RestResponse<ExcerptDTO>().ok().withData(it)
            }
        }.toMono()
    }
}
