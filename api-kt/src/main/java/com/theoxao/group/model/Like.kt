package com.theoxao.group.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "like")
class Like {
    var userId: String? = null
    var nickName: String = ""
    var postId: String? = null
    var createAt = Date()

    constructor()
    constructor(userId: String?, nickName: String, postId: String?) {
        this.userId = userId
        this.nickName = nickName
        this.postId = postId
    }


}