package com.yiche.bigdata.pojo;

import java.util.function.Function;

/**
 * Created by yangyuchen on 20/12/2017.
 */
public class DashBoardDatasetWithRowEnum {
    private long datasetId;
    private String datasetName;
    private String roleId;
    private String columnKey;
    private String columnName;
    private String rowEnumId;
    private String rowEnumName;

    public static final Function<DashBoardDatasetWithRowEnum, String> GET_TO_DATASET_FULL_ID_FUNC =
            (final DashBoardDatasetWithRowEnum dashBoardDatasetWithRowEnum) -> {
                return String.valueOf(dashBoardDatasetWithRowEnum.getDatasetId());
            };

    public static final Function<DashBoardDatasetWithRowEnum, String> GET_TO_DATASET_COLUMN_FULL_ID_FUNC =
            (final DashBoardDatasetWithRowEnum dashBoardDatasetWithRowEnum) -> {
                return String.valueOf(dashBoardDatasetWithRowEnum.getDatasetId()) + "-" + dashBoardDatasetWithRowEnum.getColumnKey();
            };

    public static final Function<DashBoardDatasetWithRowEnum, String> GET_TO_DATASET_COLUMN_ROW_ENUM_FULL_ID_FUNC =
            (final DashBoardDatasetWithRowEnum dashBoardDatasetWithRowEnum) -> {
                return String.valueOf(dashBoardDatasetWithRowEnum.getDatasetId()) + "-" + dashBoardDatasetWithRowEnum.getColumnKey() + "-" + dashBoardDatasetWithRowEnum.getRowEnumId();
            };

    public long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(long datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getRowEnumId() {
        return rowEnumId;
    }

    public void setRowEnumId(String rowEnumId) {
        this.rowEnumId = rowEnumId;
    }

    public String getRowEnumName() {
        return rowEnumName;
    }

    public void setRowEnumName(String rowEnumName) {
        this.rowEnumName = rowEnumName;
    }

    @Override
    public String toString() {
        return "DashBoardDatasetWithRowEnum{" +
                "datasetId=" + datasetId +
                ", datasetName='" + datasetName + '\'' +
                ", roleId='" + roleId + '\'' +
                ", columnKey='" + columnKey + '\'' +
                ", columnName='" + columnName + '\'' +
                ", rowEnumId='" + rowEnumId + '\'' +
                ", rowEnumName='" + rowEnumName + '\'' +
                '}';
    }
}
