package com.yiche.bigdata.util;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public final class DateUtil {
	/** format pattern: yyyyMMdd */
	public static final String YYYYMMDD = "yyyyMMdd";
	/** format pattern: yyyy-MM-dd HH:mm:ss */
	public static final String ALL = "yyyy-MM-dd HH:mm:ss";
	/** format pattern: yyyy-MM-dd */
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	/** format pattern: yyyy-ww */
	public static final String YYYY_WW = "yyyy-ww";
	/** format pattern: MM-dd */
	public static final String MM_DD = "MM-dd";
	public static final String MMDD = "MM.dd";
	/** format pattern: HH:mm:ss */
	public static final String HH_MM_SS = "HH:mm:ss";
	/** day in milliseconds */
	public static final long DAY_IN_MILLI = 86400000;

	public static final String HHMMSSSSS = "HHmmssSSS";

	// DateFormat are not synchronized.
	/** format instance: yyyy-MM-dd HH:mm:ss */
	private static final SimpleDateFormat FORMAT_ALL = new SimpleDateFormat(ALL);
	/** format instance: yyyy-MM-dd */
	private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat(
			YYYY_MM_DD);
	/** format instance: HH:mm:ss */
	private static final SimpleDateFormat FORMAT_TIME = new SimpleDateFormat(
			HH_MM_SS);

	private static final SimpleDateFormat COMPUTE_TIME = new SimpleDateFormat(
			HHMMSSSSS);

	public static final String YYYY_MM = "yyyy-MM";
	public static final String MM_CHINESE = "MM月";
	
	public static final String DD_CHINESE = "dd日";
	
	public static final String MM_DD_CHINESE = "MM月dd日";
	/**
	 * private constructor
	 */
	private DateUtil() {

	}

	/**
	 * get days between given two dates.
	 * 
	 * @param start
	 *            the start date
	 * @param end
	 *            the end date
	 * @return days between given two dates.
	 */
	public static long getInterval(final Date start, final Date end) {
		if (start == null || end == null) {
			return 0;
		}
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);
		endCalendar.set(Calendar.MILLISECOND, 0);
		endCalendar.set(Calendar.MINUTE, 0);
		endCalendar.set(Calendar.HOUR_OF_DAY, 0);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		startCalendar.set(Calendar.MILLISECOND, 0);
		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
		return (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())
				/ DAY_IN_MILLI;
	}

	/**
	 * get date after certain days.
	 * 
	 * @param start
	 *            the start date
	 * @param days
	 *            interval
	 * @return date
	 */
	public static Date getDate(final Date start, int days) {
		if (start == null || days == 0) {
			return start;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		startCalendar.set(Calendar.DATE, startCalendar.get(Calendar.DATE)
				+ days);
		return startCalendar.getTime();
	}

	/**
	 * get beginning time of a day 9点
	 * 
	 * @param date
	 *            the specified day
	 * @return the beginning time of a day
	 */
	public static Date getDayBeginning(final Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		return calendar.getTime();
	}

	/**
	 * get ending time of a day
	 * 
	 * @param date
	 *            the specified day
	 * @return the ending time of a day
	 */
	public static Date getDayEnding(final Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 999);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		return calendar.getTime();
	}
	
	   /**
     * get beginning time of a month
     * 
     * @param date the specified day
     * @return the beginning time of a month
     */
    public static Date getMonthBeginning(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        return calendar.getTime();
    }
    
    /**
  * get beginning time of a year
  * 
  * @param date the specified day
  * @return the beginning time of a year
  */
 public static Date getYearBeginning(final Date date) {
     Calendar calendar = Calendar.getInstance();
     calendar.setTime(date);
     if(calendar.get(Calendar.MONTH) == 0){
         calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) -1);
     }
     calendar.set(Calendar.MILLISECOND, 0);
     calendar.set(Calendar.SECOND, 0);
     calendar.set(Calendar.MINUTE, 0);
     calendar.set(Calendar.HOUR_OF_DAY, 0);
     calendar.set(Calendar.DAY_OF_MONTH, 1);
     calendar.set(Calendar.MONTH, 0);
     return calendar.getTime();
 }
    
     /**
      * 获取上个月最后一天
      * 
      * @param date the specified day
      * @return the ending time of a month
      */
     public static Date getLastMonthEnding(final Date date) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         calendar.set(Calendar.MILLISECOND, 999);
         calendar.set(Calendar.SECOND, 59);
         calendar.set(Calendar.MINUTE, 59);
         calendar.set(Calendar.HOUR_OF_DAY, 23);
         calendar.add(Calendar.MONTH, -1);
         int maxDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
         calendar.set(Calendar.DAY_OF_MONTH, maxDay);
         return calendar.getTime();
     }


	/**
	 * 获取当前月最后一天
	 *
	 * @param date the specified day
	 * @return the ending time of a month
	 */
	public static Date getNowLastMonthEnding(final Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 999);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		//calendar.add(Calendar.MONTH, -1);
		int maxDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, maxDay);
		return calendar.getTime();
	}
    /**
     * get ending time of a month
     * 
     * @param date the specified day
     * @return the ending time of a month
     */
    public static Date getMonthEnding(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }
    
    public static Date getLastYear(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

	/**
	 * get month 之后的 date
	 * 
	 * @param month
	 * @return
	 */
	public static Date getMonthAfterDay(int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * get days between given two dates.
	 * 
	 * @param start
	 *            the start date
	 * @param end
	 *            the end date
	 * @return days between given two dates.
	 */
	public static long getInterval(final Integer start, final Integer end) {
		if (start == null || end == null) {
			return 0;
		}
		return getInterval(parse(start), parse(end));
	}

	/**
	 * parse integer to date
	 * 
	 * @param date
	 *            integer date(yyyyMMdd)
	 * @return java.util.Date type instance
	 */
	public static Date parse(final Integer date) {
		if (date == null) {
			return null;
		}
		return parse(String.valueOf(date), YYYYMMDD);
	}

	/**
	 * parse string to date
	 * 
	 * @param str
	 *            date
	 * @param format
	 *            format pattern
	 * @return java.util.Date type instance
	 */
	public static Date parse(final String str, final String format) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 使用用户格式提取字符串日期
	 * 
	 * @param strDate
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static Date parse(Date strDate, final String format) {
		if (strDate == null) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			String tempDate = df.format(strDate);
			return df.parse(tempDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * format date
	 * 
	 * @param date
	 *            given date
	 * @param format
	 *            format pattern
	 * @return formated date string
	 */
	public static String format(final Date date, final String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return format(date, sdf);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * format date
	 * 
	 * @param date
	 *            given date
	 * @param df
	 *            format instance
	 * @return formated date string
	 */
	public static String format(final Date date, final DateFormat df) {
		if (date == null) {
			return null;
		}
		try {
			return df.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * format date with pattern "yyyy-MM-dd" synchronized, because DateFormat
	 * are not synchronized.
	 * 
	 * @param date
	 *            given date
	 * @return formated date string
	 */
	public static synchronized String formatDate(final Date date) {
		return format(date, FORMAT_DATE);
	}

	/**
	 * format date with pattern "HH:mm:ss" synchronized, because DateFormat are
	 * not synchronized.
	 * 
	 * @param date
	 *            given date
	 * @return formated time string
	 */
	public static synchronized String formatTime(final Date date) {
		return format(date, FORMAT_TIME);
	}

	/**
	 * format date with pattern "yyyy-MM-dd HH:mm:ss" synchronized, because
	 * DateFormat are not synchronized.
	 * 
	 * @param date
	 *            given date
	 * @return formated date and time string
	 */
	public static synchronized String formatAll(final Date date) {
		return format(date, FORMAT_ALL);
	}

	/**
	 * format date
	 * 
	 * @param dateInt
	 *            date(yyyyMMdd)
	 * @param format
	 *            format pattern
	 * @return formated string
	 */
	public static String format(final int dateInt, final String format) {
		Date date = parse(String.valueOf(dateInt), YYYYMMDD);
		return DateUtil.format(date, format);
	}

	public static synchronized String formatComputeTime(final Date date) {
		return format(date, COMPUTE_TIME);
	}
	
	  /**  
	    * 判断当前日期是星期几<br>  
	    * <br>  
	    * @param pTime 修要判断的时间<br>  
	    * @return dayForWeek 判断结果<br>  
	    * @Exception 发生异常<br>  
	    */   
	public  static  int  dayForWeek(String pTime) throws  Exception {  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek; 
	}  
	
	
	public static List<String> getMonthList(String date){
        Date d1 = DateUtil.getYearBeginning(DateUtil.parse(date, DateUtil.YYYY_MM_DD));
        Date d2 = DateUtil.getMonthEnding(DateUtil.parse(date, DateUtil.YYYY_MM_DD));
	    Calendar dd = Calendar.getInstance();//定义日期实例
	    dd.setTime(d1);//设置日期起始时间
	    List<String> result = new LinkedList<>();
	    while(dd.getTime().before(d2) || d2.equals(dd.getTime())){
	        //判断是否到结束日期
    	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    	    String str = sdf.format(dd.getTime());
    	    result.add(str);//输出日期结果
    	    dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
	    }
	    return result;
	}
	
	public static Set<String> getDayList(String start,String end){
        Date d1 = DateUtil.parse(start, DateUtil.YYYY_MM_DD);
        Date d2 = DateUtil.parse(end, DateUtil.YYYY_MM_DD);
        Calendar dd = Calendar.getInstance();//定义日期实例
        dd.setTime(d1);//设置日期起始时间
        Set<String> result = new LinkedHashSet<>();
        while(dd.getTime().before(d2) || d2.equals(dd.getTime())){
            //判断是否到结束日期
            String str = DateUtil.format(dd.getTime(), DateUtil.YYYY_MM_DD);
            result.add(str);//输出日期结果
            dd.add(Calendar.DAY_OF_MONTH, 1);//进行当前日期天数加1
        }
        return result;
    }
	/**比较两个日期的大小
	 * @return 1:大于, -1:小于 ,0:相等
	 * */
	public static int compare(Date dt1,Date dt2){
        if (dt1.getTime() > dt2.getTime()) {
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
            return -1;
        } else {
            return 0;
        }
	}
	public static void main(String[] args) {
		
	    Date keys = getNowLastMonthEnding(DateUtil.parse("2017-11-01", DateUtil.YYYY_MM_DD));
	    System.out.println(DateUtil.format(keys, DateUtil.YYYY_MM_DD));
	}
	
}
