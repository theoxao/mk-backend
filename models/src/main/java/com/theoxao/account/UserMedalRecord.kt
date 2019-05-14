package com.theoxao.account

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "user_medal_record")
class UserMedalRecord {

    var id: ObjectId? = null
    var medalId: String? = null
    var medal: Medal? = null
    var userId: String? = null
    var createAt = Date()

}