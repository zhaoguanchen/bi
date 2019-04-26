package com.yiche.bigdata.service.core;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yiche.bigdata.constants.ResourceType;
import com.yiche.bigdata.constants.ResultCode;
import com.yiche.bigdata.dao.BusinessLineUserDao;
import com.yiche.bigdata.dao.PermissionDao;
import com.yiche.bigdata.dao.WidgetDao;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.Result;
import com.yiche.bigdata.entity.generated.BusinessLineUser;
import com.yiche.bigdata.entity.generated.Permission;
import com.yiche.bigdata.entity.generated.ResTree;
import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.pojo.DatasourceItem;
import com.yiche.bigdata.service.BusinessLineService;
import com.yiche.bigdata.service.DomainUserFeignService;
import com.yiche.bigdata.service.ResourceTreeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResourceCenter {

    private static BaseTree resTree;

    private static BaseTree optTree;

    private static Map<String, List<DatasourceItem>> businessLineDataSources = new HashMap<>();

    private static Map<String, Permission> permissions = new HashMap<>();

    private static Map<String, List<Permission>> resOpt = new HashMap<>();

    private static Map<String, DomainUserInfo> users = new HashMap<>();

    private static Map<String, Widget> widgets = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCenter.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private OptResourceService optResourceService;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private BusinessLineUserDao businessLineUserDao;

    @Autowired
    private WidgetDao widgetDao;

    @Autowired
    private DomainUserFeignService domainUserFeignService;

    @Autowired
    private BusinessLineService businessLineService;

    @Autowired
    private ResourceTreeService resourceTreeService;

    @Scheduled(cron = "${star.resource.load.schedule.cron:0 0/5 * * * ?}")
    @PostConstruct
    private void loadResTree() {
        LOGGER.debug("Loading Resource Tree ... ");
        long start = System.currentTimeMillis();
        BaseTree tree = new BaseTree();
        BaseNode rootNode = resourceService.loadRootNode(tree);
        if (rootNode != null) {
            resourceService.loadChildNode(tree, rootNode);
            resTree = tree;
        }
        long time = System.currentTimeMillis() - start;
        LOGGER.info("Loaded [{}] Tree Node , cost time [{} ms] ... ", tree.getNodeMapping().size(), time);
    }

    @Scheduled(cron = "${star.resource.load.schedule.cron:0 0/5 * * * ?}")
    @PostConstruct
    private void loadWidgets() {
        LOGGER.debug("Loading Widget ... ");
        long start = System.currentTimeMillis();
        Map<String, Widget> widgetMap = new HashMap<>();
        List<Widget> widgetList = widgetDao.findAll();
        if (!CollectionUtils.isEmpty(widgetList)) {
            for (Widget widget : widgetList) {
                try {
                    JSONObject dataJson = JSONObject.parseObject(widget.getDataJson());
                    String dataSetId = dataJson.getString("datasetId");
                    if (StringUtils.isNotEmpty(dataSetId)) {
                        widget.setDatasetId(dataSetId);
                    }
                    String chartType = dataJson.getJSONObject("config").getString("chart_type");
                    if (StringUtils.isNotEmpty(chartType)) {
                        widget.setChartType(chartType);
                    }
                    widgetMap.put(widget.getResId(), widget);
                } catch (JSONException exception) {
                    LOGGER.info("widget [{} - {}] data json parse failure [{}] ... ", widget.getResId()
                            , widget.getName(), widget.getDataJson());
                }
            }
        }
        if (!CollectionUtils.isEmpty(widgetMap)) {
            widgets = widgetMap;
        }
        long time = System.currentTimeMillis() - start;
        LOGGER.info("Loaded [{}] Widget , cost time [{} ms] ... ", widgets.size(), time);
    }


    @Scheduled(cron = "${star.resource.load.schedule.cron:0 0/5 * * * ?}")
    @PostConstruct
    private void loadOptTree() {
        LOGGER.debug("Loading Operation Resource ... ");
        long start = System.currentTimeMillis();
        BaseTree tree = new BaseTree();
        BaseNode rootNode = optResourceService.loadRootNode(tree);
        if (rootNode != null) {
            optResourceService.loadChildNode(tree, rootNode);
            optTree = tree;
        }
        long time = System.currentTimeMillis() - start;
        LOGGER.info("Loaded [{}] Operation Resource , cost time [{} ms] ... ", tree.getNodeMapping().size(), time);
    }

    @Scheduled(cron = "${star.resource.load.schedule.cron:0 0/5 * * * ?}")
    @PostConstruct
    private void loadPermissions() {
        LOGGER.debug("Loading Permissions ... ");
        List<Permission> permissionList = permissionDao.listAll();
        Map<String, Permission> permissionMap = new HashMap<>();
        Map<String, List<Permission>> resOptMap = new HashMap<>();
        for (Permission permission : permissionList) {
            permissionMap.put(permission.getKey(), permission);
            if (StringUtils.isNotEmpty(permission.getAvailableResource())) {
                for (String resource : permission.getAvailableResource().split(",")) {
                    resOptMap.computeIfAbsent(resource, k -> new ArrayList<>()).add(permission);
                }
            }
        }
        if (!CollectionUtils.isEmpty(permissionMap)) {
            permissions = permissionMap;
        }
        if (!CollectionUtils.isEmpty(resOptMap)) {
            resOpt = resOptMap;
        }
        LOGGER.info("Loaded [{}] Permissions ... ", permissions.size());
    }

    @Scheduled(cron = "${star.resource.load.schedule.cron:0 0/5 * * * ?}")
    @PostConstruct
    private void loadDataSource() {
        LOGGER.debug("Loading business line data Source ... ");
        long start = System.currentTimeMillis();
        List<ResTree> businessLines = resourceTreeService.listAllNodeByType(ResourceType.BUSINESS_LINE.value());
        for (ResTree node : businessLines) {
            businessLineDataSources.put(node.getId(), businessLineService.getBusinessLineDataSource(node.getId()));
        }
        long time = System.currentTimeMillis() - start;
        LOGGER.info("Loading business line data Source , cost time [{} ms] ... ", time);
    }

    @Scheduled(cron = "${star.resource.load.schedule.cron:0 0/30 * * * ?}")
    @PostConstruct
    private void setUsers() {
        LOGGER.debug("Loading Users ... ");
        List<BusinessLineUser> all = businessLineUserDao.listAll();
        Map<String, DomainUserInfo> tempUsers = new HashMap<>();
        for (BusinessLineUser businessLineUser : all) {
            String userId = businessLineUser.getUserName();
            if (tempUsers.get(userId) == null) {
                DomainUserInfo domainUserInfo = searchDomainUserInfo(userId);
                if (domainUserInfo != null) {
                    tempUsers.put(userId, domainUserInfo);
                }
            }
        }
        users = tempUsers;
        LOGGER.info("Loaded [{}] Users ... ", users.size());
    }

    private DomainUserInfo searchDomainUserInfo(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        Result result = domainUserFeignService.getDomainUserInfo(userId);
        if (result != null && result.getResult() != null && result.getCode() == ResultCode.OK.value()) {
            DomainUserInfo domainUserInfo = (DomainUserInfo) result.getResult();
            /*这里需要置空一下id，这里面的id来自于sso查询服务的id，在系统中不需要，如果有的话，会造成后续copy id出现bug*/
            domainUserInfo.setId(null);
            return domainUserInfo;
        }
        return null;
    }

    public BaseTree getResTree() {
        return resTree;
    }

    public Map<String, Widget> getWidgets() {
        return widgets;
    }

    public BaseTree getOptTree() {
        return optTree;
    }

    public Map<String, Permission> getPermissions() {
        return permissions;
    }

    public Map<String, List<Permission>> getResOpt() {
        return resOpt;
    }

    public Map<String, List<DatasourceItem>> getBusinessLineDataSources() {
        return businessLineDataSources;
    }

    public DomainUserInfo getUser(String userId) {
        if (users.get(userId) == null) {
            DomainUserInfo domainUserInfo = searchDomainUserInfo(userId);
            if (domainUserInfo != null) {
                users.put(userId, domainUserInfo);
            } else {
                DomainUserInfo domainUserInfoNotExist = new DomainUserInfo();
                domainUserInfoNotExist.setDomainAccount(userId);
                domainUserInfoNotExist.setRealName(userId);
                users.put(userId, domainUserInfoNotExist);
            }
            return domainUserInfo;
        } else {
            return users.get(userId);
        }
    }

    public DomainUserInfo getUserInfo(String userId) {
        if (users.get(userId) == null) {
            DomainUserInfo domainUserInfo = searchDomainUserInfo(userId);
            if (domainUserInfo != null) {
                users.put(userId, domainUserInfo);
            }
            return domainUserInfo;
        } else {
            return users.get(userId);
        }
    }
}
