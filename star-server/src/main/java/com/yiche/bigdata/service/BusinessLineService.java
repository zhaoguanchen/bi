package com.yiche.bigdata.service;

import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.pojo.BusinessLineItem;
import com.yiche.bigdata.entity.pojo.DatasourceItem;

import java.util.List;
import java.util.Map;

public interface BusinessLineService {

    /**
     * 创建业务线
     * @param businessLineItem
     * @return Result.class
     */
    Result addBusinessLine(BusinessLineItem<String> businessLineItem);

    /**
     * 所有业务线列表（无权限控制）
     * @return Result.class
     */
    Result listBusinessLine();

    /**
     * 分页显示所有业务线列表（无权限控制）
     * @param queryItem
     * @return Result.class
     */
    Result listBusinessLinePaged(PagedQueryItem<Map> queryItem);

    /**
     * 通过名称获取业务线
     * @param name
     * @return Result.class
     */
    Result getBusinessLineByName(String name);

    /**
     * 查询业务线详细信息(包括事实表和数据源)
     * @param businessLine
     * @return Result.class
     */
    Result getBusinessLineDetail(String businessLine);


    /**
     * 根据Id更新业务线信息
     * @param businessLineItem
     * @return Result.class
     */
    Result updateBusinessLine(BusinessLineItem businessLineItem);


    /**
     * 查询业务线所有用户的一级部门列表
     * @param businessLine
     * @return Result.class
     */
    Result getAllDepartment(String businessLine);


    /**
     * 根据id删除业务线
     * @param businessLine
     * @return Result.class
     */
    Result deleteBusinessLine(String businessLine);


    /**
     * 强制删除业务线（包含空目录）
     * @param businessLine
     * @return Result.class
     */
    Result deleteBusinessLineForce(String businessLine);


    /**
     * 根据Id查询业务线
     * @param businessLine
     * @return Result.class
     */
    Result getBusinessLineById(String businessLine);


    /**
     * 查询所有数据源
     * @return Result.class
     */
    Result listAllDataSource();


    /**
     * 获取业务线对应的数据源信息
     * @param businessLine
     * @return List<DatasourceItem>
     */
    List<DatasourceItem> getBusinessLineDataSource(String businessLine);

}
