package com.theoxao.group.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.theoxao.group.model.Group
import com.theoxao.group.model.Member
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.BeanUtils

import java.util.Date

/**
 * Created by theo on 2018/12/18
 */
@JsonIgnoreProperties("createAt", "creatorId")
class GroupDTO {
    var id: String? = null

    @ApiModelProperty("创建者ID")
    var creatorId: String? = null
    @ApiModelProperty("小组名")
    var name: String? = null
    @ApiModelProperty("小组备注")
    var remark: String? = null
    @ApiModelProperty("背景图")
    var image: String? = null
    @ApiModelProperty("小组类型")
    var type: Int? = null
    var createAt: Date? = null
    @ApiModelProperty("小组成员数")
    var memberCount: Int? = null
    @ApiModelProperty("小组成员信息")
    var members: List<Member>? = null
    @ApiModelProperty("是否是创建者")
    var owner: Boolean? = false
    @ApiModelProperty(value = "小组未读消息数", example = "0")
    var messageCount: Int? = 0

    companion object {

        fun fromEntity(entity: Group): GroupDTO {
            val record = GroupDTO()
            BeanUtils.copyProperties(entity, record)
            record.id = entity.id?.toHexString()
            record.memberCount = entity.members.size
            return record
        }
    }
}
