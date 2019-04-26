package com.yiche.bigdata.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.constants.log.InfoType;
import com.yiche.bigdata.constants.log.OpType;
import com.yiche.bigdata.dao.LogDao;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.generated.Log;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.pojo.log.LogForFlume;
import com.yiche.bigdata.service.LogService;
import com.yiche.bigdata.service.core.UserContext;
import com.yiche.bigdata.service.core.UserContextContainer;
import com.yiche.bigdata.utils.IpUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private LogDao logDao;

    @Autowired
    protected UserContextContainer userContextContainer;

    /**
     * 用户相关
     */
    @Override
    public void saveNewUser(DomainUserInfo user) {

        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log log = getLog(userInfo, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());

        JSONObject json = new JSONObject();
        json.put("do", "add");
        json.put("targetUserId", user.getRealName());
        log.setAct(JSONUtils.toJSONString(json));
        log.setActDesc(new StringBuilder().append("管理员").append(userInfo.getRealName()).append("新添加了用户").append(user.getRealName()).toString());
        logDao.save(log);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG, log.getUserId(), log.getIp(), OpType.AUTHLOG_USERMANAGE, json, "");
        logForFlume.write();
    }

    /**
     * 角色相关
     */
    @Override
    public void saveRole(Role role) {
        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log dashboardLog = getLog(userInfo, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());

        JSONObject json = new JSONObject();
        json.put("do", "add");
        json.put("targetRoleId", role.getName());
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(userInfo.getRealName()).append("进行了添加角色的操作").toString());
        logDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG, dashboardLog.getUserId(), dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE, json, "");
        logForFlume.write();
    }

    @Override
    public void updateRole(Role role) {
        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log dashboardLog = getLog(userInfo, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());

        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetRoleId", role.getName());
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(userInfo.getRealName()).append("进行了修改角色的操作").toString());
        logDao.save(dashboardLog);

        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG, dashboardLog.getUserId(), dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE, json, "");
        logForFlume.write();
    }

    @Override
    public void deleteRole(String roleId) {
        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log dashboardLog = getLog(userInfo, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());

        JSONObject json = new JSONObject();
        json.put("do", "delete");
        json.put("targetRoleId", roleId);
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(userInfo.getRealName()).append("进行了删除角色操作").toString());
        logDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG, dashboardLog.getUserId(), dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE, json, "");
        logForFlume.write();
    }

    @Override
    public void updateRoleRes(String roleId, List<List<String>> permissionLog) {
        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log dashboardLog = getLog(userInfo, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetRoleId", roleId);
        json.put("now", permissionLog);

        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(userInfo.getRealName()).append("进行了角色赋权的操作").toString());
        logDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG, dashboardLog.getUserId(), dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE, json, "");
        logForFlume.write();
    }

    @Override
    public void deleteUserRoles(String userName, String roleId) {
        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log log = getLog(userInfo, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());

        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetUserIds", userName);
        json.put("targetRoleIds", roleId);
        log.setAct(JSONUtils.toJSONString(json));
        log.setActDesc(new StringBuilder().append("管理员").append(userInfo.getRealName()).append("进行了用户撤销角色的操作").toString());
        logDao.save(log);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG, log.getUserId(), log.getIp(), OpType.AUTHLOG_USERMANAGE, json, "");
        logForFlume.write();
    }

    @Override
    public void updateUserRole(String userName, List roleIdList, String businessLine) {
        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log log = getLog(userInfo, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());

        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetUserId", userName);
        json.put("targetRoleIds", roleIdList);
        log.setAct(JSONUtils.toJSONString(json));
        log.setActDesc(new StringBuilder().append("管理员").append(userInfo.getRealName()).append("进行了用户赋予角色的操作").toString());
        logDao.save(log);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG, log.getUserId(), log.getIp(), OpType.AUTHLOG_USERMANAGE, json, "");
        logForFlume.write();
    }

    @Override
    public void tableToxls(HSSFWorkbook wb, long time) {
        DomainUserInfo userInfo = getUserContext().getUserInfo();
        Log dashboardLog = getLog(userInfo, InfoType.SENSITIVELOG.getStringName(), OpType.SENSITIVELOG_DATA.getStringName());

        JSONObject json = new JSONObject();
        json.put("do", "select");
        json.put("useTime", time + "毫秒");
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("用户").append(userInfo.getRealName()).append("进行了下载操作").toString());
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row = sheet.getRow(0);
        int rowNumber = row.getPhysicalNumberOfCells();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rowNumber; i++) {
            if (i != rowNumber - 1) {
                builder.append(row.getCell(i).getStringCellValue()).append(",");
            } else {
                builder.append(row.getCell(i).getStringCellValue());
            }
        }
        dashboardLog.setOptional(builder.toString());
        logDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.SENSITIVELOG, dashboardLog.getUserId(), dashboardLog.getIp(), OpType.SENSITIVELOG_DATA, json, dashboardLog.getOptional());
        logForFlume.write();
    }

    private Log getLog(DomainUserInfo userInfo, String infoType, String opType) {

        Log log = new Log();
        log.setDepartment(userInfo.getDepartment());
        ///获取用户的中文名
        // log.setUserId(String.valueOf(userInfo.getId()));
        log.setUserId(userInfo.getUserName());
        log.setLogTime(new Date());
        log.setInfoType(infoType);
        log.setOpType(opType);

        log.setIp(IpUtils.getRemoteIp());
        return log;
    }

    protected UserContext getUserContext() {
        String token = TokenUtils.getToken();
        return userContextContainer.getUserContext(token);
    }
}
