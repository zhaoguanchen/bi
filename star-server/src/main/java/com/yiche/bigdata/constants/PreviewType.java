package com.yiche.bigdata.constants;

public enum PreviewType {

    PREVIEW_BY_ID("0", "根据ID获取预览"),

    PREVIEW_BY_JSON("1", "根据JSON获取预览");

    private final String value;
    private final String name;

    PreviewType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

}
