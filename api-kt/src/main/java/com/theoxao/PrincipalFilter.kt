package com.theoxao

import com.theoxao.commons.security.Principal
import com.theoxao.commons.security.PrincipalParser
import com.theoxao.resolver.Filter
import io.ktor.application.ApplicationCall
import org.springframework.stereotype.Component


/**
 * @author theo
 * @date 2019/5/13
 */
@Component
class PrincipalFilter : Filter {
    override fun after(call: ApplicationCall) {
        Principal.release()
    }

    override fun before(call: ApplicationCall) {
        val parser = PrincipalParser("auth-principal")
        parser.parsePrincipal(call.request.headers).ifPresent(Principal.Companion::store)
    }
}