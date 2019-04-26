package com.yiche.bigdata.constants;

public enum ResultCode {

    OK(0, "成功"),

    JSON_ERROR(0, "JSON校验失败"),

    HTTP_ERROR_MSRP(400, "缺少请求参数"),

    HTTP_ERROR_HMNR(400, "参数解析失败"),

    HTTP_ERROR_MANV(400, "参数验证失败"),

    HTTP_ERROR_BIND(400, "参数绑定失败"),

    HTTP_ERROR_CV(400, "参数验证失败"),

    HTTP_ERROR_V(400, "参数验证失败"),

    HTTP_ERROR_HRMNS(405, "不支持当前请求方法"),

    HTTP_ERROR_TMTNS(415, "不支持当前媒体类型"),

    HTTP_ERROR_SERVICE(500, "业务逻辑异常"),

    HTTP_ERROR_DAO(500, "数据持久层逻辑异常"),

    HTTP_ERROR_DIV(500, "操作数据库出现异常"),

    HTTP_ERROR_RE(500, "feign的eureka第三方服务调用异常"),

    HTTP_ERROR_E(500, "通用异常"),

    VALID_ERROR(401, "参数错误，未通过验证"),

    USER_NAME_INVALID(100001, "用户名无效"),

    EX_TOKEN_OVERDUE(100009, "登陆超时, 或token无效"),

    AUTHENTICATION_SERVICE_ERROR(100010, "认证服务异常"),

    NO_AVAILABLE_BUSINESS_LINE(110000, "无可用业务线"),

    NO_AVAILABLE_MENU(110001, "用户无权限"),

    NO_PERMISSION(110002, "用户无权限"),

    BUSINESS_LINE_EXIST(200001, "业务线已存在"),

    BUSINESS_LINE_DATASOURCE_EMPTY(200002, "业务线关联数据资源为空"),

    DATA_TABLE_NOT_EXIST(200101, "事实表不存在"),

    DELETE_DATA_TABLE_FAILURE(200102, "删除事实表失败"),

    LOAD_DATA_TABLE_FAILURE(200103, "加载事实表信息失败"),

    LOAD_METADATA_FAILURE(200104, "获取元数据信息失败"),

    SOME_DATA_RESOURCE_ASSIGNED_FAILURE(200105, "获取元数据信息失败"),

    DASHBOARD_NOT_EXIST(200201, "大盘不存在"),

    DASHBOARD_EXIST_MORE(200202, "出现一条业务线对应多个大盘数据"),

    REPORT_NOT_EXIST(200301, "报表不存在"),

    WIDGET_NOT_EXIST(200401, "图表不存在"),

    WIDGET_NAME_EXIST(200402, "图表名称已存在"),

    REPORT_NAME_EXIST(200402, "报告名称已存在"),

    WIDGET_DATA_JSON_PARSE_ERROR(200409, "图表配置数据解析错误"),

    DATASET_NOT_EXIST(200501, "数据集不存在"),

    DATA_SET_DATA_ERROR(200502, "数据集数据异常"),

    DATA_SET_USED_BY_WIDGET(200503, "数据集被单图使用，不可直接删除"),

    DATA_SET_NAME_EXIST(200504, "数据集名称已存在"),

    DIRECTORY_NAME_EXIST(200504, "文件夹名称已存在"),

    DATA_SET_USED_BY_REPORT(200503, "数据集被报告使用，不可直接删除"),

    NOT_ALLOWED_TO_DELETE_DEFAULT_DIRECTORY(200505, "默认文件夹不可删除"),

    DATA_SET_ROW_PERMISSION_OVER_LIMIT(200601, "每个数据集最多能设置5条行级权限"),

    BUSINESS_LINE_NOT_EXIST(300001, "业务线不存在"),

    BUSINESS_LINE_USER_EXIST(300001, "业务线已添加该用户"),

    BUSINESS_LINE_USER_NOT_EXIST(300002, "用户不存在"),

    DELETE_BUSINESS_LINE_USER_FAILURE(300003, "删除用户失败"),

    BUSINESS_LINE_ROLE_EXIST(310001, "角色已存在"),

    ROLE_NOT_EXIST(310002, "角色不存在"),

    UPDATE_USER_ROLE_REL_FAILURE(320001, "修改用户角色失败"),

    DATASOURCE_CONNECT_FAILURE(500000, "连接数据源失败"),

    DATASOURCE_NOT_EXIST(500001, "数据源不存在"),

    DATASOURCE_PARAM_NOT_EXIST(500002, "数据源配置参数不存在"),

    NO_DATASOURCE_EXIST(500100, "无可用数据源"),

    SEARCH_SERVER_NOT_EXIST(600001, "查询服务不存在"),

    SEARCH_SERVER_PARAM_NOT_EXIST(600002, "查询数据不存在"),

    SEARCH_SERVER_PARAM_EXCEPTION(600003, "查询参数异常"),

    SEARCH_SERVER_EXCEPTION(600004, "查询异常"),

    SEARCH_SERVER_TIME_OUT(600005, "查询超时，请重试"),

    SEARCH_SERVER_ERROR(600006, "查询服务错误"),

    SEARCH_SERVER_COLUMN_ERROR(600007, "自定义字段设计有误,重新检查"),

    DIRECTORY_NAME_REPEAT(800001, "目录名称重复"),

    NODE_NOT_EXIST(800002, "该节点不存在"),

    PARENT_NODE_NOT_EXIST(800003, "父节点不存在"),

    DELETE_NODE_FAILURE_CHILD_EXIST(800004, "该节点下有子元素, 删除节点失败"),

    DELETE_NODE_FAILURE(800005, "删除节点失败"),

    MODIFY_NODE_FAILURE(800006, "修改节点信息失败"),

    DEFAULT_DIRECTORY_NOT_EXIST(800007, "默认文件夹不存在"),

    INPUT_EMPTY(900302, "输入参数为空"),

    PERMISSION_SAVE_FAILURE(900501, "权限保存失败"),

    PERMISSION_NOT_EXITS(900501, " 不存在的权限类型"),

    ID_EMPTY(900605, "ID为空"),

    NO_RESULT_FOUND(900802, "查询结果为空"),

    DELETE_DATA_FAILURE(900802, "删除数据失败"),

    DATA_PERSISTENCE_FAILURE(900801, "数据持久化失败"),

    EXPRESSION_CONTAINS_CHINESE_CHARACTER(700001, "表达式包含中文字符");


    private final int value;
    private final String reasonPhrase;

    ResultCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}
