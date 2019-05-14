package com.theoxao.read.controller

import com.theoxao.commons.annotations.RequireAuth
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.UserBookDTO
import com.theoxao.read.service.ShelfService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Created by theo on 2018/11/14
 */
@RestController
@RequestMapping("shelf")
class ShelfController(private val shelfService: ShelfService) {

    @ApiOperation("获取用户书架书籍列表")
    @RequireAuth
    @GetMapping("/list")
    fun list(@ApiParam("分类/标签名，不传则显示全部") @RequestParam(required = false) tag: String?): Mono<RestResponse<List<UserBookDTO>>> {
        return shelfService.findByTagId(Principal.get().id, tag)
    }

}
