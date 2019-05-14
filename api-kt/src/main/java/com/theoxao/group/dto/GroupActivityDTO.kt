package com.theoxao.group.dto

import com.theoxao.group.enums.ActivityEnum
import com.theoxao.group.model.GroupActivity
import org.springframework.beans.BeanUtils

class GroupActivityDTO {
    var groupId: String? = null
    var userId: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var operation: String? = null

    companion object {
        fun fromEntity(entity: GroupActivity): GroupActivityDTO {
            val record = GroupActivityDTO()
            BeanUtils.copyProperties(entity, record)
            record.operation = ActivityEnum.parse(entity.operation)
            return record
        }
    }
}