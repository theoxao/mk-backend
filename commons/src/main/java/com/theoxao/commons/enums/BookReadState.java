package com.theoxao.commons.enums;

/**
 * Created by theo on 2018/11/7
 */
public enum BookReadState {
    not_read(0, "未读"),
    reading(1, "正在阅读"),
    read(2, "已读");

    private Integer code;
    private String text;

    BookReadState(Integer code, String text) {
        this.code = code;
        this.text = text;
    }
}
