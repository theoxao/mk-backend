package com.theoxao.book.service

import com.theoxao.book.dto.BookDTO
import com.theoxao.book.dto.BookDetailDTO
import com.theoxao.book.dto.BookES
import com.theoxao.book.model.Entity
import com.theoxao.book.model.SearchKeyword
import com.theoxao.book.repository.BookESRepository
import com.theoxao.commons.web.HttpClient
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.model.Booku
import com.theoxao.utils.DoubanUtils
import com.theoxao.utils.EntityTransfer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import org.bson.types.ObjectId
import org.springframework.beans.BeanUtils
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.charset.Charset
import java.util.regex.Pattern

@Service
class BookService(private val mongoTemplate: ReactiveMongoTemplate, private val bookESRepository: BookESRepository, private val mongo: MongoTemplate) {

    fun findByIsbn(isbn: String): Mono<RestResponse<List<BookDTO>>> {


        try {
            val list = bookESRepository.findByIsbnOrderByWeightDesc(isbn)
            if (list.isNotEmpty()) {
                list.forEach {
                    it.weight++
                }
                bookESRepository.saveAll(list)
                return Mono.just(RestResponse<List<BookDTO>>().ok().withData(list.map {
                    it.toEntity();
                }))
            }
        } catch (e: Exception) {

        }
        return mongoTemplate.find(Query.query(Criteria.where("isbn").`is`(isbn)), Booku::class.java)
                .map {
                    val update = Update()
                    update.set("weight", it.weight + 1)
                    mongo.updateFirst(Query.query(Criteria.where("_id").`is`(it.id)), update, Booku::class.java)
                    it.weight = it.weight + 1
                    bookESRepository.save(it.es())
                    EntityTransfer.bookuToBookDTO(it)
                }.collectList().map {
                    if (it.isEmpty())
                        throw RuntimeException("it is null ")
                    RestResponse<List<BookDTO>>().ok().withData(it)
                }.onErrorResume { _ ->
                    findOnDouban(isbn).map {
                        RestResponse<List<BookDTO>>().ok().withData(it)
                    }.defaultIfEmpty(RestResponse.notFound())
                }.defaultIfEmpty(RestResponse.notFound())

    }

    fun findById(id: String): RestResponse<BookDetailDTO> {
        val book = mongo.findById(ObjectId(id), Booku::class.java) ?: return RestResponse.notFound()
        val record = BookDetailDTO()
        if (book.isbn != null) {
            val entity = mongo.findOne(Query.query(Criteria.where("isbn").`is`(book.isbn)), Entity::class.java)
            entity?.let {
                BeanUtils.copyProperties(entity, record)
            }
        }
        BeanUtils.copyProperties(book, record)
        return RestResponse<BookDetailDTO>().ok().withData(record)
    }

    private fun findOnDouban(isbn: String): Mono<List<BookDTO>> {
        val xml = String(HttpClient.get("http://api.douban.com/book/subject/isbn/$isbn?apikey=0ebc51a97f80c16d2c34f4fc7e824447"), Charset.defaultCharset())
        if (StringUtils.isBlank(xml)) {
            return Mono.empty()
        }
        val record = Booku()
        DoubanUtils().parse(xml, record)
        bookESRepository.save(BookES.fromEntity(record))
        return mongoTemplate.save(record).map {
            EntityTransfer.bookuToBookDTO(it)
        }.flux().collectList()
    }

    fun search(keyword: String, type: String?): Mono<RestResponse<List<BookDTO>>> {
        mongo.save(SearchKeyword(keyword))
        val pattern: Pattern = Pattern.compile("\\d{10,13}")
        val matcher = pattern.matcher(keyword)
        if (matcher.find()) {
            return findByIsbn(keyword)
        }
        return when (type) {
            "name" -> Mono.just(RestResponse<List<BookDTO>>().ok().withData(bookESRepository.findBookESByName(keyword).map {
                it.toEntity()
            }))
            "author" -> Mono.just(RestResponse<List<BookDTO>>().ok().withData(bookESRepository.findBookESByAuthor(keyword).map {
                it.toEntity()
            }))
            else -> Mono.just(RestResponse<List<BookDTO>>().ok().withData(bookESRepository.findByIsbnAndName(keyword, keyword).map {
                it.toEntity()
            }))
        }
    }

    fun sync() {
        repeat(12000) { offset ->
            GlobalScope.launch {
                val list = mongo.find(Query().skip(offset * 100L).limit(100), Entity::class.java)
                val books = list.filter {
                    !mongo.exists(Query.query(Criteria.where("isbn").`is`(it.isbn)), Booku::class.java)
                }.map {
                    it.book()
                }
                mongo.bulkOps(BulkOperations.BulkMode.UNORDERED, Booku::class.java)
                        .insert(books).execute()
                println("saved ${books.size}")
            }
        }


    }

    fun Booku.es(): BookES {
        val record = BookES()
        record.id = this.id.toString()
        record.name = this.name
        record.author = this.author
        record.image = this.image
        record.publisher = this.publisher
        record.isbn = this.isbn
        record.weight = this.weight
        record.page = this.page
        return record
    }

    fun Entity.book(): Booku {
        val record = Booku()
        record.name = this.name
        record.image = this.image
        record.author = this.author
        record.sourceId = this.id.toInt()
        record.isbn = this.isbn
        record.publisher = this.publisher
        record.authorIntro = this.authorDesc
        record.intro = this.desc
        return record
    }

    fun Entity.es(): BookES {
        val record = BookES()
        record.id = this.bid
        record.name = this.name
        record.image = this.image
        record.publisher = this.publisher
        record.author = this.author
        record.isbn = this.isbn
        return record
    }

    fun syncES(drop: Int?) {
        if (drop == 1) {
            bookESRepository.deleteAll()
        }
        repeat(140) { os ->
            GlobalScope.launch {
                val list = mongo.find(Query().skip(os * 10000L).limit(10000), Booku::class.java)
                val ess = list.map {
                    it.es()
                }
                bookESRepository.saveAll(ess)
                println("saved ${list.size}")
            }
        }
    }

    private fun findDouban(isbn: String?): Booku? {
        try {
            val xml = String(HttpClient.get("http://api.douban.com/book/subject/isbn/$isbn?apikey=0ebc51a97f80c16d2c34f4fc7e824447"), Charset.defaultCharset())
            if (StringUtils.isBlank(xml)) {
                return null
            }
            val record = Booku()
            DoubanUtils().parse(xml, record)
            return record
        } catch (e: Exception) {
            println("request failed")
            return null
        }
    }

    fun updatePage() {
        repeat(140) { os ->
            GlobalScope.launch {
                val list = mongo.find(Query.query(Criteria.where("page").exists(false)).skip(os * 10000L).limit(10000), Booku::class.java)
                list.forEach { b ->
                    val record = findDouban(b.isbn)
                    record?.let {
                        it.image?.let { image ->
                            b.image = image
                        }
                        b.page = it.page
                        mongo.save(b)
                        println("saved one ${b.id}")
                    }
                }
            }
        }

    }

}
