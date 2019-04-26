package com.yiche.bigdata.entity.vo;

import java.util.List;
import java.util.Map;

public class TableColumnInfoVo {

    private String column;
    private String display;
    private String name;
    private String type;
    private String format;

    private List<RowPermissionOptionVo> options;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<RowPermissionOptionVo> getOptions() {
        return options;
    }

    public void setOptions(List<RowPermissionOptionVo> options) {
        this.options = options;
    }
}
