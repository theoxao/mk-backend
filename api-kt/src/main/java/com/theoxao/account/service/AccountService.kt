package com.theoxao.account.service

import com.theoxao.account.Message
import com.theoxao.account.UserMedalRecord
import com.theoxao.account.dto.MedalRecordDTO
import com.theoxao.account.dto.ProfileDTO
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.model.Excerpt
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccountService(private val mongoTemplate: ReactiveMongoTemplate) {


    fun profile(principal: Principal): RestResponse<ProfileDTO> {
        val record = ProfileDTO(principal.id, principal.displayName, principal.avatarUrl)
        record.news = mongoTemplate.count(Query.query(Criteria.where("userId").`is`(principal.id)), Message::class.java).block()
                ?: 0
        record.medalCount = mongoTemplate.count(Query.query(Criteria.where("userId").`is`(principal.id)), UserMedalRecord::class.java).block()
                ?: 0
        record.excerptCount = mongoTemplate.count(Query.query(Criteria.where("userId").`is`(principal.id)), Excerpt::class.java).block()
                ?: 0
        return RestResponse<ProfileDTO>().ok().withData(record)
    }

    fun medalList(userId: String?): Mono<RestResponse<List<MedalRecordDTO>>> {
        return mongoTemplate.find(Query.query(Criteria.where("userId").`is`(userId)), UserMedalRecord::class.java)
                .map {
                    MedalRecordDTO.fromEntity(it)
                }.collectList().map {
                    RestResponse<List<MedalRecordDTO>>().ok().withData(it)
                }.defaultIfEmpty(RestResponse.notFound())

    }
}