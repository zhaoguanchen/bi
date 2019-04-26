package com.yiche.bigdata.services;

import org.springframework.stereotype.Service;

/**
 * Created by root on 2018/4/24.
 */
@Service
public class LogService {
   /* @Autowired
    private DashboardLogDao dashboardLogDao;

    @Autowired
    private UserCheckDao userCheckDao;
    public void deleteUserRoles(HttpServletRequest request, String userIds, String roleIds, User user) {
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetUserIds", userIds);
        json.put("targetRoleIds", roleIds);
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(user.getName()).append("进行了用户撤销角色的操作").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE,json,"");
        logForFlume.write();
    }

    public void updateUserRole(HttpServletRequest request, String userIds, String roleIds, User user) {
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetUserId",userIds);
        json.put("targetRoleIds", roleIds);
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(user.getName()).append("进行了用户赋予角色的操作").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE,json,"");
        logForFlume.write();
    }

    public void saveNewUser(HttpServletRequest request, JSONObject jsonObject, User user) {
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "add");
        json.put("targetUserId", jsonObject.getString("userName"));
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(user.getName()).append("新添加了用户").append(jsonObject.getString("userName")).toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE,json,"");
        logForFlume.write();
    }

    public void updateRoleRes(HttpServletRequest request, String roleId, String res, User user) {
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetRoleId", roleId);
        json.put("now", res);
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(user.getName()).append("进行了角色赋权的操作").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE,json,"");
        logForFlume.write();
    }

    public void saveRole(HttpServletRequest request, JSONObject jsonObject, User user) {
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "add");
        json.put("targetRoleId", jsonObject.getString("roleName"));
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(user.getName()).append("进行了添加角色的操作").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE,json,"");
        logForFlume.write();
    }

    public void updateRole(HttpServletRequest request, JSONObject jsonObject, User user) {
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "update");
        json.put("targetRoleId", jsonObject.getString("roleName"));
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(user.getName()).append("进行了修改角色的操作").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE,json,"");
        logForFlume.write();
    }

    public void deleteRole(HttpServletRequest request, String roleId, User user) {
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.AUTHLOG.getStringName(), OpType.AUTHLOG_USERMANAGE.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "delete");
        json.put("targetRoleId", roleId);
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("管理员").append(user.getName()).append("进行了删除角色操作").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.AUTHLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.AUTHLOG_USERMANAGE,json,"");
        logForFlume.write();
    }

    public void tableToxls(HSSFWorkbook wb, HttpServletRequest request, User user, long time){
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.SENSITIVELOG.getStringName(), OpType.SENSITIVELOG_DATA.getStringName());
        JSONObject json = new JSONObject();
        json.put("do", "select");
        json.put("useTime", time+"毫秒");
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("用户").append(user.getName()).append("进行了下载操作").toString());
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row = sheet.getRow(0);
        int rowNumber = row.getPhysicalNumberOfCells();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<rowNumber;i++){
            if(i!=rowNumber-1){
                builder.append(row.getCell(i).getStringCellValue()).append(",");
            }else {
                builder.append(row.getCell(i).getStringCellValue());
            }
        }
        dashboardLog.setOptional(builder.toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.SENSITIVELOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.SENSITIVELOG_DATA,json,dashboardLog.getOptional());
        logForFlume.write();
    }
    public void authSuccess(HttpServletRequest request, User user){
        DashboardLog dashboardLog = getDashboardLog(request, user, InfoType.LOADLOG.getStringName(), OpType.LOADLOG_AUTH.getStringName());
        JSONObject json = new JSONObject();
        json.put("do","login");
        json.put("result","success");
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append(user.getName()).append("登录成功").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.LOADLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.LOADLOG_DOMAIN,json,"");
        logForFlume.write();
    }

    public void authFailure(HttpServletRequest request){
        DashboardLog dashboardLog = new DashboardLog();
        dashboardLog.setIp(IpUtils.getRemoteIp(request));
        dashboardLog.setLogTime(new Date());
        dashboardLog.setInfoType(InfoType.LOADLOG.getStringName());
        dashboardLog.setOpType(OpType.LOADLOG_AUTH.getStringName());
        JSONObject json = new JSONObject();
        json.put("do","login");
        json.put("result","fail");
        dashboardLog.setAct(JSONUtils.toJSONString(json));
        dashboardLog.setActDesc(new StringBuilder().append("ip为").append(IpUtils.getRemoteIp(request)).append("的机器登录失败").toString());
        dashboardLogDao.save(dashboardLog);
        //写日志到文件
        LogForFlume logForFlume = new LogForFlume(InfoType.LOADLOG,dashboardLog.getUserId(),dashboardLog.getIp(), OpType.LOADLOG_DOMAIN,json,"");
        logForFlume.write();
    }
    private DashboardLog getDashboardLog(HttpServletRequest request, User user, String infoType, String opType) {
        String response = userCheckDao.checkUserNameLegal(user.getUsername());
        JSONObject result = JSONObject.parseObject(response);
        String[] departments = result.getString("department").split("-");
        DashboardLog dashboardLog = new DashboardLog();
        dashboardLog.setDepartment(new StringBuilder().append(departments[0]).append("-").append(departments[1]).toString());
        dashboardLog.setIp(IpUtils.getRemoteIp(request));
        dashboardLog.setUserId(user.getName());
        dashboardLog.setLogTime(new Date());
        dashboardLog.setInfoType(infoType);
        dashboardLog.setOpType(opType);
        return dashboardLog;
    }*/
}
