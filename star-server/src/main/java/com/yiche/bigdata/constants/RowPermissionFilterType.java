package com.yiche.bigdata.constants;

import java.util.ArrayList;
import java.util.List;

public enum RowPermissionFilterType {

    DATE_BETWEEN("date_between", "时间范围"),

//    VALUE_BETWEEN("value_between", "区间"),

    VALUE_EQUAL("value_equal", "等于"),

    VALUE_NOT_EQUAL("value_not_equal", "不等于"),

    VALUE_GTE("value_gte", "大于等于"),

    VALUE_GT("value_gt", "大于"),

    VALUE_LTE("value_lte", "小于等于"),

    VALUE_LT("value_lt", "小于"),

    IN("in", "包含"),

    NOT_IN("not_in", "不包含"),

    STRING_CONTAIN("string_contain", "包含"),

    STRING_NOT_CONTAIN("string_not_contain", "不包含"),

    STRING_EQUAL("string_equal", "完全匹配"),

    STRING_NOT_EQUAL("string_not_equal", "不完全匹配"),

    STRING_REGULAR_MATCH("string_regular_match", "与正则表达式匹配"),

    STRING_REGULAR_NOT_MATCH("string_regular_not_match", "与正则表达式不匹配");

    private final String value;
    private final String name;

    RowPermissionFilterType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static RowPermissionFilterType getByValue(String value) {
        for(RowPermissionFilterType typeEnum : RowPermissionFilterType.values()) {
            if(typeEnum.value == value) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + value);
    }

    public static List<RowPermissionFilterType> getVauleTypeKeys(String dataType){
        List<RowPermissionFilterType> valueTypes = new ArrayList<>();
        RowPermissionFilterType[] values = RowPermissionFilterType.values();
        for (RowPermissionFilterType filterType : values) {
            if(filterType.value.startsWith(dataType)){
                valueTypes.add(filterType);
            }
        }
        return valueTypes;
    }

}
