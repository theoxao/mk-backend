package com.theoxao.read.vo

import io.swagger.annotations.ApiParam

class ReadRequestVO {
    @ApiParam("此次阅读ID , 结束阅读/暂停阅读/继续阅读时必填")
    var id: String? = null
    @ApiParam(value = "用户书籍编号", required = true)
    var refBook: String? = null
    @ApiParam(value = "结束时提交当前页数 ,结束时必填", example = "1")
    var currentPage: Int = 0
    /**
     * 开始阅读 1，结束阅读 0 ，暂停阅读 -1
     */
    @ApiParam(value = "开始阅读 1，结束阅读 0 ，暂停阅读 -1  继续阅读 -2", required = true, example = "1")
    var operation: Int? = null
    @ApiParam(value = "阅读类型 1 普通阅读 2 定时阅读", required = true, defaultValue = "1", example = "1")
    var type: Int? = 1
    @ApiParam(value = "定时阅读时长")
    var duration: Long? = null
}
