package com.theoxao.read.dto

import com.theoxao.read.model.ReadLog
import com.theoxao.read.model.ReadRecord
import io.swagger.annotations.ApiModelProperty
import lombok.Getter
import lombok.Setter
import org.springframework.beans.BeanUtils

import java.util.Date

/**
 * Created by theo on 2018/12/3
 */
class ReadLogDTO {
    var id: String? = null
    var userId: String? = null
    var refBook: String? = null
    var startAt: Date? = null
    var endAt: Date? = null
    var duration: Long? = null
    var createAt: Date? = null
    @ApiModelProperty("正在阅读书籍阅读状态 1 还在继续，-1 暂停中 ,0 已经结束")
    var currentStatus: Int = 0
    /**
     * 阅读类型 0 普通阅读 1 倒计时阅读
     */
    var type: Int? = null

    companion object {

        fun fromEntity(entity: ReadLog, currentStatus: Int = 0): ReadLogDTO {
            val record = ReadLogDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id.toHexString()
            record.currentStatus = currentStatus
            return record
        }

        fun fromRecord(entity: ReadRecord, currentStatus: Int = 0): ReadLogDTO {
            val record = ReadLogDTO()
            BeanUtils.copyProperties(entity, record)
            record.currentStatus = currentStatus
            return record
        }
    }
}
