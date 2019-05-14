package com.theoxao.book.dto

import com.theoxao.read.model.Booku
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "maikan-book", type = "book")
class BookES {
    @Id
    var id: String? = null
    var name: String? = null
    var author: String? = null
    var publisher: String? = null
    var isbn: String? = null
    var intro: String? = null
    var page: String? = null
    var image: String? = null
    var weight: Long = 0

    companion object {
        fun fromEntity(entity: Booku): BookES {
            val record = BookES()
            record.id = entity.id.toString()
            record.name = entity.name
            record.author = entity.author
            record.image = entity.image
            record.intro = entity.intro
            record.publisher = entity.publisher
            record.isbn = entity.isbn
            record.page = entity.page
            return record
        }
    }

    fun toEntity(): BookDTO {
        val record = BookDTO()
        record.id = this.id
        record.name = this.name
        record.author = this.author
        record.publisher = this.publisher
        record.introduction = this.intro
        record.cover = this.image
        record.pageCount = this.page
        record.isbn = this.isbn
        record.weight=this.weight
        return record
    }
}