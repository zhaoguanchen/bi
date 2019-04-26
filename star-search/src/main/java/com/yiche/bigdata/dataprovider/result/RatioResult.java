package com.yiche.bigdata.dataprovider.result;

import java.io.Serializable;

/**
 * Created by yangyuchen on 02/02/2018.
 */
public class RatioResult implements Serializable{
    private static final String TREND_UP = "up";
    private static final String TREND_DOWN = "down";
    private static final String TREND_FLAT = "flat";
    private static final String TREND_INVALID = "invalid";
    private static final String LINK_RATIO = "环比";
    private static final String YOY_RATIO = "同比";
    private static final String NO_RATIO_NAME="";
    public static final int NO_RATIO_TYPE = 0;
    public static final int LINK_RATIO_TYPE = 1;
    public static final int YOY_RATIO_TYPE=2;
    private static final double INVALID_RATIO = Double.MIN_VALUE;
    private static final double ZERO_VINCENCY = 10e-6;


    private double value;
    private String name;
    private String trend;
    private int ratioType;

    public RatioResult() {
        this(NO_RATIO_TYPE);
    }

    public RatioResult(int ratioType) {
        this.value = INVALID_RATIO;
        this.trend = TREND_INVALID;
        this.ratioType = ratioType;
        switch (ratioType){
            case LINK_RATIO_TYPE:
                this.name = LINK_RATIO;
                break;
            case YOY_RATIO_TYPE:
                this.name = YOY_RATIO;
                break;
                default:
                    this.name = NO_RATIO_NAME;
        }
    }

    public static RatioResult calculate(double curVal, double prevVal, int ratioType){
        //expression: (curVal - preVal)/preVal
        RatioResult result = new RatioResult();
        if(prevVal < ZERO_VINCENCY && curVal >= ZERO_VINCENCY ){
            result.setValue(1.0);
            result.setTrend(TREND_UP);
        }
        else if(prevVal <ZERO_VINCENCY && curVal < ZERO_VINCENCY){
            result.setValue(INVALID_RATIO);
            result.setTrend(TREND_INVALID);
        }
        else{
            double signedRatio = (curVal - prevVal)/prevVal;
            result.setValue(Math.abs(signedRatio));
            if(signedRatio > 0){
                result.setTrend(TREND_UP);
            }else if (signedRatio < 0){
                result.setTrend(TREND_DOWN);
            }else{
                result.setTrend(TREND_FLAT);
            }
        }

        switch (ratioType){
            case LINK_RATIO_TYPE:{
                result.setName(LINK_RATIO);
                break;
            }
            case YOY_RATIO_TYPE:{
                result.setName(YOY_RATIO);
                break;
            }
        }

        return result;
    }

    public int getRatioType() {
        return ratioType;
    }

    public void setRatioType(int ratioType) {
        this.ratioType = ratioType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
