package com.yiche.bigdata.constant;

/**
 * Created by yangyuchen on 22/12/2017.
 */
public enum ErrorCode {
    SUCCESS(0,"成功"),
    FAILURE(9999,"未知异常");

    private int code;
    private String message;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
