package com.theoxao.auth.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.theoxao.account.UserAccount
import com.theoxao.auth.dto.UserAccountDTO
import com.theoxao.commons.security.Principal
import com.theoxao.commons.web.RestResponse
import lombok.extern.slf4j.Slf4j
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.util.UriTemplate
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by theo on 2018/12/3
 */
@Slf4j
@Service
class AuthService(private val objectMapper: ObjectMapper, private val mongoTemplate: ReactiveMongoTemplate, private val redisTemplate: StringRedisTemplate) {

    private val appId = "wx965054fdad151919"
    private val secret = "2df63eaa1b641d7f05108ca41b2483ca"
    private val CLIENT = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build()

    fun handleSessionRequest(code: String, nickName: String, avatarUrl: String): RestResponse<UserAccountDTO> {
        var nickName = nickName
        var avatarUrl = avatarUrl
        val url = SESSION_URI.expand(appId, secret, code).toString()
        try {
            val response = CLIENT.newCall(Request.Builder().url(url).build()).execute()
            val sessionNode = objectMapper.readTree(String(response.body()!!.bytes(), Charset.defaultCharset()))
            if (sessionNode.get("errcode") != null) {
                throw RuntimeException("获取session失败")
            }
            val openId = sessionNode.get("openid").asText()
            val sessionKey = sessionNode.get("session_key").asText()
            var unionId = ""
            if (sessionNode.get("unionid") != null) {
                unionId = sessionNode.get("unionid").asText()
            }
            var account: UserAccount? = null
            var result: UserAccountDTO? = null

            val query = Query.query(Criteria.where("openId").`is`(openId))
            account = findUserAccount(query)
            if (account != null) {
                //已存在用户
//                if (StringUtils.isEmpty(account.nickName)) {
//                    //无用户名信息
//                    val update = Update()
//                    update.set("nickName", nickName)
//                    update.set("avatarUrl", avatarUrl)
//                    mongoTemplate.upsert(query, update, UserAccount::class.java).subscribe()
//                }
                val record = mongoTemplate.findOne(query, UserAccount::class.java).block()
                if (record != null) {
                    result = UserAccountDTO.fromEntity(record)
                    result.token = generateToken(result)
                    return RestResponse<UserAccountDTO>().ok().withData(result)
                }
                return RestResponse.notFound()
            } else {
                //用户不存在，需要新建
                val record = UserAccount(openId, sessionKey, unionId)
                val id = ObjectId()
                record.id = id
                record.nickName = nickName
                record.avatarUrl = avatarUrl
                mongoTemplate.save(record).subscribe()
                result = UserAccountDTO.fromEntity(record)
            }

            result.token = generateToken(result)
            return RestResponse<UserAccountDTO>().ok().withData(result)
        } catch (e: IOException) {
            throw RuntimeException("请求失败")
        }
    }

    private fun generateToken(userAccount: UserAccountDTO): String {
        val key = UUID.randomUUID().toString()
        val principal = Principal()
        principal.displayName = userAccount.nickName!!
        principal.id = userAccount.id!!
        principal.avatarUrl = userAccount.avatarUrl!!
        principal.token = key
        var principalText = ""
        try {
            principalText = objectMapper.writeValueAsString(principal)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }

        redisTemplate.boundValueOps("token::$key").set(principalText, 5, TimeUnit.DAYS)
        return key
    }

    private fun findUserAccount(query: Query): UserAccount? {
        return mongoTemplate.findOne(query, UserAccount::class.java).block()
    }

    companion object {

        private val SESSION_URI = UriTemplate("https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={secret}&js_code={code}&grant_type=authorization_code")
    }

}
