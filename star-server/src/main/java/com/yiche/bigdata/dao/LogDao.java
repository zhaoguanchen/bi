package com.yiche.bigdata.dao;

import com.yiche.bigdata.entity.generated.Log;
import com.yiche.bigdata.mapper.generated.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogDao {
    @Autowired
    private LogMapper logMapper;

    public int save(Log log) {
        return logMapper.insert(log);
    }
}
