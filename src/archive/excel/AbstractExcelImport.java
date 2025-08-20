package com.phantoms.phantomsbackend.common.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.phantoms.phantomsbackend.common.utils.TH.ExcelUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * excel数据导入监听抽象类(采用模版模式, 百万数据处理方式)
 * @author 头秃程序员
 */
@Slf4j
public abstract class AbstractExcelImport<T,E> extends AnalysisEventListener<T> {
    /**
     * 自定义用于暂时存储data。
     */
    protected List<E> dataList = new CopyOnWriteArrayList<>();

    public List<E> getDataList() {
        return dataList;
    }

    @Override
    public void invoke(T object, AnalysisContext context) {


        //数据存储
        dataList.add((E) object);
        //百万数据处理方式，达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (dataList.size() >= ExcelUtil.SAVE_DB_MAX_SIZE) {
            saveData();
            log.info("数据量达到{}条,保存内存数据到db", ExcelUtil.SAVE_DB_MAX_SIZE);
        }
    }

//    @Override
//    public void doAfterAllAnalysed(AnalysisContext context) {
//        log.info("解析excel数据完成！！！");
//        saveData();
//        log.info("保存内存数据到db");
//    }

    /**
     * 保存数据到 DB
     */
    private void saveData() {
        if (dataList.size() > 0) {
            doSaveData(dataList);
            dataList.clear();
        }
    }
    protected abstract void doSaveData(List<E> data);
}
