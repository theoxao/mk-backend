package com.theoxao.read.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.theoxao.read.model.UserTag
import lombok.Getter
import lombok.Setter
import org.springframework.beans.BeanUtils

import java.util.Date

/**
 * Created by theo on 2018/12/3
 */
@JsonIgnoreProperties("userId")
class UserTagDTO {
    var id: String? = null
    var userId: String? = null
    var tag: String? = null
    var order: Int? = null
    var createAt: Date = Date()

    constructor()

    constructor(tag: String?) {
        this.tag = tag
    }


    companion object {
        fun fromEntity(entity: UserTag): UserTagDTO {
            val record = UserTagDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }
}
