package com.theoxao.read.service

import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.UserTagDTO
import com.theoxao.read.model.UserTag
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagService(private val mongoTemplate: ReactiveMongoTemplate) {

    fun findUserTags(userId: String): Mono<RestResponse<List<UserTagDTO>>> {
        val tags = mongoTemplate.find(Query.query(Criteria.where("userId").`is`(userId)), UserTag::class.java)
                .map { UserTagDTO.fromEntity(it) }
        return Flux.just(UserTagDTO("全部")).concatWith(tags)
                .collectList().map { RestResponse<List<UserTagDTO>>().ok().withData(it) }
    }

    fun save(userId: String, tag: String): Mono<RestResponse<UserTagDTO>> {
        val userTag = UserTag(userId, tag)
        return mongoTemplate.save(userTag).map {
            RestResponse<UserTagDTO>().ok().withData(UserTagDTO.fromEntity(it))
        }.defaultIfEmpty(RestResponse.notFound())
    }

    fun remove(userId: String, id: String): Mono<RestResponse<Any>> {
        return mongoTemplate.remove(Query.query(Criteria.where("id").`is`(id)).addCriteria(Criteria.where("userId").`is`(userId)), UserTag::class.java).map<RestResponse<Any>> {
            Assert.isTrue(it.deletedCount > 0, "删除失败")
            RestResponse.success()
        }
    }
}
