package com.theoxao.account.web

import com.theoxao.account.dto.MedalRecordDTO
import com.theoxao.account.dto.MessageDTO
import com.theoxao.account.dto.ProfileDTO
import com.theoxao.account.service.AccountService
import com.theoxao.account.service.MessageService
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/account")
class AccountController(private val accountService: AccountService, private val messageService: MessageService) {

    @ApiOperation("个人信息")
    @GetMapping("/profile")
    fun profile(): RestResponse<ProfileDTO> {
        val principal = Principal.get()
        return accountService.profile(principal)
    }

    @ApiOperation("消息列表")
    @GetMapping("/message_list")
    fun messageList(@ApiParam("最后一条消息ID") offsetId: String?): Mono<RestResponse<List<MessageDTO>>> {
        return messageService.listMessage(Principal.get().id, offsetId)
    }

    @ApiOperation("勋章列表")
    @GetMapping("/medal_list")
    fun medalList(): Mono<RestResponse<List<MedalRecordDTO>>> {
        return accountService.medalList(Principal.get().id)
    }
}