package com.theoxao.auth.web

import com.theoxao.auth.dto.UserAccountDTO
import com.theoxao.auth.service.AuthService
import com.theoxao.commons.web.RestResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by theo on 2018/11/16
 */
@RestController
@RequestMapping("auth")
class AuthController(private val authService: AuthService) {

    @RequestMapping("login")
    fun login(@RequestParam code: String, @RequestParam nickName: String, @RequestParam avatarUrl: String): RestResponse<UserAccountDTO> {
        return authService.handleSessionRequest(code, nickName, avatarUrl)
    }


}
