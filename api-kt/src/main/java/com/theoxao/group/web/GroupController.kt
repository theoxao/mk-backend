package com.theoxao.group.web

import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import com.theoxao.group.dto.GroupActivityDTO
import com.theoxao.group.dto.GroupDTO
import com.theoxao.group.model.Member
import com.theoxao.group.service.GroupService
import com.theoxao.group.vo.GroupVO
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.apache.commons.lang3.StringUtils
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

/**
 * Created by theo on 2018/12/18
 */
@RestController
@RequestMapping("/group")
class GroupController(private val groupService: GroupService) {

    @ApiOperation("创建小组")
    @RequestMapping("/create", method = [RequestMethod.POST])
    fun create(@ModelAttribute vo: GroupVO): RestResponse<GroupDTO>? {
        val principal = Principal.get()
        val group = vo.bean()
        group.creatorId = principal.id
        group.createAt = Date()
        group.members = Arrays.asList(Member(principal.id, principal.displayName, principal.displayName, principal.avatarUrl, true))
        return groupService.create(group, principal, vo.imageFile)
    }

    @ApiOperation("我的小组列表")
    @RequestMapping("/list" ,method = [RequestMethod.GET])
    suspend fun userGroupList(): RestResponse<List<GroupDTO?>> {
        //TODO 小组消息
        println(Principal.get().id)
        return groupService.groupList(Principal.get().id)
    }

    @ApiOperation("小组信息详情")
    @RequestMapping("/detail" ,method = [RequestMethod.GET])
    fun groupDetail(@ApiParam("小组ID") @RequestParam id: String):RestResponse<GroupDTO> {
        return groupService.findById(id, Principal.get().id)
    }

    @ApiOperation("小组信息编辑提交")
    @PostMapping("/edit")
    fun groupEdit(@ApiParam("小组ID") @RequestParam id: String, @ApiParam("小组名") name: String?, @ApiParam("备注") remark: String?): Mono<RestResponse<Any>> {
        return groupService.editGroup(id, name, remark, Principal.get().id)
    }

    @ApiOperation("小组成员列表")
    @GetMapping("/members")
    fun groupMembers(@ApiParam("小组ID") @RequestParam id: String): Mono<RestResponse<List<Member>>> {
        return groupService.findMembers(id, Principal.get().id)
    }

    @ApiOperation("修改小组昵称")
    @PostMapping("/display_name_edit")
    fun editDisplayName(@ApiParam("小组ID") @RequestParam id: String, @ApiParam("修改后的昵称") name: String?): Mono<RestResponse<Any>> {
        Assert.isTrue(StringUtils.isNotBlank(name), "昵称不能为空")
        return groupService.editName(Principal.get().id, id, name)
    }

    @ApiOperation("退出小组")
    @PostMapping("/quit_group")
    fun quitGroup(@ApiParam("小组ID") @RequestParam id: String): Mono<RestResponse<Any>> {
        return groupService.quit(Principal.get(), id)
    }

    @ApiOperation("加入小组")
    @PostMapping("/join")
    fun joinGroup(@ApiParam("小组ID") @RequestParam id: String, @ApiParam("用户昵称") @RequestParam nickName: String, @ApiParam("用户头像") avatarUrl: String): Mono<RestResponse<Any>> {
        val member = Member(Principal.get().id, nickName, nickName, avatarUrl, false)
        return groupService.joinGroup(member, id, Principal.get())
    }

    @ApiOperation("动态列表")
    @GetMapping("/activity")
    fun activityList(groupId: String): Mono<RestResponse<List<GroupActivityDTO>>> {
        return groupService.activities(groupId)
    }
}
