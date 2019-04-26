package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.Widget;
import com.yiche.bigdata.entity.generated.WidgetExample;
import com.yiche.bigdata.mapper.generated.WidgetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WidgetDao {

    @Autowired
    WidgetMapper widgetMapper;

    public Widget getWidgetByResId(String resId) {
        return widgetMapper.selectByPrimaryKey(resId);
    }

    public boolean addWidget(Widget widget) {
        int result = widgetMapper.insert(widget);
        return result > 0;
    }

    public boolean updateWidget(Widget widget) {
        int result = widgetMapper.updateByPrimaryKeyWithBLOBs(widget);
        return result > 0;
    }

    public boolean deleteWidget(String resId) {
        int result = widgetMapper.deleteByPrimaryKey(resId);
        return result > 0;
    }

    public List<Widget> findWidgets(List<String> resIdList) {
        WidgetExample example = new WidgetExample();
        example.createCriteria().andResIdIn(resIdList);
        return widgetMapper.selectByExampleWithBLOBs(example);
    }

    public List<Widget> findAll() {
        WidgetExample example = new WidgetExample();
        example.createCriteria();
        return widgetMapper.selectByExampleWithBLOBs(example);
    }

    public boolean countByDateSetId(String dataSetId) {
        int result = widgetMapper.countByDateSetId(dataSetId);
        return result > 0;
    }

}
