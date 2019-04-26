package com.yiche.bigdata.dataprovider;

import com.google.common.collect.Ordering;
import com.yiche.bigdata.dataprovider.annotation.DatasourceParameter;
import com.yiche.bigdata.dataprovider.annotation.QueryParameter;
import com.yiche.bigdata.pojo.MetadataTableInfoOptions;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.reflections.ReflectionUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by yfyuan on 2016/8/15.
 */
public class DataProviderViewManager {

    private static VelocityEngine velocityEngine;

    static {
//        Properties props = new Properties();
//        String fileDir = DataProviderViewManager.class.getResource("/template/config").getPath();
//        try {
//            fileDir = URLDecoder.decode(fileDir, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
//        props.setProperty(velocityEngine.FILE_RESOURCE_LOADER_PATH, fileDir);
//        props.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
//        props.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
//        props.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
//        props.setProperty("log4j.logger.org.apache.velocity", "ERROR");
//        velocityEngine = new VelocityEngine(props);

    }

    private static Map<String, String> rendered = new HashMap<>();

    public static List<Map<String, Object>> getQueryParams(Boolean isAdmin, String type, String page, List<MetadataTableInfoOptions> options) {
        Class clz = DataProviderManager.getDataProviderClass(type);
        Set<Field> fieldSet = ReflectionUtils.getAllFields(clz, ReflectionUtils.withAnnotation(QueryParameter.class));
        List<Field> fieldList = fieldOrdering.sortedCopy(fieldSet);
        List<Map<String, Object>> params = null;
        try {
            Object o = clz.newInstance();
            params = new ArrayList<>();
            for (Field field : fieldList) {
                // 如果是超级管理员，加入index和type两个输入框
                if (!isAdmin && (field.getName().equalsIgnoreCase("index") || field.getName().equalsIgnoreCase("type"))) {
                    continue;
                }
                field.setAccessible(true);

                QueryParameter queryParameter = field.getAnnotation(QueryParameter.class);
                Map<String, Object> param = new HashMap<>();
                param.put("label", queryParameter.label());
                param.put("type", queryParameter.type().toString());
                param.put("name", (String) field.get(o));
                param.put("placeholder", queryParameter.placeholder());
                param.put("value", queryParameter.value());

                // 如果是ES的话，添加查询元数据所有事实表列表
                if (type.equalsIgnoreCase("Elasticsearch")) {
                    param.put("options", options.stream().toArray());
                }else{
                    param.put("options", queryParameter.options());
                }
                param.put("checked", queryParameter.checked());
                param.put("required", queryParameter.required());

                /*
                不同页面显示不同输入框
                 */
                String pageType = queryParameter.pageType();
                if (pageType.contains("all") || StringUtils.isBlank(page)) {
                    params.add(param);
                } else if ("test.html".equals(page) && pageType.contains("test")) {
                    params.add(param);
                } else if ("dataset.html".equals(page) && pageType.contains("dataset")) {
                    params.add(param);
                } else if ("widget.html".equals(page) && pageType.contains("widget")) {
                    params.add(param);
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String getQueryView(Boolean isAdmin, String type, String page,List<MetadataTableInfoOptions> optionList) {
        List<Map<String, Object>> params = getQueryParams(isAdmin, type, page, optionList);
        if (params != null && params.size() > 0) {
//            VelocityContext context = new VelocityContext();
//            context.put("params", params);
//            StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
//            velocityEngine.mergeTemplate("query.vm", "utf-8", context, stringBuilderWriter);
//            return stringBuilderWriter.toString();
            Velocity.init();
            // 创建一个上下文环境，此实例是非线程安全的
            VelocityContext context = new VelocityContext();
            // 用对象填充context
            context.put("params", params);
            // 通过一个InputStreamReader读取模板文件
            Reader reader = new InputStreamReader(DataProviderViewManager.class.getResourceAsStream("/template/config/query.vm"));
            // 创建一个字符串输出流，模板输出的目标
            StringWriter writer = new StringWriter();
            // 根据模板上下文对模板求值，logMsgName字符串为发生异常时候记录模板异常提供方便
            Velocity.evaluate(context, writer, "query.vm", reader);
            return writer.toString();
        }
        return null;
    }

    public static List<Map<String, Object>> getDatasourceParams(String type) {
        Class clz = DataProviderManager.getDataProviderClass(type);
        Set<Field> fieldSet = ReflectionUtils.getAllFields(clz, ReflectionUtils.withAnnotation(DatasourceParameter.class));
        List<Field> fieldList = fieldOrdering.sortedCopy(fieldSet);
        List<Map<String, Object>> params = null;
        try {
            Object o = clz.newInstance();
            params = new ArrayList<>();
            for (Field field : fieldList) {
                field.setAccessible(true);
                DatasourceParameter datasourceParameter = field.getAnnotation(DatasourceParameter.class);
                Map<String, Object> param = new HashMap<>();
                param.put("label", datasourceParameter.label());
                param.put("type", datasourceParameter.type().toString());
                param.put("name", (String) field.get(o));
                param.put("placeholder", datasourceParameter.placeholder());
                param.put("value", datasourceParameter.value());
                param.put("options", datasourceParameter.options());
                param.put("checked", datasourceParameter.checked());
                param.put("required", datasourceParameter.required());
                params.add(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String getDatasourceView(String type) {
        List<Map<String, Object>> params = getDatasourceParams(type);
        if (params != null && params.size() > 0) {
//            VelocityContext context = new VelocityContext();
//            context.put("params", params);
//            StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
//            velocityEngine.mergeTemplate("datasource.vm", "utf-8", context, stringBuilderWriter);
//            return stringBuilderWriter.toString();
            // 初始化Velocity引擎，init对引擎VelocityEngine配置了一组默认的参数
            // VelocityEngine是单例模式，线程安全
            Velocity.init();
            // 创建一个上下文环境，此实例是非线程安全的
            VelocityContext context = new VelocityContext();
            // 用对象填充context
            context.put("params", params);
            // 通过一个InputStreamReader读取模板文件
            Reader reader = new InputStreamReader(DataProviderViewManager.class.getResourceAsStream("/template/config/datasource.vm"));
            // 创建一个字符串输出流，模板输出的目标
            StringWriter writer = new StringWriter();
            // 根据模板上下文对模板求值，logMsgName字符串为发生异常时候记录模板异常提供方便
            Velocity.evaluate(context, writer, "datasource.vm", reader);
            return writer.toString();
        }
        return null;
    }

    private static Ordering<Field> fieldOrdering = Ordering.from(new Comparator<Field>() {
        @Override
        public int compare(Field o1, Field o2) {
            return Integer.compare(getOrder(o1), getOrder(o2));
        }

        private int getOrder(Field field) {
            field.setAccessible(true);
            DatasourceParameter datasourceParameter = field.getAnnotation(DatasourceParameter.class);
            if (datasourceParameter != null) {
                return datasourceParameter.order();
            }
            QueryParameter queryParameter = field.getAnnotation(QueryParameter.class);
            if (queryParameter != null) {
                return queryParameter.order();
            }
            return 0;
        }
    });

}