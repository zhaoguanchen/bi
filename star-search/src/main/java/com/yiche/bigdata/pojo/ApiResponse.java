package com.yiche.bigdata.pojo;

import java.io.Serializable;


public class ApiResponse implements Serializable {

    private static final long serialVersionUID = 4775582366583523461L;

    private int errorNo = 0;

    private String errorMsg = "";

    private Object data;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }



    public ApiResponse(){}

    public ApiResponse(int errorNo, String errorMsg) {
        this.errorMsg = errorMsg;
        this.errorNo = errorNo;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "errorNo=" + errorNo +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
