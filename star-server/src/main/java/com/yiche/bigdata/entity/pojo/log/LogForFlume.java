package com.yiche.bigdata.entity.pojo.log;

import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.constants.log.InfoType;
import com.yiche.bigdata.constants.log.OpType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lb on 2018/4/24.
 */
public class LogForFlume {

    private static final Logger LOG = LoggerFactory.getLogger(LogForFlume.class);

    private InfoType infotype;
    private String interfaceNo;
    private LogBase base;
    private LogOperation operation;
    private LogOptional optional;

    /**
     * 构建传输给安委会的日志
     * @param infoType 日志类型
     * @param userId 操作人
     * @param ipAddr 操作人ip
     * @param opType 操作类型
     * @param act 操作
     * @param optional 附加信息，目前只有下载时涉及的字段，涉及多个字段，用“,“分割
     */
    public LogForFlume(InfoType infoType, String userId, String ipAddr, OpType opType, JSONObject act, String optional){
        this.infotype = infoType;
        this.interfaceNo = "BI_001";//固定BI_001
        this.base = new LogBase(userId,ipAddr);
        this.operation = new LogOperation(opType,act);
        this.optional = new LogOptional(optional);
    }

    public void write(){
        String log = JSONObject.toJSONString(this);
        LOG.info(log);
    }

    public InfoType getInfotype() {
        return infotype;
    }

    public void setInfotype(InfoType infotype) {
        this.infotype = infotype;
    }

    public String getInterfaceNo() {
        return interfaceNo;
    }

    public void setInterfaceNo(String interfaceNo) {
        this.interfaceNo = interfaceNo;
    }

    public LogBase getBase() {
        return base;
    }

    public void setBase(LogBase base) {
        this.base = base;
    }

    public LogOperation getOperation() {
        return operation;
    }

    public void setOperation(LogOperation operation) {
        this.operation = operation;
    }

    public LogOptional getOptional() {
        return optional;
    }

    public void setOptional(LogOptional optional) {
        this.optional = optional;
    }
}
