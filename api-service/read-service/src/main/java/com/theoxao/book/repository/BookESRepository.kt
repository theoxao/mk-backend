package com.theoxao.book.repository

import com.theoxao.book.dto.BookES
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface BookESRepository : ElasticsearchRepository<BookES, String> {
    fun findBookESByName(name: String ): List<BookES>
    fun findBookESByAuthor(author:String):List<BookES>
    fun findBookESByIsbn(isbn:String):List<BookES>
    fun findByIsbnAndName(isbn:String,name:String):List<BookES>
    fun findByIsbnOrderByWeightDesc(isbn:String):List<BookES>
}