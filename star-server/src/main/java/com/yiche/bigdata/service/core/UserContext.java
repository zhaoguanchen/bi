package com.yiche.bigdata.service.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.RoleType;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.generated.FilterComponent;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.pojo.DatasourceItem;
import com.yiche.bigdata.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class UserContext {

    private String contextId;

    private String userId;

    private DomainUserInfo userInfo;

    private Map<String, Role> roleMap = new HashMap<>();

    private Map<String, String> resourcePermissions = new HashMap<>();

    //key数据集Id, 每个数据集只对应一个业务线, value为该数据集的行级权限
    private Map<String, List<FilterComponent>> dataSetRowPermission = new HashMap<>();

    //key为业务线Id, value为该业务下的数据源权限
    private Map<String, List<DatasourceItem>> dataSourcePermission = new HashMap<>();

    //key为业务线Id, value为该业务下的菜单权限
    private Map<String, UserOptPermission> optPermissions = new HashMap<>();

    private BaseTree resTree;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCenter.class);

    public UserContext(String userId){
        this.contextId = CommonUtils.getUUID();
        this.userId = userId;
        resourcePermissions.put("0", null);
        optPermissions.put("0", null);
    }

    public String getUserId() {
        return userId;
    }

    public DomainUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(DomainUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Map<String, Role> getRoleMap() {
        return roleMap;
    }

    public BaseTree getResTree() {
        return resTree;
    }

    public void setResTree(BaseTree resTree) {
        this.resTree = resTree;
    }

    public Map<String, String> getResourcePermissions() {
        return resourcePermissions;
    }

    public Map<String, UserOptPermission> getOptPermissions() {
        return optPermissions;
    }

    public BaseTree getBusinessLineUserOptTree(String businessLine){
        if(this.getOptPermissions().get("0") != null){
            return  getOptPermissions().get("0").getOptTree();
        }else {
            if(getOptPermissions().get(businessLine) == null){
                return null;
            }
            return getOptPermissions().get(businessLine).getOptTree();
        }
    }

    public BaseNode getDashboard(String businessLine){
        BaseNode businessLineNode = getResTree().getNodeMapping().get(businessLine);
        BaseNode lastNode = null;
        for (BaseNode children: businessLineNode.getChildrenNodes()) {
            if(children.getType().equals(ResourceType.DASHBOARD)){
                if(lastNode == null || children.getCreateTime().after(lastNode.getCreateTime())){
                    lastNode = children;
                }
            }
        }
        return lastNode;
    }

    public BaseNode getResourceNode(String resId){
        return this.getResTree().getNodeMapping().get(resId);
    }

    public List<String> getBusinessLineRoleIds(String businessLine){
        List<String> roleIdList = new ArrayList<>();
        for(String key : roleMap.keySet()) {
            Role role = roleMap.get(key);
            if(role != null && role.getBusinessLine().equals(businessLine)){
                roleIdList.add(role.getId());
            }
        }
        return roleIdList;
    }

    public RoleType getMaxRole(String businessLine){
        boolean isBusinessAdmin = false;
        for(String key : roleMap.keySet()) {
            Role role = roleMap.get(key);
            if(role == null){
                return RoleType.BUSINESS_TYPE;
            }
            if(role.getType().equals(RoleType.SUPER_ADMIN_TYPE.value())){
                return RoleType.SUPER_ADMIN_TYPE;
            }else if(role.getType().equals(RoleType.BUSINESS_ADMIN_TYPE.value())
                    && role.getBusinessLine().equals(businessLine)){
                isBusinessAdmin = true;
            }
        }
        if (isBusinessAdmin){
            return RoleType.BUSINESS_ADMIN_TYPE;
        }
        return RoleType.BUSINESS_TYPE;
    }

    protected void addRowPermission(FilterComponent rowPermission){
      List<FilterComponent>  rowPermissionList = dataSetRowPermission.get(rowPermission.getResId());
      if(rowPermissionList == null){
          rowPermissionList = new ArrayList<>();
          dataSetRowPermission.put(rowPermission.getResId(), rowPermissionList);
      }
        rowPermissionList.add(rowPermission);
    }

    public List<FilterComponent> getDataSetRowPermission(String resId){
        return dataSetRowPermission.get(resId);
    }

    protected void addDatasourcePermission(String businessLine, DatasourceItem datasourceItem){
        List<DatasourceItem> datasourceItemList = dataSourcePermission.get(businessLine);
        if(datasourceItemList == null){
            datasourceItemList = new ArrayList<>();
            dataSourcePermission.put(businessLine, datasourceItemList);
        }
        datasourceItemList.add(datasourceItem);
    }

    public Map<String, List<DatasourceItem>> getDataSourcePermission() {
        return dataSourcePermission;
    }

}
