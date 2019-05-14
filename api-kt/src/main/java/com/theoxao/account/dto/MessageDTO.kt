package com.theoxao.account.dto

import com.theoxao.account.Message
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.BeanUtils

class MessageDTO {

    var id: String? = null
    @ApiModelProperty("消息发送者ID")
    var sender: String? = null
    @ApiModelProperty("消息发送者昵称")
    var nickName: String? = null
    @ApiModelProperty("消息发送者头像")
    var avatarUrl: String? = null
    @ApiModelProperty("消息类型")
    var type: Int = 0
    @ApiModelProperty("消息主体 \n" +
            " 1.点赞、评论 type=1/2\t返回发言内容(content:String , images:Array ,[评论Id-commentId:String ],[评论内容-comment:String])\n" +
            " 2.回复评论 type=3\t返回(评论内容-comment:String , 回复内容-reply:String ,该回复Id-commentId:String)\n" +
            " 3.系统消息 type=4\t返回(消息内容-content:String)")
    var content: Map<String, Any?> = emptyMap()
    var read: Boolean = false

    companion object {

        fun fromEntity(entity: Message): MessageDTO {
            val record = MessageDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            return record
        }
    }
}