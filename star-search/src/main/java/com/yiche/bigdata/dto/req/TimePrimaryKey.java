package com.yiche.bigdata.dto.req;


/**
 * Created by yangyuchen on 25/01/2018.
 */
public class TimePrimaryKey {
    private String column;
    private String id;
    private String alias;
    private String type;
    private DataTypeReq dataType;

    public DataTypeReq getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeReq dataType) {
        this.dataType = dataType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TimePrimaryKey{" +
                "column='" + column + '\'' +
                ", id='" + id + '\'' +
                ", alias='" + alias + '\'' +
                ", type='" + type + '\'' +
                ", dataType=" + dataType +
                '}';
    }
}
