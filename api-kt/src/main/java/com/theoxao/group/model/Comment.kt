package com.theoxao.group.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "comment")
class Comment {
    var id: ObjectId? = null
    var postId: String? = null
    var content: String? = null
    var posterName: String? = null
    var posterId: String? = null
    var replyName: String? = null
    var replyId: String? = null
    var createAt: Date = Date()
}