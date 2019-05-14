package com.theoxao.account.service

import com.theoxao.account.Message
import com.theoxao.account.dto.MessageDTO
import com.theoxao.account.repository.MessageRepository
import com.theoxao.commons.web.RestResponse
import org.apache.commons.lang3.StringUtils
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MessageService(private val mongoTemplate: ReactiveMongoTemplate, private val messageRepository: MessageRepository) {

    val messageCount = 10

    fun listMessage(userId: String, offsetId: String?): Mono<RestResponse<List<MessageDTO>>> {
        if (StringUtils.isNotBlank(offsetId)) {
            val update = Update()
            update.set("read", true)
            //mongoTemplate.find(Query.query(Criteria.where("userId").`is`(userId).and("_id").lt(ObjectId(offsetId)).and("delete").`is`(false)), Message::class.java)
            return mongoTemplate.find(Query.query(Criteria.where("userId").`is`(userId).and("_id").lt(ObjectId(offsetId)).and("delete").`is`(false)).limit(messageCount), Message::class.java)
                    .map {
                        MessageDTO.fromEntity(it)
                    }.collectList().flatMap { list ->
                        mongoTemplate.updateMulti(Query.query(Criteria.where("_id").`in`(list.map { m -> m.id })), update, Message::class.java).map {
                            RestResponse<List<MessageDTO>>().ok().withData(list)
                        }
                    }.defaultIfEmpty(RestResponse.notFound())
        } else {
            val update = Update()
            update.set("read", true)
            //mongoTemplate.find(Query.query(Criteria.where("userId").`is`(userId).and("_id").lt(ObjectId(offsetId)).and("delete").`is`(false)), Message::class.java)
            return mongoTemplate.find(Query.query(Criteria.where("userId").`is`(userId).and("delete").`is`(false)).limit(messageCount), Message::class.java)
                    .map {
                        MessageDTO.fromEntity(it)
                    }.collectList().flatMap { list ->
                        mongoTemplate.updateMulti(Query.query(Criteria.where("_id").`in`(list.map { m -> m.id })), update, Message::class.java).map {
                            RestResponse<List<MessageDTO>>().ok().withData(list)
                        }
                    }.defaultIfEmpty(RestResponse.notFound())
        }
    }

}
