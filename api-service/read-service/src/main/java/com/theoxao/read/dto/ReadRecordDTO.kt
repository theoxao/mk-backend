package com.theoxao.read.dto

import com.theoxao.read.model.ReadLog
import com.theoxao.read.model.ReadRecord
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.BeanUtils
import java.util.*

class ReadRecordDTO {
    var id: String? = null
    var refBook: String? = null
    var startAt: Date? = null
    var endAt: Date? = null
    @ApiModelProperty("正在阅读书籍阅读状态 1 还在继续，-1 暂停中 ,0 已经结束")
    var status: Int = 0
    @ApiModelProperty("本次阅读持续时长，进行中的需要累加上最后一次起始时间到当前时间的数据")
    var duration: Long = 0
    var currentPage: Int = 0

    companion object {
        fun fromEntity(entity: ReadRecord): ReadRecordDTO {
            val record = ReadRecordDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }


}
