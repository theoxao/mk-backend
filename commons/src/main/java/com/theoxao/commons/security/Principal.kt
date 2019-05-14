package com.theoxao.commons.security

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.Getter
import lombok.Setter
import org.springframework.util.Assert

@Getter
@Setter
class Principal {
    lateinit var id: String

    lateinit var token: String

    lateinit var displayName: String

    lateinit var avatarUrl: String

    @JsonIgnore
    var principalText: String? = null
        set(principalText) {
            field = this.principalText
        }

    companion object {
        private val store = ThreadLocal<Principal>()

        /**
         * principal是否存在
         *
         * @return true: 存在, false: 不存在
         */
        fun exists(): Boolean {
            return store.get() != null
        }

        /**
         * 获取principal
         *
         * @return 保存的principal
         */
        fun get(): Principal {

            val principal = store.get()
            Assert.notNull(principal, "认证失败")
            return principal
        }

        /**
         * 保存principal到线程，供后续使用
         *
         * @param principal principal
         */
        fun store(principal: Principal) {
            store.set(principal)
        }

        /**
         * 释放保存的principal
         */
        fun release() {
            store.remove()
        }

        /**
         * 用于测试时设置principal的快捷方法
         *
         * @param id          用户ID
         * @param token       认证令牌
         * @param displayName 显示昵称
         */
        fun prepareForTest(id: String, token: String, displayName: String, avatarUrl: String) {
            val principal = Principal()
            principal.id = id
            principal.token = token
            principal.displayName = displayName
            principal.avatarUrl = avatarUrl
            store(principal)
        }
    }
}
