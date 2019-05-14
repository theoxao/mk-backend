package com.theoxao.read.dto

import com.theoxao.read.model.UserBook
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.BeanUtils
import java.util.*

/**
 * Created by theo on 2018/12/3
 */
open class UserBookDTO {

    var id: String? = null
    @ApiModelProperty("关联书籍ID")
    var refBookId: String? = null
    var userId: String? = null
    var name: String? = null
    var isbn: String? = null
    var cover: String? = null
    var author: String? = null
    var publisher: String? = null
    var pageCount: Int? = null
    @ApiModelProperty("归还时间")
    var returnDate: Date? = null
    var remark: String? = null
    var recentPage:Int = 0
    /**
     * tag name list
     */
    var tag: String? = null

    /**
     * 阅读状态 1 正在阅读 2 已读  3 未读 0 不读
     */
    @ApiModelProperty(" 阅读状态 1 正在阅读 2 已读  3 未读 0 不读")
    var state: Int? = null
    /**
     * 添加时间
     */
    @ApiModelProperty("添加时间")
    var createAt: Date? = Date()
    var updateAt: Date? = null

    companion object {
        fun fromEntity(entity: UserBook): UserBookDTO {
            val record = UserBookDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }
}
