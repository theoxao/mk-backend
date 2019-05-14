package com.theoxao.read.vo

import com.theoxao.commons.AbstractVO
import com.theoxao.commons.web.Create
import com.theoxao.read.model.Excerpt
import io.swagger.annotations.ApiParam
import lombok.Getter
import lombok.Setter

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import java.util.Date

/**
 * Created by theo on 2018/11/13
 */
class ExcerptVO : AbstractVO<Excerpt>() {
    @NotEmpty(groups = arrayOf(Create::class))
    @ApiParam(value = "摘要关联的书籍编号", required = true)
    var refBook: String? = null
    @NotNull(groups = arrayOf(Create::class))
    @ApiParam(value = "摘要位置", required = true, example = "1")
    var refPage: Int? = null
    @ApiParam(value = "摘要内容", required = true)
    var content: String? = null
    @ApiParam(value = "摘要图片地址列表")
    var images: MutableList<String> = arrayListOf()
	var quoto :String = ""
}
