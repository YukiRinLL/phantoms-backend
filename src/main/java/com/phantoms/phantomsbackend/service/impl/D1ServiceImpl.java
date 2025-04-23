package com.phantoms.phantomsbackend.service.impl;

import co.casterlabs.d1.result.D1Result;
import com.phantoms.phantomsbackend.common.utils.D1Util;
import com.phantoms.phantomsbackend.service.D1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class D1ServiceImpl implements D1Service {

    private final D1Util d1Util;

    @Autowired
    public D1ServiceImpl(D1Util d1Util) {
        this.d1Util = d1Util;
    }

    @Override
    public D1Result query(String sql) throws Exception {
        return d1Util.query(sql);
    }

    @Override
    public D1Result[] batchQuery(List<String> sqls) throws Exception {
        return d1Util.batchQuery(String.join(";", sqls));
    }
}