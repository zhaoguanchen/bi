package com.yiche.bigdata.elasticsearch.aggregation;

import com.yiche.bigdata.util.json.JSONBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Peter on 2017/5/7.
 */
public class AggregationBuilder {

    public static JSONBuilder termsAggregation(String fieldName, int size, Object missing) {
        return JSONBuilder.json("terms", JSONBuilder.json()
                .put("field", fieldName)
                .put("size", size)
                .put("missing", missing)
        );
    }

    public static JSONBuilder termsAggregationDate(String fieldName, int size) {
        return JSONBuilder.json("terms", JSONBuilder.json()
                .put("field", fieldName)
                .put("size", size)
        );
    }

    public static JSONBuilder dateHistAggregation(String fieldName, String interval, int min_doc_count) {
        return dateHistAggregation(fieldName, interval, 0, null, null);
    }

    public static JSONBuilder dateHistAggregation(String fieldName, String interval, int min_doc_count, Long min,
                                                  Long max) {
        String format = "yyyy-MM-dd";
        JSONBuilder extendedBound = JSONBuilder.json();
        if (min != null) {
            extendedBound.put("min", timestamp2DateStr(min, format));
        }
        if (max != null) {
            extendedBound.put("max", timestamp2DateStr(max, format));
        }
        TimeZone tz = Calendar.getInstance().getTimeZone();
        return JSONBuilder.json("date_histogram",
                JSONBuilder.json().put("field", fieldName)
                        .put("format", format)
                        .put("time_zone", tz.getID())
                        .put("interval", interval)
                        .put("min_doc_count", min_doc_count)
                        .put("extended_bounds", extendedBound)
        );
    }

    private static String timestamp2DateStr(long timestamp, String format) {
        Date date = new Date();
        date.setTime(timestamp);
        return new SimpleDateFormat(format).format(date);
    }
}
