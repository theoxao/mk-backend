package com.theoxao.read.service

import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.BookDetailDTO
import com.theoxao.read.dto.ReadLogDTO
import com.theoxao.read.dto.ReadRecordDTO
import com.theoxao.read.model.*
import com.theoxao.read.vo.ReadRequestVO
import com.theoxao.utils.EntityTransfer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.*
import kotlin.collections.HashMap

@Service
class ReadService(private val mongoTemplate: ReactiveMongoTemplate, private val reactiveMongoOperations: ReactiveMongoOperations, private val template: MongoTemplate) {

    fun addUserBook(userBook: UserBook): Mono<RestResponse<Any>> {
        return mongoTemplate.save(userBook).map<RestResponse<Any>> {
            RestResponse.success()
        }.defaultIfEmpty(RestResponse<Any>().ok().withError("保存失败"))
    }

    fun readLog(principal: Principal, offsetId: String?, size: Int, userBookId: String): Mono<RestResponse<List<ReadRecordDTO>>> {
        var limit = size
        var offset: String? = null
        if (size == 0)
            limit = 3
        else
            offset = offsetId

        val query = Query.query(Criteria.where("userId").`is`(principal.id).and("refBook").`is`(userBookId))
        if (StringUtils.isNotBlank(offset)) {
            query.addCriteria(Criteria.where("_id").lt(offset!!))
        }
        query.with(Sort(Sort.Direction.DESC, "startAt"))
        query.limit(limit)

        return mongoTemplate.find(query, ReadRecord::class.java).map {
            ReadRecordDTO.fromEntity(it)
        }.collectList().map<RestResponse<List<ReadRecordDTO>>> {
            RestResponse<List<ReadRecordDTO>>().ok().withData(it)
        }
    }


    fun handleOperation(userId: String, vo: ReadRequestVO): Mono<RestResponse<ReadLogDTO>> {
        return reactiveMongoOperations.inTransaction().execute { template ->
            val update = Update()
            update.set("updateAt", Date())
            template.updateFirst(Query(Criteria.where("_id").`is`(vo.refBook)), update, UserBook::class.java).flatMap {
                template.save(buildOperationLog(userId, vo)).flatMap<RestResponse<ReadLogDTO>> { _ ->
                    when (vo.operation) {
                        1 -> {
                            //开始
                            val readLog = ReadLog()
                            readLog.startAt = Date()
                            readLog.updateAt = Date()
                            readLog.refBook = vo.refBook
                            readLog.type = vo.type
                            readLog.userId = userId
                            readLog.id = ObjectId()
                            readLog.kindId = readLog.id
                            template.findOne(Query.query(Criteria.where("userId").`is`(userId)).with(Sort(Sort.Direction.DESC, "startAt")), ReadLog::class.java)
                                    .flatMap {
                                        if (!it.finished) {
                                            Mono.just(RestResponse<ReadLogDTO>().withError("已存在未完成阅读记录").withData(ReadLogDTO.fromEntity(it, 1)))
                                        } else
                                            template.save(readLog).map { log ->
                                                RestResponse<ReadLogDTO>().ok().withData(ReadLogDTO.fromEntity(log, 1))
                                            }
                                    }.switchIfEmpty(template.save(readLog).map<RestResponse<ReadLogDTO>> {
                                        RestResponse<ReadLogDTO>().ok().withData(ReadLogDTO.fromEntity(it, 1))
                                    })
                        }
                        0 -> {
                            //结束
                            template.findById(ObjectId(vo.id), ReadLog::class.java)
                                    .flatMap {
                                        Assert.isTrue(it.userId == userId, "用户信息错误")
                                        Assert.isTrue(!it.finished, "此次阅读已结束")
                                        val update = Update()
                                        update.set("duration", Date().time - it.createAt.time)
                                        update.set("endAt", Date())
                                        update.set("updateAt", Date())
                                        update.set("finished", true)
                                        // TODO 判断上次阅读页数是否小于当前提交页数
                                        Assert.isTrue(vo.currentPage > 0, "页数需要大于零")
                                        update.set("currentPage", vo.currentPage)
                                        template.updateFirst(Query.query(Criteria.where("_id").`is`(it.id)), update, ReadLog::class.java).map { _ ->
                                            it.kindId
                                        }
                                    }.map { kindId ->
                                        updateProgress(vo.refBook, vo.currentPage, template)
                                        updateRecord(userId, vo.refBook, kindId, vo.currentPage, template)
                                    }.flatMap<RestResponse<ReadLogDTO>> {
                                        it.map { record ->
                                            RestResponse<ReadLogDTO>().ok().withData(ReadLogDTO.fromRecord(record, 0))
                                        }
                                    }
                        }
                        -1 -> {
                            //暂停
                            template.findById(ObjectId(vo.id), ReadLog::class.java)
                                    .flatMap {
                                        Assert.isTrue(!it.finished, "此次阅读已结束")
                                        Assert.isTrue(it.userId == userId, "用户信息错误")
                                        val update = Update()
                                        update.set("duration", Date().time - it.createAt.time)
                                        update.set("endAt", Date())
                                        update.set("updateAt", Date())
                                        update.set("finished", false)
                                        template.updateFirst(Query.query(Criteria.where("_id").`is`(it.id)), update, ReadLog::class.java)
                                                .map { r ->
                                                    Assert.isTrue(r.modifiedCount > 0, "更新失败")
                                                    RestResponse<ReadLogDTO>().ok().withData(ReadLogDTO.fromEntity(it, -1))
                                                }
                                    }
                        }
                        -2 -> {
                            //继续
                            template.findById(ObjectId(vo.id), ReadLog::class.java)
                                    .flatMap {
                                        template.exists(Query.query(Criteria.where("previousId").`is`(ObjectId(vo.id))), ReadLog::class.java).flatMap { exist ->
                                            Assert.isTrue(!exist, "已经继续，请勿重复点击")
                                            Assert.isTrue(!it.finished, "此次阅读已结束")
                                            Assert.isTrue(it.userId == userId, "用户信息错误")
                                            val newLog = ReadLog()
                                            newLog.startAt = Date()
                                            newLog.refBook = vo.refBook
                                            newLog.type = vo.type
                                            newLog.userId = userId
                                            newLog.previousId = it.id
                                            newLog.kindId = it.kindId
                                            template.save(newLog)
                                                    .map {
                                                        RestResponse<ReadLogDTO>().ok().withData(ReadLogDTO.fromEntity(it, 1))
                                                    }
                                        }
                                    }
                        }
                        else -> {
                            Mono.just(RestResponse<ReadLogDTO>().withError("记录不存在"))
                        }
                    }
                }
            }
        }.toMono()
    }

    private fun buildOperationLog(userId: String, vo: ReadRequestVO): ReadOperation {
        val record = ReadOperation()
        record.currentPage = vo.currentPage
        record.duration = vo.duration
        record.userId = userId
        record.operation = vo.operation
        record.refBookId = vo.refBook
        record.type = vo.type
        return record
    }

    fun detail(principal: Principal, id: String): Mono<RestResponse<BookDetailDTO>> {
        return mongoTemplate.findById(ObjectId(id), UserBook::class.java)
                .map {
                    BookDetailDTO.fromEntity(it)
                }.map {
                    it.recentRecord = recentRecord(null, principal)
                    it
                }.flatMap {
                    mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(it.refBookId)), Booku::class.java)
                            .map { book ->
                                it.refBook = EntityTransfer.bookuToBookDTO(book)
                                it
                            }
                            .map { result ->
                                RestResponse<BookDetailDTO>().ok().withData(result)
                            }.defaultIfEmpty(RestResponse<BookDetailDTO>().ok().withData(it))

                }.switchIfEmpty(Mono.just(RestResponse<BookDetailDTO>().withStatus(404).withError("书籍不存在")))
    }

    private fun recentRecord(bookId: String?, principal: Principal): ReadRecordDTO {
        val readRecord = ReadRecordDTO()
        val ones = arrayListOf<ReadLog>()
        val query = Query.query(Criteria.where("userId").`is`(principal.id)).with(Sort(Sort.Direction.DESC, "startAt"))
        bookId?.let { query.addCriteria(Criteria.where("refBook").`is`(it)) }
        val first = template.findOne(query, ReadLog::class.java)
        if (first != null) {
            if (!first.finished)
                readRecord.status = if (first.duration == 0L) 1 else -1
            else
                readRecord.currentPage = first.currentPage ?: 0
            readRecord.refBook = first.refBook
            ones.add(first)
            var one = first
            while (one?.previousId != null) {
                one = template.findById(one.previousId!!, ReadLog::class.java)
                one?.let { ones.add(it) }
            }
            readRecord.startAt = ones.last().startAt
            readRecord.endAt = first.endAt
            readRecord.duration = ones.map {
                it.duration
            }.reduce { acc, duration ->
                acc + duration
            }
        }
        return readRecord
    }

    fun recentReadBookId(principal: Principal): String? {
        val query = Query.query(Criteria.where("userId").`is`(principal.id)).with(Sort(Sort.Direction.DESC, "startAt"))
        return template.findOne(query, ReadLog::class.java)?.refBook
    }

    fun readStat(id: String, principal: Principal): Mono<RestResponse<Map<String, Any>>> {
        val userBook = template.findById(ObjectId(id), UserBook::class.java)
        Assert.notNull(userBook, "记录不存在")
        val query = Query.query(Criteria.where("userId").`is`(principal.id).and("finished").`is`(true).and("refBook").`is`(id)).with(Sort(Sort.Direction.ASC, "startAt"))
        val result = HashMap<String, Any>()
        return mongoTemplate.find(query, ReadLog::class.java).map { log ->
            var that = log

            if (userBook!!.state == 2 || (log.currentPage ?: 0) == userBook.pageCount)
                result["finished"] = true
            result["progress"] = "${(log.currentPage ?: 0)}/${userBook.pageCount}"

            val ones = arrayListOf<ReadLog>()
            ones.add(that)
            while (that?.previousId != null) {
                that = template.findById(that.previousId!!, ReadLog::class.java)
                that?.let { ones.add(it) }
            }
            ones.map {
                it.duration
            }.reduce { acc, duration ->
                acc + duration
            }
        }.reduce { t: Long, u: Long ->
            t + u
        }.map {
            result["totalDuration"] = it
            if (userBook!!.returnDate != null && !userBook.returned) {
                result["remainDays"] = Math.abs(userBook.returnDate!!.time - System.currentTimeMillis()) / 86400000
            }
            RestResponse<Map<String, Any>>().ok().withData(result)
        }
    }

    private fun updateRecord(userId: String, refBook: String?, kindId: ObjectId?, currentPage: Int?, template: ReactiveMongoOperations): Mono<ReadRecord> {
        return template.find(Query.query(Criteria.where("kindId").`is`(kindId)), ReadLog::class.java).collectSortedList { o1, o2 -> o1.id.compareTo(o2.id) }.flatMap {
            val one = this.template.findOne(Query.query(Criteria.where("userId").`is`(userId)).addCriteria(Criteria.where("refBook").`is`(refBook)).with(Sort(Sort.Direction.DESC, "startAt")), ReadRecord::class.java)
            var initPage = one?.currentPage ?: 0
            if (one == null) {
                initPage = this.template.findOne(Query.query(Criteria.where("_id").`is`(refBook)), UserBook::class.java)?.initPage ?: 0
            }
            val readRecord = ReadRecord(userId, refBook, it.first().startAt, it.last().endAt, kindId, currentPage, currentPage?.minus(initPage)?:0)
            readRecord.duration = it.map { i -> i.duration }.reduce { acc, l -> acc + l }
            template.save(readRecord)
        }
    }

    private fun updateProgress(id: String?, recentPage: Int, template: ReactiveMongoOperations) {
        GlobalScope.launch {
            val update = Update()
            update.set("recentPage", recentPage)
            template.updateFirst(Query.query(Criteria.where("_id").`is`(id)), update, UserBook::class.java).subscribe()
        }
    }


}























