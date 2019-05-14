package com.theoxao.system.web

import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author theo
 * @date 2019/5/13
 */
@RestController
@RequestMapping("/system")
class SystemController {

    @RequestMapping("ids")
    fun generateId(count: Int): List<String> {
        return (0 until count).map {
            ObjectId().toHexString()
        }
    }

}