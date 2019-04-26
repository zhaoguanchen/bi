package com.yiche.bigdata.service.core;

import com.yiche.bigdata.constants.DataSourceType;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.RoleType;
import com.yiche.bigdata.dao.*;
import com.yiche.bigdata.entity.generated.*;
import com.yiche.bigdata.entity.pojo.DatasourceItem;
import com.yiche.bigdata.service.AuthenticationFeignService;
import com.yiche.bigdata.service.ResourceTreeService;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserContextContainer {

    @Autowired
    private ResourceCenter resourceCenter;

    @Autowired
    private UserRoleRelDao userRoleRelDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AuthenticationFeignService authenticationFeignService;

    @Autowired
    private ResourceTreeService resourceTreeService;

    @Autowired
    private RoleResRelDao roleResRelDao;

    @Autowired
    private BusinessLineUserDao businessLineUserDao;

    @Autowired
    private FilterComponentDao rowPermissionDao;

    @Autowired
    private DatasourcePermissionDao datasourcePermissionDao;

    @Autowired
    private DataTableDao dataTableDao;

    @Value("${star.admin.user:}")
    private String adminUser;

    private static final String ADMIN_KEY = "admin";

    private static final String ROOT_ID = "0";

    private static Map<String, UserContext> userContextContainer = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCenter.class);

    //获得用户上下文对象，查询缓存，禁止在业务代码中修改缓存数据
    public UserContext getUserContext(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        UserContext userContext = userContextContainer.get(token);
        if (userContext == null) {
            long start = System.currentTimeMillis();
            String userId = authenticationFeignService.getLoginUser(token);
            LOGGER.debug("Creating UserContext for user [{}] ...", userId);
            userContext = new UserContext(userId);
            //加载用户静态信息
            loadUserInfo(userContext);
            long time1 = System.currentTimeMillis();
            LOGGER.debug("Load User Info, cost time [{}] ...", time1 - start);

            //加载用户角色权限
            loadRoleAndPermissions(userContext);
            long time2 = System.currentTimeMillis();
            LOGGER.debug("Load User Role And Permissions, cost time [{}] ...", time2 - time1);

            //加载用户个人资源权限
            loadPersonalResPermission(userContext);
            long time3 = System.currentTimeMillis();
            LOGGER.debug("Load User Personal Res Permission, cost time [{}] ...", time3 - time2);

            //加载用户资源树
            loadPermissionTree(userContext);
            long time4 = System.currentTimeMillis();
            LOGGER.debug("load User Permission Tree, cost time [{}] ...", time4 - time3);

            //加载用户行级权限
            loadRowPermission(userContext);
            long time5 = System.currentTimeMillis();
            LOGGER.debug("load User Row Permission, cost time [{}] ...", time5 - time4);

            //加载用Mysql数据源权限
            loadDataSourcePermission(userContext);
            long time6 = System.currentTimeMillis();
            LOGGER.debug("load User Data Source Permission, cost time [{}] ...", time6 - time5);

            long time = System.currentTimeMillis() - start;
            LOGGER.debug("Created UserContext for user [{}] , cost time [{}] ...", userId, time);
            userContextContainer.put(token, userContext);
        }
        return userContext;
    }

    private void loadUserInfo(UserContext userContext) {
        userContext.setUserInfo(resourceCenter.getUser(userContext.getUserId()));
    }

    private void loadRoleAndPermissions(UserContext userContext) {
        List<UserRoleRel> userRoleRelList = userRoleRelDao.listUserRoles(userContext.getUserId());
        Set<String> roleIdList = new LinkedHashSet<>();
        //从配置文件中读取超管用户
        if (StringUtils.isNotEmpty(adminUser)) {
            String[] adminUserArr = adminUser.split(",");
            for (String adminUser : adminUserArr) {
                if (userContext.getUserId().equals(adminUser)) {
                    roleIdList.add(ADMIN_KEY);
                }
            }
        }
        if (!CollectionUtils.isEmpty(userRoleRelList) || !CollectionUtils.isEmpty(roleIdList)) {
            userRoleRelList.stream().forEach(rel -> roleIdList.add(rel.getRoleId()));
            List<Role> roleList = roleDao.listRoles(roleIdList.stream().collect(Collectors.toList()));
            if (roleIdList.contains(ADMIN_KEY)) {
                Role adminRole = roleDao.findRoleById(ADMIN_KEY);
                roleList.add(adminRole);
            }
            if (!CollectionUtils.isEmpty(roleList)) {
                for (Role role : roleList) {
                    userContext.getRoleMap().put(role.getId(), role);
                    //BI管理员
                    if (role.getType() == RoleType.SUPER_ADMIN_TYPE.value()) {
                        userContext.getResourcePermissions().clear();
                        userContext.getResourcePermissions().put(ROOT_ID, ADMIN_KEY);
                        userContext.getOptPermissions().clear();
                        userContext.getOptPermissions().put(ROOT_ID, getAdminOptPermission());
                        return;
                    }
                    //业务线管理员
                    if (role.getType() == RoleType.BUSINESS_ADMIN_TYPE.value()) {
                        List<ResTree> resource = resourceTreeService
                                .listBusinessLineResource(role.getBusinessLine());
                        for (ResTree resTree : resource) {
                            userContext.getResourcePermissions().put(resTree.getId(), ADMIN_KEY);
                        }
                    }
                    loadRolePermissions(userContext, role);
                }
            }
        }
    }

    private void loadRolePermissions(UserContext userContext, Role role) {
        List<RoleResRel> roleResRelList = roleResRelDao.listRoleResource(role.getId());
        for (RoleResRel roleResRel : roleResRelList) {
            String resId = roleResRel.getResId();
            ResTree resNode = resourceTreeService.getNodeById(resId);
            String permissions = roleResRel.getPermissions();
            //操作权限资源
            if (resNode == null) {
                calculateOptPermission(userContext, resId, role.getBusinessLine(), permissions);
            }
            //数据资源资源
            else {
                //加载用户可用业务线
                List<BusinessLineUser> businessLineUsers = businessLineUserDao.listUserBusinessLine(userContext.getUserId());
                if (!CollectionUtils.isEmpty(businessLineUsers)) {
                    for (BusinessLineUser businessLineUser : businessLineUsers) {
                        userContext.getResourcePermissions().put(businessLineUser.getBusinessLine(), null);
                    }
                }
                //计算用户数据资源权限
                calculateResourcePermission(userContext, resNode, permissions);
            }
        }
        //根据模板角色添加操作资源权限
        calculateOptPermission(userContext, role);
    }

    private void calculateOptPermission(UserContext userContext, Role role) {
        Role commonRole = null;
        if (role.getType() == RoleType.BUSINESS_ADMIN_TYPE.value()) {
            commonRole = roleDao.findRoleById("business_line_admin");
        } else if (role.getType() == RoleType.BUSINESS_TYPE.value()) {
            commonRole = roleDao.findRoleById("business_line");
        }
        if (commonRole != null) {
            String businessLine = role.getBusinessLine();
            commonRole.setBusinessLine(businessLine);
            UserOptPermission userOptPermission = userContext.getOptPermissions().get(businessLine);
            if (userOptPermission == null) {
                userOptPermission = new UserOptPermission();
                userOptPermission.setBusinessLine(businessLine);
                userContext.getOptPermissions().put(businessLine, userOptPermission);
            }
            List<RoleResRel> roleResRelList = roleResRelDao.listRoleResource(commonRole.getId());
            for (RoleResRel roleResRel : roleResRelList) {
                String permissions = roleResRel.getPermissions();
                String optPermissions = userOptPermission.getOptPermissions().get(roleResRel.getResId());
                if (StringUtils.isEmpty(optPermissions)) {
                    optPermissions = permissions;
                } else {
                    if (optPermissions.contains(ADMIN_KEY)) {
                        userOptPermission.getOptPermissions().put(roleResRel.getResId(), ADMIN_KEY);
                        return;
                    }
                    for (String permission : permissions.split(",")) {
                        if (!optPermissions.contains(permission)) {
                            optPermissions = optPermissions + "," + permission;
                        }
                    }
                }
                userOptPermission.getOptPermissions().put(roleResRel.getResId(), optPermissions);
            }
        }
    }

    private void calculateOptPermission(UserContext userContext, String resId, String businessLine, String permissions) {

        UserOptPermission userOptPermission = userContext.getOptPermissions().get(businessLine);
        if (userOptPermission == null) {
            userOptPermission = new UserOptPermission();
            userOptPermission.setBusinessLine(businessLine);
            userContext.getOptPermissions().put(businessLine, userOptPermission);
        }
        String optPermissions = userOptPermission.getOptPermissions().get(resId);
        if (ADMIN_KEY.equalsIgnoreCase(optPermissions)) {
            return;
        }
        if (StringUtils.isEmpty(optPermissions)) {
            optPermissions = permissions;
        } else {
            if (optPermissions.contains(ADMIN_KEY)) {
                userOptPermission.getOptPermissions().put(resId, ADMIN_KEY);
                return;
            }
            for (String permission : permissions.split(",")) {
                if (!optPermissions.contains(permission)) {
                    optPermissions = optPermissions + "," + permission;
                }
            }
        }
        userOptPermission.getOptPermissions().put(resId, optPermissions);
    }

    private UserOptPermission getAdminOptPermission() {
        UserOptPermission userOptPermission = new UserOptPermission();
        userOptPermission.setBusinessLine(ROOT_ID);
        userOptPermission.getOptPermissions().put(ROOT_ID, ADMIN_KEY);
        userOptPermission.setOptTree(resourceCenter.getOptTree()
                .filter(resourceCenter.getOptTree().getNodeMapping().keySet(), -1));
        return userOptPermission;
    }


    private void calculateResourcePermission(UserContext userContext, ResTree resNode, String permissions) {
        String resPermissions = userContext.getResourcePermissions().get(resNode.getId());
        //以赋予管理员权限的资源不计算权限
        if (ADMIN_KEY.equalsIgnoreCase(resPermissions)) {
            return;
        }
        if (ResourceType.directoryTypes().contains(resNode.getType().toString())) {
            if (StringUtils.isEmpty(resPermissions)) {
                resPermissions = permissions;
            } else {
                for (String permission : permissions.split(",")) {
                    if (!resPermissions.contains(permission)) {
                        resPermissions = resPermissions + "," + permission;
                    }
                }
            }
            userContext.getResourcePermissions().put(resNode.getId(), resPermissions);
            List<ResTree> children = resourceTreeService.listChildren(resNode.getId());
            for (ResTree child : children) {
                calculateResourcePermission(userContext, child, resPermissions);
            }
        } else {
            //实体资源
            if (StringUtils.isEmpty(resPermissions)) {
                resPermissions = permissions;
            } else {
                if (StringUtils.isEmpty(permissions)) {
                    permissions = "view";
                }
                for (String permission : permissions.split(",")) {
                    if (!resPermissions.contains(permission)) {
                        resPermissions = resPermissions + "," + permission;
                    }
                }
            }
            userContext.getResourcePermissions().put(resNode.getId(), resPermissions);
        }
    }

    private void loadPersonalResPermission(UserContext userContext) {
        List<ResTree> nodes = resourceTreeService.listNodeByCreator(userContext.getUserId());
        Map<Integer, String> permissionMap = new HashMap<>();
        for (ResTree node : nodes) {
            if (StringUtils.isEmpty(node.getBusinesLine())) {
                continue;
            }
            if (userContext.getMaxRole(node.getBusinesLine()).equals(RoleType.SUPER_ADMIN_TYPE)
                    || userContext.getMaxRole(node.getBusinesLine()).equals(RoleType.BUSINESS_ADMIN_TYPE)) {
                continue;
            }
            if (userContext.getResourcePermissions().get(node.getId()) == null) {
                String permissions = permissionMap.get(node.getType());
                if (StringUtils.isEmpty(permissions)) {
                    List<Permission> permissionList = resourceCenter.getResOpt().get(node.getType().toString());
                    if (!CollectionUtils.isEmpty(permissionList)) {
                        StringBuilder permissionString = new StringBuilder();
                        permissionList.stream().forEach(permission -> permissionString.append(permission.getKey()).append(","));
                        permissionString.deleteCharAt(permissionString.length() - 1);
                        permissions = permissionString.toString();
                        permissionMap.put(node.getType(), permissions);
                    }
                }
                if (StringUtils.isNotEmpty(permissions)) {
                    userContext.getResourcePermissions().put(node.getId(), permissions);
                }
            }
        }
    }


    private void loadPermissionTree(UserContext userContext) {
        loadResPermissionTree(userContext);
        loadOptPermissionTree(userContext);
    }

    private void loadResPermissionTree(UserContext userContext) {
        Map<String, String> resourcePermissions = userContext.getResourcePermissions();
        if (ADMIN_KEY.equals(resourcePermissions.get(ROOT_ID))) {
            userContext.setResTree(resourceCenter.getResTree()
                    .filter(resourceCenter.getResTree().getNodeMapping().keySet(), -1));
            return;
        }
        List<String> additionalParentResIds = new ArrayList<>();
        for (String resId : resourcePermissions.keySet()) {
            additionalParentResourcePermissions(additionalParentResIds, resourcePermissions, resId);
        }
        for (String resId : additionalParentResIds) {
            resourcePermissions.put(resId, null);
        }
        userContext.setResTree(resourceCenter.getResTree()
                .filter(userContext.getResourcePermissions().keySet(), -1));
    }

    private void additionalParentResourcePermissions(List<String> additionalParentResIds, Map<String, String> resourcePermissions, String resId) {
        BaseNode node = resourceCenter.getResTree().getNodeMapping().get(resId);
        if (node == null || node.getType().equals(ResourceType.BUSINESS_LINE)
                || ROOT_ID.equals(node.getPid())
                || resourcePermissions.containsKey(node.getPid())
                || additionalParentResIds.contains(node.getPid())) {
            return;
        }
        additionalParentResIds.add(node.getPid());
        additionalParentResourcePermissions(additionalParentResIds, resourcePermissions, node.getPid());
    }

    private void loadOptPermissionTree(UserContext userContext) {
        Map<String, UserOptPermission> userOptPermissionMap = userContext.getOptPermissions();
        //BI管理员菜单资源前面逻辑已添加
        if (userOptPermissionMap.get(ROOT_ID) != null
                && ADMIN_KEY.equals(userOptPermissionMap.get(ROOT_ID).getOptPermissions().get(ROOT_ID))) {
            return;
        }
        for (String businessLine : userOptPermissionMap.keySet()) {
            UserOptPermission userOptPermission = userOptPermissionMap.get(businessLine);
            if (userOptPermission != null) {
                BaseTree optTree = resourceCenter.getOptTree()
                        .filter(userOptPermission.getOptPermissions().keySet(), -1);
                userOptPermission.setOptTree(optTree);
            }
        }
    }

    private void loadRowPermission(UserContext userContext) {
        List<BaseNode> businessLines = userContext.getResTree().getRootNode().getChildrenNodes();
        if (!CollectionUtils.isEmpty(businessLines)) {
            for (BaseNode businessLine : businessLines) {
                RoleType roleType = userContext.getMaxRole(businessLine.getId());
                //管理员用户不计算行级权限
                if (roleType == RoleType.SUPER_ADMIN_TYPE || roleType == RoleType.BUSINESS_ADMIN_TYPE) {
                    continue;
                }
                List<String> busRoleIdList = userContext.getBusinessLineRoleIds(businessLine.getId());
                if (!CollectionUtils.isEmpty(busRoleIdList)) {
                    List<FilterComponent> rowPermissionList = rowPermissionDao.findByRoles(busRoleIdList);
                    if (!CollectionUtils.isEmpty(rowPermissionList)) {
                        for (FilterComponent rowPermission : rowPermissionList) {
                            userContext.addRowPermission(rowPermission);
                        }
                    }
                }
            }
        }
    }

    private void loadDataSourcePermission(UserContext userContext) {
        List<BaseNode> businessLines = userContext.getResTree().getRootNode().getChildrenNodes();
        if (!CollectionUtils.isEmpty(businessLines)) {
            for (BaseNode businessLine : businessLines) {
                List<DatasourceItem> datasourceItemList = resourceCenter.getBusinessLineDataSources().get(businessLine.getId());
                if (datasourceItemList != null) {
                    RoleType roleType = userContext.getMaxRole(businessLine.getId());
                    //管理员用户为业务线的mysql数据源权限
                    if (roleType == RoleType.SUPER_ADMIN_TYPE || roleType == RoleType.BUSINESS_ADMIN_TYPE) {
                        userContext.getDataSourcePermission().put(businessLine.getId(), datasourceItemList);
                    } else {
                        List<DatasourceItem> userDatasourceItemList = new ArrayList<>();
                        for (DatasourceItem datasourceItem : datasourceItemList) {
                            if (datasourceItem.getType().equals(DataSourceType.ES_TYPE.getName())) {
                                if (!CollectionUtils.isEmpty(datasourceItem.getTables())) {
                                    List<BaseNode> childrenNode = businessLine.getChildrenNodes();
                                    List<String> dataTableIdList = new ArrayList<>();
                                    for (BaseNode node : childrenNode) {
                                        if (ResourceType.DATA_TABLE.equals(node.getType())) {
                                            dataTableIdList.add(node.getId());
                                        }
                                    }
                                    if (!CollectionUtils.isEmpty(dataTableIdList)) {
                                        List<String> dataTableNameList = new ArrayList<>();
                                        List<DataTable> dataTables = dataTableDao.findByIds(dataTableIdList);
                                        if (!CollectionUtils.isEmpty(dataTables)) {
                                            for (DataTable dataTable : dataTables) {
                                                dataTableNameList.add(dataTable.getTableName());
                                            }
                                        }
                                        for (Object dataTable : datasourceItem.getTables()) {
                                            if (dataTableNameList.contains(((DataTable) dataTable).getTableName())) {
                                                userDatasourceItemList.add(datasourceItem);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                Set<String> userRoles = userContext.getRoleMap().keySet();
                                DatasourcePermission datasourcePermission
                                        = datasourcePermissionDao.findOne(datasourceItem.getId(), businessLine.getId());
                                if (!CollectionUtils.isEmpty(userRoles)) {
                                    for (String roleId : userRoles) {
                                        if (datasourcePermission != null &&
                                                StringUtils.isNotEmpty(datasourcePermission.getRoleIds()) &&
                                                datasourcePermission.getRoleIds().contains(roleId)) {
                                            userDatasourceItemList.add(datasourceItem);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        userContext.getDataSourcePermission().put(businessLine.getId(), userDatasourceItemList);
                    }
                }

            }
        }
    }

    //每天小时销毁token超时的 user content
    @Scheduled(cron = "${star.user.content.destroy.schedule.cron:0 0 0/1 * * ?}")
    private void userContextDestroy() {
        List<String> removeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userContextContainer)) {
            LOGGER.info("User content container size [{}]", userContextContainer.size());
            for (String token : userContextContainer.keySet()) {
                try {
                    String user = authenticationFeignService.getLoginUser(token);
                    if (StringUtils.isEmpty(user)) {
                        removeList.add(token);
                    }
                } catch (FeignException e) {
                    if (e.status() == 404) {
                        removeList.add(token);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(removeList)) {
                removeList.forEach(key -> userContextContainer.remove(key));
            }
        }
        LOGGER.info("Destroyed [{}] user content ... ", removeList.size());
    }
}
