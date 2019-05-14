package com.theoxao.group.dto

import com.theoxao.group.model.Comment
import org.springframework.beans.BeanUtils

class CommentDTO {
    var id: String? = null
    var postId: String? = null
    var content: String? = null
    var posterName: String? = null
    var posterId: String? = null
    var replyName: String? = null
    var replyId: String? = null


    companion object {
        fun fromEntity(entity: Comment): CommentDTO {
            val record = CommentDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }
}