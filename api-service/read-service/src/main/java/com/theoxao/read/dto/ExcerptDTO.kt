package com.theoxao.read.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.theoxao.read.model.Excerpt
import io.swagger.annotations.ApiParam
import lombok.Getter
import lombok.Setter
import org.springframework.beans.BeanUtils

import java.util.Date

/**
 * Created by theo on 2018/11/13
 */
class ExcerptDTO {
    var id: String? = null
    var userId: String? = null
    var refBook: String? = null
    var refPage: Int? = null
    var content: String? = null
    var images: MutableList<String> = arrayListOf()
    @ApiParam(value = "创建时间")
    var createAt: Date? = null

    companion object {
        fun fromEntity(entity: Excerpt): ExcerptDTO {
            val record = ExcerptDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }
}
