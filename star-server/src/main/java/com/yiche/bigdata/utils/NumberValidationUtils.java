package com.yiche.bigdata.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidationUtils {
    private static boolean isMatch(String regex, String expression){
        if (expression == null || expression.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(expression);
        return isNum.matches();
    }


    //判断整数（int）
    public static boolean isInteger(String expression) {
        return isMatch("^[-\\+]?[\\d]*$", expression);
    }

    //判断浮点数（double和float）
    public static boolean isDouble(String expression) {
        return isMatch("^[-\\+]?[.\\d]*$", expression);
    }

    //判断数值
    public static boolean isNumber(String expression) {
        return isInteger(expression) || isDouble(expression);
    }
}
