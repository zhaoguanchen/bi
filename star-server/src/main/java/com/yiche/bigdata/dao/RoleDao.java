package com.yiche.bigdata.dao;

import com.yiche.bigdata.config.Page;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.generated.Role;
import com.yiche.bigdata.entity.generated.RoleExample;
import com.yiche.bigdata.mapper.generated.RoleMapper;
import com.yiche.bigdata.utils.PaginationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
public class RoleDao {

    @Autowired
    private RoleMapper roleMapper;

    public boolean addRole(Role role) {
        return roleMapper.insert(role) > 0;
    }

    public Role findRole(String name, String businessLine) {
        RoleExample example = new RoleExample();
        example.createCriteria().andNameEqualTo(name).andBusinessLineEqualTo(businessLine);
        List<Role> result = roleMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    public List<Role> listRoles(List<String> roleIdList) {
        RoleExample example = new RoleExample();
        example.createCriteria().andIdIn(roleIdList);
        return roleMapper.selectByExample(example);
    }

    public Role findRoleById(String roleId) {
        return roleMapper.selectByPrimaryKey(roleId);
    }

    public boolean deleteRole(String roleId) {
        return roleMapper.deleteByPrimaryKey(roleId) > 0;
    }

    public boolean updateRoleById(Role role) {
        return roleMapper.updateByPrimaryKeySelective(role) > 0;
    }

    public long countUser(Map<String, Object> param) {
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria()
                .andBusinessLineEqualTo((String) param.get("businessLine"));
        String nameSearch = (String) param.get("nameSearch");
        if (org.apache.commons.lang.StringUtils.isNotEmpty(nameSearch)) {
            criteria.andNameLike("%" + nameSearch + "%");
        }
        long count = roleMapper.countByExample(example);
        return count;
    }

    public List<Role> listPagedRole(PagedQueryItem<Map> pagedQueryItem) {
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria()
                .andBusinessLineEqualTo((String) pagedQueryItem.getCondition().get("businessLine"));
        String nameSearch = (String) pagedQueryItem.getCondition().get("nameSearch");
        if (StringUtils.isNotEmpty(nameSearch)) {
            criteria.andNameLike("%" + nameSearch + "%");
        }
        Page page = new Page(PaginationUtil.startValue(pagedQueryItem.getPageNo()
                , pagedQueryItem.getPageSize()), pagedQueryItem.getPageSize());
        example.setPage(page);
        List<Role> roles = roleMapper.selectByExample(example);
        return roles;
    }

    public List<Role> listRole(String businessLine) {
        RoleExample example = new RoleExample();
        example.createCriteria().andBusinessLineEqualTo(businessLine);
        return roleMapper.selectByExample(example);
    }

}
