package com.yiche.bigdata.entity.pojo.log;

import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.constants.log.OpType;
import org.apache.commons.lang.StringUtils;

/**
 * Created by lb on 2018/4/25.
 * 基本信息
 */
public class LogOperation {
    //static

    //private
    private OpType type;
    private JSONObject act;
    private String abnormal;

    //构造
    public LogOperation(OpType opType, JSONObject act){
        this.type = opType;
        this.act = act;
        this.abnormal = StringUtils.EMPTY;
    }


    //tostring


    //get set
    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

    public JSONObject getAct() {
        return act;
    }

    public void setAct(JSONObject act) {
        this.act = act;
    }

    public String getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(String abnormal) {
        this.abnormal = abnormal;
    }
}
