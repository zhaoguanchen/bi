package com.yiche.bigdata.entity.dto;

import com.yiche.bigdata.constants.ResultCode;

import java.io.Serializable;

public class Result<T> implements Serializable {

	private static final long serialVersionUID = -8432231312610403150L;

	private int code;

	private String message;

	private T result;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public void setCodeAndMsg(ResultCode resultCode){
		this.code = resultCode.value();
		this.message = resultCode.getReasonPhrase();
	}

	public Result() {
	}

	public Result(int code, String message) {
		this.setCode(code);
		this.setMessage(message);
	}

	@Override
	public String toString() {
		return "Result{" +
				"code=" + code +
				", message='" + message + '\'' +
				", result=" + result +
				'}';
	}
}