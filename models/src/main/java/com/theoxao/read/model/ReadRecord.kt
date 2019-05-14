package com.theoxao.read.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("read_record")
class ReadRecord(var userId: String, var refBook: String?, var startAt: Date?, var endAt: Date?, var kindId: ObjectId?, var currentPage: Int? ,
                 var pageCount: Int) {
    var id: ObjectId? = null
    var duration: Long = 0
}