package com.theoxao.group.model

import io.swagger.annotations.ApiModelProperty
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

/**
 * Created by theo on 2018/12/18
 */
@Getter
@Setter
@NoArgsConstructor
open class Member {
    @ApiModelProperty("用户ID")
    var userId: String? = null
    @ApiModelProperty("小组昵称")
    var displayName: String? = null
    @ApiModelProperty("用户昵称")
    var nickName: String? = null
    @ApiModelProperty(value = "用户角色", example = "0")
    var role: Int? = 0
    @ApiModelProperty("用户头像")
    var avatarUrl: String? = null
    @ApiModelProperty("是否是所有者")
    var owner: Boolean = false

    constructor()

    constructor(userId: String, displayName: String, nickName: String, avatarUrl: String, owner: Boolean) {
        this.userId = userId
        this.displayName = displayName
        this.nickName = nickName
        this.avatarUrl = avatarUrl
        this.owner = owner
    }
}
