package com.yiche.bigdata.service.impl;

import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.DatasourcePermissionDao;
import com.yiche.bigdata.dao.RoleDao;
import com.yiche.bigdata.dao.RoleResRelDao;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.*;
import com.yiche.bigdata.entity.pojo.BusinessLineItem;
import com.yiche.bigdata.entity.pojo.MetadataTableInfo;
import com.yiche.bigdata.entity.pojo.MetadataTableInfoOptions;
import com.yiche.bigdata.entity.pojo.PermissionPageSaveItem;
import com.yiche.bigdata.entity.vo.*;
import com.yiche.bigdata.service.*;
import com.yiche.bigdata.service.core.ResourceCenter;
import com.yiche.bigdata.service.core.UserContextContainer;
import com.yiche.bigdata.utils.CommonUtils;
import com.yiche.bigdata.utils.ResultUtils;
import com.yiche.bigdata.utils.TokenUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleResRelDao roleResRelDao;

    @Autowired
    private DatasourcePermissionDao datasourcePermissionDao;

    @Autowired
    private ResourceTreeService resourceTreeService;

    @Autowired
    private DataTableService dataTableService;

    @Autowired
    private RowPermissionService rowPermissionService;

    @Autowired
    private BusinessLineService businessLineService;

    @Autowired
    private ResourceCenter resourceCenter;

    @Autowired
    private LogService logService;

    @Autowired
    private UserContextContainer userContextContainer;

    @Override
    public Result listRoleDataResourcePermission(String roleId, String resType) {
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(resType)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }

        //查询业务线下所有权限
        Map<String, PermissionVo> optPermissions = new LinkedHashMap();
        List<TreeNodePermissionVo> resourcePermissionVoList = new ArrayList<>();
        String businessLine = role.getBusinessLine();
        Map<String, RoleResRel> roleResRelMap = new HashMap();

        List<Integer> types = new ArrayList<>();
        for (String typeStr : resType.split(",")) {
            types.add(Integer.parseInt(typeStr));

            //实体资源需要查找文件夹权限
            if (!ResourceType.directoryTypes().contains(typeStr)) {
                Integer directoryType = ResourceType.getDirectoryType(Integer.parseInt(typeStr));
                if (!types.contains(directoryType)) {
                    types.add(directoryType);
                }
            }
        }

        List<RoleResRel> roleResRelList = roleResRelDao.listRoleResource(roleId, types);
        if (!CollectionUtils.isEmpty(roleResRelList)) {
            roleResRelList.stream().forEach(roleResRel -> roleResRelMap.put(roleResRel.getResId(), roleResRel));
        }

        for (String typeStr : resType.split(",")) {
            Integer type = Integer.parseInt(typeStr);
            List<ResTree> resources;
            if (!ResourceType.directoryTypes().contains(type.toString())) {
                calculateDataOptPermission(optPermissions, role, type);
                resources = resourceTreeService.
                        listChildrenNodesByType(ResourceType.getDirectoryType(type), businessLine);
            } else {
                resources = resourceTreeService.
                        listChildrenNodesByType(type, businessLine);
            }
            for (ResTree resTree : resources) {
                if (ResourceType.directoryTypes().contains(resTree.getType().toString())) {
                    TreeNodePermissionVo treeNodePermissionVo = calculateResTreeNodePermission(resTree, roleResRelMap, resType, null);
                    resourcePermissionVoList.add(treeNodePermissionVo);
                }
            }
        }

        List<PermissionVo> optPermissionList = new ArrayList<>();
        optPermissions.values().stream().forEach(permissionVo -> optPermissionList.add(permissionVo));

        //构造返回对象 DataResPermissionVo
        DataResPermissionVo dataResPermissionVo = new DataResPermissionVo();
        dataResPermissionVo.setOptPermission(optPermissionList);
        dataResPermissionVo.setDataPermission(resourcePermissionVoList);
        return ResultUtils.buildResult(ResultCode.OK, dataResPermissionVo);
    }

    private TreeNodePermissionVo calculateResTreeNodePermission(ResTree resTree, Map<String, RoleResRel> roleResRelMap, String targetType, RoleResRel parentRel) {
        RoleResRel roleResRel = roleResRelMap.get(resTree.getId());
        TreeNodePermissionVo treeNodePermissionVo = new TreeNodePermissionVo();
        BeanUtils.copyProperties(resTree, treeNodePermissionVo);
        if (ResourceType.directoryTypes().contains(resTree.getType().toString())) {
            if (roleResRel != null) {
                treeNodePermissionVo.setRecursion(true);
                if (parentRel == null) {
                    parentRel = roleResRel;
                }
            }
            List<ResTree> resources = resourceTreeService.listChildren(resTree.getId());
            if (!CollectionUtils.isEmpty(resources) && !ResourceType.directoryTypes().contains(targetType)) {
                List<TreeNodePermissionVo> list = new ArrayList<>();
                for (ResTree treeNode : resources) {
                    list.add(calculateResTreeNodePermission(treeNode, roleResRelMap, targetType, parentRel));
                }
                treeNodePermissionVo.setChildren(list);
            }
        }
        if (resTree.getType().toString().equals(targetType)) {
            List<PermissionVo> permissionVos = getPermissionVoList(resTree, roleResRel, parentRel);
            treeNodePermissionVo.setPermissions(rebuildPermissionVoList(permissionVos));
        }
        return treeNodePermissionVo;
    }


    private List<PermissionVo> getPermissionVoList(ResTree resTree, RoleResRel roleResRel, RoleResRel parentRel) {
        List<Permission> permissionList = resourceCenter.getResOpt().get(Integer.toString(resTree.getType()));
        if (permissionList == null) {
            return null;
        }
        List<PermissionVo> permissionVos = new ArrayList<>();
        for (Permission permission : permissionList) {
            //只处理类型1的权限
            if (permission.getType() == 1) {
                PermissionVo permissionVo = new PermissionVo();
                BeanUtils.copyProperties(permission, permissionVo);
                if (parentRel != null && StringUtils.isNotEmpty(parentRel.getPermissions())
                        && parentRel.getPermissions().contains(permission.getKey())) {
                    permissionVo.setChecked(true);
                    if (!ResourceType.directoryTypes().contains(resTree.getType().toString())) {
                        permissionVo.setLocked(true);
                    }
                } else if (roleResRel != null && roleResRel.getPermissions().contains(permission.getKey())) {
                    permissionVo.setChecked(true);
                }
                permissionVos.add(permissionVo);
            }
        }

        String roleId = null;
        if (parentRel != null) {
            roleId = parentRel.getRoleId();
        } else if (roleResRel != null) {
            roleId = roleResRel.getRoleId();
        }
        getRowPermission(resTree, roleId, permissionVos);

        return permissionVos;
    }

    private void getRowPermission(ResTree resTree, String roleId, List<PermissionVo> permissionVos) {
        if (resTree.getType() == ResourceType.DATA_SET.value()) {
            List<FilterComponent> rowPermissionList = rowPermissionService
                    .findRowPermission(roleId, resTree.getId());
            PermissionVo permissionVo = new PermissionVo();
            permissionVo.setKey("row_permission");
            permissionVo.setName("行级权限");
            if (!CollectionUtils.isEmpty(rowPermissionList)) {
                permissionVo.setChecked(true);
            } else {
                permissionVo.setChecked(false);
            }
            permissionVos.add(permissionVo);
        }
    }


    private void calculateDataOptPermission(Map<String, PermissionVo> permissionVos, Role role, Integer type) {
        RoleResRel roleResRel = roleResRelDao.getRoleRes(role.getId(), role.getBusinessLine());
        List<Permission> permissionList = resourceCenter.getResOpt().get(Integer.toString(type));
        if (permissionList == null) {
            return;
        }
        for (Permission permission : permissionList) {
            PermissionVo permissionVo = new PermissionVo();
            BeanUtils.copyProperties(permission, permissionVo);
            if (permissionVos.get(permissionVo.getKey()) == null) {
                //2类型的权限为挂在业务线的权限，适用于所有资源
                if (permission.getType() == 2) {
                    permissionVos.put(permission.getKey(), permissionVo);
                }
            }
            if (roleResRel == null) {
                continue;
            }
            if (roleResRel.getPermissions().contains(permission.getKey())) {
                permissionVo.setChecked(true);
                permissionVos.put(permission.getKey(), permissionVo);
            }
        }
    }

    private List<PermissionVo> rebuildPermissionVoList(List<PermissionVo> permissionVoList) {
        String mergedKey = "copy,edit,delete";
        String mergedName = "编辑(复制/修改/删除)";
        boolean mergedFlag = false;
        List<PermissionVo> rebuildList = new ArrayList<>();
        for (PermissionVo permissionVo : permissionVoList) {
            if (mergedKey.contains(permissionVo.getKey())) {
                if (!mergedFlag) {
                    permissionVo.setId(-1);
                    permissionVo.setKey(mergedKey);
                    permissionVo.setName(mergedName);
                    rebuildList.add(permissionVo);
                }
                mergedFlag = true;
            } else {
                rebuildList.add(permissionVo);
            }
        }
        return rebuildList;
    }

    @Override
    public Result listRoleDashboardPermission(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }
        List<ResourcePermissionVo> voList = new ArrayList<>();
        List<ResTree> dashboardNode = resourceTreeService
                .listBusinessLineNodeByType(ResourceType.DASHBOARD.value(), role.getBusinessLine());
        if (CollectionUtils.isEmpty(dashboardNode)) {
            return ResultUtils.buildResult(ResultCode.DASHBOARD_NOT_EXIST);
        }
        List<Permission> permissionList = resourceCenter.getResOpt()
                .get(Integer.toString(ResourceType.DASHBOARD.value()));
        if (permissionList == null) {
            return ResultUtils.buildResult(ResultCode.NO_RESULT_FOUND);
        }
        //一个业务线下只有一个大盘
        for (ResTree resTree : dashboardNode) {
            ResourcePermissionVo vo = new ResourcePermissionVo();
            List<PermissionVo> permissionVos = new ArrayList<>();
            BeanUtils.copyProperties(resTree, vo);
            RoleResRel roleResRel = roleResRelDao.getRoleRes(roleId, resTree.getId());
            for (Permission permission : permissionList) {
                PermissionVo permissionVo = new PermissionVo();
                BeanUtils.copyProperties(permission, permissionVo);
                permissionVos.add(permissionVo);
                if (roleResRel != null) {
                    if (StringUtils.isEmpty(roleResRel.getPermissions())) {
                        if ("view".equals(permission.getKey())) {
                            permissionVo.setChecked(true);
                        }
                    } else {
                        if (roleResRel.getPermissions().contains(permission.getKey())) {
                            permissionVo.setChecked(true);
                        }
                    }
                }
                vo.setPermissions(permissionVos);
            }
            voList.add(vo);
            //只取第一条大盘数据
            break;
        }
        return ResultUtils.buildResult(ResultCode.OK, voList);
    }

    @Override
    public Result listRoleDataTablePermission(String roleId, String datasourceId) {
        if (StringUtils.isEmpty(roleId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }
        Result<List<MetadataTableInfoOptions>> searchResult = dataTableService.listBusinessLineDataTables(role.getBusinessLine());
        if (searchResult.getCode() != ResultCode.OK.value()) {
            return searchResult;
        }
        List<MetadataTableInfoOptions> availableTables = searchResult.getResult();
        Map<String, RoleResRel> roleResRelMap = new HashMap();
        List<RoleResRel> roleResRelList = roleResRelDao
                .listRoleResource(roleId, ResourceType.DATA_TABLE.value());
        if (!CollectionUtils.isEmpty(roleResRelList)) {
            roleResRelList.stream().forEach(roleResRel -> roleResRelMap.put(roleResRel.getResId(), roleResRel));
        }
        List<DataTableCategoryPermissionVo> resultVo = new ArrayList<>();
        for (MetadataTableInfoOptions metadataTableInfoOptions : availableTables) {
            DataTableCategoryPermissionVo vo = new DataTableCategoryPermissionVo();
            String dataBaseName = metadataTableInfoOptions.getTitle();
            MetadataTableInfo[] metadataTables = metadataTableInfoOptions.getList();
            if (metadataTables == null) {
                continue;
            }
            List<DataTablePermissionVo> dataTablePermissionVoList = new ArrayList<>();
            for (MetadataTableInfo metadataTableInfo : metadataTables) {
                DataTablePermissionVo dataTablePermissionVo = new DataTablePermissionVo();
                BeanUtils.copyProperties(metadataTableInfo, dataTablePermissionVo);
                RoleResRel roleResRel = roleResRelMap.get(metadataTableInfo.getResId());
                if (roleResRel != null) {
                    dataTablePermissionVo.setChecked(true);
                }
                dataTablePermissionVoList.add(dataTablePermissionVo);
            }
            vo.setTitle(dataBaseName);
            vo.setList(dataTablePermissionVoList);
            resultVo.add(vo);
        }
        return ResultUtils.buildResult(ResultCode.OK, resultVo);
    }

    @Override
    public Result resetResourcePermission(PermissionPageSaveItem permissionPageSaveItem) {
        List<TreeNodePermissionVo> permissionSaveItems = permissionPageSaveItem.getResourcePermissionList();
        if (StringUtils.isEmpty(permissionPageSaveItem.getRoleId()) || StringUtils.isEmpty(permissionPageSaveItem.getResType())) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }

        if (!CommonUtils.checkNumber(permissionPageSaveItem.getResType())) {
            return ResultUtils.buildResult(ResultCode.VALID_ERROR);
        }
        String roleId = permissionPageSaveItem.getRoleId();
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }

        roleResRelDao.deleteRoleResType(role.getId(),
                Integer.parseInt(permissionPageSaveItem.getResType()));

        Set<String> allPermission = new HashSet<>();
        allPermission.addAll(resourceCenter.getPermissions().keySet());
        //校验输入时不去除"copy,edit,delete")
        allPermission.add("copy,edit,delete");
        List<List<String>> permissionLog = new ArrayList<>();
        if (String.valueOf(ResourceType.DATA_SET_DIRECTORY.value()).equalsIgnoreCase(permissionPageSaveItem.getResType())) {
            //数据集文件夹权限
            for (TreeNodePermissionVo permissionSaveItem : permissionSaveItems) {
                String resId = permissionSaveItem.getId();
                ResTree res = resourceTreeService.getNodeById(resId);
                if (res == null) {
                    continue;
                }
                List<String> permissionKeys = new ArrayList<>();
                List<PermissionVo> permissions = permissionSaveItem.getPermissions();
                //新增权限处理
                for (PermissionVo permissionKey : permissions) {
                    if (permissionKey.isChecked()) {
                        permissionKeys.add(permissionKey.getKey());
                    }
                }
                if (updateOptPermission(permissionPageSaveItem, role, res, permissionKeys)) {
                    continue;
                }
                //删除权限时permission为空
                if (CollectionUtils.isEmpty(permissionKeys)) {
                    continue;
                } else {
                    permissionKeys.retainAll(allPermission);
                    if (CollectionUtils.isEmpty(permissionKeys)) {
                        continue;
                    }
                }

                if (roleResRelDao.getRoleRes(roleId, resId) == null) {
                    RoleResRel roleResRel = new RoleResRel();
                    roleResRel.setResId(resId);
                    roleResRel.setRoleId(roleId);
                    roleResRel.setResType(res.getType());
                    roleResRel.setPermissions(StringUtils.join(permissionKeys, ","));
                    roleResRelDao.insert(roleResRel);
                }
                permissionLog.add(permissionKeys);
            }
        } else {
            //数据集权限
            for (TreeNodePermissionVo permissionSaveItem : permissionSaveItems) {
                if (permissionSaveItem.getChildren() == null) {
                    continue;
                }
                List<TreeNodePermissionVo> children = permissionSaveItem.getChildren();
                for (TreeNodePermissionVo item : children) {

                    String resId = item.getId();
                    ResTree res = resourceTreeService.getNodeById(resId);
                    if (res == null) {
                        continue;
                    }
                    List<String> permissionKeys = new ArrayList<>();
                    List<PermissionVo> permissions = item.getPermissions();
                    //新增权限处理
                    for (PermissionVo permissionKey : permissions) {
                        if (permissionKey.isChecked()) {
                            permissionKeys.add(permissionKey.getKey());
                        }
                    }

                    if (updateOptPermission(permissionPageSaveItem, role, res, permissionKeys)) {
                        continue;
                    }

                    //删除权限时permission为空
                    if (CollectionUtils.isEmpty(permissionKeys)) {
                        continue;
                    } else {
                        permissionKeys.retainAll(allPermission);
                        if (CollectionUtils.isEmpty(permissionKeys)) {
                            continue;
                        }
                    }

                    if (roleResRelDao.getRoleRes(roleId, resId) == null) {
                        RoleResRel roleResRel = new RoleResRel();
                        roleResRel.setResId(resId);
                        roleResRel.setRoleId(roleId);
                        roleResRel.setResType(res.getType());
                        roleResRel.setPermissions(StringUtils.join(permissionKeys, ","));
                        roleResRelDao.insert(roleResRel);
                    }
                    permissionLog.add(permissionKeys);
                }


            }
        }


        logService.updateRoleRes(roleId, permissionLog);

        return ResultUtils.buildResult(ResultCode.OK);
    }

    private boolean updateOptPermission(PermissionPageSaveItem permissionPageSaveItem, Role role, ResTree res, List<String> permissions) {
        String resId = res.getId();
        if (res.getType() == ResourceType.BUSINESS_LINE.value()) {
            String permissionKey = null;
            String resType = permissionPageSaveItem.getResType();
            if ("102,103".contains(resType)) {
                permissionKey = "add_report";
            } else if ("104,105".contains(resType)) {
                permissionKey = "add_widget";
            } else if ("300,301".contains(resType)) {
                permissionKey = "add_dataset";
            }
            if (StringUtils.isNotEmpty(permissionKey)) {
                RoleResRel roleResRel = roleResRelDao.getRoleRes(role.getId(), resId);
                if (roleResRel == null) {
                    return false;
                }
                List<String> permissionList = new ArrayList<>();
                if (CollectionUtils.isEmpty(permissions)) {
                    if (StringUtils.isNotEmpty(roleResRel.getPermissions())) {
                        permissionList = CommonUtils.stringToList(roleResRel.getPermissions());
                        permissionList.remove(permissionKey);
                    }
                } else {
                    if (StringUtils.isEmpty(roleResRel.getPermissions())) {
                        permissionList.add(permissionKey);
                    } else {
                        permissionList = CommonUtils.stringToList(roleResRel.getPermissions());
                        if (permissionList.contains(permissionKey)) {
                            return true;
                        }
                        permissionList.add(permissionKey);
                    }
                }
                if (CollectionUtils.isEmpty(permissionList)) {
                    roleResRelDao.deleteRoleRes(role.getId(), resId);
                } else {
                    roleResRelDao.updateRoleResPermissions(role.getId(), resId, CommonUtils.listToString(permissionList));
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public Result listDataSource(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }
        Result searchResult = businessLineService.getBusinessLineDetail(role.getBusinessLine());
        if (searchResult.getCode() != ResultCode.OK.value()) {
            return searchResult;
        }
        return ResultUtils.buildResult(ResultCode.OK, ((BusinessLineItem) searchResult.getResult()).getDatasource());
    }

    @Override
    public Result updateDatasourcePermission(String roleId, String datasourceId, Boolean checked) {
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(datasourceId) || checked == null) {
            return ResultUtils.buildResult(ResultCode.INPUT_EMPTY);
        }
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }
        DatasourcePermission datasourcePermission =
                datasourcePermissionDao.findOne(datasourceId, role.getBusinessLine());
        if (datasourcePermission == null) {
            return ResultUtils.buildResult(ResultCode.MODIFY_NODE_FAILURE);
        }
        String roleIds = datasourcePermission.getRoleIds();
        if (StringUtils.isEmpty(roleIds)) {
            if (checked) {
                roleIds = roleId;
            }
        } else {
            List<String> roleIdList = CommonUtils.stringToList(roleIds);
            if (checked) {
                if (!roleIdList.contains(roleId)) {
                    roleIdList.add(roleId);
                }
            } else {
                if (roleIdList.contains(roleId)) {
                    roleIdList.remove(roleId);
                }
            }
            if (CollectionUtils.isEmpty(roleIdList)) {
                roleIds = "";
            } else {
                roleIds = CommonUtils.listToString(roleIdList);
            }
        }
        String user = userContextContainer.getUserContext(TokenUtils.getToken()).getUserId();
        datasourcePermission.setLastModifier(user);
        datasourcePermission.setLastModifyTime(new Date());
        datasourcePermission.setRoleIds(roleIds);
        if (datasourcePermissionDao.upadate(datasourcePermission)) {
            return ResultUtils.buildResult(ResultCode.OK);
        } else {
            return ResultUtils.buildResult(ResultCode.PERMISSION_SAVE_FAILURE);
        }
    }

    @Override
    public Result getDatasourcePermission(String roleId, String datasourceId) {
        Role role = roleDao.findRoleById(roleId);
        if (role == null) {
            return ResultUtils.buildResult(ResultCode.ROLE_NOT_EXIST);
        }
        DatasourcePermission datasourcePermission =
                datasourcePermissionDao.findOne(datasourceId, role.getBusinessLine());
        PermissionVo permissionVo = new PermissionVo();
        permissionVo.setName("使用");
        permissionVo.setKey("view");
        if (StringUtils.isNotEmpty(datasourcePermission.getRoleIds())
                && datasourcePermission.getRoleIds().contains(roleId)) {
            permissionVo.setChecked(true);
        }
        return ResultUtils.buildResult(ResultCode.OK, permissionVo);
    }
}
