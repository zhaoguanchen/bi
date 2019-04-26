package com.yiche.bigdata.constants;

public enum ResourceType {

    COMMON_TYPE(0, "普通节点"),

    PAGE_TYPE(1, "页面"),

    OPT_TYPE(2, "按钮"),

    BUSINESS_LINE(100, "业务线"),

    DASHBOARD(101, "业务大盘"),

    REPORT_DIRECTORY(102, "报表目录"),

    REPORT(103, "报表"),

    WIDGET_DIRECTORY(104, "图表目录"),

    WIDGET(105, "图表"),

    DATA_TABLE_DIRECTORY(200, "事实表目录"),

    DATA_TABLE(201, "事实表"),

    DATA_SET_DIRECTORY(300, "数据集目录"),

    DATA_SET(301, "数据集");

    private final int value;
    private final String name;

    ResourceType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static String directoryTypes() {
        return "102,104,200,300";
    }

    public static Integer getDirectoryType(Integer resType) {
        switch (resType){
            case 103 :
                return 102;
            case 105 :
                return 104;
            case 201 :
                return 200;
            case 301 :
                return 300;
            default:
                return null;
        }
    }

    public static String[] getBusinessLineLevelPermission(Integer resType) {
        switch (resType){
            case 101:
                return new String[]{"add_dashboard"};
            case 102 :
                return new String[]{"add_report"};
            case 103 :
                return new String[]{"add_report"};
            case 104 :
                return new String[]{"add_widget"};
            case 105 :
                return new String[]{"add_widget"};
            case 300 :
                return new String[]{"add_dataset"};
            case 301 :
                return new String[]{"add_dataset"};
            default:
                return null;
        }
    }

}
