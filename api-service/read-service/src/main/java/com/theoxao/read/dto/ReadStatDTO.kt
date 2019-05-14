package com.theoxao.read.dto

import io.swagger.annotations.ApiModelProperty
import lombok.Getter
import lombok.Setter

/**
 * Created by theo on 2018/11/15
 */
class ReadStatDTO {

    @ApiModelProperty("阅读页数")
    var pages: String? = null
    @ApiModelProperty("累计阅读时间")
    var totalTime: String? = null
    @ApiModelProperty("预计剩余时间")
    var remainTime: String? = null


}
