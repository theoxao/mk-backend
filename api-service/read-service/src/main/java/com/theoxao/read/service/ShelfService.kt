package com.theoxao.read.service

import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.UserBookDTO
import com.theoxao.read.model.UserBook
import org.apache.commons.lang3.StringUtils
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * Created by theo on 2018/11/14
 */
@Service
class ShelfService(private val mongoTemplate: ReactiveMongoTemplate) {

    fun findByTagId(userId: String, tag: String?): Mono<RestResponse<List<UserBookDTO>>> {
        var query = Query.query(Criteria.where("userId").`is`(userId)).with(Sort(Sort.Direction.DESC, "updateAt"))
        if (StringUtils.isNotBlank(tag)) {
            query = query.addCriteria(Criteria.where("tag").`is`(tag))
        }
        return mongoTemplate.find(query, UserBook::class.java)
                .map { UserBookDTO.fromEntity(it) }
                .collectList().map { RestResponse<List<UserBookDTO>>().ok().withData(it) }
    }
}
