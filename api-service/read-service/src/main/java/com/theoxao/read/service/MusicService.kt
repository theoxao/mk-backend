package com.theoxao.read.service

import com.theoxao.commons.web.RestResponse
import com.theoxao.read.model.Music
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MusicService(private val mongoTemplate: ReactiveMongoTemplate) {

    fun musicList(): Mono<RestResponse<List<Music>>> {

        return mongoTemplate.findAll(Music::class.java).collectList().map {
            RestResponse<List<Music>>().ok().withData(it)
        }
    }

}