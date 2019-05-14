package com.theoxao.commons.web;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.theoxao.commons.dto.Err;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RestResponse<T> {
    public static final String STATIC_ERROR_RESPONSE = "{\"status\": 500, \"error\": \"服务器内部错误!\", \"data\": null}";
    /**
     * 请求成功
     */
    public static final int SC_OK = 200;
    /**
     * 重定向
     */
    public static final int SC_MOVED_TEMPORARILY = 302;
    /**
     * 服务器异常
     */
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    /**
     * 资源不存在
     */
    public static final int SC_NOT_FOUND = 404;
    /**
     * 没有权限
     */
    public static final int SC_UNAUTHORIZED = 401;
    /**
     * 需要付费
     */
    public static final int SC_PAYMENT_REQUIRED = 402;
    private int status = SC_INTERNAL_SERVER_ERROR;
    private String error;
    private Long timestamp = new Date().getTime();
    private T data;

    public RestResponse(T data) {
        this.status = SC_OK;
        this.data = data;
    }

    public static <T> RestResponse<T> success() {
        return new RestResponse<T>().ok();
    }

    public static <T> RestResponse<T> notFound() {
        return new RestResponse<T>().withStatus(SC_NOT_FOUND);
    }

    public static <T> RestResponse<T> notFound(String error) {
        return new RestResponse<T>().withStatus(SC_NOT_FOUND).withError(error);
    }

    public static <T> RestResponse<T> error(String error) {
        return new RestResponse<T>().withStatus(SC_INTERNAL_SERVER_ERROR).withError(error);
    }

    public RestResponse<T> ok() {
        this.status = SC_OK;
        return this;
    }

    public RestResponse<T> withStatus(int status) {
        this.status = status;
        return this;
    }

    public RestResponse<T> withError(String error) {
        this.error = error;
        return this;
    }

    public RestResponse<T> withError(Err error) {
        if (error == null) {
            return this.ok();
        }
        this.error = error.msg;
        this.status = error.code;
        return this;
    }

    public RestResponse<T> withData(T data) {
        this.data = data;
        return this;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return status == SC_OK;
    }
}
