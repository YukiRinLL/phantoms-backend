package com.phantoms.phantomsbackend.common.excel;

import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Row;

public class RowHeightWriteHandler extends SimpleRowHeightStyleStrategy {

    private final Short contentHeightChild;

    public RowHeightWriteHandler(Integer headRowHeight, Integer contentRowHeight ) {
        super(headRowHeight.shortValue(), contentRowHeight.shortValue());
        this.contentHeightChild = contentRowHeight.shortValue();
    }

    @Override
    protected void setContentColumnHeight(Row row, int relativeRowIndex) {
        row.setHeightInPoints(contentHeightChild); // 设置行高为20点
    }


}
