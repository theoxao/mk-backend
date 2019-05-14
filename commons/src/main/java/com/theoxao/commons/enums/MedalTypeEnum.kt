package com.theoxao.commons.enums

enum class MedalTypeEnum {

    BOOK_COUNT(1, "阅读书籍数"),
    PAGE_COUNT(2, "阅读页数"),
    READ_DURATION(3, "连续阅读天数"),
    ADD_EXCERPT(4, "添加摘录数"),
    EDIT_EXCERPT(5, "编辑摘录数"),
    ADD_BORROW_EXCERPT(6, "借书摘录"),
    READ_RECORD(7, "阅读记录相关");

    var code: Int
    private var text: String

    constructor(code: Int, text: String) {
        this.code = code
        this.text = text
    }
}