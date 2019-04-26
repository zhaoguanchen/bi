package com.yiche.bigdata.dao;

import com.yiche.bigdata.config.Page;
import com.yiche.bigdata.entity.dto.DomainUserInfo;
import com.yiche.bigdata.entity.dto.PagedQueryItem;
import com.yiche.bigdata.entity.generated.BusinessLineUser;
import com.yiche.bigdata.entity.generated.BusinessLineUserExample;
import com.yiche.bigdata.mapper.BusinessLineMapper;
import com.yiche.bigdata.mapper.generated.BusinessLineUserMapper;
import com.yiche.bigdata.utils.PaginationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BusinessLineUserDao {

    @Autowired
    private BusinessLineUserMapper businessLineUserMapper;

    @Autowired
    private BusinessLineMapper businessLineMapper;

    public boolean addUser(BusinessLineUser businessLineUser) {
        int result = businessLineUserMapper.insert(businessLineUser);
        return result > 0;
    }

    public BusinessLineUser findUser(String userName, String businessLine) {
        BusinessLineUserExample example = new BusinessLineUserExample();
        example.createCriteria().andBusinessLineEqualTo(businessLine)
                .andUserNameEqualTo(userName);
        List<BusinessLineUser> users = businessLineUserMapper.selectByExample(example);
        if (users == null || users.size() == 0) {
            return null;
        } else {
            return users.get(0);
        }
    }

    public boolean deleteUser(String userName, String businessLine) {
        BusinessLineUserExample example = new BusinessLineUserExample();
        example.createCriteria().andBusinessLineEqualTo(businessLine)
                .andUserNameEqualTo(userName);
        return businessLineUserMapper.deleteByExample(example) > 0;
    }

    public List<BusinessLineUser> listAll() {
        BusinessLineUserExample example = new BusinessLineUserExample();
        example.createCriteria();
        List<BusinessLineUser> users = businessLineUserMapper.selectByExample(example);
        return users;
    }

    public List<BusinessLineUser> listUserBusinessLine(String userId) {
        BusinessLineUserExample example = new BusinessLineUserExample();
        example.createCriteria().andUserNameEqualTo(userId);
        List<BusinessLineUser> users = businessLineUserMapper.selectByExample(example);
        return users;
    }

    public List<BusinessLineUser> listPagedUser(PagedQueryItem<Map> pagedQueryItem) {
        Map<String, Object> param = pagedQueryItem.getCondition();
        BusinessLineUserExample example = getBusinessLineUserExample(param);
        Page page = new Page(PaginationUtil.startValue(pagedQueryItem.getPageNo()
                , pagedQueryItem.getPageSize()), pagedQueryItem.getPageSize());
        example.setPage(page);
        List<BusinessLineUser> users = businessLineUserMapper.selectByExample(example);
        return users;
    }

    public long countUser(Map<String, Object> param) {
        BusinessLineUserExample example = getBusinessLineUserExample(param);
        long count = businessLineUserMapper.countByExample(example);
        return count;
    }

    private BusinessLineUserExample getBusinessLineUserExample(Map<String, Object> param) {
        BusinessLineUserExample example = new BusinessLineUserExample();
        BusinessLineUserExample.Criteria criteria = example.createCriteria()
                .andBusinessLineEqualTo((String) param.get("businessLine"));
        BusinessLineUserExample.Criteria criteriaOr = example.or()
                .andBusinessLineEqualTo((String) param.get("businessLine"));

        String nameSearch = (String) param.get("nameSearch");
        if (StringUtils.isNotEmpty(nameSearch)) {
            criteria.andRealNameLike("%" + nameSearch + "%");
            criteriaOr.andUserNameLike(("%" + nameSearch + "%"));
        }
        String department = (String) param.get("department");
        if (StringUtils.isNotEmpty(department) && !department.contains("不限")) {
            criteria.andDepartmentLike("%" + department + "%");
            criteriaOr.andDepartmentLike("%" + department + "%");
        }
        return example;
    }

    public List<String> getAllDepartment(String businessLine) {
        return businessLineMapper.getAllDepartment(businessLine);
    }

}
