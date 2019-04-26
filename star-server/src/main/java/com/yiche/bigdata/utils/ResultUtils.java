package com.yiche.bigdata.utils;

import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.entity.dto.Result;

public class ResultUtils {

    public static Result buildResult(ResultCode resultCode){
        Result result = new Result();
        result.setCodeAndMsg(resultCode);
        return result;
    }

    public static Result buildResult(ResultCode resultCode, String msg){
        Result result = new Result();
        result.setCodeAndMsg(resultCode);
        result.setMessage(msg);
        return result;
    }

    public static Result buildResult(ResultCode resultCode, Object content){
        Result result = buildResult(resultCode);
        result.setResult(content);
        return result;
    }
}
