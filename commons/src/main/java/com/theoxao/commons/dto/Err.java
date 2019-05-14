package com.theoxao.commons.dto;


import com.theoxao.commons.web.RestResponse;

/**
 * 错误信息
 * Created by hulingwei on 2017/3/16
 */
public class Err {
    public final String msg;
    public final int code;

    public Err(String msg) {
        this.msg = msg;
        this.code = RestResponse.SC_INTERNAL_SERVER_ERROR;
    }

    public Err(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }
}
