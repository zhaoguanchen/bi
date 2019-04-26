package com.yiche.bigdata.dataprovider.config;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.facebook.presto.jdbc.internal.joda.time.*;
import com.yiche.bigdata.constant.ReportTypeEnum;
import com.yiche.bigdata.dataprovider.expression.NowFunction;
import com.yiche.bigdata.dto.PairData;
import com.yiche.bigdata.dto.ViewDashboardColumn;
import com.yiche.bigdata.dto.ViewDashboardDatasetWithRowEnums;
import com.yiche.bigdata.dto.ViewDashboardRowEnum;
import com.yiche.bigdata.dto.req.*;
import com.yiche.bigdata.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by yangyuchen on 25/01/2018.
 */
public class AggConfigBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(AggConfigBuilder.class);

    private CfgReq cfgReq;
    private ViewDashboardDatasetWithRowEnums viewDashboardDatasetWithRowEnums;//current dataset's rowEnums
    private TimePrimaryKey timePrimaryKey;
    private TimeFilterReq timeFilterReq;
    private AggConfig aggConfig;

    public AggConfigBuilder() {
        this.aggConfig = new AggConfig();
    }

    public AggConfig build() {
        integrateCfgReq();
        if (timePrimaryKey != null) {
            boolean hasMonthRelativeTime = integrateTimeFilter();
            aggConfig.setHasMonthRelativeTime(hasMonthRelativeTime);
        }
        integrateRowEnums();
        if (timePrimaryKey != null) {
            furtherFormatting();
        }
        return aggConfig;
    }

    //step1
    private void integrateCfgReq() {
        if (cfgReq == null) {
            return;
        }
        final List<AttrReq> rowsReq = cfgReq.getRows();
        final List<AttrReq> columnsReq = cfgReq.getColumns();
        final List<AttrReq> filtersReq = cfgReq.getFilters();
        final List<ValueReq> valuesReq = cfgReq.getValues();

        List<DimensionConfig> rows = Collections.EMPTY_LIST;
        List<DimensionConfig> columns = Collections.EMPTY_LIST;
        List<ConfigComponent> filters = Collections.EMPTY_LIST;
        List<ValueConfig> values = Collections.EMPTY_LIST;


        if (!CollectionUtils.isEmpty(rowsReq)) {
            rows = rowsReq.stream().map(rowReq -> DimensionConfig.builtFromAttrReq(rowReq)).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(columnsReq)) {
            columns = columnsReq.stream().map(columnReq -> DimensionConfig.builtFromAttrReq(columnReq)).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(filtersReq)) {
            filters = filtersReq.stream().map(filterReq -> DimensionConfig.builtFromAttrReq(filterReq)).collect(Collectors.toList());
            ;
        }
        if (!CollectionUtils.isEmpty(valuesReq)) {
            values = valuesReq.stream().map(valueReq -> ValueConfig.builtFrom(valueReq)).collect(Collectors.toList());
        }

        aggConfig.setRows(rows);
        aggConfig.setColumns(columns);
        aggConfig.setFilters(filters);
        aggConfig.setValues(values);
    }

    //step2
    private boolean integrateTimeFilter() {
        boolean monthRelativeTime = false;

        if(timePrimaryKey.getDataType()==null || timePrimaryKey.getDataType().getType() == null){
            return false;
        }
//        final String timeFormat = timePrimaryKey.getDataType().getFormat();


        if (timeFilterReq == null) {
            //时间筛选条件为空
            timeFilterReq = new TimeFilterReq();
            timeFilterReq.setTimePrimaryColumn(timePrimaryKey.getColumn());
        }

        //时间筛选条件不为空
        String timePrimaryKeyName = timeFilterReq.getTimePrimaryColumn();
        String reportTypeName = timeFilterReq.getType();
        List<String> timeValues = timeFilterReq.getValues();


        List<DimensionConfig> rows = aggConfig.getRows();
        List<DimensionConfig> columns = aggConfig.getColumns();
        List<ConfigComponent> filters = aggConfig.getFilters();
        List<DimensionConfig> modifiedFilters = new LinkedList();
        ReportTypeEnum reportType = ReportTypeEnum.getType(reportTypeName);
        if (!CollectionUtils.isEmpty(filters)) {
            modifiedFilters.addAll(filters.stream().map(filter -> (DimensionConfig) filter).collect(Collectors.toList()));
        }

        boolean rowHasTime = getTimePrimaryKeyDimConf(rows) != null;
        boolean colHasTime = getTimePrimaryKeyDimConf(columns) != null;
        if (rowHasTime) {//行维中有时间主键时
            if(hasMonthRelativeTime(rows)){
                monthRelativeTime = true;
            }
            rows = integrateTimePrimaryKeyWithAttrList(rows, reportType).stream().map(row -> (DimensionConfig) row).collect(Collectors.toList());
            aggConfig.setRows(rows);
        }
        if (colHasTime) {//列维中有时间主键时
            if(hasMonthRelativeTime(columns)){
                monthRelativeTime = true;
            }
            columns = integrateTimePrimaryKeyWithAttrList(columns, reportType).stream().map(column -> (DimensionConfig) column).collect(Collectors.toList());
            aggConfig.setColumns(columns);
        }
        if (!rowHasTime && !colHasTime) {
            if(hasMonthRelativeTime(filters)){
                monthRelativeTime = true;
            }
            filters = integrateTimePrimaryKeyWithAttrList(filters, reportType).stream().map(filter -> (DimensionConfig) filter).collect(Collectors.toList());
            aggConfig.setFilters(filters);
        }
        return monthRelativeTime;
    }

    private boolean hasMonthRelativeTime(List<? extends ConfigComponent> configList){
        List<DimensionConfig> dimensionConfigList = new LinkedList<>();

        if (!CollectionUtils.isEmpty(configList)) {
            dimensionConfigList = configList.stream().map(config -> {
                return (DimensionConfig) config;
            }).collect(Collectors.toList());
        }

        if(CollectionUtils.isEmpty(dimensionConfigList)){
            return false;
        }

        DimensionConfig timePrimaryDimConf = getTimePrimaryKeyDimConf(dimensionConfigList);
        if(timePrimaryDimConf == null){
            return false;
        }
        else{
            if(timePrimaryDimConf.hasRelativeValues()){
                List<String> monthGranulityNowFuncs = timePrimaryDimConf.getValues().stream()
                        .filter(valuePair -> {
                            PairData pairData = JSONObject.parseObject(valuePair, PairData.class);
                            return NowFunction.MONTH_GRANUALITY == NowFunction.getGranuality(pairData.getKey());
                        }).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(monthGranulityNowFuncs)){
                    //初始化选中">="最近一月时走"="的逻辑
                    boolean flag = monthGranulityNowFuncs.stream().anyMatch(predicate ->
                            predicate.equals("{\"key\":\"{now('M',-1,'yyyy-MM-dd')}\",\"value\":\"{now('M',-1,'yyyy-MM-dd')}\"}")
                    );
                    if(flag){
                        return false;
                    }
                    return true;
                }
                else{
                    return false;
                }
            }
            return false;
        }
    }

    private String getFormattedTimeValueStr(String format, String timeValue) {
        String result = "";
        if (StringUtils.isEmpty(format) || StringUtils.isEmpty(timeValue)) {
            return result;
        }
        if (StringUtils.equals(format, DateUtil.YYYY_MM)) {
            SimpleDateFormat yyyyMmFormat = new SimpleDateFormat(DateUtil.YYYY_MM);
            Date timeValueDate = null;//hard code for now, will substitute with variable later
            try {
                timeValueDate = yyyyMmFormat.parse(timeValue);
                SimpleDateFormat monthFormat = new SimpleDateFormat(format);
                result = monthFormat.format(timeValueDate);
                return result;
            } catch (ParseException e) {
                return result;
            }
        } else if (StringUtils.equals(format, DateUtil.YYYY_WW)) {
            SimpleDateFormat yyyyWwFormat = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
            Date timeValueDate = null;//hard code for now, will substitute with variable later
            try {
                timeValueDate = yyyyWwFormat.parse(timeValue);
                SimpleDateFormat weekFormat = new SimpleDateFormat(DateUtil.YYYY_WW);
                result = weekFormat.format(timeValueDate);
                return result;
            } catch (ParseException e) {
                return result;
            }
        } else {
            //default to format yyyy-MM-dd, no further formatting
            return timeValue;
        }
    }

    private List<? extends ConfigComponent> integrateTimePrimaryKeyWithAttrList(List<? extends ConfigComponent> configList, ReportTypeEnum reportTypeEnum) {
        List<DimensionConfig> dimensionConfigList = new LinkedList<>();
//        LOG.info("integrateTimePrimaryKeyWithAttrList configList={} reportTypeEnum={}", configList.toString(), reportTypeEnum.toString());

        if (!CollectionUtils.isEmpty(configList)) {
            dimensionConfigList = configList.stream().map(config -> {
                return (DimensionConfig) config;
            }).collect(Collectors.toList());
        }

        final String timePrimaryKeyName = timeFilterReq.getTimePrimaryColumn();
        List<String> timeValues = timeFilterReq.getValues();//时间筛选条件
        final String timeFormat = timePrimaryKey.getDataType().getFormat();//时间主键格式
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);

        DimensionConfig timePrimaryDimConf = getTimePrimaryKeyDimConf(dimensionConfigList);//时间过滤条件
        List<String> dimenConfValues = Collections.EMPTY_LIST;//时间过滤条件
        boolean relativeTime = false;
        if (timePrimaryDimConf != null) {//如果有时间过滤条件，那么将时间筛选条件合并到时间过滤条件中，注意在这个方法中不会出现时间过滤条件为空的情况，因为调用之前已经排除了
            //删除之前filters过滤条件中的时间主键，暂时存放在 timePrimaryDimConf， 组装好之后再加入 filters
            for (int i = 0; i < dimensionConfigList.size(); i++) {
                DimensionConfig config = (DimensionConfig) dimensionConfigList.get(i);
                if (StringUtils.equals(timePrimaryKeyName, config.getColumnName())) {
                    dimenConfValues = timePrimaryDimConf.getValues();//时间过滤条件
                    relativeTime = timePrimaryDimConf.hasRelativeValues();
                    dimensionConfigList.remove(i);
                    break;
                }
            }
        }

        if (reportTypeEnum == ReportTypeEnum.COMMON_REPORT) {//普通报表
            if (CollectionUtils.isEmpty(dimenConfValues)) {//无时间过滤条件的普通报表
                if (CollectionUtils.isEmpty(timeValues)) {//1. 无时间筛选条件，无时间过滤条件的普通报表
                    //无操作
                    if (timePrimaryDimConf != null){
                        dimensionConfigList.add(timePrimaryDimConf);
                    }
                } else if (timeValues.size() == 2) {//2. 筛选条件会传入开始时间和结束时间，无时间过滤条件的普通报表
                    //使用时间筛选条件拼凑时间过滤条件
                    final String startDate = getFormattedTimeValueStr(timeFormat, timeValues.get(0));
                    final String endDate = getFormattedTimeValueStr(timeFormat, timeValues.get(1));

                    ValueTupleReq startTuple = new ValueTupleReq(startDate);
                    ValueTupleReq endTuple = new ValueTupleReq(endDate);

                    DimensionConfig dimensionConfig = new DimensionConfig();
                    if (StringUtils.equals(timeValues.get(0), timeValues.get(1))) {
                        dimensionConfig.setFilterType("=");
                    } else {
                        dimensionConfig.setFilterType("[a,b]");
                    }
                    List<String> values = new LinkedList<>();
                    values.add(startTuple.toString());
                    values.add(endTuple.toString());
                    dimensionConfig.setValues(values);
                    dimensionConfig.setColumnName(timePrimaryKeyName);

                    dimensionConfigList.add(dimensionConfig);
                }
            } else if (relativeTime) {//过滤条件为相对时间的普通报表
                if (CollectionUtils.isEmpty(timeValues)) {//3. 无时间筛选条件，过滤条件为相对时间的普通报表
                    //将原来拿走的相对的时间过滤条件放回
                    Date now = new Date();
                    String formattedNowDateStr = simpleDateFormat.format(now);

                    List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String nowFunc = pairData.getKey();
                        String shiftedVal = shiftRelativeTimeExpr(nowFunc, formattedNowDateStr, timeFormat);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    DimensionConfig dimensionConfig = new DimensionConfig();


                    dimensionConfig.setColumnName(timePrimaryKeyName);


                    //add relative beginning
                    List<String> beginningVals = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String nowFunc = pairData.getKey();
                        String shiftedVal = getRelativeBeginningStr(nowFunc, formattedNowDateStr, timeFormat);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    values.addAll(beginningVals);
                    dimensionConfig.setFilterType("[a,b)");
                    dimensionConfig.setValues(values);
                    dimensionConfigList.add(dimensionConfig);
                } else if (timeValues.size() == 2) {//4. 筛选条件会传入开始时间和结束时间，过滤条件为相对时间的普通报表
                    //新的需求指示：用绝对的筛选条件代替相对的过滤条件
                    List<String> values = timeValues.stream().map(timeStr -> {
                        String shiftedVal = getFormattedTimeValueStr(timeFormat, timeStr);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    DimensionConfig dimensionConfig = new DimensionConfig();
                    dimensionConfig.setValues(values);
                    dimensionConfig.setFilterType("[a,b]");
                    dimensionConfig.setColumnName(timePrimaryKeyName);
                    dimensionConfigList.add(dimensionConfig);

//                    try {
//                        final String dateStr1 = timeValues.get(0);
//                        final String dateStr2 = timeValues.get(1);
//                        final Date date1 = simpleDateFormat.parse(dateStr1);
//                        final Date date2 = simpleDateFormat.parse(dateStr2);
//                        String endTimeStr = date1.after(date2) ? dateStr1 : dateStr2;

//                        String relPoint = getFormattedTimeValueStr(timeFormat, endTimeStr);//以结束时间为相对时间点
//                        List<String> values = dimenConfValues.stream().map(pairDataStr -> {
//                            final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
//                            String nowFunc = pairData.getKey();
//                            String shiftedVal = shiftRelativeTimeExpr(nowFunc, relPoint, timeFormat);
//                            ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
//                            return valueTupleReq.toString();
//                        }).collect(Collectors.toList());
//
//                        DimensionConfig dimensionConfig = new DimensionConfig();
//                        dimensionConfig.setValues(values);
//                        dimensionConfig.setFilterType(timePrimaryDimConf.getFilterType());
//                        dimensionConfig.setColumnName(timePrimaryKeyName);
//                        dimensionConfigList.add(dimensionConfig);

                    //add relative beginning
//                        List<String> beginningVals = dimenConfValues.stream().map(pairDataStr -> {
//                            final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
//                            String nowFunc = pairData.getKey();
//                            String shiftedVal = getRelativeBeginningStr(nowFunc, relPoint, timeFormat);
//                            ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
//                            return valueTupleReq.toString();
//                        }).collect(Collectors.toList());
//
//                        DimensionConfig beginningDimensionConfig = new DimensionConfig();
//                        beginningDimensionConfig.setValues(beginningVals);
//                        beginningDimensionConfig.setFilterType("<");
//                        beginningDimensionConfig.setColumnName(timePrimaryKeyName);
//                        dimensionConfigList.add(beginningDimensionConfig);
//                    } catch (ParseException e) {
//                        LOG.error("timeValue parsing error: currently parsing " + timeValues + e);
//                    }
                }
            } else {//过滤条件为绝对时间的普通报表
                if (CollectionUtils.isEmpty(timeValues)) {//5. 无时间筛选条件，过滤条件为绝对时间的普通报表
                    //将原来拿走的绝对时间过滤条件放回
                    List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String unformattedTime = pairData.getKey();
                        String shiftedVal = getFormattedTimeValueStr(timeFormat, unformattedTime);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    DimensionConfig dimensionConfig = new DimensionConfig();
                    dimensionConfig.setValues(values);
                    dimensionConfig.setFilterType(timePrimaryDimConf.getFilterType());
                    dimensionConfig.setColumnName(timePrimaryKeyName);
                    dimensionConfigList.add(dimensionConfig);
                } else if (timeValues.size() == 2) {//6. 筛选条件会传入开始时间和结束时间，过滤条件为绝对时间的普通报表
                    //以绝对的时间筛选条件替换绝对的时间过滤条件
                    final String startDate = getFormattedTimeValueStr(timeFormat, timeValues.get(0));
                    final String endDate = getFormattedTimeValueStr(timeFormat, timeValues.get(1));

                    ValueTupleReq startTuple = new ValueTupleReq(startDate);
                    ValueTupleReq endTuple = new ValueTupleReq(endDate);

                    DimensionConfig dimensionConfig = new DimensionConfig();
                    if (StringUtils.equals(timeValues.get(0), timeValues.get(1))) {
                        dimensionConfig.setFilterType("=");
                    } else {
                        dimensionConfig.setFilterType("[a,b]");
                    }

                    List<String> values = new LinkedList<>();
                    values.add(startTuple.toString());
                    values.add(endTuple.toString());
                    dimensionConfig.setValues(values);
                    dimensionConfig.setColumnName(timePrimaryKeyName);

                    dimensionConfigList.add(dimensionConfig);
                }
            }
        } else if (ReportTypeEnum.UNSET == reportTypeEnum) {//图表的查询
            //创建 "<=当前时间"的时间过滤条件，注意时间格式
//            LOG.info("UNSET dimenConfValues={} timePrimaryDimConf={}", dimenConfValues.toString(), timePrimaryDimConf.toString());

            if (CollectionUtils.isEmpty(dimenConfValues)) {//7. 无时间过滤条件的图表
                //无操作
                if (timePrimaryDimConf != null) {
                    timePrimaryDimConf.setValues(dimenConfValues);
                    dimensionConfigList.add(timePrimaryDimConf);
                }
            } else if (relativeTime) {//8. 过滤条件为相对时间图表
                //将原来拿走的时间过滤条件放回，注意月表now表达式中的时间格式
                Date now = new Date();

                String[] formattedNowDateStr = {simpleDateFormat.format(now)};
                if(timeFormat.equals("yyyy-WW")){
                    Calendar instance = Calendar.getInstance();
                    instance.setTime(now);
                    int week = instance.get(Calendar.WEEK_OF_YEAR);
                    String year = new SimpleDateFormat("yyyy").format(now);
                    if(week<10){
                        formattedNowDateStr[0]=year+"-0"+week;
                    }else {
                        formattedNowDateStr[0]=year+"-"+week;
                    }
                }
                LOG.info("relative timePrimaryKey={} timeFormat={} now={} formattedNowDateStr={}",
                        timePrimaryKey.toString(), timeFormat.toString(), now, formattedNowDateStr.toString());

                LOG.info("UNSET relative  dimenConfValues={} timePrimaryDimConf={} timeFormat={} formattedNowDateStr={}", dimenConfValues.toString(),
                        timePrimaryDimConf.toString(), timeFormat.toString(), formattedNowDateStr.toString());

                List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                    final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                    String nowFunc = pairData.getKey();
                    String shiftedVal = shiftRelativeTimeExpr(nowFunc, formattedNowDateStr[0], timeFormat);
                    LOG.info("UNSET inner shiftedVal={} timePrimaryDimConf={} timeFormat={}", shiftedVal.toString());
                    ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                    return valueTupleReq.toString();
                }).collect(Collectors.toList());
                LOG.info("UNSET outter values={} ", values.toString());

                DimensionConfig dimensionConfig = new DimensionConfig();
                dimensionConfig.setValues(values);
                dimensionConfig.setFilterType(timePrimaryDimConf.getFilterType());
                dimensionConfig.setColumnName(timePrimaryKeyName);


                //add relative beginning
                List<String> beginningVals = dimenConfValues.stream().map(pairDataStr -> {
                    final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                    String nowFunc = pairData.getKey();
                    String shiftedVal = getRelativeBeginningStr(nowFunc, formattedNowDateStr[0], timeFormat);
                    ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                    return valueTupleReq.toString();
                }).collect(Collectors.toList());

                if(!CollectionUtils.isEmpty(values) && !CollectionUtils.isEmpty(beginningVals)){
                    values.addAll(beginningVals);
                    dimensionConfig.setValues(values);
                    dimensionConfig.setFilterType("[a,b)");
                    //将初始化选中“>=”最近一月改走“=”的逻辑修改月报环比的bug
                    if(timeFormat.equals("yyyy-MM")){
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(now);
                        int month = instance.get(Calendar.MONTH);
                        String year = new SimpleDateFormat("yyyy").format(now);
                        String lastMonth=null;
                        if(month<10){
                            lastMonth=year+"-0"+month;
                        }else {
                            lastMonth=year+"-"+month;
                        }
                        JSONObject valuesJson = new JSONObject();
                        valuesJson.put("key",lastMonth);
                        valuesJson.put("value",lastMonth);
                        values.clear();
                        values.add(JSONUtils.toJSONString(valuesJson));
                        dimensionConfig.setFilterType("=");
                    }
                }

                dimensionConfigList.add(dimensionConfig);
            } else {//9. 过滤条件为绝对时间的图表
                //将原来拿走的时间过滤条件放回，注意月表now表达式中的时间格式
                List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                    final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                    String unformattedTime = pairData.getKey();
                    String shiftedVal = getFormattedTimeValueStr(timeFormat, unformattedTime);
                    ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                    return valueTupleReq.toString();
                }).collect(Collectors.toList());

                DimensionConfig dimensionConfig = new DimensionConfig();
                dimensionConfig.setValues(values);
                dimensionConfig.setFilterType(timePrimaryDimConf.getFilterType());
                dimensionConfig.setColumnName(timePrimaryKeyName);
                dimensionConfigList.add(dimensionConfig);
            }
        } else {//日报、周报、月报
            if (CollectionUtils.isEmpty(dimenConfValues)) {//无时间过滤条件的日、周、月报
                if (CollectionUtils.isEmpty(timeValues)) {//10. 无时间筛选条件，无时间过滤条件的日、周、月报
                    //无操作
                    if (timePrimaryDimConf != null){
                        dimensionConfigList.add(timePrimaryDimConf);
                    }
                } else if (timeValues.size() == 1) {//11. 有结束时间筛选条件，无时间过滤条件的日、周、月报
                    //创建 "<=结束时间筛选条件" 的时间过滤条件，注意月表时间格式
                    String endTime = getFormattedTimeValueStr(timeFormat, timeValues.get(0));
                    ValueTupleReq endTuple = new ValueTupleReq(endTime);
                    List<String> values = new LinkedList<>();
                    values.add(endTuple.toString());
                    DimensionConfig dimensionConfig = new DimensionConfig();
                    dimensionConfig.setFilterType("≤");
                    dimensionConfig.setColumnName(timePrimaryKeyName);
                    dimensionConfig.setValues(values);
                    dimensionConfigList.add(dimensionConfig);
                }
            } else if (relativeTime) {//过滤条件为相对时间的日、周、月报
                if (CollectionUtils.isEmpty(timeValues)) {//12. 无时间筛选条件，过滤条件为相对时间的日、周、月报
                    //将原来拿走的时间过滤条件放回，注意月表now表达式中的时间格式
                    Date now = new Date();
//                    String formattedNowDateStr = simpleDateFormat.format(now);
                    final String[] formattedNowDateStr={simpleDateFormat.format(now)};
                    if(timeFormat.equals("yyyy-WW")){
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(now);
                        int week = instance.get(Calendar.WEEK_OF_YEAR);
                        String year = new SimpleDateFormat("yyyy").format(now);
                        if(week<10){
                            formattedNowDateStr[0]=year+"-0"+week;
                        }else {
                            formattedNowDateStr[0]=year+"-"+week;
                        }
                    }
                    List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String nowFunc = pairData.getKey();
                        String shiftedVal = shiftRelativeTimeExpr(nowFunc, formattedNowDateStr[0], timeFormat);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    DimensionConfig dimensionConfig = new DimensionConfig();
//                    dimensionConfig.setValues(values);
//                    dimensionConfig.setFilterType(timePrimaryDimConf.getFilterType());
                    dimensionConfig.setColumnName(timePrimaryKeyName);


                    //add relative beginning
                    List<String> beginningVals = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String nowFunc = pairData.getKey();
                        String shiftedVal = getRelativeBeginningStr(nowFunc, formattedNowDateStr[0], timeFormat);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

//                    DimensionConfig beginningDimensionConfig = new DimensionConfig();
//                    beginningDimensionConfig.setValues(beginningVals);
                    values.addAll(beginningVals);
                    dimensionConfig.setValues(values);
                    dimensionConfig.setFilterType("[a,b)");
                    dimensionConfigList.add(dimensionConfig);
                    //将初始化选中“>=”最近一月改走“=”的逻辑修改月报环比的bug
                    if(timeFormat.equals("yyyy-MM")){
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(now);
                        int month = instance.get(Calendar.MONTH);
                        String year = new SimpleDateFormat("yyyy").format(now);
                        String lastMonth=null;
                        if(month<10){
                            lastMonth=year+"-0"+month;
                        }else {
                            lastMonth=year+"-"+month;
                        }
                        JSONObject valuesJson = new JSONObject();
                        valuesJson.put("key",lastMonth);
                        valuesJson.put("value",lastMonth);
                        values.clear();
                        values.add(JSONUtils.toJSONString(valuesJson));
                        dimensionConfig.setFilterType("=");
                    }
//                    beginningDimensionConfig.setFilterType("<");
//                    beginningDimensionConfig.setColumnName(timePrimaryKeyName);
//                    dimensionConfigList.add(beginningDimensionConfig);
                } else if (timeValues.size() == 1) {//13. 有结束时间筛选条件， 过滤条件为相对时间的日、周、月报
                    //以"结束时间"为相对时间点，平移相对时间now表达式， 注意月表now表达式中的时间格式
                    String relPoint = getFormattedTimeValueStr(timeFormat, timeValues.get(0));

                    List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String nowFunc = pairData.getKey();
                        String shiftedVal = shiftRelativeTimeExpr(nowFunc, relPoint, timeFormat);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    DimensionConfig dimensionConfig = new DimensionConfig();


                    dimensionConfig.setColumnName(timePrimaryKeyName);


                    //add relative beginning
                    List<String> beginningVals = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String nowFunc = pairData.getKey();
                        String shiftedVal = getRelativeBeginningStr(nowFunc, relPoint, timeFormat);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    values.addAll(beginningVals);
//                    DimensionConfig beginningDimensionConfig = new DimensionConfig();
                    dimensionConfig.setValues(values);
                    //beginningDimensionConfig.setFilterType("<");
                    dimensionConfig.setFilterType("[a,b)");
//                    beginningDimensionConfig.setColumnName(timePrimaryKeyName);
//                    dimensionConfigList.add(beginningDimensionConfig);
                    dimensionConfigList.add(dimensionConfig);
                }
            } else {//过滤条件为绝对时间的日、周、月报
                if (CollectionUtils.isEmpty(timeValues)) {//14. 无时间筛选条件，过滤条件为绝对时间的日、周、月报
                    //将原来拿走的绝对时间过滤条件放回，注意月表时间格式
                    List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String unformattedTime = pairData.getKey();
                        String shiftedVal = getFormattedTimeValueStr(timeFormat, unformattedTime);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    DimensionConfig dimensionConfig = new DimensionConfig();
                    dimensionConfig.setValues(values);
                    dimensionConfig.setFilterType(timePrimaryDimConf.getFilterType());
                    dimensionConfig.setColumnName(timePrimaryKeyName);
                    dimensionConfigList.add(dimensionConfig);
                } else if (timeValues.size() == 1) {//15. 有结束时间筛选条件， 过滤条件为绝对时间的日、周、月报
                    //将结束时间筛选条件替换原来过滤条件中的绝对结束时间，注意月表时间格式
                    List<String> values = dimenConfValues.stream().map(pairDataStr -> {
                        final PairData pairData = JSONObject.parseObject(pairDataStr, PairData.class);
                        String unformattedTime = pairData.getKey();
                        String shiftedVal = getFormattedTimeValueStr(timeFormat, unformattedTime);
                        ValueTupleReq valueTupleReq = new ValueTupleReq(shiftedVal);
                        return valueTupleReq.toString();
                    }).collect(Collectors.toList());

                    DimensionConfig dimensionConfig = new DimensionConfig();
                    String relPoint = getFormattedTimeValueStr(timeFormat, timeValues.get(0));
                    //set the current point to relPoint
                    ValueTupleReq relTuple = new ValueTupleReq(relPoint);
//                    LOG.info("aggconfigBuilder vale={}", values);
//                    LOG.info("aggconfigBuilder timePrimaryDimConf={}", timePrimaryDimConf.toString());

                    if (values.size() == 2) {
                        values.remove(1);
                        values.add(relTuple.toString());
                        dimensionConfig.setFilterType("[a,b]");
                    } else if (values.size() == 1) {
//                        values.remove(0);
                        values.add(relTuple.toString());
                        if (timePrimaryDimConf.getFilterType().equals("≤") || timePrimaryDimConf.getFilterType().equals("<")) {
                            values.remove(0);
                            dimensionConfig.setFilterType(timePrimaryDimConf.getFilterType());
                        } else {
                            dimensionConfig.setFilterType("[a,b]");
                        }
//                        LOG.info("aggconfigBuilder timePrimaryDimConf={}", timePrimaryDimConf.toString());
                    } else {
                        dimensionConfig.setFilterType("=");
                    }

                    dimensionConfig.setValues(values);
                    dimensionConfig.setColumnName(timePrimaryKeyName);
                    dimensionConfigList.add(dimensionConfig);
                }
            }
        }
//        LOG.info("integrateTimePrimaryKeyWithAttrList dimensionConfigList={} ", dimensionConfigList.toString());

        return dimensionConfigList;
    }


    /**
     * @param nowFunc    (相对的过滤条件）
     * @param relTimeStr （时间筛选条件字符串）
     * @param timeFormat
     * @return
     */
    private String shiftRelativeTimeExpr(String nowFunc, String relTimeStr, String timeFormat) {
        //将不同的时间粒度都转化成"日"粒度的
        String result = "";
        final int nowFuncType = NowFunction.getGranuality(nowFunc);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);

        Date relPointDate = null;
        try {
            relPointDate = simpleDateFormat.parse(relTimeStr);
        } catch (ParseException e) {
            LOG.error("timeValue parsing error: currently parsing " + relTimeStr + e);
        }
        DateTime relPointDateTime = new DateTime(relPointDate);
        Date now = new Date();
        DateTime nowDateTime = new DateTime(now);

        final int relPointDayOfWeek = relPointDateTime.getDayOfWeek();
        final int relPointDayOfMonth = relPointDateTime.getDayOfMonth();
        final int relNowDiffDays = Math.abs(Days.daysBetween(nowDateTime, relPointDateTime).getDays());
        final int relNowDiffWeeks = Math.abs(Weeks.weeksBetween(nowDateTime, relPointDateTime).getWeeks());
        int relNowDiffMonths = nowDateTime.getMonthOfYear() - relPointDateTime.getMonthOfYear() + (nowDateTime.getYear() - relPointDateTime.getYear()) * 12;

        DateTime prevMonth = nowDateTime.plusMonths(-relNowDiffMonths - 1);

//        final int prevMonthDays = Math.abs(Days.daysBetween(prevMonth, nowDateTime).getDays());
        final int prevMonthDays = prevMonth.dayOfMonth().getMaximumValue();


        Integer prevNowFuncInt = Integer.MIN_VALUE;
        try {

            final String[] relativeTimeValueTokens = nowFunc.split(",");
            prevNowFuncInt = Math.abs(Integer.parseInt(relativeTimeValueTokens[1]));
        } catch (NumberFormatException e) {
            LOG.error("Error parsing nowFunction: " + nowFunc);
        }

        int curNowFuncInt = Integer.MIN_VALUE;
        if (StringUtils.equals(DateUtil.YYYY_MM_DD, timeFormat)) {
            //时间主键粒度到日时，返回 {now('D',-x,'yyyy-MM-dd')}形式的相对时间表达式
            if (NowFunction.DAY_GRANUALITY == nowFuncType) {
                curNowFuncInt = -prevNowFuncInt - relNowDiffDays;
            } else if (NowFunction.WEEK_GRANUALITY == nowFuncType) {
                //当前只支持提前一周
                curNowFuncInt = -relPointDayOfWeek - relNowDiffDays - 7 + 1;
            } else if (NowFunction.MONTH_GRANUALITY == nowFuncType) {
                //当前只支持提前一个月
                curNowFuncInt = -relPointDayOfMonth - relNowDiffDays - prevMonthDays + 1;
            }
            result = "{now('D'," + curNowFuncInt + ",'" + timeFormat + "')}";
        } else if (StringUtils.equals(DateUtil.YYYY_MM, timeFormat)) {
            //时间主键粒度为月时，返回 {now('M',-x,'yyyy-MM'0}形式的相对时间表达式
            curNowFuncInt = -prevNowFuncInt - relNowDiffMonths;
            result = "{now('M'," + curNowFuncInt + ",'" + timeFormat + "')}";
        } else if (StringUtils.equals("yyyy-WW", timeFormat)) {
            curNowFuncInt = -prevNowFuncInt - relNowDiffWeeks;
//            result = "{now('W'," + curNowFuncInt + ",'" + timeFormat + "')}";
            /*周表中选定>=最近一周时生效*/
            result = "{now('W'," + curNowFuncInt + ",'" + "yyyy-ww" + "')}";
        }
        return result;
    }

    private String getRelativeBeginningStr(String nowFunc, String relTimeStr, String timeFormat) {
        //将不同的时间粒度都转化成"日"粒度的
        String result = "";
        final int nowFuncType = NowFunction.getGranuality(nowFunc);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);

        Date relPointDate = null;
        try {
            relPointDate = simpleDateFormat.parse(relTimeStr);
        } catch (ParseException e) {
            LOG.error("timeValue parsing error: currently parsing " + relTimeStr + e);
        }
        DateTime relPointDateTime = new DateTime(relPointDate);
        Date now = new Date();
        DateTime nowDateTime = new DateTime(now);

        final int relPointDayOfWeek = relPointDateTime.getDayOfWeek();
        final int relPointDayOfMonth = relPointDateTime.getDayOfMonth();
        final int relNowDiffDays = Math.abs(Days.daysBetween(nowDateTime, relPointDateTime).getDays());
        final int relNowDiffMonths = Math.abs(Months.monthsBetween(nowDateTime, relPointDateTime).getMonths());
        final int relNowDiffWeeks = Math.abs(Weeks.weeksBetween(nowDateTime, relPointDateTime).getWeeks());
        DateTime prevMonth = nowDateTime.plusMonths(-1);
//        final int prevMonthDays = Math.abs(Days.daysBetween(prevMonth, nowDateTime).getDays());

        Integer prevNowFuncInt = Integer.MIN_VALUE;
        try {

            final String[] relativeTimeValueTokens = nowFunc.split(",");
            prevNowFuncInt = Math.abs(Integer.parseInt(relativeTimeValueTokens[1]));
        } catch (NumberFormatException e) {
            LOG.error("Error parsing nowFunction: " + nowFunc);
        }

        int curNowFuncInt = Integer.MIN_VALUE;
        if (StringUtils.equals(DateUtil.YYYY_MM_DD, timeFormat)) {
            //时间主键粒度到日时，返回 {now('D',-x,'yyyy-MM-dd')}形式的相对时间表达式
            if (NowFunction.DAY_GRANUALITY == nowFuncType) {
                curNowFuncInt = -relNowDiffDays;
            } else if (NowFunction.WEEK_GRANUALITY == nowFuncType) {
                //当前只支持提前一周
                curNowFuncInt = -relPointDayOfWeek - relNowDiffDays + 1;
            } else if (NowFunction.MONTH_GRANUALITY == nowFuncType) {
                //当前只支持提前一个月
                curNowFuncInt = -relPointDayOfMonth - relNowDiffDays + 1;
            }
            result = "{now('D'," + curNowFuncInt + ",'" + timeFormat + "')}";
        } else if (StringUtils.equals(DateUtil.YYYY_MM, timeFormat)) {
            //时间主键粒度为月时，返回 {now('M',-x,'yyyy-MM'0}形式的相对时间表达式
            curNowFuncInt = -prevNowFuncInt - relNowDiffMonths + 1;
            result = "{now('M'," + curNowFuncInt + ",'" + timeFormat + "')}";
        } else if (StringUtils.equals("yyyy-WW", timeFormat)) {
            curNowFuncInt = -relNowDiffWeeks;
//            result = "{now('W'," + curNowFuncInt + ",'" + timeFormat + "')}";
             /*周表中选定>=最近一周时生效*/
            result = "{now('W'," + curNowFuncInt + ",'" + "yyyy-ww" + "')}";
        }

        return result;
    }


//    private List<Date> getTimeIntervalDateList(String dateStr1, String dateStr2, SimpleDateFormat simpleDateFormat) {
//        final String startDateStr = dateStr1;
//        final String endDateStr = dateStr2;
//        Date startDate = null;
//        Date endDate = null;
//
//        if (!StringUtils.isEmpty(startDateStr)) {
//            try {
//                startDate = simpleDateFormat.parse(startDateStr);
//            } catch (ParseException e) {
//                LOG.info("start date parsing exception: " + startDateStr + e);
//            }
//        }
//        if (!StringUtils.isEmpty(endDateStr)) {
//            try {
//                endDate = simpleDateFormat.parse(endDateStr);
//            } catch (ParseException e) {
//                LOG.info("end date parsing exception: " + startDateStr + e);
//            }
//        }
//        List<Date> dateList = new LinkedList<>();
//        if (startDate != null && endDate != null) {
//            if (startDate.after(endDate)) {
//                Date temp = startDate;
//                startDate = endDate;
//                endDate = startDate;
//            }
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(startDate);
//            while (!startDate.after(endDate)) {
//                dateList.add(calendar.getTime());
//                calendar.add(Calendar.DATE, 1);//这里可能还有考虑时间主键为月的情况
//                startDate = calendar.getTime();
//            }
//
//        }
//        return dateList;
//    }
//

    /**
     * 获取 configList 中时间主键的 DimensionConfig
     * 如果 configList 中存在时间主键的 DimensionConfig, 则返回这个 DimensionConfig
     * 否则返回 null
     *
     * @param configList
     * @return
     */
    private DimensionConfig getTimePrimaryKeyDimConf(List<DimensionConfig> configList) {
        DimensionConfig timePrimaryKeyDimConfig = null;
        List<DimensionConfig> timePrimaryKeyDimConfList = Collections.EMPTY_LIST;
        final String timePrimaryKeyName = timePrimaryKey.getColumn();
        if (!CollectionUtils.isEmpty(configList)) {
            final List<DimensionConfig> attrList = configList.stream().filter(o -> o instanceof DimensionConfig).map(o -> {
                return (DimensionConfig) o;
            }).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(attrList)) {
                timePrimaryKeyDimConfList = attrList.stream().filter((DimensionConfig dimConf) -> {
                    return StringUtils.equals(timePrimaryKeyName, dimConf.getColumnName());
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(timePrimaryKeyDimConfList)) {
                    timePrimaryKeyDimConfig = timePrimaryKeyDimConfList.get(0);
                }
            }
        }
        return timePrimaryKeyDimConfig;
    }

    //step3
    private void integrateRowEnums() {
        if (!ObjectUtils.isEmpty(viewDashboardDatasetWithRowEnums)) {

            List<ViewDashboardColumn> columnList = viewDashboardDatasetWithRowEnums.getColumnList();
            if (!CollectionUtils.isEmpty(columnList)) {
                final List<ConfigComponent> filters = aggConfig.getFilters();
                final List<ConfigComponent> modifiedFilters = new LinkedList<>();
                modifiedFilters.addAll(filters);
                columnList.stream().forEach(viewDashboardColumn -> {
                    List<ViewDashboardRowEnum> rowEnumPairList = viewDashboardColumn.getValueList();
                    List<String> enumPairList = Collections.EMPTY_LIST;
                    if (!CollectionUtils.isEmpty(rowEnumPairList)) {
                        enumPairList = rowEnumPairList.stream().map(rowEnumPair -> {
                            ValueTupleReq valueTupleReq = new ValueTupleReq();
                            final String value = rowEnumPair.getName();
                            valueTupleReq.setKey(value);
                            valueTupleReq.setValue(value);
                            return JSONObject.toJSONString(valueTupleReq);
                        }).collect(Collectors.toList());
                    }
                    DimensionConfig rowEnumFilter = new DimensionConfig();
                    rowEnumFilter.setColumnName(viewDashboardColumn.getColumnKey());
                    rowEnumFilter.setFilterType("=");
                    rowEnumFilter.setValues(enumPairList);
                    modifiedFilters.add(rowEnumFilter);
                });
                aggConfig.setFilters(modifiedFilters);
            }
        }
    }

    //step4
    public void furtherFormatting() {
        //过滤条件如果是相对时间类型，则将其值换做now表达式本身
        final List<DimensionConfig> rows = aggConfig.getRows();
        final List<DimensionConfig> columns = aggConfig.getColumns();
        final List<ConfigComponent> filters = aggConfig.getFilters();

        Consumer<List> attrListConsumer = (List attrConfigList) -> {
            if (!CollectionUtils.isEmpty(attrConfigList)) {
                attrConfigList.stream().distinct().forEach(attrConf -> {
                    if (attrConf instanceof DimensionConfig) {
                        DimensionConfig dim = (DimensionConfig) attrConf;
                        if (dim.hasRelativeValues()) {
                            List<String> valObjStrList = dim.getValues();
                            List<String> valStrList = valObjStrList.stream().map(valObjStr -> {
                                try {
                                    ValueTupleReq valueTupleReq = JSONObject.parseObject(valObjStr, ValueTupleReq.class);
                                    return valueTupleReq.getValue();
                                } catch (Exception e) {
                                    return "";
                                }
                            }).filter(str -> !StringUtils.isEmpty(str)).collect(Collectors.toList());
                            dim.setValues(valStrList);
                        }
                    }
                });
            }
        };

        attrListConsumer.accept(rows);
        attrListConsumer.accept(columns);
        attrListConsumer.accept(filters);

    }

    public AggConfigBuilder setCfgReq(CfgReq cfgReq) {
        this.cfgReq = cfgReq;
        return this;
    }

    public AggConfigBuilder setViewDashboardDatasetWithRowEnums(ViewDashboardDatasetWithRowEnums viewDashboardDatasetWithRowEnums) {
        this.viewDashboardDatasetWithRowEnums = viewDashboardDatasetWithRowEnums;
        return this;
    }

    public AggConfigBuilder setTimeFilterReq(TimeFilterReq timeFilterReq) {
        this.timeFilterReq = timeFilterReq;
        return this;
    }

    public AggConfigBuilder setTimePrimaryKey(TimePrimaryKey timePrimaryKey) {
        this.timePrimaryKey = timePrimaryKey;
        return this;
    }
}
