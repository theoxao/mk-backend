package com.theoxao.read.controller

import com.theoxao.commons.annotations.RequireAuth
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.UserTagDTO
import com.theoxao.read.service.TagService
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("tag")
class TagController(private val tagService: TagService) {

    @ApiOperation("用户个人分类/标签列表")
    @RequireAuth
    @GetMapping("list")
    fun userTags(): Mono<RestResponse<List<UserTagDTO>>> {
        val userId = Principal.get().id
        return tagService.findUserTags(userId)
    }

    @RequireAuth
    @PostMapping("add")
    fun add(@RequestParam tag: String): Mono<RestResponse<UserTagDTO>> {
        val userId = Principal.get().id
        return tagService.save(userId, tag)
    }

    @RequireAuth
    @PostMapping("remove")
    fun remove(id: String): Mono<RestResponse<Any>> {
        val userId = Principal.get().id
        return tagService.remove(userId, id)
    }

    @GetMapping("recommend")
    fun recommend(): RestResponse<ArrayList<String>>? {
        return RestResponse<ArrayList<String>>().ok().withData(arrayListOf("工具", "课外读物", "下午茶", "少儿", "诗集", "励志"))
    }

}
