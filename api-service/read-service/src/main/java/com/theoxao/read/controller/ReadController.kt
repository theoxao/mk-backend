package com.theoxao.read.controller

import org.springframework.web.bind.annotation.RequestBody
import com.theoxao.commons.annotations.RequireAuth
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.read.dto.BookDetailDTO
import com.theoxao.read.dto.Coordinate
import com.theoxao.read.dto.ReadLogDTO
import com.theoxao.read.dto.ReadRecordDTO
import com.theoxao.read.enums.StatSource
import com.theoxao.read.enums.StatType
import com.theoxao.read.service.ReadService
import com.theoxao.read.service.StatService
import com.theoxao.read.vo.ReadRequestVO
import com.theoxao.read.vo.UserBookVO
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.util.Assert
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("read")
class ReadController(private val readService: ReadService, private val statService: StatService) {

    @RequireAuth
    @ApiOperation("添加书籍")
    @PostMapping("add_book")
    fun addNewBook(@RequestBody @Validated vo: UserBookVO): Mono<RestResponse<Any>> {
        val userBook = vo.bean()
		userBook.initPage=vo.currentPage
        userBook.userId = Principal.get().id
        return readService.addUserBook(userBook)
    }

    /**
     * 书籍阅读详情
     */
    @ApiOperation("根据ID返回书籍阅读详情")
    @GetMapping("detail")
    fun detail(id: String): Mono<RestResponse<BookDetailDTO>> {
        val principal = Principal.get()
        Assert.notNull(principal, "登录信息有误")
        return readService.detail(principal, id)
    }

    /**
     * 查询阅读进度记录
     *
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("获取某本书阅读进度记录")
    @RequireAuth
    @GetMapping("read_log")
    fun readRecord(@ApiParam("最后一条记录id")
                   offsetId: String?,
                   @ApiParam("每次加载的记录数，不传默认20  ，传0则返回三条")
                   @RequestParam(required = false, defaultValue = "20")
                   size: Int,
                   @ApiParam("用户书籍编号")
                   id: String): Mono<RestResponse<List<ReadRecordDTO>>> {
        return readService.readLog(Principal.get(), offsetId, size, id)
    }

    @ApiModelProperty("阅读统计")
    @GetMapping("read_stat")
    fun readStat(@RequestParam id: String): Mono<RestResponse<Map<String, Any>>> {
        return readService.readStat(id, Principal.get())
    }
    /**
     * 开始阅读
     */
    @RequireAuth
    @PostMapping("read_operate")
    fun readOperation(@ModelAttribute vo: ReadRequestVO): Mono<RestResponse<ReadLogDTO>> {
        if (vo.id == null)
            Assert.isTrue(vo.operation == 1, "阅读进度ID不能为空")
        else
            Assert.isTrue(vo.operation != 1, "开始阅读时不需要提供进度ID")
        return readService.handleOperation(Principal.get().id, vo)
    }

    @GetMapping("chart_stat")
    fun chartStat(refBook: String?, type: Int ,source:Int): Mono<RestResponse<List<Coordinate>>> {
        return statService.statByTime(refBook, Principal(), StatType.getType(type) ,StatSource.getType(source))
    }













}
