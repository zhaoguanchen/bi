package com.yiche.bigdata.entity.vo;


import java.util.List;

public class DataTableCategoryPermissionVo {

    private String title;
    private List<DataTablePermissionVo> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DataTablePermissionVo> getList() {
        return list;
    }

    public void setList(List<DataTablePermissionVo> list) {
        this.list = list;
    }
}
