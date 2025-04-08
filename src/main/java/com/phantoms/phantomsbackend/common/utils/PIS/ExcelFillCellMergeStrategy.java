package com.phantoms.phantomsbackend.common.utils.PIS;


import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.Data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

@Data
public class ExcelFillCellMergeStrategy implements CellWriteHandler {
    private int[] mergeColumnIndex;
    private int mergeRowIndex;

    public ExcelFillCellMergeStrategy() {
    }

    public ExcelFillCellMergeStrategy(int mergeRowIndex, int[] mergeColumnIndex) {
        this.mergeRowIndex = mergeRowIndex;
        this.mergeColumnIndex = mergeColumnIndex;
    }

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
        //当前行
        int curRowIndex = cell.getRowIndex();
        //当前列
        int curColIndex = cell.getColumnIndex();

        if (curRowIndex > mergeRowIndex) {
            for (int i = 0; i < mergeColumnIndex.length; i++) {
                if (curColIndex == mergeColumnIndex[i]) {
                    mergeWithPrevRow(writeSheetHolder, cell, curRowIndex, curColIndex);
                    break;
                }
            }
        }
    }

    /**
     * 当前单元格向上合并
     *
     * @param writeSheetHolder
     * @param cell             当前单元格
     * @param curRowIndex      当前行
     * @param curColIndex      当前列
     */
    private void mergeWithPrevRow(WriteSheetHolder writeSheetHolder, Cell cell, int curRowIndex, int curColIndex) {
        // 获取当前单元格的数据和上一行同一列的数据，通过数据是否相同进行合并
        Object curData;
        Object preData;

        // 根据当前单元格的类型获取值
        if (cell.getCellType() == CellType.STRING) {
            curData = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            curData = cell.getNumericCellValue();
        } else {
            curData = null; // 如果是其他类型，可以根据需要处理
        }

        // 获取上一行同一列的单元格
        Cell preCell = cell.getSheet().getRow(curRowIndex - 1).getCell(curColIndex);

        // 根据上一行单元格的类型获取值
        if (preCell != null) {
            if (preCell.getCellType() == CellType.STRING) {
                preData = preCell.getStringCellValue();
            } else if (preCell.getCellType() == CellType.NUMERIC) {
                preData = preCell.getNumericCellValue();
            } else {
                preData = null; // 如果是其他类型，可以根据需要处理
            }
        } else {
            preData = null;
        }

        // 比较当前单元格与上一行同一列的单元格数据是否相同，相同则合并
        if (curData != null && curData.equals(preData)) {
            Sheet sheet = writeSheetHolder.getSheet();
            List<CellRangeAddress> mergeRegions = sheet.getMergedRegions();
            boolean isMerged = false;

            // 遍历已有的合并区域
            for (int i = 0; i < mergeRegions.size() && !isMerged; i++) {
                CellRangeAddress cellRangeAddr = mergeRegions.get(i);
                // 如果上一个单元格已经被合并，则先移除原有的合并区域，再重新添加合并区域
                if (cellRangeAddr.isInRange(curRowIndex - 1, curColIndex)) {
                    sheet.removeMergedRegion(i);
                    cellRangeAddr.setLastRow(curRowIndex);
                    sheet.addMergedRegion(cellRangeAddr);
                    isMerged = true;
                }
            }

            // 如果上一个单元格未被合并，则新增合并区域
            if (!isMerged) {
                CellRangeAddress cellRangeAddress = new CellRangeAddress(curRowIndex - 1, curRowIndex, curColIndex, curColIndex);
                sheet.addMergedRegion(cellRangeAddress);
            }
        }
    }
}