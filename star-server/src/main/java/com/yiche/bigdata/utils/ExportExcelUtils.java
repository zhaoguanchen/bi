package com.yiche.bigdata.utils;

import com.alibaba.fastjson.JSONArray;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

public class ExportExcelUtils
{

    public static void exportExcel(List<String> headers, JSONArray dataset, ByteArrayOutputStream out)
    {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFPalette customPalette = wb.getCustomPalette();
        customPalette.setColorAtIndex(IndexedColors.BLUE.index, (byte) 26, (byte) 127, (byte) 205);
        customPalette.setColorAtIndex(IndexedColors.BLUE_GREY.index, (byte) 56, (byte) 119, (byte) 166);
        customPalette.setColorAtIndex(IndexedColors.GREY_25_PERCENT.index, (byte) 235, (byte) 235, (byte) 235);

        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setColor(IndexedColors.WHITE.getIndex());
        titleFont.setFontName("微软雅黑");
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font thFont = wb.createFont();
        thFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle thStyle = wb.createCellStyle();
        thStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        thStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        thStyle.setBorderBottom(BorderStyle.THIN);
        thStyle.setBottomBorderColor(IndexedColors.BLUE_GREY.getIndex());
        thStyle.setBorderLeft(BorderStyle.THIN);
        thStyle.setLeftBorderColor(IndexedColors.BLUE_GREY.getIndex());
        thStyle.setBorderRight(BorderStyle.THIN);
        thStyle.setRightBorderColor(IndexedColors.BLUE_GREY.getIndex());
        thStyle.setBorderTop(BorderStyle.THIN);
        thStyle.setTopBorderColor(IndexedColors.BLUE_GREY.getIndex());
        thStyle.setFont(thFont);
        thStyle.setAlignment(HorizontalAlignment.CENTER);
        thStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        thStyle.setShrinkToFit(true);

        CellStyle tStyle = wb.createCellStyle();
        tStyle.setBorderBottom(BorderStyle.THIN);
        tStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tStyle.setBorderLeft(BorderStyle.THIN);
        tStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tStyle.setBorderRight(BorderStyle.THIN);
        tStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tStyle.setBorderTop(BorderStyle.THIN);
        tStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        tStyle.setAlignment(HorizontalAlignment.CENTER);
        tStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tStyle.setShrinkToFit(true);

        CellStyle percentStyle = wb.createCellStyle();
        percentStyle.cloneStyleFrom(tStyle);
        percentStyle.setDataFormat((short) 0xa);
        Sheet sheet = wb.createSheet();

        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(thStyle);
        }
        for (int i = 0; i < dataset.size(); i++) {
            row = sheet.createRow(i + 1);
            JSONArray json = dataset.getJSONArray(i);
            for(int j=0;j<json.size();j++){
                Cell cell = row.createCell(j);
                cell.setCellValue(json.getString(j));
                cell.setCellStyle(tStyle);
            }
        }

        int max = 0;
        Iterator<Row> i = sheet.rowIterator();
        while (i.hasNext()) {
            if (i.next().getLastCellNum() > max) {
                max = i.next().getLastCellNum();
            }
        }
        for (int colNum = 0; colNum < max; colNum++) {
            sheet.autoSizeColumn(colNum, true);
        }
        try{
            wb.write(out);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}