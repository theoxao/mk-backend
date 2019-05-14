package com.theoxao.account

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "medal")
class Medal {

    var id: ObjectId? = null
    var name: String? = null
    var desc: String? = null
    var type: Int? = null
    var condition: Int = 0
    var conditionDesc: String? = null

}