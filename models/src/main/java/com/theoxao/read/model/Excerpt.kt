package com.theoxao.read.model

import lombok.Getter
import lombok.Setter
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

import java.util.Date

/**
 * Created by theo on 2018/11/13
 */
@Getter
@Setter
@Document(collection = "user_excerpt")
class Excerpt {
    var id: ObjectId? = null
    var userId: String? = null
    var refBook: String? = null
    var refPage: Int? = null
    var content: String = ""
    var images: MutableList<String> = arrayListOf()
    var createAt: Date = Date()
    var updateAt: Date = Date()
	var quoto :String = ""
}
