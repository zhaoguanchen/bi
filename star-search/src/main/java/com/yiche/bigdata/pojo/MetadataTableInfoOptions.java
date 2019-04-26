package com.yiche.bigdata.pojo;


/**
 * Created by jmy on 2017/7/17.
 */
public class MetadataTableInfoOptions {
    private String title;
    private MetadataTableInfo[] list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MetadataTableInfo[] getList() {
        return list;
    }

    public void setList(MetadataTableInfo[] list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MetadataTableInfoOptions{" +
                "title='" + title + '\'' +
                ", list=" + list +
                '}';
    }

    public MetadataTableInfoOptions(){}

    public MetadataTableInfoOptions(String title, MetadataTableInfo[] list) {
        this.title = title;
        this.list = list;
    }

}
