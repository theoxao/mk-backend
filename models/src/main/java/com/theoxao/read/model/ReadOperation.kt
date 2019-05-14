package com.theoxao.read.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "read_operation_log")
class ReadOperation {
    var id: ObjectId? = null
    var userId: String? = null
    var refBookId: String? = null
    var type: Int? = null
    var operation: Int? = null
    var currentPage: Int? = null
    var duration: Long? = null
    var createAt = Date()
}