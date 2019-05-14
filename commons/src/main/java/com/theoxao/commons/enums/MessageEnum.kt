package com.theoxao.commons.enums

enum class MessageEnum {

    LIKE(1),
    COMMENT(2),
    REPLY(3),
    SYSTEM(4);

    var code: Int

    constructor(code: Int) {
        this.code = code
    }


}