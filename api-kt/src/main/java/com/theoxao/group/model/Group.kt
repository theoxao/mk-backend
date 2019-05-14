package com.theoxao.group.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

/**
 * Created by theo on 2018/12/18
 */
@Document(collection = "group")
class Group {
    var id: ObjectId? = null
    var creatorId: String? = null
    var name: String? = null
    var remark: String? = null
    var image: String? = null
    /**
     * 小组类型
     */
    var type: Int? = null
    var createAt: Date? = null
    var members: MutableList<Member> = arrayListOf()
}
