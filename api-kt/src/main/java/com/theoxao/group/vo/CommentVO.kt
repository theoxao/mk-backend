package com.theoxao.group.vo

import com.theoxao.commons.AbstractVO
import com.theoxao.group.model.Comment
import io.swagger.annotations.ApiParam
import javax.validation.constraints.NotBlank

class CommentVO : AbstractVO<Comment>() {

    @ApiParam("评论内容", required = true)
    @NotBlank
    var content: String? = null

    @ApiParam("发言ID", required = true)
    @NotBlank
    var postId: String? = null

    @ApiParam("被评论者姓名 若有", required = false)
    var replyName: String? = null

    @ApiParam("被评论者ID 若有", required = false)
    var replyId: String? = null
}