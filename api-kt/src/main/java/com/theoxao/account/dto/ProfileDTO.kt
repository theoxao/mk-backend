package com.theoxao.account.dto

import io.swagger.annotations.ApiModelProperty

class ProfileDTO {
    var userId: String? = null
    var nickName: String? = null
    var avatarUrl: String? = null
    @ApiModelProperty("新消息数")
    var news: Long = 0
    @ApiModelProperty("累计阅读时间，已格式化")
    var readTotalTime: String? = null
    @ApiModelProperty("持续阅读天数")
    var readDuration: Int = 0
    @ApiModelProperty("阅读勋章数")
    var medalCount: Long = 0
    @ApiModelProperty("图书摘录数")
    var excerptCount: Long = 0

    constructor()
    constructor(userId: String?, nickName: String?, avatarUrl: String?) {
        this.userId = userId
        this.nickName = nickName
        this.avatarUrl = avatarUrl
    }

}