package com.theoxao.read.dto

import com.theoxao.book.dto.BookDTO
import com.theoxao.read.model.UserBook
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.BeanUtils

/**
 * Created by theo on 2018/11/14
 */
class BookDetailDTO : UserBookDTO() {
    var refBook: BookDTO? = null
    @ApiModelProperty("根据此字段中status判断是否有遗留的阅读记录")
    var recentRecord: ReadRecordDTO? = null

    companion object {
        fun fromEntity(entity: UserBook): BookDetailDTO {
            val record = BookDetailDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }
}
