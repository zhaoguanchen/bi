package com.yiche.bigdata.utils;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;
import com.yiche.bigdata.entity.dto.search.ValueReq;
import com.yiche.bigdata.service.impl.DataManagerServiceImpl;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionUtils.class);

    public static char[] OPT = {'+', '-', '*', '/'};

    public static char[] BRACKET = {'(', ')'};

    public static String[] FUN = {"sum", "count", "avg", "max", "min"};

    public static FelEngine FEL;

    static {
        //初始化时间长
        FEL = new FelEngineImpl();
    }

    public static String prepare(String expression) {
        char[] charArray = expression.toCharArray();
        //将字段中的'/'替换为'~'
        String fixedStr = expression;
        for (int i = 0; i < charArray.length; i++) {
            char character = charArray[i];
            if (ArrayUtils.contains(OPT, character)) {
                //字段需要用小括号括起来，'/'前不是括号的认为是字段的一部分
                if (character == OPT[3] && (i == 0 || charArray[i - 1] != BRACKET[1])) {
                    charArray[i] = '~';
                }
            }
        }
        return new String(charArray);
    }

    public static List<ValueReq> getMeasures(String expression) {
        List<ValueReq> measures = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        char[] charArray = StringUtils.reverse(prepare(expression)).toCharArray();
        StringBuilder subExpression = new StringBuilder();
        StringBuilder funNameRev = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            char character = charArray[i];
            if (subExpression.length() > 0) {
                if (ArrayUtils.contains(OPT, character)) {
                    if (funNameRev.length() > 0) {
                        ValueReq valueReq = getValueReq(subExpression, funNameRev);
                        measures.add(valueReq);
                    }
                    subExpression = new StringBuilder();
                    funNameRev = new StringBuilder();
                } else {     //'(' 前面 不是运算符,则为聚合函数
                    funNameRev.append(character);
                }
            } else if (character == BRACKET[1]) {
                stack.push(character);
            } else if (character == BRACKET[0]) {
                while (!stack.empty()) {
                    char popChar = stack.pop();
                    if (popChar == BRACKET[1]) {
                        break;
                    } else {
                        subExpression.append(popChar);
                    }
                }
            } else {
                stack.push(character);
            }
        }
        if (funNameRev.length() > 0 && subExpression.length() > 0) {
            ValueReq valueReq = getValueReq(subExpression, funNameRev);
            measures.add(valueReq);
        }
        return measures;
    }

    private static ValueReq getValueReq(StringBuilder subExpression, StringBuilder funNameRev) {
        ValueReq valueReq = new ValueReq();
        String funName = StringUtils.reverse(funNameRev.toString()).toLowerCase();
        String measure = subExpression.toString().replaceAll("~", "/");
        if (ArrayUtils.contains(FUN, funName)) {
            valueReq.setColumn(measure);
            valueReq.setAggType(funName);
        }
        return valueReq;
    }

    public static String calculate(Map<String, Integer> columnMap, String expression, ArrayList rowData) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fixExpression = expression.toLowerCase();
        FelContext ctx = FEL.getContext();
        for (String aggExpression : columnMap.keySet()) {
            String columnKey = "_" + columnMap.get(aggExpression);
            fixExpression = StringUtils.replace(fixExpression, aggExpression, columnKey);
            if (fixExpression.contains(columnKey)) {
                ctx.set("_" + columnMap.get(aggExpression)
                        , Double.parseDouble((String) rowData.get(columnMap.get(aggExpression))));
            }
        }
        Object result = FEL.eval(fixExpression);
        if (result != null) {
            return df.format(result);
        } else {
            return "0.00";
        }
    }

    public static String getAggExpression(String column, String fun) {
        return (fun + BRACKET[0] + column + BRACKET[1]).toLowerCase();
    }

    /**
     * 判断字符串是否包含中文及中文字符
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


}
