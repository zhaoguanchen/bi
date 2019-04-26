package com.yiche.bigdata.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        return uuidStr;
    }

    public static boolean checkNumber(String string){
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static String listToString(List<String> list){
        if(! CollectionUtils.isEmpty(list)){
            StringBuilder sb = new StringBuilder();
            for (String string : list) {
                if(StringUtils.isNotEmpty(string)){
                    sb.append(string);
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return null;
    }

    public static String listToString(List<String> list, String ifNullString){
        if(! CollectionUtils.isEmpty(list)){
            StringBuilder sb = new StringBuilder();
            for (String string : list) {
                if(StringUtils.isEmpty(string)){
                    sb.append(ifNullString);
                }else {
                    sb.append(string);
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return null;
    }

    public static List<String> stringToList(String string){
        if(StringUtils.isEmpty(string)){
            return null;
        }else{
            return new ArrayList<>(CollectionUtils.arrayToList(string.split(",")));
        }
    }

    public static String getString(Object object){
        return  object==null?"":object.toString();
    }
}
