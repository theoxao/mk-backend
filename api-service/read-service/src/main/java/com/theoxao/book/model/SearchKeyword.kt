package com.theoxao.book.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "search_keyword")
class SearchKeyword {
    var keyword: String = ""
    var createAt: Date = Date()

    constructor()
    constructor(keyword: String) {
        this.keyword = keyword
    }
}