package com.yiche.bigdata.entity.pojo.log;

import org.apache.commons.lang.StringUtils;

/**
 * Created by lb on 2018/4/25.
 * 基本信息
 */
public class LogOptional {
    //static

    //private
    private String dataClass;
    private String databasename;
    private String tablename;
    private String fieldname;

    //构造
    public LogOptional(String optional){
        this.dataClass = "2";//涉及数据级别，1~4分别对应商密|受限|对内公开|对外公开级别
        this.databasename = StringUtils.EMPTY;
        this.tablename = StringUtils.EMPTY;
        this.fieldname = optional;
    }


    //tostring



    //get set

    public String getDataClass() {
        return dataClass;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    public String getDatabasename() {
        return databasename;
    }

    public void setDatabasename(String databasename) {
        this.databasename = databasename;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
}
