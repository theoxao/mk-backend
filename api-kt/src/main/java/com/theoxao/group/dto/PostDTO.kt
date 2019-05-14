package com.theoxao.group.dto

import com.theoxao.group.Post
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.BeanUtils
import java.text.SimpleDateFormat
import java.util.*

class PostDTO {

    var id: String? = null
    var groupId: String? = null
    var userId: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    var refBook: String? = null
    var refBookName: String? = null
    var content: String? = null
    var images: MutableList<String> = arrayListOf()
    var createAt: Date? = null
    var updateAt: Date = Date()
    var timeDisplay: String? = null
    var liked: Boolean = false
    var likeList: MutableList<String> = arrayListOf()
    var comments: MutableList<CommentDTO> = arrayListOf()

    companion object {
        fun fromEntity(entity: Post): PostDTO {
            val record = PostDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            record.timeDisplay = displayTime(entity.createAt)
            return record
        }

        private fun displayTime(date: Date): String {
            val dateFormat = SimpleDateFormat("MM-dd")
            val timeFormat = SimpleDateFormat("HH:mm")
            date.let {
                if (DateUtils.isSameDay(Date(), date))
                    return "今天 " + timeFormat.format(it)
                val calendar = Calendar.getInstance()
                calendar.time = Date()
                calendar.set(Calendar.DAY_OF_YEAR, -1)
                if (DateUtils.isSameDay(calendar.time, date))
                    return "昨天 " + timeFormat.format(it)
                return dateFormat.format(it) + " " + timeFormat.format(it)
            }
        }
    }

}