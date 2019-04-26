package com.yiche.bigdata.kylin;

import org.apache.commons.lang3.StringUtils;
import com.yiche.bigdata.dataprovider.config.DimensionConfig;
import com.yiche.bigdata.dataprovider.config.ValueConfig;
import com.yiche.bigdata.dataprovider.util.SqlSyntaxHelper;
import com.yiche.bigdata.kylin.model.KylinBaseModel;


/**
 * Created by zyong on 2017/9/18.
 */
public class KylinSyntaxHelper extends SqlSyntaxHelper {

    private KylinBaseModel kylinModel;

    public KylinSyntaxHelper(KylinBaseModel kylinModel) {
        this.kylinModel = kylinModel;
    }

    @Override
    public String getProjectStr(DimensionConfig config) {
        return kylinModel.getColumnWithAliasPrefix(config.getColumnName());
    }

    @Override
    public String getDimMemberStr(DimensionConfig config, int index) {
        String columnType = kylinModel.getColumnType(config.getColumnName());
        if (columnType.startsWith("varchar") || columnType.startsWith("date")) {
            return "'" + config.getValues().get(index) + "'";
        } else {
            return config.getValues().get(index);
        }
    }

    @Override
    public String getAggStr(ValueConfig vConfig) {
        String columnStr = vConfig.getColumn();
        if(!StringUtils.isEmpty(columnStr)&&columnStr.contains("(")&&columnStr.contains(")")){
            return columnStr;
        }
        if (columnStr.indexOf(".") != -1) {
            columnStr = StringUtils.substringAfter(vConfig.getColumn(), ".");
        }
        switch (vConfig.getAggType()) {
            case "sum":
                return "SUM(" + kylinModel.getColumnWithAliasPrefix(vConfig.getColumn()) + ") AS sum_" + columnStr;
            case "avg":
                return "AVG(" + kylinModel.getColumnWithAliasPrefix(vConfig.getColumn()) + ") AS avg_" + columnStr;
            case "max":
                return "MAX(" + kylinModel.getColumnWithAliasPrefix(vConfig.getColumn()) + ") AS max_" + columnStr;
            case "min":
                return "MIN(" + kylinModel.getColumnWithAliasPrefix(vConfig.getColumn()) + ") AS min_" + columnStr;
            case "distinct":
                return "COUNT(DISTINCT " + kylinModel.getColumnWithAliasPrefix(vConfig.getColumn()) + ") AS count_d_" + columnStr;
            case "null":
                return  columnStr;
            default:
                return "COUNT(" + kylinModel.getColumnWithAliasPrefix(vConfig.getColumn()) + ") AS count_" + columnStr;
        }
    }


}
