package com.yiche.bigdata.dataprovider.aggregator;

import com.yiche.bigdata.dataprovider.DataProvider;
import com.yiche.bigdata.dataprovider.config.AggConfig;
import com.yiche.bigdata.dataprovider.config.CompositeConfig;
import com.yiche.bigdata.dataprovider.config.ConfigComponent;
import com.yiche.bigdata.dataprovider.config.DimensionConfig;
import com.yiche.bigdata.dataprovider.result.AggregateResult;
import com.yiche.bigdata.dto.PairData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yfyuan on 2017/1/13.
 */
public interface Aggregatable {

    /**
     * The data provider that support DataSource side Aggregation must implement this method.
     *
     * @param columnName
     * @return
     */
    String[] queryDimVals(String columnName, AggConfig config) throws Exception;

//    List<PairData> queryDimVals(String columnName, AggConfig config) throws Exception;

    /**
     * The data provider that support DataSource side Aggregation must implement this method.
     *
     * @return
     */
    String[] getColumn() throws Exception;

    Map<String,String> getColumnsAndType() throws Exception;
    Map<String, String> getColumnTypes() throws Exception;

    /**
     * The data provider that support DataSource side Aggregation must implement this method.
     *
     * @param ac aggregate configuration
     * @return
     */
    AggregateResult queryAggData(AggConfig ac) throws Exception;

    default String viewAggDataQuery(AggConfig ac) throws Exception {
        return "Not Support";
    }

//    default ConfigComponent separateNull(ConfigComponent configComponent) {
//        if (configComponent instanceof DimensionConfig) {
//            DimensionConfig cc = (DimensionConfig) configComponent;
//
//            if (("=".equals(cc.getFilterType()) || "≠".equals(cc.getFilterType()))
//                    && cc.getValues().size() > 1
//                    && cc.getValues().stream().anyMatch(s -> DataProvider.NULL_STRING.equals(s))) {
//
//                CompositeConfig compositeConfig = new CompositeConfig();
//                compositeConfig.setType("=".equals(cc.getFilterType()) ? "OR" : "AND");
//                cc.setValues(cc.getValues().stream()
//                        .filter(s -> !DataProvider.NULL_STRING.equals(s))
//                        .collect(Collectors.toList()));
//
//                compositeConfig.getConfigComponents().add(cc);
//
//                DimensionConfig nullCc = new DimensionConfig();
//                nullCc.setColumnName(cc.getColumnName());
//                nullCc.setFilterType(cc.getFilterType());
//                nullCc.setValues(new ArrayList<>());
//                nullCc.getValues().add(DataProvider.NULL_STRING);
//
//                compositeConfig.getConfigComponents().add(nullCc);
//                return compositeConfig;
//            }
//        }
//        return configComponent;
//    }
}
