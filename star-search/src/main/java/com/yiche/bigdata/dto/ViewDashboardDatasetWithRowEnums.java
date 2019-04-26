package com.yiche.bigdata.dto;

import com.yiche.bigdata.pojo.DashBoardDatasetWithRowEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by yangyuchen on 20/12/2017.
 */
public class ViewDashboardDatasetWithRowEnums {

    private String datasetId;
    private String datasetName;
    private List<ViewDashboardColumn> columnList;

    public static Predicate<DashBoardDatasetWithRowEnum> distinctByKey(Function<DashBoardDatasetWithRowEnum, String> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply((DashBoardDatasetWithRowEnum) t));
    }

    public static List<ViewDashboardDatasetWithRowEnums> builtFrom(List<DashBoardDatasetWithRowEnum> dashBoardDatasetWithRowEnumList) {
        List<ViewDashboardDatasetWithRowEnums> resultList = Collections.EMPTY_LIST;

        if (CollectionUtils.isNotEmpty(dashBoardDatasetWithRowEnumList)) {
            final Map<String, List<DashBoardDatasetWithRowEnum>> fullRowEnumIdMap = dashBoardDatasetWithRowEnumList.stream()
                    .collect(Collectors.groupingBy(DashBoardDatasetWithRowEnum.GET_TO_DATASET_COLUMN_ROW_ENUM_FULL_ID_FUNC, Collectors.toList()));

            final Map<String, List<DashBoardDatasetWithRowEnum>> fullColumnIdMap = dashBoardDatasetWithRowEnumList.stream()
                    .collect(Collectors.groupingBy(DashBoardDatasetWithRowEnum.GET_TO_DATASET_COLUMN_FULL_ID_FUNC, Collectors.toList()));

            final Map<String, List<DashBoardDatasetWithRowEnum>> fullDatasetIdMap = dashBoardDatasetWithRowEnumList.stream()
                    .collect(Collectors.groupingBy(DashBoardDatasetWithRowEnum.GET_TO_DATASET_FULL_ID_FUNC, Collectors.toList()));

            //dataset_list
            resultList = dashBoardDatasetWithRowEnumList.stream()
                    .filter(distinctByKey(DashBoardDatasetWithRowEnum.GET_TO_DATASET_FULL_ID_FUNC))
                    .map(dashBoardDatasetWithRowEnum -> {
                        //dataset
                        ViewDashboardDatasetWithRowEnums viewDashboardDatasetWithRowEnums = new ViewDashboardDatasetWithRowEnums();

                        final String fullDatasetId = DashBoardDatasetWithRowEnum.GET_TO_DATASET_FULL_ID_FUNC.apply(dashBoardDatasetWithRowEnum);

                        //column_list
                        final List<ViewDashboardColumn> viewDashboardColumnList = fullDatasetIdMap.get(fullDatasetId).stream()
                                .filter(distinctByKey(DashBoardDatasetWithRowEnum.GET_TO_DATASET_COLUMN_FULL_ID_FUNC))
                                .map(dashBoardDatasetWithRowEnum1 -> {

                                            //column
                                            final String fullColunmId = DashBoardDatasetWithRowEnum.GET_TO_DATASET_COLUMN_FULL_ID_FUNC.apply(dashBoardDatasetWithRowEnum1);
                                            //row_list
                                            List<ViewDashboardRowEnum> viewDashboardRowEnumList =
                                                    fullColumnIdMap.get(fullColunmId)
                                                            .stream()
                                                            .filter(distinctByKey(DashBoardDatasetWithRowEnum.GET_TO_DATASET_COLUMN_ROW_ENUM_FULL_ID_FUNC))
                                                            .map(dashBoardDatasetWithRowEnum2 -> {

                                                                //row
                                                                final String fullRowEnumId = DashBoardDatasetWithRowEnum.GET_TO_DATASET_COLUMN_ROW_ENUM_FULL_ID_FUNC.apply(dashBoardDatasetWithRowEnum2);
                                                                ViewDashboardRowEnum viewDashboardRowEnum = new ViewDashboardRowEnum();

                                                                final List<DashBoardDatasetWithRowEnum> rowInfoList = fullRowEnumIdMap.get(fullRowEnumId);
                                                                if (CollectionUtils.isNotEmpty(rowInfoList)) {
                                                                    DashBoardDatasetWithRowEnum dashBoardDatasetWithRowEnum4 = rowInfoList.get(0);
                                                                    viewDashboardRowEnum.setId(dashBoardDatasetWithRowEnum4.getRowEnumId());
                                                                    viewDashboardRowEnum.setName(dashBoardDatasetWithRowEnum4.getRowEnumName());
                                                                }
                                                                return viewDashboardRowEnum;
                                                            }).collect(Collectors.toList());

                                            ViewDashboardColumn viewDashboardColumn = new ViewDashboardColumn();

                                            final String columnKey = dashBoardDatasetWithRowEnum1.getColumnKey();
                                            final String columnName = dashBoardDatasetWithRowEnum1.getColumnName();
                                            if (StringUtils.isNotEmpty(columnKey) && StringUtils.isNotEmpty(columnName) && CollectionUtils.isNotEmpty(viewDashboardRowEnumList)) {
                                                viewDashboardColumn.setColumnKey(columnKey);
                                                viewDashboardColumn.setColumnName(columnName);
                                                viewDashboardColumn.setValueList(viewDashboardRowEnumList);
                                            }

                                            return viewDashboardColumn;
                                        }
                                ).filter(viewDashboardColumn -> {
                                    return StringUtils.isNotEmpty(viewDashboardColumn.getColumnKey());
                                }).collect(Collectors.toList());

                        if (CollectionUtils.isNotEmpty(viewDashboardColumnList)) {
                            viewDashboardDatasetWithRowEnums.setColumnList(viewDashboardColumnList);
                        } else {
                            viewDashboardDatasetWithRowEnums.setColumnList(Collections.EMPTY_LIST);
                        }

                        viewDashboardDatasetWithRowEnums.setDatasetId(String.valueOf(dashBoardDatasetWithRowEnum.getDatasetId()));
                        viewDashboardDatasetWithRowEnums.setDatasetName(dashBoardDatasetWithRowEnum.getDatasetName());
                        return viewDashboardDatasetWithRowEnums;
                    }).collect(Collectors.toList());
        }

        return resultList;
    }


    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public List<ViewDashboardColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ViewDashboardColumn> columnList) {
        this.columnList = columnList;
    }

}
