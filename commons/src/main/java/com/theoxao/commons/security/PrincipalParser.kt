package com.theoxao.commons.security

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import java.nio.charset.Charset

import java.util.Base64
import java.util.Optional

/**
 * Created by Dolphin on 2018/4/3
 */
class PrincipalParser(private val principalHeader: String) {
    private val objectMapper = ObjectMapper()

    init {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        INSTANCE = this
    }

    fun parsePrincipal(headers: io.ktor.http.Headers): Optional<Principal> {
        if (INSTANCE == null) {
            log.error("PrincipalParser not initialized!")
            return Optional.empty()
        }
        val principalStr = headers[INSTANCE!!.principalHeader]
        println(principalStr)
        if (principalStr != null && principalStr.isNotEmpty()) {
            try {
                val principal = INSTANCE!!.objectMapper.readValue(String(Base64.getDecoder().decode(principalStr), Charset.forName("UTF-8")), Principal::class.java)
                principal.principalText = principalStr
                return Optional.of(principal)
            } catch (e: Exception) {
                log.error("failed to parse principal:$principalStr", e)
            }

        }
        return Optional.empty()
    }

    companion object {
        private val log = LoggerFactory.getLogger(PrincipalParser::class.java)
        private var INSTANCE: PrincipalParser? =null

        fun parseHeader(headers: HttpHeaders): Header {
            val header = Header()
            // 提取产品编号
            val productStr = headers.getFirst("x-i-product")

            if (productStr != null) {
                try {
                    header.product = java.lang.Long.parseLong(productStr)
                } catch (e: NumberFormatException) {
                    log.error("malformed product in header:$productStr", e)
                }

            }
            // 提取渠道编号
            val channelStr = headers.getFirst("x-i-channel")

            if (channelStr != null) {
                try {
                    header.channel = java.lang.Long.parseLong(channelStr)
                } catch (e: NumberFormatException) {
                    log.error("malformed channel in header:$channelStr", e)
                }

            }
            return header
        }
    }
}
