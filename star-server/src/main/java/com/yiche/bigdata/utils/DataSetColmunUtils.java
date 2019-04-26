package com.yiche.bigdata.utils;

public class DataSetColmunUtils {

    public static String getTypeKey(String colType){
        String dataType = "";
        if("keyword,text".contains(colType)){
            dataType = "string";
        }else if("date".contains(colType)){
            dataType = "date";
        }else if("long,integer,short,byte,double,float".contains(colType)){
            dataType = "value";
        }
        return dataType;
    }

    public static String getDisplayName(String colType, String colName){
        String colTypeString = "";
        if("keyword,text".contains(colType)){
            colTypeString = "字符串";
        }else if("date".contains(colType)){
            colTypeString = "日期时间";
        }else if("long,integer,short,byte,double,float".contains(colType)){
            colTypeString = "数值类型";
        }
        return String.format(colName + "(%s)", colTypeString);
    }
}
