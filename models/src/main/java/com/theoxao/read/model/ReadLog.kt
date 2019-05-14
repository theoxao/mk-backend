package com.theoxao.read.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "read_log")
class ReadLog {

    var id: ObjectId = ObjectId()
    @Indexed
    var userId: String? = null
    var refBook: String? = null
    var startAt: Date? = null
    var endAt: Date? = null
    var duration: Long = 0
    var currentPage: Int? = null
    /**
     * 阅读类型 0 普通阅读 1 倒计时阅读
     */
    var type: Int? = null
    var createAt: Date = Date()
    var updateAt: Date = Date()
    @Indexed
    var finished: Boolean = false
    @Indexed
    var previousId: ObjectId? = null
    @Indexed
    var kindId: ObjectId? = null
}
