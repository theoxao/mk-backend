package com.theoxao.group.enums

enum class ActivityEnum {
    CREATE_GROUP(1, "创建了小组"),
    JOIN_GROUP(2, "加入小组"),
    QUIT_GROUP(3, "退出小组");

    var code: Int
    var text: String

    constructor(code: Int, text: String) {
        this.code = code
        this.text = text
    }

    companion object {
        fun parse(code: Int): String {
            for (value in values()) {
                if (value.code == code)
                    return value.text
            }
            return ""
        }

    }


}