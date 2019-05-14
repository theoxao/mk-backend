package com.theoxao.group

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "post")
class Post {
    var id: ObjectId? = null
    var groupId: String? = null
    var userId: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var refBook: String? = null
    var content: String? = null
    var images: MutableList<String> = arrayListOf()
    var createAt: Date = Date()
    var updateAt: Date = Date()
    var deleted: Boolean = false
    var deleteUserId: String? = null
    var excerptId: String? = null

    constructor()

    constructor(groupId: String?, userId: String?, content: String? ) {
        this.groupId = groupId
        this.userId = userId
        this.content = content

    }
}