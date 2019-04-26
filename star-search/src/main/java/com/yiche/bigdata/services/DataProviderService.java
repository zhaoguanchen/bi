package com.yiche.bigdata.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.facebook.presto.jdbc.internal.joda.time.DateTime;
import com.facebook.presto.jdbc.internal.joda.time.Days;
import com.facebook.presto.jdbc.internal.joda.time.Months;
import com.facebook.presto.jdbc.internal.joda.time.Weeks;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yiche.bigdata.dao.MetadataDao;
import com.yiche.bigdata.dataprovider.DataProvider;
import com.yiche.bigdata.dataprovider.DataProviderManager;
import com.yiche.bigdata.dataprovider.config.AggConfig;
import com.yiche.bigdata.dataprovider.config.AggConfigBuilder;
import com.yiche.bigdata.dataprovider.config.ConfigComponent;
import com.yiche.bigdata.dataprovider.config.DimensionConfig;
import com.yiche.bigdata.dataprovider.result.AggregateResult;
import com.yiche.bigdata.dataprovider.result.ColumnIndex;
import com.yiche.bigdata.dataprovider.result.RatioResult;
import com.yiche.bigdata.dto.DataProviderResult;
import com.yiche.bigdata.dto.PairData;
import com.yiche.bigdata.dto.ViewDashboardDatasetWithRowEnums;
import com.yiche.bigdata.dto.req.AttrReq;
import com.yiche.bigdata.dto.req.CfgReq;
import com.yiche.bigdata.dto.req.TimeFilterReq;
import com.yiche.bigdata.dto.req.TimePrimaryKey;
import com.yiche.bigdata.exception.CBoardException;
import com.yiche.bigdata.pojo.*;
import com.yiche.bigdata.util.DateUtil;
import com.yiche.bigdata.util.DeepCopyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by yfyuan on 2016/8/15.
 */
@Repository
public class DataProviderService {

    private static final Logger LOG = LoggerFactory.getLogger(DataProviderService.class);

    @Autowired
    private DatasetService datasetService;
    @Autowired
    private MetadataDao metadataDao;

    @Value("#{'${admin_user_id}'.split(',')}")
    private List<String> adminUserId;

    public AggregateResult queryAggData(DashboardDatasource dashboardDatasource,
                                        Map<String, String> query,
                                        DashboardDataset dashboardDataset,
                                        TimeFilterReq timeFilterReq, CfgReq cfgReq,
                                        boolean hasLinkRatio, boolean hasYoYRatio, boolean reload,
                                        List<DashBoardDatasetWithRowEnum> dashBoardDatasetWithRowEnums,
                                        List<FilterPartExpression> filterPartExpressionList) {

        final AggregateResult result = new AggregateResult();
        result.setYoyRatioResult(new RatioResult());//default
        result.setLinkRatioResult(new RatioResult());//default

        //1. get timePrimaryKey
//        final TimePrimaryKey timePrimaryKey = datasetService.getDatasetTimePrimaryKey(datasetId);
        final TimePrimaryKey timePrimaryKey = datasetService.getDatasetTimePrimaryKey(dashboardDataset);

        //2. get majorResult
        AggregateResult majorQuery = getMajorResult(dashboardDatasource, timePrimaryKey, query, dashboardDataset, timeFilterReq, cfgReq, reload
                , dashBoardDatasetWithRowEnums, filterPartExpressionList);

        if (!ObjectUtils.isEmpty(majorQuery)) {
            List<ColumnIndex> colList = majorQuery.getColumnList();
            if (!CollectionUtils.isEmpty(colList)) {
                Map<String, ColumnIndex> nameColMap = new LinkedHashMap<>();

                colList.stream().forEach(col -> {
                    if (col != null) {
                        final String name = col.getName();
                        if (nameColMap.get(name) == null) {
                            nameColMap.put(name, col);
                        }
                    }
                });
                List<ColumnIndex> distinctCols = nameColMap.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
                IntStream.of(1, distinctCols.size()).forEach(index -> {
                    ColumnIndex col = distinctCols.get(index - 1);
                    col.setIndex(index - 1);
                });
                result.setColumnList(distinctCols);
            } else {
                result.setColumnList(colList);
            }
            DataProviderResult dataProviderResult = getColumns(dashboardDatasource, query, dashboardDataset, false);
            addType(majorQuery,dataProviderResult);
            result.setData(majorQuery.getData());

            addType(majorQuery,  dataProviderResult);
            //3. calculate linkRatio and yoyRatio
            RatioResult linkRatio = null;
            if (hasLinkRatio) {
                AggregateResult majorQueryCopy = (AggregateResult) DeepCopyUtil.copy(majorQuery);
                linkRatio = getLinkRatio(majorQueryCopy, timePrimaryKey, dashboardDatasource, query, dashboardDataset, reload);
                result.setLinkRatioResult(linkRatio);
            }
            RatioResult yoyRatio = null;
            if (hasYoYRatio) {
                AggregateResult majorQueryCopy = (AggregateResult) DeepCopyUtil.copy(majorQuery);
                yoyRatio = getYoYRatio(majorQueryCopy, timePrimaryKey, dashboardDatasource, query, dashboardDataset, reload);
                result.setYoyRatioResult(yoyRatio);
            }
        }

        return result;
    }
    private  void  addType(  AggregateResult majorQuery,DataProviderResult  columnList){
        List<ColumnIndex> colList=majorQuery.getColumnList();
        List<PairData>  pairDataList=columnList.getColumns();
        if(pairDataList==null||colList==null
        ||colList.isEmpty()||pairDataList.isEmpty()){
            return;
        }
        colList.forEach(column ->{
            for(PairData pairData:pairDataList){
                if(column.getName().equals(pairData.getKey())){
                    column.setType(pairData.getValue());
            }
            }
        });
        return;
    }
    private AggregateResult getMajorResult(DashboardDatasource dashboardDatasource,
                                           TimePrimaryKey timePrimaryKey, Map<String, String> query,
                                           DashboardDataset dashboardDataset,
                                           TimeFilterReq timeFilterReq, CfgReq cfgReq, boolean reload,
                                           List<DashBoardDatasetWithRowEnum> dashBoardDatasetWithRowEnums,
                                           List<FilterPartExpression> filterPartExpressionList) {

        //1. prepare inputs for building aggConfig
        ViewDashboardDatasetWithRowEnums viewDashboardDatasetWithRowEnum = new ViewDashboardDatasetWithRowEnums();
        List<ViewDashboardDatasetWithRowEnums> viewDashboardDatasetWithRowEnums = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(dashBoardDatasetWithRowEnums)) {
            viewDashboardDatasetWithRowEnums = ViewDashboardDatasetWithRowEnums
                    .builtFrom(dashBoardDatasetWithRowEnums)
                    .stream()
                    .filter(e -> Long.parseLong (e.getDatasetId()) == dashboardDataset.getId())
                    .filter(e -> e.getDatasetId().equals(String.valueOf(dashboardDataset.getId())))
                    .collect(Collectors.toList());
            viewDashboardDatasetWithRowEnum = viewDashboardDatasetWithRowEnums.get(0);
        }

        //2. build aggConfig
        AggConfigBuilder majorQueryConfigBuilder = new AggConfigBuilder();
        AggConfig aggConfig = majorQueryConfigBuilder
                .setViewDashboardDatasetWithRowEnums(viewDashboardDatasetWithRowEnum)
                .setCfgReq(cfgReq)
                .setTimeFilterReq(timeFilterReq)
                .setTimePrimaryKey(timePrimaryKey)
                .build();
        aggConfig.setFilterPartExpressionList(filterPartExpressionList);
        try {
            Dataset dataset = dashboardDataset == null ? null : new Dataset(dashboardDataset);
            attachCustom(dataset, aggConfig);
            DataProvider dataProvider = getDataProvider(dashboardDatasource, query, dataset);
            return dataProvider.getAggData(aggConfig, reload);
        } catch (Exception e) {
            LOG.error(e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            throw new CBoardException("查询信息配置异常，请检查您的查询请求");
        }
    }


    public AggregateResult addColumnTest(DashboardDatasource dashboardDatasource, Map<String, String> query,
                                   CfgReq cfgReq, boolean reload) {


        //2. build aggConfig
        AggConfigBuilder majorQueryConfigBuilder = new AggConfigBuilder();
        AggConfig aggConfig = majorQueryConfigBuilder
                .setViewDashboardDatasetWithRowEnums(new ViewDashboardDatasetWithRowEnums())
                .setCfgReq(cfgReq)
                .setTimeFilterReq(null)
                .setTimePrimaryKey(null)
                .build();
        aggConfig.setFilterPartExpressionList(null);
        try {
//            Dataset dataset = dashboardDataset == null ? null : new Dataset(dashboardDataset);
//            attachCustom(dataset, aggConfig);
            DataProvider dataProvider = getDataProvider(dashboardDatasource, query, null);
            return dataProvider.getAggData(aggConfig, reload);
        } catch (Exception e) {
            LOG.error(e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            throw new CBoardException("查询信息配置异常，请检查您的查询请求");
        }
    }
    /**
     * 环比： 日环比，月环比
     *
     * @param majorQuery
     * @param timePrimaryKey
     * @param query
     * @param reload
     * @return
     */
    private RatioResult getLinkRatio(AggregateResult majorQuery, TimePrimaryKey timePrimaryKey,
                                     DashboardDatasource dashboardDatasource,
                                     Map<String, String> query,
                                     DashboardDataset dashboardDataset,
                                     boolean reload) {

        return calculateRatio(majorQuery, timePrimaryKey, dashboardDatasource, query, dashboardDataset, reload, RatioResult.LINK_RATIO_TYPE);
    }

    private RatioResult calculateRatio(AggregateResult majorQuery, TimePrimaryKey timePrimaryKey,
                                       DashboardDatasource dashboardDatasource,
                                       Map<String, String> query,
                                       DashboardDataset dashboardDataset,
                                       boolean reload, int ratioType) {

        RatioResult result = new RatioResult(ratioType);
        final String timeFormat = timePrimaryKey.getDataType().getFormat();
        final AggConfig aggConfig = majorQuery.getAggConfig();
        final String timePrimaryKeyColName = timePrimaryKey.getColumn();
        final boolean hasMonthRelativeTime = aggConfig.isHasMonthRelativeTime();


        String[][] dataTwoDimArr = majorQuery.getData();
        if (dataTwoDimArr.length != 1) {
            return result;
        }


        Double curVal = 0.0;
        try {
            curVal = Double.parseDouble(dataTwoDimArr[0][0]);
        } catch (Exception e) {
            return result;
        }


        List<DimensionConfig> rows = aggConfig.getRows();
        List<DimensionConfig> cols = aggConfig.getColumns();
        List<ConfigComponent> filters = aggConfig.getFilters();

        shiftConfigCompoments(rows, timePrimaryKeyColName, timeFormat, ratioType, hasMonthRelativeTime);
        shiftConfigCompoments(cols, timePrimaryKeyColName, timeFormat, ratioType, hasMonthRelativeTime);
        shiftConfigCompoments(filters, timePrimaryKeyColName, timeFormat, ratioType, hasMonthRelativeTime);

        AggregateResult prevAggResult;
        try {
            Dataset dataset = dashboardDataset == null ? null : new Dataset(dashboardDataset);
            DataProvider dataProvider = getDataProvider(dashboardDatasource, query, dataset);
            prevAggResult = dataProvider.getAggData(aggConfig, reload);//
        } catch (Exception e) {
            LOG.error(e.getMessage() + e.getStackTrace().toString());
            throw new CBoardException("查询信息配置异常，请检查您的查询请求");
        }

        String[][] prevDataArr = prevAggResult.getData();
        if (prevDataArr.length != 1) {
            return result;
        }

        Double prevVal = prevDataArr[0].length == 2 ? Double.parseDouble(prevAggResult.getData()[0][1]) : (prevAggResult.getData()[0][0] == null ? 0 : Double.parseDouble(prevAggResult.getData()[0][0]));
        result = RatioResult.calculate(curVal, prevVal, ratioType);

        return result;
    }

    /**
     * 同比: 日报表的周同比，和月报表的年同比
     *
     * @param majorQuery
     * @param timePrimaryKey
     * @param query
     * @param reload
     * @return
     */
    private RatioResult getYoYRatio(AggregateResult majorQuery, TimePrimaryKey timePrimaryKey,
                                    DashboardDatasource dashboardDatasource,
                                    Map<String, String> query,
                                    DashboardDataset dashboardDataset,
                                    boolean reload) {

        return calculateRatio(majorQuery, timePrimaryKey, dashboardDatasource, query, dashboardDataset, reload, RatioResult.YOY_RATIO_TYPE);
    }

    private DataProvider getDataProvider(DashboardDatasource dashboardDatasource, Map<String, String> query, Dataset dataset) throws Exception {
        if (dataset != null) {
            query = dataset.getQuery();
        }

        JSONObject datasourceConfig = JSONObject.parseObject(dashboardDatasource.getConfig());
        Map<String, String> dataSource = Maps.transformValues(datasourceConfig, Functions.toStringFunction());
        DataProvider dataProvider = DataProviderManager.getDataProvider(dashboardDatasource.getType(), dataSource, query);

        if (dataset != null && dataset.getInterval() != null && dataset.getInterval() > 0) {
            dataProvider.setInterval(dataset.getInterval());
        }
        return dataProvider;
    }

    private void attachCustom(Dataset dataset, AggConfig aggConfig) {
        if (dataset == null || aggConfig == null) {
            return;
        }
        Consumer<DimensionConfig> predicate = (config) -> {
            if (StringUtils.isNotEmpty(config.getId())) {
                String custom = (String) JSONPath.eval(dataset.getSchema(), "$.dimension[id='" + config.getId() + "'][0].custom");

                if (custom == null) {
                    custom = (String) JSONPath.eval(dataset.getSchema(), "$.dimension[type='level'].columns[id='" + config.getId() + "'][0].custom");
                }
                config.setCustom(custom);
            }
        };
        aggConfig.getRows().forEach(predicate);
        aggConfig.getColumns().forEach(predicate);
    }

    private void shiftConfigCompoments(List<? extends ConfigComponent> configList, String timePrimaryKeyColName, String timeFormat, int ratioType, boolean hasMonthRelativeTime) {
        if (!CollectionUtils.isEmpty(configList)) {
            String filterType = StringUtils.EMPTY;
            for (ConfigComponent config : configList) {
                if (config instanceof DimensionConfig) {
                    DimensionConfig dimConf = (DimensionConfig) config;
                    if (timePrimaryKeyColName.equals(dimConf.getColumnName())) {
                        filterType = dimConf.getFilterType();
                    }
                }
            }

            List<String> allDimConfVals = configList.stream().filter(
                    configComponent -> configComponent instanceof DimensionConfig
            ).filter(configComponent -> {
                DimensionConfig dimConf = (DimensionConfig) configComponent;
                return StringUtils.equals(dimConf.getColumnName(), timePrimaryKeyColName);
            }).map(configComp -> ((DimensionConfig) configComp).getValues()).flatMap(List::stream).collect(Collectors.toList());

            int shiftTimeUnits = 1;
            int monthStartDateShiftUnit = 0;
            int monthEndDateShiftUnit = 0;
            if (allDimConfVals.size() == 2) {
                try {
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
                    Date startDate = simpleDateFormat.parse(allDimConfVals.get(0));
                    DateTime startDateTime = new DateTime(startDate);
                    Date endDate = simpleDateFormat.parse(allDimConfVals.get(1));
                    DateTime endDateTime = new DateTime((endDate));
                    if (StringUtils.equals(DateUtil.YYYY_MM, timeFormat)) {
                        shiftTimeUnits = Math.abs(Months.monthsBetween(startDateTime, endDateTime).getMonths());
                        //根据开闭区间调整
                        if ("[a,b]".equals(filterType)) {
                            shiftTimeUnits += 1;
                        }
//                        shiftTimeUnits = Math.abs(Months.monthsBetween(startDateTime, endDateTime).getMonths());
                    } else if (StringUtils.equals(DateUtil.YYYY_MM_DD, timeFormat)) {
                        if (!hasMonthRelativeTime) {
                            shiftTimeUnits = Math.abs(Days.daysBetween(startDateTime, endDateTime).getDays());
                            //根据开闭区间调整
                            if ("[a,b]".equals(filterType)) {
                                shiftTimeUnits += 1;
                            }
                        } else {
                            DateTime prevStartDateTime = startDateTime.minusMonths(1);
                            Calendar startCalender = new GregorianCalendar(prevStartDateTime.getYear(), prevStartDateTime.getMonthOfYear() - 1, prevStartDateTime.getDayOfMonth());
                            monthStartDateShiftUnit = startCalender.getActualMaximum(Calendar.DAY_OF_MONTH);

                            DateTime prevEndDateTime = endDateTime.minusMonths(1);
                            Calendar endCalendar = new GregorianCalendar(prevEndDateTime.getYear(), prevEndDateTime.getMonthOfYear() - 1, prevEndDateTime.getDayOfMonth());
                            monthEndDateShiftUnit = endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        }
                    } else if (StringUtils.equals("yyyy-WW", timeFormat)) {
                        shiftTimeUnits = Math.abs(Weeks.weeksBetween(startDateTime, endDateTime).getWeeks());
                        //根据开闭区间调整
                        if ("[a,b]".equals(filterType)) {
                            shiftTimeUnits += 1;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            final int finalShiftTimeUnits = shiftTimeUnits;
            final int finalMonthStartDateShiftUnit = monthStartDateShiftUnit;
            final int finalMonthEndDateShiftUnit = monthEndDateShiftUnit;

            configList.stream().filter(configComponent -> configComponent instanceof DimensionConfig).filter(configComponent -> {
                DimensionConfig dimConf = (DimensionConfig) configComponent;
                return StringUtils.equals(dimConf.getColumnName(), timePrimaryKeyColName);
            }).forEach(configComponent -> {
                DimensionConfig dimConf = (DimensionConfig) configComponent;
                //dimConf.setFilterType("[a,b)");
                List<String> dimConfVals = dimConf.getValues();
                if (!CollectionUtils.isEmpty(dimConfVals)) {
                    List<String> prevRowVals = new LinkedList<>();
                    if (!hasMonthRelativeTime) {

                        dimConfVals.stream().forEach(rowVal -> {
                            String prevDateStr = getPreviousTimeStr(rowVal, timeFormat, ratioType, finalShiftTimeUnits);
                            prevRowVals.add(prevDateStr);
                        });
                        dimConf.setValues(prevRowVals);
                    } else {
                        String value0 = dimConfVals.get(0);
                        String prevStartDateStr = getPreviousTimeStr(value0, timeFormat, ratioType, finalMonthStartDateShiftUnit);
                        prevRowVals.add(prevStartDateStr);

                        String value1 = dimConfVals.get(1);
                        String prevEndDateStr = getPreviousTimeStr(value1, timeFormat, ratioType, finalMonthEndDateShiftUnit);
                        prevRowVals.add(prevEndDateStr);
                        dimConf.setValues(prevRowVals);
                    }

                }
            });
        }
    }

    private String getPreviousTimeStr(String curTimeStr, String format, int ratioType, int shiftTimeUnits) {
       /* 当前默认:
        yyyy-MM-dd 格式的数据是日粒度的；
        yyyy-MM 格式的数据是月粒度的；
         */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String prevDateStr = "";
        Date curDate;
        try {
            curDate = simpleDateFormat.parse(curTimeStr);
        } catch (ParseException e) {
            LOG.error("Error parsing time " + curTimeStr + " by using format " + format + " use now as curDate");
            curDate = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        Date prevDate;
        if (StringUtils.equals(format, DateUtil.YYYY_MM)) {//月粒度时间
            if (ratioType == RatioResult.LINK_RATIO_TYPE) {//环比
                calendar.add(Calendar.MONTH, -shiftTimeUnits);
            } else if (ratioType == RatioResult.YOY_RATIO_TYPE) {//同比
                calendar.add(Calendar.YEAR, -1);
            }
        } else if (StringUtils.equals(format, DateUtil.YYYY_MM_DD)) {//日粒度数据
            if (ratioType == RatioResult.LINK_RATIO_TYPE) {//环比
                calendar.add(Calendar.DATE, -shiftTimeUnits);
            } else if (ratioType == RatioResult.YOY_RATIO_TYPE) {//同比
                calendar.add(Calendar.YEAR, -1);
            }
        } else if (StringUtils.equals(format, "yyyy-WW")) {
            //解决周表同比环比不准确问题
            String[] split = curTimeStr.split("-");
            Integer preYear = Integer.valueOf(split[0]) - 1;
            if (ratioType == RatioResult.LINK_RATIO_TYPE) {//环比
                Integer week = Integer.valueOf(split[1]);
                int preWeek = week - 1;
                if (week == 1) {
                    prevDateStr = preYear + "-52";
                } else {
                    if (preWeek < 10) {
                        prevDateStr = split[0] + "-0" + preWeek;
                    } else {
                        prevDateStr = split[0] + "-" + preWeek;
                    }
                }
            } else if (ratioType == RatioResult.YOY_RATIO_TYPE) {//同比
                prevDateStr = preYear + "-" + split[1];
            }
            return prevDateStr;
        }
        prevDate = calendar.getTime();
        prevDateStr = simpleDateFormat.format(prevDate);
        return prevDateStr;
    }

    public DataProviderResult getColumns(DashboardDatasource dashboardDatasource, Map<String, String> query,
                                         DashboardDataset dashboardDataset, boolean reload) {

        DataProviderResult dps = new DataProviderResult();
        try {
            Dataset dataset = dashboardDataset == null ? null : new Dataset(dashboardDataset);
            List<PairData> columsList = Lists.newArrayList();

            //add metadata logic
//            DashboardDatasource dashboardDatasource = this.datasourceDao.getDatasource(datasourceId);
            if (dashboardDatasource != null && dashboardDatasource.getType().equalsIgnoreCase("Elasticsearch")) {
                String tableName = query.containsKey("table") ? query.get("table") : "dm_autoreport_app_business";
                TableDetail tableDetail = this.metadataDao.getTableDetailByName(tableName);
                if (tableDetail != null) {
                    List<NodeMappingInfo> tableColumnList = tableDetail.getDimenList();
                    tableColumnList.addAll(tableDetail.getMetricList());
                    if (tableColumnList != null && !tableColumnList.isEmpty()) {
                        for (NodeMappingInfo nodeMappingInfo : tableColumnList) {
                           ;
                            PairData pairData = new PairData(nodeMappingInfo.getKey(), swichType( nodeMappingInfo.getEs().getType()),
                                    getColumnAlias(nodeMappingInfo.getKey(),dashboardDatasource.getType()));
                            columsList.add(pairData);
                        }
                    }
//
//                    List<String> tableNameList = tableColumnList.stream().map(
//                            metadataTableInfo -> metadataTableInfo.getKey()).collect(Collectors.toList());

//                    columsList = tableNameList.toArray(new String[tableNameList.size()]);
                }
            } else if (dataset != null && dataset.getQuery().containsKey("index")) {
                JSONArray jsonArray = JSONArray.parseArray(dataset.getSchema().getString("dimension"));
                if (jsonArray != null && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        PairData pairData = new PairData(jsonArray.getJSONObject(i).getString("column"), jsonArray
                                .getJSONObject(i).getString("alias"));
                        columsList.add(pairData);
                    }
                }
            } else {
//                DataProvider dataProvider = getDataProvider(datasourceId, query, dataset);
                DataProvider dataProvider = getDataProvider(dashboardDatasource, query, dataset);
//                String[] columnArray = dataProvider.getColumn(reload);
              Map<String,String> columnType= dataProvider.getType();

                for (Map.Entry<String, String> entry : columnType.entrySet()) {
                    String type=entry.getValue();
                        PairData pairData = new PairData(entry.getKey(),swichType( entry.getValue()),
                                getColumnAlias(entry.getKey(),dashboardDatasource.getType()));
                        columsList.add(pairData);
                }
//                if (columnArray != null && columnArray.length > 0) {
//                    for (String str : columnArray) {
//                        PairData pairData = new PairData(str, str);
//                        columsList.add(pairData);
//                    }
//                }
            }
            dps.setColumns(columsList);
            dps.setMsg("1");
        } catch (Exception e) {
            e.printStackTrace();
            dps.setMsg(e.getMessage());
        }
        return dps;
    }

    private String swichType(String type){
        if(StringUtils.isEmpty(type)
                ||type.toUpperCase().contains("CHAR")
                ||type.toUpperCase().contains("KEYWORD")){
            return  "string";
        }else if(type.toUpperCase().contains("INT")
                ||type.toUpperCase().contains("DOUBLE")
                ||type.toUpperCase().contains("LONG")){
            return "num";
        }else if(type.toUpperCase().contains("DATE")) {
            return "date";
        }else{
            return "string";
        }
    }

    public String [] getDimensionValues(DashboardDatasource dashboardDatasource, Map<String, String> query,
                                             DashboardDataset dashboardDataset, String columnName, AggConfig config, boolean reload) {
        try {
            Dataset dataset = dashboardDataset == null ? null : new Dataset(dashboardDataset);
            attachCustom(dataset, config);
            DataProvider dataProvider = getDataProvider(dashboardDatasource, query, dataset);
            //  String[] result = dataProvider.getDimVals(columnName, config, reload);
            String []result = dataProvider.getDimVals(columnName, config, reload);
//            Collections.sort(result, new Comparator<PairData>() {
//                @Override
//                public int compare(PairData o1, PairData o2) {
//                    return o1.getKey().compareTo(o2.getKey());
//                }
//            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
//        try {
//            Dataset dataset = getDataset(datasetId);
//            attachCustom(dataset, config);
//            DataProvider dataProvider = getDataProvider(datasourceId, query, dataset);
//            String[] result = dataProvider.getDimVals(columnName, config, reload);
//            return result;
//        } catch (Exception e) {
//            LOG.error("", e);
//        }
//        return null;
    }

    public Map<String, String> getColumnTypes(DashboardDatasource dashboardDatasource, Map<String, String> query,
                                              DashboardDataset dashboardDataset, AggConfig config, boolean reload) {
        try {
            Dataset dataset = dashboardDataset == null ? null : new Dataset(dashboardDataset);
            attachCustom(dataset, config);
            DataProvider dataProvider = getDataProvider(dashboardDatasource, query, dataset);
            Map<String, String> result = dataProvider.getType();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String viewAggDataQuery(DashboardDatasource dashboardDatasource, Map<String, String> query,
                                   DashboardDataset dashboardDataset, AggConfig config) {
        try {
            Dataset dataset = dashboardDataset == null ? null : new Dataset(dashboardDataset);
            attachCustom(dataset, config);
            DataProvider dataProvider = getDataProvider(dashboardDatasource, query, dataset);
            return dataProvider.getViewAggDataQuery(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CBoardException(e.getMessage());
        }
    }

    public ServiceStatus test(String type, String dataSourceConfig, String query) {
        try {
            DataProvider dataProvider = DataProviderManager.getDataProvider(
                    type,
                    Maps.transformValues(JSONObject.parseObject(dataSourceConfig), Functions.toStringFunction()),
                    Maps.transformValues(JSONObject.parseObject(query), Functions.toStringFunction()));

            dataProvider.getData();
            return new ServiceStatus(ServiceStatus.Status.Success, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceStatus(ServiceStatus.Status.Fail, e.getMessage());
        }
    }

    private String getColumnAlias(String column,String type){
        if(StringUtils.isEmpty(column)){
            return  "";
        }
        if(StringUtils.isEmpty(type)){
            return column;
        }
           if("jdbc".equalsIgnoreCase(type)){
          return      column;
           }else if("elasticsearch".equalsIgnoreCase(type)){
              String [] columnArr= column.split("/");
              return  columnArr[columnArr.length-1];
           }else if("kylin".equalsIgnoreCase(type)){
               String [] columnArr= column.split("\\.");
               return  columnArr[columnArr.length-1];
           }else{
               return column;
           }
    }
}
