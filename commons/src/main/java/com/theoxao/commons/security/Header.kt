package com.theoxao.commons.security

import lombok.Getter
import lombok.Setter

/**
 * 接口请求头信息
 * Created by hulingwei on 2017/3/16
 */
@Getter
@Setter
class Header {
    /**
     * 渠道号
     */
    var channel: Long = 0
        set(channel) {
            field = this.channel
        }
    /**
     * 产品编号
     */
    var product: Long = 0
        set(product) {
            field = this.product
        }

    companion object {
        private val store = ThreadLocal<Header>()

        /**
         * 获取Header
         *
         * @return 保存的Header
         */
        fun get(): Header {
            return store.get()
        }

        /**
         * 保存Header到线程，供后续使用
         *
         * @param header Header
         */
        fun store(header: Header) {
            store.set(header)
        }

        /**
         * 释放保存的header
         */
        fun release() {
            store.remove()
        }

        /**
         * 用于测试时设置header的快捷方法
         *
         * @param channel 渠道号
         */
        fun prepareForTest(product: Long, channel: Long) {
            val header = Header()
            header.product = product
            header.channel = channel
            store(header)
        }
    }
}
