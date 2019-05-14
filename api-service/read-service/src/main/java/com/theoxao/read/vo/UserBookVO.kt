package com.theoxao.read.vo

import com.theoxao.commons.AbstractVO
import com.theoxao.read.model.UserBook
import io.swagger.annotations.ApiParam
import org.hibernate.validator.constraints.Length
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * Created by theo on 2018/11/8
 */
class UserBookVO : AbstractVO<UserBook>() {

    @ApiParam(value = "书籍ISBN", required = true)
    var isbn: String? = null

    @ApiParam("书籍封面地址，无则使用默认图片")
    var cover: String? = null

    @NotBlank
    @ApiParam("书名")
    var name: String? = null

    @NotBlank
    @ApiParam("作者名")
    var author: String? = null

    @ApiParam("出版社")
    var publisher: String? = null

    @NotNull
    @ApiParam(value = "页数", required = true, example = "1")
    var pageCount: Int? = null

    @ApiParam("分类/标签")
    var tag: String? = null

    @ApiParam(value = "当前阅读页数", example = "20")
    var currentPage: Int? = null

    @ApiParam("阅读状态 1 正在阅读 2 已读  3 未读 ")
    var state: Int? = null

    @ApiParam("关联书籍编号")
    var refBookId: String? = null

    @ApiParam("归还日期")
    var returnDate: Date? = null

    @ApiParam("借书备注")
    var remark: String? = null
}
