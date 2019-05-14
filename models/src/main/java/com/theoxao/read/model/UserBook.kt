package com.theoxao.read.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user_book")
class UserBook {

    var id: ObjectId? = null
    var refBookId: String? = null
    @Indexed(unique = false)
    var userId: String? = null
    @Indexed(unique = false)
    var isbn: String? = null
    var name: String? = null
    var cover: String? = null
    var author: String? = null
    var publisher: String? = null
    var pageCount: Int = 1
    var returnDate: Date? = null
    var returned: Boolean = false
    var remark: String? = null
    var tag: String? = null
    var state: Int = 3
	var recentPage : Int = 0
    var initPage: Int? = 0
    var createAt: Date = Date()
    var updateAt: Date = Date()
}
