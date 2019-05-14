package com.theoxao.group.model

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "group_activity")
class GroupActivity {
    var groupId: String? = null
    var userId: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var operation: Int = 0
    var createAt = Date()

    constructor()
    constructor(groupId: String?, userId: String?, nickName: String?, avatarUrl: String?, operation: Int) {
        this.groupId = groupId
        this.userId = userId
        this.nickName = nickName
        this.avatarUrl = avatarUrl
        this.operation = operation
    }

}