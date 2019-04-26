package com.yiche.bigdata.dataprovider;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.googlecode.aviator.AviatorEvaluator;
import com.yiche.bigdata.dataprovider.aggregator.Aggregatable;
import com.yiche.bigdata.dataprovider.aggregator.InnerAggregator;
import com.yiche.bigdata.dataprovider.config.AggConfig;
import com.yiche.bigdata.dataprovider.config.CompositeConfig;
import com.yiche.bigdata.dataprovider.config.ConfigComponent;
import com.yiche.bigdata.dataprovider.config.DimensionConfig;
import com.yiche.bigdata.dataprovider.expression.NowFunction;
import com.yiche.bigdata.dataprovider.result.AggregateResult;
import com.yiche.bigdata.dto.PairData;
import com.yiche.bigdata.util.NaturalOrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zyong on 2017/1/9.
 */
public abstract class DataProvider {

    private InnerAggregator innerAggregator;
    protected Map<String, String> dataSource;
    protected Map<String, String> query;
    private int resultLimit;
    private long interval = 12 * 60 * 60; // second
    private boolean isUsedForTest = false;
    public static final String NULL_STRING = "0.0";
    private static final Logger logger = LoggerFactory.getLogger(DataProvider.class);

    static {
        AviatorEvaluator.addFunction(new NowFunction());
    }

    public abstract boolean doAggregationInDataSource();
    /**

     * get the aggregated data by user's widget designer
     *
     * @return
     */
    public final AggregateResult getAggData(AggConfig ac, boolean reload) throws Exception {
        evalValueExpression(ac);
//        if (isDataSourceAggInstance()) {
            return ((Aggregatable) this).queryAggData(ac);
//        } else {queryAggData
//            checkAndLoad(reload);
//            return innerAggregator.queryAggData(ac);
//        }
    }


    public void test() throws Exception {
        getData();
    }

    public final String getViewAggDataQuery(AggConfig config) throws Exception {
        evalValueExpression(config);
        if (this instanceof Aggregatable && doAggregationInDataSource()) {
            return ((Aggregatable) this).viewAggDataQuery(config);
        } else {
            return "Not Support";
        }
    }
    public boolean isUsedForTest() {
        return isUsedForTest;
    }
    /**
     * Get the options values of a dimension column
     *
     * @param columnName
     * @return
     */
    /*
    public final String[] getDimVals(String columnName, AggConfig config, boolean reload) throws Exception {
        String[] dimVals = null;
        evalValueExpression(config);
        if (this instanceof Aggregatable && doAggregationInDataSource()) {
            dimVals = ((Aggregatable) this).queryDimVals(columnName, config);
        } else {
            checkAndLoad(reload);
            dimVals = innerAggregator.queryDimVals(columnName, config);
        }
//        return Arrays.stream(dimVals)
//                .map(member -> {
//                    return Objects.isNull(member) ? NULL_STRING : member;
//                })
//                .sorted(new NaturalOrderComparator()).limit(1000).toArray(String[]::new);
        return Arrays.stream(dimVals)
                .map(member -> {
                    return Objects.isNull(member) ? NULL_STRING : member;
                }).toArray(String[]::new);
    }
*/
    public final String[] getDimVals(String columnName, AggConfig config, boolean reload) throws Exception {
//        List<PairData> dimVals = null;
//        evalValueExpression(config);
//        if (this instanceof Aggregatable && doAggregationInDataSource()) {
//            dimVals = ((Aggregatable) this).queryDimVals(columnName, config);
//        } else {
//            checkAndLoad(reload);
//            dimVals = innerAggregator.queryDimVals(columnName, config);
//        }
//        return dimVals;
        String[] dimVals = null;
        evalValueExpression(config);
        if (isDataSourceAggInstance()) {
            dimVals = ((Aggregatable) this).queryDimVals(columnName, config);
        } else {
            checkAndLoad(reload);
            dimVals = innerAggregator.queryDimVals(columnName, config);
        }
        return Arrays.stream(dimVals)
                .map(member -> {
                    return Objects.isNull(member) ? NULL_STRING : member;
                })
                .sorted(new NaturalOrderComparator()).limit(1000).toArray(String[]::new);


    }
    public boolean isDataSourceAggInstance() {
        if (this instanceof Aggregatable && doAggregationInDataSource()) {
            return true;
        } else {
            return false;
        }
    }
    public final String[] getColumn(boolean reload) throws Exception {
        String[] columns = null;
//        if (this instanceof Aggregatable && doAggregationInDataSource()) {
            columns = ((Aggregatable) this).getColumn();
//        } else {
//            checkAndLoad(reload);
//            columns = innerAggregator.getColumn();
//        }
//     Map<String,String>   result = ((Aggregatable) this).getColumnsAndType();
        Arrays.sort(columns);
        return columns;
    }

    public final Map<String, String> getType() throws Exception {
        Map<String, String> columnTypes = ((Aggregatable) this).getColumnsAndType();
        /*
        if (this instanceof Aggregatable && doAggregationInDataSource()) {
            columnTypes = ((Aggregatable) this).getColumnTypes();
        } else {
            columnTypes = innerAggregator.getColumnTypes();
        }
        */
        return columnTypes;
    }

    private void checkAndLoad(boolean reload) throws Exception {
        String key = getLockKey();
        synchronized (key.intern()) {
            if (reload || !innerAggregator.checkExist()) {
                String[][] data = getData();
                if(data!=null) {
                    innerAggregator.loadData(data, interval);
                }
                logger.info("loadData {}", key);
            }
        }
    }

    private void evalValueExpression(AggConfig ac) {
        if (ac == null) {
            return;
        }
        ac.getFilters().forEach(e -> evaluator(e));
        ac.getColumns().forEach(e -> evaluator(e));
        ac.getRows().forEach(e -> evaluator(e));
    }

    private void evaluator(ConfigComponent e) {
        if (e instanceof DimensionConfig) {
            DimensionConfig dc = (DimensionConfig) e;
            dc.setValues(dc.getValues().stream().flatMap(v -> getFilterValue(v)).collect(Collectors.toList()));
        }
        if (e instanceof CompositeConfig) {
            CompositeConfig cc = (CompositeConfig) e;
            cc.getConfigComponents().forEach(_e -> evaluator(_e));
        }
    }
    public InnerAggregator getInnerAggregator() {
        return innerAggregator;
    }
    private Stream<String> getFilterValue(String value) {
        List<String> list = new ArrayList<>();

        if (value == null || !(value.startsWith("{") && value.endsWith("}"))) {
            list.add(value);
        } else if (value.startsWith("{") && value.endsWith("}")) {
            if (value.contains("value")) {
                JSONObject jsonObject = JSONObject.parseObject(value);
                String text = jsonObject.getString("value");
                list.add(text);
            }else if (value.contains("key")) {
                JSONObject jsonObject = JSONObject.parseObject(value);
                String text = jsonObject.getString("key");
                list.add(text);
            } else {
                list.add(AviatorEvaluator.compile(value.substring(1, value.length() - 1), true).execute().toString());
            }
        }
        return list.stream();
    }

    public String getLockKey() {
        String dataSourceStr = JSONObject.toJSON(dataSource).toString();
        String queryStr = JSONObject.toJSON(query).toString();
        return Hashing.md5().newHasher().putString(dataSourceStr + queryStr, Charsets.UTF_8).hash().toString();
    }

    public List<DimensionConfig> filterCCList2DCList(List<ConfigComponent> filters) {
        List<DimensionConfig> result = new LinkedList<>();
        filters.stream().forEach(cc -> {
            result.addAll(configComp2DimConfigList(cc));
        });
        return result;
    }

    public List<DimensionConfig> configComp2DimConfigList(ConfigComponent cc) {
        List<DimensionConfig> result = new LinkedList<>();
        if (cc instanceof DimensionConfig) {
            result.add((DimensionConfig) cc);
        } else {
            Iterator<ConfigComponent> iterator = cc.getIterator();
            while (iterator.hasNext()) {
                ConfigComponent next = iterator.next();
                result.addAll(configComp2DimConfigList(next));
            }
        }
        return result;
    }
    public static ConfigComponent separateNull(ConfigComponent configComponent) {
        if (configComponent instanceof DimensionConfig) {
            DimensionConfig cc = (DimensionConfig) configComponent;
            if (("=".equals(cc.getFilterType()) || "≠".equals(cc.getFilterType())) && cc.getValues().size() > 1 &&
                    cc.getValues().stream().anyMatch(s -> DataProvider.NULL_STRING.equals(s))) {
                CompositeConfig compositeConfig = new CompositeConfig();
                compositeConfig.setType("=".equals(cc.getFilterType()) ? "OR" : "AND");
                cc.setValues(cc.getValues().stream().filter(s -> !DataProvider.NULL_STRING.equals(s)).collect(Collectors.toList()));
                compositeConfig.getConfigComponents().add(cc);
                DimensionConfig nullCc = new DimensionConfig();
                nullCc.setColumnName(cc.getColumnName());
                nullCc.setFilterType(cc.getFilterType());
                nullCc.setValues(new ArrayList<>());
                nullCc.getValues().add(DataProvider.NULL_STRING);
                compositeConfig.getConfigComponents().add(nullCc);
                return compositeConfig;
            }
        }
        return configComponent;
    }


    abstract public String[][] getData() throws Exception;

    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    public void setResultLimit(int resultLimit) {
        this.resultLimit = resultLimit;
    }

    public int getResultLimit() {
        return resultLimit;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setInnerAggregator(InnerAggregator innerAggregator) {
        this.innerAggregator = innerAggregator;
    }

}
