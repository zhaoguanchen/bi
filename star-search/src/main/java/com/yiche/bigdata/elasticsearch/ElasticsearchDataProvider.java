package com.yiche.bigdata.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.yiche.bigdata.cache.CacheManager;
import com.yiche.bigdata.cache.HeapCacheManager;
import com.yiche.bigdata.dataprovider.DataProvider;
import com.yiche.bigdata.dataprovider.Initializing;
import com.yiche.bigdata.dataprovider.aggregator.Aggregatable;
import com.yiche.bigdata.dataprovider.annotation.DatasourceParameter;
import com.yiche.bigdata.dataprovider.annotation.ProviderName;
import com.yiche.bigdata.dataprovider.annotation.QueryParameter;
import com.yiche.bigdata.dataprovider.config.*;
import com.yiche.bigdata.dataprovider.result.AggregateResult;
import com.yiche.bigdata.dataprovider.result.ColumnIndex;
import com.yiche.bigdata.dto.EnumTableDto;
import com.yiche.bigdata.dto.PairData;
import com.yiche.bigdata.elasticsearch.query.QueryBuilder;
import com.yiche.bigdata.pojo.FilterPartExpression;
import com.yiche.bigdata.pojo.NodeMappingInfo;
import com.yiche.bigdata.pojo.TableDetail;
import com.yiche.bigdata.services.MetadataService;
import com.yiche.bigdata.util.json.JSONBuilder;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.yiche.bigdata.elasticsearch.aggregation.AggregationBuilder.*;
import static com.yiche.bigdata.elasticsearch.query.QueryBuilder.*;
import static com.yiche.bigdata.util.SqlMethod.coalesce;
import static com.yiche.bigdata.util.json.JSONBuilder.json;

/**
 * Created by yfyuan on 2017/3/17.
 */
@ProviderName(name = "Elasticsearch")
public class ElasticsearchDataProvider extends DataProvider implements Aggregatable, Initializing {

    @Autowired
    private MetadataService metadataService;

    @Value("${es.data.maxLength}")
    private String maxDataLength;

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchDataProvider.class);

    @DatasourceParameter(label = "Elasticsearch Server *", type = DatasourceParameter.Type.Input, value = "localhost:9200", placeholder = "domain:port", required = true, order = 1)
    protected String SERVERIP = "serverIp";

    @QueryParameter(label = "Index (Optional)", type = QueryParameter.Type.Input, required = false, order = 2)
    protected String INDEX = "index";

    @QueryParameter(label = "Type (Optional)", type = QueryParameter.Type.Input, required = false, order = 3)
    protected String TYPE = "type";

    @QueryParameter(label = "事实表 *", type = QueryParameter.Type.Select, required = true, order = 1)
    protected String TABLE = "table";

    @DatasourceParameter(label = "UserName (Optional)", type = DatasourceParameter.Type.Input, order = 4)
    private String USERNAME = "username";

    @DatasourceParameter(label = "Password (Optional)", type = DatasourceParameter.Type.Password, order = 5)
    private String PASSWORD = "password";

    @QueryParameter(label = "Override Aggregations", pageType = "dataset,widget", type = QueryParameter.Type.TextArea, order = 6)
    private String OVERRIDE = "override";

    @DatasourceParameter(label = "Charset (Default: utf-8)", type = DatasourceParameter.Type.Input, order = 7)
    private String CHARSET = "charset";

    private JSONObject overrideAggregations = new JSONObject();

    private static final CacheManager<Map<String, String>> typesCache = new HeapCacheManager<>();

    private static final JSONPath jsonPath_value = JSONPath.compile("$..value");

    private static final List<String> numericTypes = new ArrayList<>();

    private static final Integer NULL_NUMBER = -999;

    static {
        numericTypes.add("long");
        numericTypes.add("integer");
        numericTypes.add("short");
        numericTypes.add("byte");
        numericTypes.add("double");
        numericTypes.add("float");
    }

    @Override
    public boolean doAggregationInDataSource() {
        return true;
    }

    @Override
    public  String [] queryDimVals(String columnName, AggConfig config) throws Exception {
        JSONObject request = new JSONObject();
        request.put("size", 10000);
        request.put("aggregations", getAggregation(columnName, config));

        if (config != null) {
            JSONArray filter = getFilter(config);
            if (filter.size() > 0) {
                request.put("query", buildFilterDSL(config));
            }
        }
        List<EnumTableDto> enumTableDtoList = this.metadataService.getEnumTableInfoByNodeKey(columnName);

        List<PairData> resultList = new ArrayList<>();
        if (enumTableDtoList != null && !enumTableDtoList.isEmpty()) {
            for (int i = 0; i < enumTableDtoList.size(); i++) {
                PairData pairData = new PairData(enumTableDtoList.get(i).getId(), enumTableDtoList.get(i).getName());
                resultList.add(pairData);
            }
        } else {
            JSONObject response = post(getSearchUrl(request), request);
            LOG.info("query es get dimension request:{}, url:{}", request.toString(), getSearchUrl(request).toString());
            LOG.info("query es get dimension aggregations:{}", response.getJSONObject("aggregations"));

            String[] filtered = response.getJSONObject("aggregations").getJSONObject(columnName).getJSONArray("buckets")
                    .stream()
                    .map(e -> ((JSONObject) e).containsKey("key_as_string") ? ((JSONObject) e).getString
                            ("key_as_string") : ((JSONObject) e).getString("key"))
                    .filter(Objects::nonNull)
                    .map(e -> e.replaceAll(NULL_NUMBER.toString(), NULL_STRING))
                    .toArray(String[]::new);
            if (filtered != null && filtered.length > 0) {
                for (int i = 0; i < filtered.length; i++) {
                    PairData pairData = new PairData(filtered[i], filtered[i]);
                    resultList.add(pairData);
                }
            }
        }
        if(resultList.isEmpty()){
            return  null;
        }
        List<String> valueList=new ArrayList<>();
        resultList.forEach(bean ->{
            valueList.add(bean.getValue());
        });
        String[] array = new String[valueList.size()];
        String[] result=valueList.toArray(array);
        return result;
    }

    private JSONArray getFilter(AggConfig config) {
        Stream<DimensionConfig> c = config.getColumns().stream();
        Stream<DimensionConfig> r = config.getRows().stream();
        Stream<ConfigComponent> f = config.getFilters().stream();
        Stream<ConfigComponent> filters = Stream.concat(Stream.concat(c, r), f);
        JSONArray result = new JSONArray();
        filters.map(e -> separateNull(e)).map(e -> configComponentToFilter(e)).filter(e -> e != null).forEach(result::add);
        return result;
    }

    private JSONObject configComponentToFilter(ConfigComponent cc) {
        if (cc instanceof DimensionConfig) {
            return getFilterPart((DimensionConfig) cc);
        } else if (cc instanceof CompositeConfig) {
            CompositeConfig compositeConfig = (CompositeConfig) cc;
            BoolType boolType = BoolType.MUST;
            if ("AND".equalsIgnoreCase(compositeConfig.getType())) {
                boolType = BoolType.MUST;
            } else if ("OR".equalsIgnoreCase(compositeConfig.getType())) {
                boolType = BoolType.SHOULD;
            }
            JSONArray boolArr = new JSONArray();
            compositeConfig.getConfigComponents().stream().map(e -> separateNull(e)).map(e -> configComponentToFilter
                    (e)).forEach(boolArr::add);
            return boolFilter(boolType, boolArr);
        }
        return null;
    }

    private JSONObject getFilterPart(DimensionConfig config) {
        if (config.getValues().size() == 0) {
            return null;
        }
        String fieldName = config.getColumnName();
        String v0 = config.getValues().get(0);
        String v1 = null;
        if (config.getValues().size() == 2) {
            v1 = config.getValues().get(1);
        }
        if (NULL_STRING.equals(v0)) {
            switch (config.getFilterType()) {
                case "=":
                case "≠":
                    return nullQuery(fieldName, "=".equals(config.getFilterType()));
                default:
            }
        }
        switch (config.getFilterType()) {
            case "=":
            case "eq":
                return termsQuery(fieldName, config.getValues());
            case "≠":
            case "ne":
                return getFilterPartEq(BoolType.MUST_NOT, fieldName, config.getValues());
            case ">":
                return rangeQuery(fieldName, v0, null);
            case "<":
                return rangeQuery(fieldName, null, v0);
            case "≥":
                return rangeQuery(fieldName, v0, null, true, true);
            case "≤":
                return rangeQuery(fieldName, null, v0, true, true);
            case "(a,b]":
                return rangeQuery(fieldName, v0, v1, false, true);
            case "[a,b)":
                return rangeQuery(fieldName, v0, v1, true, false);
            case "(a,b)":
                return rangeQuery(fieldName, v0, v1, false, false);
            case "[a,b]":
                return rangeQuery(fieldName, v0, v1, true, true);
            default:
        }
        return null;
    }

    private JSONObject getFilterPartEq(BoolType boolType, String fieldName, List<String> values) {
        JSONArray boolArr = new JSONArray();
        values.stream()
                .map(e -> termQuery(fieldName, e))
                .forEach(boolArr::add);
        return QueryBuilder.boolFilter(boolType, boolArr);
    }

    protected JSONObject post(String url, JSONObject request) throws Exception {
        HttpResponse httpResponse = null;
        String userName = dataSource.get(USERNAME);
        String password = dataSource.get(PASSWORD);
        String chartset = dataSource.get(CHARSET) == null ? "utf-8" : dataSource.get(CHARSET);
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            httpResponse = Request.Post(url).bodyString(request.toString(), ContentType.APPLICATION_JSON).execute()
                    .returnResponse();
        } else {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            HttpPost httpPost = new HttpPost(url);
            StringEntity reqEntity = new StringEntity(request.toString());
            httpPost.setEntity(reqEntity);
            httpResponse = httpClientBuilder.build().execute(httpPost, getHttpContext());
        }

        String response = EntityUtils.toString(httpResponse.getEntity(), chartset);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            return JSONObject.parseObject(response);
        } else {
            throw new Exception(response);
        }
    }

    protected JSONObject get(String url) throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClientBuilder.build().execute(httpget, getHttpContext());
        return JSONObject.parseObject(EntityUtils.toString(response.getEntity(), dataSource.get(CHARSET)));
    }

    private HttpClientContext getHttpContext() {

        HttpClientContext context = HttpClientContext.create();
        String userName = dataSource.get(USERNAME);
        String password = dataSource.get(PASSWORD);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return context;
        }

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(AuthScope.ANY),
                new UsernamePasswordCredentials(userName, password)
        );
        context.setCredentialsProvider(provider);
        AuthCache authCache = new BasicAuthCache();
        context.setAuthCache(authCache);
        return context;
    }

    private JSONObject getOverrideTermsAggregation(String columnName) {
        if (overrideAggregations.containsKey(columnName)) {
            JSONObject override = new JSONObject();
            override.put(columnName, overrideAggregations.getJSONObject(columnName));
            return override;
        }
        return null;
    }

    private JSONObject getAggregation(String columnName, AggConfig config) {
        DimensionConfig d = new DimensionConfig();
        d.setColumnName(columnName);
        return getAggregation(d, config);
    }

    private JSONObject getAggregation(DimensionConfig d, AggConfig config) {
        JSONObject aggregation = null;
        try {
            Map<String, String> types = getTypes();
            JSONObject overrideAgg = getOverrideTermsAggregation(d.getColumnName());

            // Build default aggregation
            LOG.info("getAggregation  types:{}, DimensionConfig:{}", types.toString(), d.toString());
            switch (types.getOrDefault(d.getColumnName(), "default")) {
                case "date":
                    //  aggregation = buildDateHistAggregation(d.getColumnName(), config);
                    //  break;
                    //  修复ES索引字段出现特殊字符JsonPath无法解析的bug
                    aggregation = json(d.getColumnName(), termsAggregationDate(d.getColumnName(), 10000));
                    break;
                default:
                    Object missing = numericTypes.contains(getTypes().get(d.getColumnName())) ? NULL_NUMBER :
                            NULL_STRING;
                    aggregation = json(d.getColumnName(), termsAggregation(d.getColumnName(), 10000, missing));
            }
            // Query Override
            if (overrideAgg != null) {
                aggregation = overrideAgg;
            }
            // Schema Override
            if (StringUtils.isNotEmpty(d.getCustom())) {
                aggregation = json(d.getColumnName(), JSONObject.parseObject(d.getCustom()).get("esBucket"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aggregation;
    }

    private JSONObject buildDateHistAggregation(String columnName, AggConfig config) throws Exception {
        if (config == null) {
            return queryBound(columnName, config);
        }
        String intervalStr = "10m";
        JSONObject queryDsl = buildFilterDSL(config);
        Object object = JSONPath.compile("$.." + columnName.replace(".", "\\.")).eval(queryDsl);
        List<JSONObject> array = (List) object;
        Long lower = array.stream()
                .map(jo -> coalesce(jo.getLong("gt"), jo.getLong("gte")))
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        Long upper = array.stream()
                .map(jo -> coalesce(jo.getLong("lt"), jo.getLong("lte")))
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(System.currentTimeMillis());

        if (lower == null || lower >= upper) {
            return queryBound(columnName, config);
        }
        intervalStr = dateInterval(lower, upper);
        return json(columnName, dateHistAggregation(columnName, intervalStr, 0, lower, upper));
    }

    private JSONObject queryBound(String columnName, AggConfig config) {
        String maxKey = "max_ts";
        String minKey = "min_ts";
        JSONBuilder request = json("size", 0).
                put("aggregations", json().
                        put(minKey, json("min", json("field", columnName))).
                        put(maxKey, json("max", json("field", columnName)))
                );

        if (config != null) {
            JSONArray filter = getFilter(config);
            if (filter.size() > 0) {
                request.put("query", buildFilterDSL(config));
            }
        }

        String intervalStr = "10m";
        try {
            JSONObject response = post(getSearchUrl(request), request);
            long maxTs = coalesce(response.getJSONObject("aggregations").getJSONObject(maxKey).getLong("value"), 0L);
            long minTs = coalesce(response.getJSONObject("aggregations").getJSONObject(minKey).getLong("value"), 0L);
            intervalStr = dateInterval(minTs, maxTs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json(columnName, dateHistAggregation(columnName, intervalStr, 0));
    }

    protected String dateInterval(long minTs, long maxTs) {
        String intervalStr = "1m";
        int buckets = 100;
        long stepTs = (maxTs - minTs) / buckets;
        long minutesOfDuration = Duration.ofMillis(stepTs).toMinutes();
        long secondsOfDuration = Duration.ofMillis(stepTs).toMillis() / 1000;
        if (minutesOfDuration > 0) {
            intervalStr = minutesOfDuration + "m";
        } else if (secondsOfDuration > 0) {
            intervalStr = secondsOfDuration + "s";
        }
        return intervalStr;
    }

    protected String getMappingUrl() {
//        checkIndexAndType();
//        return String.format("http://%s/%s/_mapping/%s", dataSource.get(SERVERIP), query.get(INDEX), query.get(TYPE));
        if (query.get(TYPE) != null && !query.get(TYPE).isEmpty()) {
            return String.format("http://%s/%s/_mapping/%s", dataSource.get(SERVERIP), query.get(INDEX), query.get
                    (TYPE));
        } else {
            return String.format("http://%s/%s/_mapping", dataSource.get(SERVERIP), query.get(INDEX));
        }
    }

    protected String getSearchUrl(JSONObject request) {
//        checkIndexAndType();
//        return String.format("http://%s/%s/%s/_search", dataSource.get(SERVERIP), query.get(INDEX), query.get(TYPE));
        LOG.info("Posting to ElasticSearch: " + String.format("http://%s/%s/_search", dataSource.get(SERVERIP), query.get(INDEX)));
        LOG.info("Query: " + request.toString());
        return String.format("http://%s/%s/_search", dataSource.get(SERVERIP), query.get(INDEX));
    }

//    private void checkIndexAndType(){
//        if ((INDEX.equalsIgnoreCase("index") || TYPE.equalsIgnoreCase("type")) &&
//                this.query.containsKey("index") && this.query.containsKey("type")) {
//            INDEX = this.query.get("index");
//            TYPE = this.query.get("type");
////            TableDetail tableDetail = this.metadataService.getTableDetail(TABLE);
////            if (innerAggregator.getQuery().containsKey("abc")) {
////                String index = tableDetail.getIndex();
////                INDEX = tableDetail.getIndex() + ".*";
////                TYPE = TABLE;
////            }
//        }
//    }

    @Override
    public String[] getColumn() throws Exception {
        typesCache.remove(getKey());
        Map<String, String> types = getTypes();
        return types.keySet().toArray(new String[0]);
    }

    @Override
    public Map<String, String> getColumnsAndType() throws Exception {
        return getTypes();
    }

    @Override
    public Map<String, String> getColumnTypes() throws Exception {
        return getTypes();
    }

    private Map<String, String> getTypes() throws Exception {
        String key = getKey();
        Map<String, String> types = typesCache.get(key);
        if (types == null || CollectionUtils.isEmpty(types)) {
            synchronized (key.intern()) {
                types = typesCache.get(key);
                if (types == null) {
                    JSONObject mapping = get(getMappingUrl());
                    if (!StringUtils.isBlank(query.get(TYPE))) {
                        mapping = mapping.getJSONObject(mapping.keySet().iterator().next()).getJSONObject("mappings")
                                .getJSONObject(query.get(TYPE));
                    } else {
                        JSONObject tempMapping = mapping.getJSONObject(mapping.keySet().iterator().next());
                        Set<String> keySet = tempMapping.getJSONObject("mappings").keySet();
                        for (String tempKey : keySet) {
                            if (tempKey.equalsIgnoreCase("_default_")) {
                                continue;
                            } else {
                                mapping = tempMapping.getJSONObject("mappings")
                                        .getJSONObject(tempKey);
                                break;
                            }
                        }

                    }

                    types = new HashMap<>();
                    getField(types, new DefaultMapEntry(null, mapping), null);
                    if (types != null && !CollectionUtils.isEmpty(types)) {
                        typesCache.put(key, types, 1 * 60 * 60 * 1000);
                    }
                }
            }
        }
        return types;
    }

    @Override
    public AggregateResult queryAggData(AggConfig config) throws Exception {
        LOG.info("queryAggData");
        JSONObject request = getQueryAggDataRequest(config);
        JSONObject response = post(getSearchUrl(request), request);
        Stream<DimensionConfig> dimStream = Stream.concat(config.getColumns().stream(), config.getRows().stream());
        List<ColumnIndex> dimensionList = dimStream.map(ColumnIndex::fromDimensionConfig).collect(Collectors.toList());
        List<ColumnIndex> valueList = config.getValues().stream().map(ColumnIndex::fromValueConfig).collect
                (Collectors.toList());
        List<ColumnIndex> columnList = new ArrayList<>();
        columnList.addAll(dimensionList);
        columnList.addAll(valueList);
        IntStream.range(0, columnList.size()).forEach(j -> columnList.get(j).setIndex(j));
        List<String[]> result = new ArrayList<>();
        JSONObject aggregations = response.getJSONObject("aggregations");
        getAggregationResponse(aggregations, result, null, 0, dimensionList, valueList);
        // limit 10000
        List<String[]> tempResultArray = Lists.newArrayList();
        for (int i = 0; i < result.size(); i++) {
            String[] tempStringArray = new String[result.get(i).length];
            for (int j = 0; j < result.get(i).length; j++) {
                tempStringArray[j] = result.get(i)[j].replaceAll(NULL_NUMBER.toString(), NULL_STRING);
            }
            tempResultArray.add(tempStringArray);
        }
        String[][] _result = tempResultArray.toArray(new String[][]{});

        // extract tableName  for example: /dm_autoreport_app_traffic/dimen/dateTime  result: dm_autoreport_app_traffic
        String tableName = columnList.get(0).getName().split("/")[1].toString();
        if (dimensionList != null && !dimensionList.isEmpty()) {
            List<EnumTableDto> enumTableDtoList = Lists.newArrayList();
            for (ColumnIndex columnIndex : dimensionList) {
                try {
                    List<EnumTableDto> tempEnumTableList = this.metadataService.getEnumTableInfoByNodeKey(columnIndex
                            .getName());
                    if (tempEnumTableList != null) {
                        enumTableDtoList.addAll(tempEnumTableList);
                    }
                } catch (Exception e) {
                    LOG.error("query metadata service error  columnIndex:{}, exception message:{}", columnIndex
                            .toString(), e.getMessage());

                }
            }
            if (!enumTableDtoList.isEmpty()) {
                Map<String, String> enumTableInfoMap = enumTableDtoList.stream().collect(
                        Collectors.toMap(EnumTableDto::getId, EnumTableDto::getName));
                for (int i = 0; i < _result.length; i++) {
                    for (int j = 0; j < _result[i].length; j++) {
                        if (enumTableInfoMap.containsKey(_result[i][j])) {
                            _result[i][j] = enumTableInfoMap.get(_result[i][j]);
                        }
                    }
                }
            }
        }
        TableDetail tableDetail = null;
        try {
            tableDetail = this.metadataService.getTableDetail(tableName);
        } catch (Exception e) {
            LOG.error("query metadata service error getTableDetail tableName :{}, exception message:{}", tableName
                    .toString(), e.getMessage());
        }

        if (tableDetail != null) {
            List<NodeMappingInfo> tableColumnList = tableDetail.getDimenList();
            tableColumnList.addAll(tableDetail.getMetricList());
            Map<String, String> columnKeyValueMap = tableColumnList.stream().collect(
                    Collectors.toMap(NodeMappingInfo::getKey, NodeMappingInfo::getName));
            /*
            for (String[] strings : _result) {
                for (int i : numericIdx) {
                    if (columnKeyValueMap.containsKey(strings[i])) {
                        strings[i] = columnKeyValueMap.get(strings[i]);
                    }
                }
            }
            */
            columnList.stream().filter(columnIndex ->
                    columnKeyValueMap.containsKey(columnIndex.getName())).forEach(columnIndex ->
                    columnIndex.setAlias(columnKeyValueMap.get(columnIndex.getName())));
        } else {
            columnList.stream().map(columnIndex -> {
                columnIndex.setAlias(columnIndex.getName());
                return columnIndex;
            }).collect(Collectors.toList());
        }

        //  计算kpi类型：日、周、月同比/环比
        String kpi_type = "";
        if (config.getRows().size() > 0 && config.getRows().get(0).getFilterType().equals("dateRange")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String[] times = config.getRows().get(0).getValues().get(0).split("/");
            Calendar calendar1 = new GregorianCalendar();
            calendar1.setTime(format.parse(times[0]));
            Calendar calendar2 = new GregorianCalendar();
            calendar2.setTime(format.parse(times[1]));
            calendar2.add(Calendar.DATE, -1);
            Date start = new Date(), end = new Date();
            try {
                start = format.parse(format.format(calendar1.getTime()));
                end = format.parse(format.format(calendar2.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String startTime = format.format(start), endTime = format.format(end);
            long day = (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000);
            if (day == 0l) {
                kpi_type = "day";
            } else if (day == 6 && calendar1.get(Calendar.DAY_OF_WEEK) == 2 && calendar2.get(Calendar.DAY_OF_WEEK) ==
                    1) {
                kpi_type = "week";
            } else if (startTime.split("-")[0].equals(endTime.split("-")[0]) && startTime.
                    split("-")[1].equals
                    (endTime.split("-")[1]) && format.format(calendar1.getTime())
                    .split("-")[2].equals("01") && calendar2.get(Calendar.DATE)
                    == calendar2.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                kpi_type = "month";
            } else if (Integer.parseInt(times[0].split("-")[0]) + 1 == Integer.parseInt(times[1].split("-")[0]) &&
                    times[0].endsWith
                            ("-01-01") && times[1].endsWith("-01-01")) {
                kpi_type = "year";
            }
        }
        return new AggregateResult(columnList, _result, kpi_type);
    }

    //  解析为ElasticSearch的查询语法
    private JSONObject getQueryAggDataRequest(AggConfig config) throws Exception {
        Stream<DimensionConfig> c = config.getColumns().stream();
        Stream<DimensionConfig> r = config.getRows().stream();
        Stream<DimensionConfig> aggregationStream = Stream.concat(c, r);
        List<JSONObject> termAggregations =
                aggregationStream.map(e -> getAggregation(e, config))
                        .collect(Collectors.toList());
        //  组装 range aggregation
        if (config.getRows().size() > 0 && config.getRows().get(0).getFilterType().equals("dateRange")) {
            termAggregations.clear();
            JSONObject dateRange = new JSONObject();
            dateRange.put("field", config.getRows().get(0).getColumnName());
            JSONArray jsonArray = new JSONArray();
            List<String> timeList = config.getRows().get(0).getValues();
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            JSONObject jsonObject3 = new JSONObject();
            if (timeList.size() > 0 && timeList.get(0).split("/").length == 2) {
                jsonObject1.put("from", timeList.get(0).split("/")[0]);
                jsonObject1.put("to", timeList.get(0).split("/")[1]);
                jsonArray.add(0, jsonObject1);
            }

            if (timeList.size() > 1 && timeList.get(1).split("/").length == 2) {
                jsonObject2.put("from", timeList.get(1).split("/")[0]);
                jsonObject2.put("to", timeList.get(1).split("/")[1]);
                jsonArray.add(1, jsonObject2);
            }

            if (timeList.size() > 2 && timeList.get(2).split("/").length == 2) {
                jsonObject3.put("from", timeList.get(2).split("/")[0]);
                jsonObject3.put("to", timeList.get(2).split("/")[1]);
                jsonArray.add(2, jsonObject3);
            }

            dateRange.put("ranges", jsonArray);
            JSONObject dateObject = new JSONObject();
            dateObject.put("date_range", dateRange);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(config.getRows().get(0).getColumnName().toString(), dateObject);
            termAggregations.add(jsonObject);

        }
        JSONObject metricAggregations = getMetricAggregation(config.getValues(), getTypes());
        termAggregations.add(metricAggregations);

        JSONObject request = new JSONObject();
        for (int i = termAggregations.size() - 1; i > 0; i--) {
            JSONObject pre = termAggregations.get(i - 1);
            String key = pre.keySet().iterator().next();
            pre.getJSONObject(key).put("aggregations", termAggregations.get(i));
        }

        request.put("size", 0);
        request.put("query", buildFilterDSL(config));
        request.put("aggregations", termAggregations.get(0));
        return request;
    }

    public JSONObject buildFilterDSL(AggConfig config) {
        JSONObject boolJson = boolFilter(BoolType.FILTER, getFilter(config));

        List<FilterPartExpression> expressions = config.getFilterPartExpressionList();
        JSONArray mustArray = new JSONArray();
        JSONArray mustNotArray = new JSONArray();
        JSONArray shouldArray = new JSONArray();
        if(! CollectionUtils.isEmpty(expressions)){
            for (FilterPartExpression filterPartExpression : expressions) {
                JSONObject jsonObject = buildOneFilter(filterPartExpression);
                if(jsonObject != null){
                    if("and".equalsIgnoreCase(filterPartExpression.getConnector())){
                        if("not_equal, not_in, regular_not_match".contains(filterPartExpression.getOperator())){
                            mustNotArray.add(jsonObject);
                        }else{
                            mustArray.add(jsonObject);
                        }
                    }else if("or".equalsIgnoreCase(filterPartExpression.getConnector())){
                        if("not_equal, not_in, regular_not_match".contains(filterPartExpression.getOperator())){
                            mustNotArray.add(jsonObject);
                        }else{
                            shouldArray.add(jsonObject);
                        }
                    }
                }
            }
        }
        JSONObject jsonObject = boolJson.getJSONObject("bool");
        if(mustArray.size() > 0){
            jsonObject.put(BoolType.MUST.toString(), mustArray);
        }
        if(mustNotArray.size() > 0){
            jsonObject.put(BoolType.MUST_NOT.toString(), mustNotArray);
        }
        if(shouldArray.size() > 0){
            jsonObject.put(BoolType.SHOULD.toString(), shouldArray);
        }
        return boolJson;
    }

    private JSONObject buildOneFilter(FilterPartExpression filterPartExpression){
        List<String> valueList = filterPartExpression.getValues();
        if(CollectionUtils.isEmpty(valueList)){
            return null;
        }
        JSONObject filterJson = null;
        if("date".contains(filterPartExpression.getType())){
            if (valueList.size() == 2) {
                filterJson = rangeQuery(filterPartExpression.getColumn(), valueList.get(0), valueList.get(1));
            }
        }else if("long,integer,short,byte,double,float".contains(filterPartExpression.getType())){
            if("value_equal,value_not_equal".contains(filterPartExpression.getOperator())){
                filterJson = json("match", json(filterPartExpression.getColumn(),
                        filterPartExpression.getValues().get(0)));
            }else{
                filterJson = json("range", json(filterPartExpression.getColumn(),
                        json(filterPartExpression.getType(), filterPartExpression.getValues().get(0))));
            }
        }
        else if("keyword,text".contains(filterPartExpression.getType())){
            if("string_equal,string_not_equal".contains(filterPartExpression.getOperator())){
                filterJson = json("match", json(filterPartExpression.getColumn(),
                        filterPartExpression.getValues().get(0)));
            }else if("string_regular_match,string_regular_not_match".contains(filterPartExpression.getOperator())){
                filterJson = json("regexp", json(filterPartExpression.getColumn(),
                        filterPartExpression.getValues().get(0)));
            }else if("string_contain,string_not_contain".contains(filterPartExpression.getOperator())){
                filterJson = json("wildcard", json(filterPartExpression.getColumn(),
                        "*" + filterPartExpression.getValues().get(0)) + "*" );
            }
        }
        return filterJson;
    }

    private void getAggregationResponse(JSONObject object, List<String[]> result, List<String> parentKeys,
                                        int dimensionLevel, List<ColumnIndex> dimensionList, List<ColumnIndex> valueList) {
        List<String> keys = new ArrayList<>();
        if (parentKeys != null) {
            keys.addAll(parentKeys);
        }
        if (dimensionLevel > 0) {
            keys.add(object.getOrDefault("key_as_string", object.getString("key")).toString());
        }
        if (dimensionLevel >= dimensionList.size()) {
            for (ColumnIndex value : valueList) {
                String valueKey = getAggregationName(value.getAggType(), value.getName());
                JSONObject valueObject = object.getJSONObject(valueKey);
                List<Object> values = (List<Object>) jsonPath_value.eval(valueObject);
                keys.add("" + values.get(0));
            }
            result.add(keys.toArray(new String[keys.size()]));
        } else {
            JSONArray buckets = object.getJSONObject(dimensionList.get(dimensionLevel).getName())
                    .getJSONArray("buckets");

            for (int i = 0; i < buckets.size() && i < Integer.parseInt(maxDataLength); i++) {
                int nextLevel = dimensionLevel + 1;
                getAggregationResponse((JSONObject) buckets.get(i), result, keys, nextLevel, dimensionList, valueList);
            }
            /*
            for (Object _bucket : buckets) {
                int nextLevel = dimensionLevel + 1;
                getAggregationResponse((JSONObject) _bucket, result, keys, nextLevel, dimensionList, valueList);
            }
            */
        }
    }

    private String getAggregationName(String aggregationType, String columnName) {
        return Hashing.crc32().newHasher().putString(aggregationType + columnName, Charsets.UTF_8).hash().toString();
    }

    private JSONObject getMetricAggregation(List<ValueConfig> configList, Map<String, String> typesCache) {
        JSONObject aggregation = new JSONObject();
        configList.stream().forEach(config -> {
            String aggregationName = getAggregationName(config.getAggType(), config.getColumn());
            String type;
            switch (config.getAggType()) {
                case "sum":
                    type = "sum";
                    break;
                case "avg":
                    type = "avg";
                    break;
                case "max":
                    type = "max";
                    break;
                case "min":
                    type = "min";
                    break;
                case "distinct":
                    type = "cardinality";
                    break;
                default:
                    type = "value_count";
                    break;
            }
            aggregation.put(aggregationName, new JSONObject());
//            if (typesCache.containsKey(config.getColumn())) {
                aggregation.getJSONObject(aggregationName).put(type, new JSONObject());
                aggregation.getJSONObject(aggregationName).getJSONObject(type).put("field", config.getColumn());
//            } else {
//                JSONObject extend = JSONObject.parseObject(config.getColumn());
//                String column = extend.getString("column");
//                JSONObject filter = extend.getJSONObject("filter");
//                JSONObject script = extend.getJSONObject("script");
//                JSONObject _aggregations = aggregation.getJSONObject(aggregationName);
//                if (filter != null) {
//                    _aggregations.put("filter", filter);
//                    _aggregations.put("aggregations", new JSONObject());
//                    _aggregations.getJSONObject("aggregations").put("agg_value", new JSONObject());
//                    _aggregations = _aggregations.getJSONObject("aggregations").getJSONObject("agg_value");
//                }
//                _aggregations.put(type, new JSONObject());
//                _aggregations = _aggregations.getJSONObject(type);
//                if (script != null) {
//                    _aggregations.put("script", script);
//                } else {
//                    _aggregations.put("field", column);
//                }
//            }
        });
        return aggregation;
    }

    @Override
    public String[][] getData() throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpGet httpget = new HttpGet(getMappingUrl());
        HttpResponse httpResponse = httpClientBuilder.build().execute(httpget, getHttpContext());
        String response = EntityUtils.toString(httpResponse.getEntity(), dataSource.get(CHARSET));
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            return new String[0][];
        } else {
            throw new Exception(response);
        }
    }

    private String getKey() {
        return Hashing.md5().newHasher().putString(JSONObject.toJSON(dataSource).toString() + JSONObject.toJSON
                (query).toString(), Charsets.UTF_8).hash().toString();
    }

    private void getField(Map<String, String> types, Map.Entry<String, Object> field, String parent) {
        JSONObject property = (JSONObject) field.getValue();
        if (property.keySet().contains("properties")) {
            for (Map.Entry e : property.getJSONObject("properties").entrySet()) {
                String key = field.getKey();
                if (parent != null) {
                    key = parent + "." + field.getKey();
                }
                getField(types, e, key);
            }
        } else {
            String key = null;
            String type = property.getString("type");
            if (parent == null) {
                key = field.getKey();
            } else {
                key = parent + "." + field.getKey();
            }
            if (isTextWithoutKeywordField(property)) {
                return;
            }
            if (isTextWithKeywordField(property)) {
                key += ".keyword";
            }
            if (key != null && type != null) {
                types.put(key, type);
            }
        }
    }

    private boolean isTextWithKeywordField(JSONObject property) {
        String type = property.getString("type");
        return "text".equals(type) && JSONPath.containsValue(property, "$.fields..type", "keyword");
    }

    private boolean isTextWithoutKeywordField(JSONObject property) {
        String type = property.getString("type");
        return "text".equals(type) && !JSONPath.containsValue(property, "$.fields..type", "keyword");
    }

    @Override
    public String viewAggDataQuery(AggConfig ac) throws Exception {
        String format = "curl -XPOST '%s?pretty' -d '\n%s'";
        JSONObject request = getQueryAggDataRequest(ac);
        String dsl = JSON.toJSONString(request, true);
        return String.format(format, getSearchUrl(request), dsl);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(query.get(OVERRIDE))) {
            overrideAggregations = JSONObject.parseObject(query.get(OVERRIDE));
        }
    }

}
