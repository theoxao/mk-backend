package com.theoxao.read.controller

import com.theoxao.commons.annotations.RequireAuth
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.Create
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.ExcerptDTO
import com.theoxao.read.service.ExcerptService
import com.theoxao.read.vo.ExcerptVO
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

/**
 * 摘录相关
 * Created by theo on 2018/11/13
 */
@RestController
@RequestMapping("excerpt")
class ExcerptController(private val excerptService: ExcerptService) {

    @RequireAuth
    @ApiOperation("摘要列表")
    @GetMapping("index")
    fun index(@RequestParam page: Int, @RequestParam size: Int, @ApiParam("书籍ID") @RequestParam bookId: String): Mono<RestResponse<List<ExcerptDTO>>> {
        return excerptService.selectByPage(Principal.get().id, bookId, PageRequest.of(page, size))
    }

    @RequireAuth
    @ApiOperation("摘要详情")
    @GetMapping("/detail")
    fun detail(@ApiParam("摘要ID") @RequestParam id: String): Mono<RestResponse<ExcerptDTO>> {
        return excerptService.findById(Principal.get().id, id)
    }

    @RequireAuth
    @ApiOperation("新增摘要")
    @PostMapping("/add")
    fun add(@ModelAttribute @Validated(Create::class) vo: ExcerptVO, @ApiParam("是否分享到所有小组") share: Boolean): Mono<RestResponse<ExcerptDTO>> {
        val record = vo.bean()
        val principal = Principal.get()
        record.userId = principal.id
        return excerptService.add(record, principal, share)
    }

    @Deprecated("")
    @ApiOperation("编辑摘要 获取旧摘要信息")
    @RequireAuth
    @GetMapping("/edit")
    fun edit(@ApiParam("摘要ID") @RequestParam id: String): Mono<RestResponse<ExcerptDTO>> {
        return excerptService.findById(Principal.get().id, id)
    }

    @ApiOperation("编辑摘要 提交新信息")
    @RequireAuth
    @PostMapping(value = ["/edit"])
    fun edit(@ModelAttribute @Validated vo: ExcerptVO, @ApiParam("摘要ID") @RequestParam id: String): Mono<RestResponse<Any>> {
        //根据id userId查询记录 存在即更新
        val record = vo.bean()
        record.userId = Principal.get().id

        return excerptService.updateById(id, record)
    }

    @ApiOperation("移除摘要")
    @RequireAuth
    @PostMapping(value = ["/remove"])
    fun remove(@ApiParam("摘要编号") @RequestParam id: String): Mono<RestResponse<Any>> {
        return excerptService.removeRecord(Principal.get().id, id)
    }
}
