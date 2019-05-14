package com.theoxao.account.dto

import com.theoxao.account.Medal
import com.theoxao.account.UserMedalRecord
import org.bson.types.ObjectId
import org.springframework.beans.BeanUtils
import java.util.*


class MedalRecordDTO {
    var id: String? = null
    var medal: Medal? = null
    var createAt = Date()


    companion object {
        fun fromEntity(entity: UserMedalRecord): MedalRecordDTO {
            val record = MedalRecordDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }
}