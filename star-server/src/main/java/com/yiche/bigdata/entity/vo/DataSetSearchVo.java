package com.yiche.bigdata.entity.vo;

import java.util.List;

/**
 * @Author:zhaoguanchen
 * @Date:2019/3/12
 * @Description:
 */
public class DataSetSearchVo {
    private List<CommonResourceVo> dataSetFolderList;
    private List<CommonResourceVo> dataSetList;

    public List<CommonResourceVo> getDataSetFolderList() {
        return dataSetFolderList;
    }

    public void setDataSetFolderList(List<CommonResourceVo> dataSetFolderList) {
        this.dataSetFolderList = dataSetFolderList;
    }

    public List<CommonResourceVo> getDataSetList() {
        return dataSetList;
    }

    public void setDataSetList(List<CommonResourceVo> dataSetList) {
        this.dataSetList = dataSetList;
    }

    public DataSetSearchVo(List<CommonResourceVo> dataSetFolderList, List<CommonResourceVo> dataSetList) {
        this.dataSetFolderList = dataSetFolderList;
        this.dataSetList = dataSetList;
    }
}
