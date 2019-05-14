package com.theoxao.read.service

import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.Coordinate
import com.theoxao.read.enums.StatSource
import com.theoxao.read.enums.StatType
import com.theoxao.read.model.ReadRecord
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.text.SimpleDateFormat

@Service
class StatService(private val mongoTemplate: ReactiveMongoTemplate) {

    fun statByTime(refBook: String?, principal: Principal, typeBy: StatType, statSource: StatSource): Mono<RestResponse<List<Coordinate>>> {
        val dateFormat = when (typeBy) {
            StatType.BY_DATE -> SimpleDateFormat("yyyyMMdd")
            StatType.BY_PERIOD -> SimpleDateFormat("HH")
            else -> SimpleDateFormat("HH")
        }
        val query = Query.query(Criteria.where("").`is`(""))
        if (refBook != null) {
            query.addCriteria(Criteria.where("refBook").`is`(refBook))
        }
        return mongoTemplate.find(query, ReadRecord::class.java).groupBy {
            if (typeBy == StatType.BY_BOOK)
                it.refBook
            else
                dateFormat.format(it.startAt)
        }.flatMap { record ->
            record.map {
               val r=  when (statSource) {
                    StatSource.MINUTE -> it.duration
                    StatSource.PAGE -> it.pageCount.toLong()
                    else -> it.duration
                }
                r
            }.reduce { acc, i -> acc + i }.map { duration ->
                val actual = when (statSource) {
                    StatSource.MINUTE -> duration / 1000 / 60
                    else -> duration
                }
                Coordinate(record.key(), actual.toInt())
            }
        }.collectSortedList { o1, o2 ->
            o1.x!!.compareTo(o2.x!!)
        }.map {
            RestResponse<List<Coordinate>>().ok().withData(it)
        }
    }


}