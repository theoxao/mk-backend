package com.theoxao.read.model

import lombok.Getter
import lombok.Setter
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

import java.util.Date

@Document(collection = "user_tag")
class UserTag {
    var id: ObjectId? = null
    var userId: String? = null
    var tag: String? = null
    var order: Int? = null
    var creatAt: Date = Date()

    constructor()

    constructor(userId: String?, tag: String?) {
        this.userId = userId
        this.tag = tag
    }

}
