package com.phantoms.phantomsbackend.common.excel;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeadWidthHandler extends SimpleColumnWidthStyleStrategy {

    private Map<Integer,Integer> columnWidthMap;

    /**
     * @param columnWidth
     *
     */
    public HeadWidthHandler(Integer columnWidth) {
        super(columnWidth);
        columnWidthMap=new HashMap<>();

    }
    public void setColumnWidthMap(Map<Integer,Integer> columnWidthMap) {
      this.columnWidthMap=columnWidthMap;

    }

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        Sheet sheet = writeSheetHolder.getSheet();
        Integer width = columnWidthMap.get(cell.getColumnIndex());
        if (ObjectUtil.isEmpty(width)){
            sheet.setColumnWidth(cell.getColumnIndex(),columnWidth(null,null) * 256); // 单位转换，Excel列宽是以1/256字符宽度为单位
        }else{
            sheet.setColumnWidth(cell.getColumnIndex(),width * 256); // 单位转换，Excel列宽是以1/256字符宽度为单位
        }
    }
}
