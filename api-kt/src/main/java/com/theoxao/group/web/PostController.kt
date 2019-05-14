package com.theoxao.group.web

import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.group.Post
import com.theoxao.group.dto.CommentDTO
import com.theoxao.group.dto.PostDTO
import com.theoxao.group.model.Like
import com.theoxao.group.service.PostService
import com.theoxao.group.vo.CommentVO
import com.theoxao.group.vo.PostVO
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.util.Assert
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/post")
class PostController(private val postService: PostService   ) {

    @ApiOperation("小组动态列表")
    @RequestMapping("/list" ,method = [RequestMethod.GET])
    fun list(@ApiParam("小组ID") @RequestParam id: String): Mono<RestResponse<List<PostDTO>>> {
        return postService.list(id, Principal.get().id)
    }

    @ApiOperation("新发言")
    @RequestMapping("/post"  , method=[RequestMethod.POST])
    fun newPost(@ModelAttribute vo: PostVO): Mono<RestResponse<PostDTO>> {
        Assert.isTrue(vo.groupId != null, "小组编号不能为空")
        val post = Post(vo.groupId, Principal.get().id, vo.content)
        post.nickName = Principal.get().displayName
        post.avatarUrl = Principal.get().avatarUrl
        return postService.post(post ,vo.imageFiles)
    }

    @ApiOperation("删除发言")
    @PostMapping("/remove")
    fun removePost(@RequestParam groupId: String, @RequestParam postId: String): Mono<RestResponse<Any>> {
        return postService.remove(Principal.get().id, groupId, postId)
    }

    @ApiOperation("点赞接口")
    @PostMapping("/like_operate")
    fun likeOperate(@RequestParam postId: String, @ApiParam(" 0 取消点赞 1 点赞") @RequestParam operate: Int): Mono<RestResponse<List<String>>> {
        val principal = Principal.get()
        val like = Like(principal.id, principal.displayName, postId)
        return postService.like(like, operate, principal)
    }

    @ApiOperation("评论")
    @PostMapping("/comment")
    fun comment(@ModelAttribute @Validated vo: CommentVO): Mono<CommentDTO> {
        val comment = vo.bean()
        comment.createAt = Date()
        val principal = Principal.get()
        comment.posterId = principal.id
        comment.posterName = principal.displayName
        return postService.comment(comment, principal)
    }

}