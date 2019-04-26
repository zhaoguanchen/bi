package com.yiche.bigdata.dataprovider.expression;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by yfyuan on 2017/3/8.
 */
public class NowFunction extends AbstractFunction {

    public static final int UNSUPPORTED_GRANUALITY = -1;
    public static final int DAY_GRANUALITY = 0;
    public static final int WEEK_GRANUALITY = 1;
    public static final int MONTH_GRANUALITY = 2;
    public static final String DAY_REGEXPR = "^\\{now\\(\\'D.*";
    public static final String WEEK_REGEXPR = "^\\{now\\(\\'W.*";
    public static final String MONTH_REGEXPR = "^\\{now\\(\\'M.*";


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        String unit = FunctionUtils.getStringValue(arg1, env);
        Number interval = FunctionUtils.getNumberValue(arg2, env);
        String format = FunctionUtils.getStringValue(arg3, env);

        Calendar c = Calendar.getInstance();
        c.add(getUnit(unit), interval.intValue());
        if ("timestamp".equals(format)) {
            return new AviatorLong(c.getTime().getTime());
        } else {
            return new AviatorString(new SimpleDateFormat(format).format(c.getTime()));
        }
    }

    private int getUnit(String unit) {
        switch (unit) {
            case "D":
                return Calendar.DAY_OF_YEAR;
            case "Y":
                return Calendar.YEAR;
            case "M":
                return Calendar.MONTH;
            case "W":
                return Calendar.WEEK_OF_YEAR;
            case "h":
                return Calendar.HOUR;
            case "m":
                return Calendar.MINUTE;
            default:
                return Calendar.DAY_OF_YEAR;
        }
    }

    public static int getGranuality(String nowFunction) {
        if (nowFunction.matches(DAY_REGEXPR)) {
            return DAY_GRANUALITY;
        } else if (nowFunction.matches(WEEK_REGEXPR)) {
            return WEEK_GRANUALITY;
        } else if (nowFunction.matches(MONTH_REGEXPR)) {
            return MONTH_GRANUALITY;
        } else {
            return UNSUPPORTED_GRANUALITY;
        }
    }

    @Override
    public String getName() {
        return "now";
    }
}
