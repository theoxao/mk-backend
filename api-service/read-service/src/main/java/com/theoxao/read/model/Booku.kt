package com.theoxao.read.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "booku")
class Booku {
    var id: ObjectId? = null
    var sourceId: Int? = null
    var name: String? = null
    var author: String? = null
    var isbn: String? = null
    var publisher: String? = null
    var page: String? = null
    var image: String? = null
    var intro: String? = null
    var cid: Int? = null
    var doubanId: String? = null
    var authorIntro: String? = null
    var weight: Long = 0

}
