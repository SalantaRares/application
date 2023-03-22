package com.application.excel.export;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {
    public static CellStyle createFormat(Workbook workbook, short fontHeight, short backgroundColor, boolean italic, boolean bold, HorizontalAlignment align, VerticalAlignment verticalAlign) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints(fontHeight);
        font.setItalic(italic);
        font.setBold(bold);

        // Fonts are set into a style so create a new one to use.
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(backgroundColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillBackgroundColor(backgroundColor);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(align);
        style.setVerticalAlignment(verticalAlign);
        style.setWrapText(true);
        return style;
    }
}
