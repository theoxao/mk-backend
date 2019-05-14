package com.theoxao.group.vo

import com.theoxao.commons.AbstractVO
import com.theoxao.group.model.Group
import io.swagger.annotations.ApiParam
import org.springframework.web.multipart.MultipartFile

/**
 * Created by theo on 2018/12/18
 */
class GroupVO : AbstractVO<Group>() {
    @ApiParam(value = "小组名", required = true)
    var name: String? = null
    @ApiParam("备注")
    var remark: String? = null
    @ApiParam("背景图")
    var imageFile: MultipartFile? = null
    @ApiParam(value = "小组类型", example = "0")
    var type: Int? = 0
}
