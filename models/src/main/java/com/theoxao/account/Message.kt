package com.theoxao.account

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user_message")
class Message {
    var id: ObjectId? = null
    var userId: String? = null
    var sender: String? = null
    //sender nickname
    var nickName: String? = null
    var avatarUrl: String? = null
    var refId: String? = null
    var delete: Boolean = false
    var type: Int = 0
    var read: Boolean = false
    var content: Map<String, Any?> = emptyMap()


}