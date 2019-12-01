package com.tss.apiservice.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author 壮Jone
 */
public class ExcelUtils {

    public static void exportWithResponse(String sheetName, String titleName, String titleName2, String fileName, int columnNumber, int[] columnWidth,
                                          String[] columnName, String[][] dataList, HttpServletResponse response) throws Exception {
        if (columnNumber == columnWidth.length && columnWidth.length == columnName.length) {
            // 第一步，创建一个webbook，对应一个Excel文件
            HSSFWorkbook wb = new HSSFWorkbook();
            // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
            HSSFSheet sheet = wb.createSheet(sheetName);
            // sheet.setDefaultColumnWidth(15); //统一设置列宽
            for (int i = 0; i < columnNumber; i++) {
                for (int j = 0; j <= i; j++) {
                    if (i == j) {
                        // 单独设置每列的宽
                        sheet.setColumnWidth(i, columnWidth[j] * 256);
                    }
                }
            }
            // 创建第0行 也就是标题
            HSSFRow row1 = sheet.createRow((int) 0);
            // 设备标题的高度
            row1.setHeightInPoints(40);
            // 第三步创建标题的单元格样式style2以及字体样式headerFont1
            HSSFCellStyle style2 = wb.createCellStyle();
            style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
            style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            // 创建字体样式
            HSSFFont headerFont1 = (HSSFFont) wb.createFont();
            // 字体加粗
            headerFont1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            // 设置字体类型
            headerFont1.setFontName("黑体");
            // 设置字体大小
            headerFont1.setFontHeightInPoints((short) 15);
            // 为标题样式设置字体样式
            style2.setFont(headerFont1);
            // 创建标题第一列
            HSSFCell cell1 = row1.createCell(0);
            // 合并列标题
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnNumber - 1));
            // 设置值标题
            cell1.setCellValue(titleName);
            // 设置标题样式
            cell1.setCellStyle(style2);

            //創建二級表頭
            HSSFRow row2 = sheet.createRow((int) 1);
            // 设备标题的高度
            row2.setHeightInPoints(30);
            // 创建标题第一列
            HSSFCell cell2 = row2.createCell(0);
            // 第三步创建标题的单元格样式style3以及字体样式headerFont2
            HSSFCellStyle style3 = wb.createCellStyle();
            style3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style3.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
            style3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            // 创建字体样式
            HSSFFont headerFont2 = (HSSFFont) wb.createFont();
            // 字体加粗
            headerFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            // 设置字体类型
            headerFont2.setFontName("黑体");
            // 设置字体大小
            headerFont2.setFontHeightInPoints((short) 13);
            // 为标题样式设置字体样式
            style3.setFont(headerFont2);
            // 合并列标题
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnNumber - 1));
            // 设置值标题
            cell2.setCellValue(titleName2);
            // 设置标题样式
            cell2.setCellStyle(style3);

            // 创建第1行 也就是表头
            HSSFRow row = sheet.createRow((int) 2);
            // 设置表头高度
            row.setHeightInPoints(25);
            // 第四步，创建表头单元格样式 以及表头的字体样式
            HSSFCellStyle style = wb.createCellStyle();
            // 设置自动换行
            style.setWrapText(true);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            // 创建一个居中格式
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style.setBottomBorderColor(HSSFColor.BLACK.index);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            // 创建字体样式
            HSSFFont headerFont = (HSSFFont) wb.createFont();
            // 字体加粗
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            // 设置字体类型
            headerFont.setFontName("黑体");
            // 设置字体大小
            headerFont.setFontHeightInPoints((short) 11);
            // 为标题样式设置字体样式
            style.setFont(headerFont);

            // 第四.一步，创建表头的列
            for (int i = 0; i < columnNumber; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(columnName[i]);
                cell.setCellStyle(style);
            }
            // 第五步，创建单元格，并设置值
            for (int i = 0; i < dataList.length; i++) {
                row = sheet.createRow((int) i + 3);
                // 为数据内容设置特点新单元格样式1 自动换行 上下居中
                HSSFCellStyle zidonghuanhang = wb.createCellStyle();
                // 设置自动换行
                zidonghuanhang.setWrapText(true);
                // 创建一个居中格式
                zidonghuanhang.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                // 设置边框
                zidonghuanhang.setBottomBorderColor(HSSFColor.BLACK.index);
                zidonghuanhang.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                zidonghuanhang.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                zidonghuanhang.setBorderRight(HSSFCellStyle.BORDER_THIN);
                zidonghuanhang.setBorderTop(HSSFCellStyle.BORDER_THIN);
                // 为数据内容设置特点新单元格样式2 自动换行 上下居中左右也居中
                HSSFCellStyle zidonghuanhang2 = wb.createCellStyle();
                // 设置自动换行
                zidonghuanhang2.setWrapText(true);
                // 创建一个上下居中格式
                zidonghuanhang2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                // 左右居中
                zidonghuanhang2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                // 设置边框
                zidonghuanhang2.setBottomBorderColor(HSSFColor.BLACK.index);
                zidonghuanhang2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                zidonghuanhang2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                zidonghuanhang2.setBorderRight(HSSFCellStyle.BORDER_THIN);
                zidonghuanhang2.setBorderTop(HSSFCellStyle.BORDER_THIN);
                // 创建字体样式
                HSSFFont bodyFont = (HSSFFont) wb.createFont();
                // 设置字体类型
                bodyFont.setFontName("黑体");
                // 设置字体大小
                bodyFont.setFontHeightInPoints((short) 9);
                // 为标题样式设置字体样式
                zidonghuanhang2.setFont(bodyFont);
                HSSFCell datacell = null;
                for (int j = 0; j < columnNumber; j++) {
                    datacell = row.createCell(j);
                    datacell.setCellValue(dataList[i][j]);
                    datacell.setCellStyle(zidonghuanhang2);
                }
            }
            // 第六步，将文件存到浏览器设置的下载位置
            String filename = fileName + ".xls";
            response.setContentType("application/ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    .concat(String.valueOf(URLEncoder.encode(filename, "UTF-8"))));
            try (OutputStream out = response.getOutputStream()) {
                // 将数据写出去
                wb.write(out);
                String str = "导出" + fileName + "成功！";
                System.out.println(str);
            } catch (Exception e) {
                e.printStackTrace();
                String str1 = "导出" + fileName + "失败！";
                System.out.println(str1);
            }
        } else {
            System.out.println("列数目长度名称三个数组长度要一致");
        }
    }

}
