package com.application.util;

import com.application.util.excel.Excel;
import com.application.util.excel.ExcelXls;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String formatStringtoCamelcase(String value) {
        value = value.replaceAll("[!\"#$%&'()*+,./:;<=>?@\\^_`{|}~-]", " ");
        String[] arr = value.split(" ");
        String newValue = "";
        for (String ss : arr) {
            if (!newValue.isEmpty()) {
                newValue = newValue + ss.substring(0, 1).toUpperCase() + ss.substring(1).toLowerCase();
            } else {
                newValue = ss.toLowerCase();
            }
        }
        return newValue;
    }

    public static Object extractFromExcelWithColumnOrderExtractionModeCheckType(MultipartFile excelFile, Class classToExtarct) {
        if (excelFile == null) {
            throw new CustomException(Messages.FILE_NOT_NULL, HttpStatus.BAD_REQUEST);
        }

        if (excelFile.getContentType().equals("application/vnd.ms-excel")) {
            ExcelXls excel;
            try {
                excel = new ExcelXls(excelFile.getInputStream());
                return excel.getDataFromXls(classToExtarct, ExcelXls.COLUMN_ORDER);
            } catch (org.apache.poi.poifs.filesystem.OfficeXmlFileException e) {
                throw new CustomException(Messages.WRONG_FORMAT + " Eroarea: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                throw new CustomException("Eroare la extragerea fisierului din MultipartFile", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else if (excelFile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            Excel excel;
            try {
                excel = new Excel(excelFile.getInputStream());
                return excel.getDataFromXlsx(classToExtarct, Excel.COLUMN_ORDER);
            } catch (org.apache.poi.poifs.filesystem.OfficeXmlFileException e) {
                throw new CustomException(Messages.WRONG_FORMAT + " Eroarea: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                throw new CustomException("Eroare la extragerea fisierului din MultipartFile", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else throw new CustomException("Tipul de fisier nu este acceptat", HttpStatus.BAD_REQUEST);
    }


    public static String getValueAsString(Workbook workbook, Sheet sheet, String columnIndex, int rowIndex) {
        DataFormatter dataFormatter = new DataFormatter();
        String reference = decodeChacartersToDoubles(columnIndex) + rowIndex;
        CellReference cellReference = new CellReference(reference);
        Row row = sheet.getRow(cellReference.getRow());
        Cell cell = row.getCell(cellReference.getCol());
        return dataFormatter.formatCellValue(cell);
    }

    public static String getUserNameFromPrincipal(Principal principal) {
        String username = principal.getName();
        return username.substring(0, username.indexOf("@"));
    }


    public static String dateToYYYYMMDD(Long date) {
        if (date == null) return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(new Date(date));
    }
    public static int decodeColumnIndex(String oldIndex) {
        if (oldIndex.equals("A")) return 65;
        if (oldIndex.equals("B")) return 66;
        if (oldIndex.equals("C")) return 67;
        if (oldIndex.equals("D")) return 68;
        if (oldIndex.equals("E")) return 69;
        if (oldIndex.equals("F")) return 70;
        if (oldIndex.equals("G")) return 71;
        if (oldIndex.equals("H")) return 72;
        if (oldIndex.equals("I")) return 73;
        if (oldIndex.equals("J")) return 74;
        if (oldIndex.equals("K")) return 75;
        if (oldIndex.equals("L")) return 76;
        if (oldIndex.equals("M")) return 77;
        if (oldIndex.equals("N")) return 78;
        if (oldIndex.equals("O")) return 79;
        if (oldIndex.equals("P")) return 80;
        if (oldIndex.equals("Q")) return 81;
        if (oldIndex.equals("R")) return 82;
        if (oldIndex.equals("S")) return 83;
        if (oldIndex.equals("T")) return 84;
        if (oldIndex.equals("U")) return 85;
        if (oldIndex.equals("V")) return 86;
        if (oldIndex.equals("W")) return 87;
        if (oldIndex.equals("X")) return 88;
        if (oldIndex.equals("Y")) return 89;
        if (oldIndex.equals("Z")) return 90;
        if (oldIndex.equals("AA")) return 91;
        if (oldIndex.equals("AB")) return 92;
        if (oldIndex.equals("AC")) return 93;
        if (oldIndex.equals("AD")) return 94;
        if (oldIndex.equals("AE")) return 95;
        if (oldIndex.equals("AF")) return 96;
        if (oldIndex.equals("AG")) return 97;
        if (oldIndex.equals("AH")) return 98;
        if (oldIndex.equals("AI")) return 99;
        if (oldIndex.equals("AJ")) return 100;
        if (oldIndex.equals("AK")) return 101;
        if (oldIndex.equals("AL")) return 102;
        if (oldIndex.equals("AM")) return 103;
        if (oldIndex.equals("AN")) return 104;
        if (oldIndex.equals("AO")) return 105;
        if (oldIndex.equals("AP")) return 106;
        if (oldIndex.equals("AQ")) return 107;
        if (oldIndex.equals("AR")) return 108;
        if (oldIndex.equals("AS")) return 109;
        if (oldIndex.equals("AT")) return 110;
        if (oldIndex.equals("AU")) return 111;
        if (oldIndex.equals("AV")) return 112;
        if (oldIndex.equals("AW")) return 113;
        if (oldIndex.equals("AX")) return 114;
        if (oldIndex.equals("AY")) return 115;
        if (oldIndex.equals("AZ")) return 116;
        else return 0;
    }

    public static BigDecimal getValueAsBigDecimal(Workbook workbook, Sheet sheet, String columnIndex, int rowIndex) throws ParseException {
        String reference = decodeChacartersToDoubles(columnIndex) + rowIndex;
        CellReference cellReference = new CellReference(reference);
        Row row = sheet.getRow(cellReference.getRow());
        Cell cell = row.getCell(cellReference.getCol());
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);
        return new BigDecimal(cellValue.getNumberValue());
    }

    private static String decodeChacartersToDoubles(String oldIndex) {
        if (oldIndex.equals("A")) return "A";
        if (oldIndex.equals("B")) return "B";
        if (oldIndex.equals("C")) return "C";
        if (oldIndex.equals("D")) return "D";
        if (oldIndex.equals("E")) return "E";
        if (oldIndex.equals("F")) return "F";
        if (oldIndex.equals("G")) return "G";
        if (oldIndex.equals("H")) return "H";
        if (oldIndex.equals("I")) return "I";
        if (oldIndex.equals("J")) return "J";
        if (oldIndex.equals("K")) return "K";
        if (oldIndex.equals("L")) return "L";
        if (oldIndex.equals("M")) return "M";
        if (oldIndex.equals("N")) return "N";
        if (oldIndex.equals("O")) return "O";
        if (oldIndex.equals("P")) return "P";
        if (oldIndex.equals("Q")) return "Q";
        if (oldIndex.equals("R")) return "R";
        if (oldIndex.equals("S")) return "S";
        if (oldIndex.equals("T")) return "T";
        if (oldIndex.equals("U")) return "U";
        if (oldIndex.equals("V")) return "V";
        if (oldIndex.equals("W")) return "W";
        if (oldIndex.equals("X")) return "X";
        if (oldIndex.equals("Y")) return "Y";
        if (oldIndex.equals("Z")) return "Z";
        if (oldIndex.equals("[")) return "AA";
        if (oldIndex.equals("\\")) return "AB";
        if (oldIndex.equals("]")) return "AC";
        if (oldIndex.equals("^")) return "AD";
        if (oldIndex.equals("_")) return "AE";
        if (oldIndex.equals("`")) return "AF";
        if (oldIndex.equals("a")) return "AG";
        if (oldIndex.equals("b")) return "AH";
        if (oldIndex.equals("c")) return "AI";
        if (oldIndex.equals("d")) return "AJ";
        if (oldIndex.equals("e")) return "AK";
        if (oldIndex.equals("f")) return "AL";
        if (oldIndex.equals("g")) return "AM";
        if (oldIndex.equals("h")) return "AN";
        if (oldIndex.equals("i")) return "AO";
        if (oldIndex.equals("j")) return "AP";
        if (oldIndex.equals("k")) return "AQ";
        if (oldIndex.equals("l")) return "AR";
        if (oldIndex.equals("m")) return "AS";
        if (oldIndex.equals("n")) return "AT";
        if (oldIndex.equals("o")) return "AU";
        if (oldIndex.equals("p")) return "AV";
        if (oldIndex.equals("q")) return "AW";
        if (oldIndex.equals("r")) return "AX";
        if (oldIndex.equals("s")) return "AY";
        if (oldIndex.equals("t")) return "AZ";
        //se rupe
        if (oldIndex.equals("AC")) return "AC";
        if (oldIndex.equals("AD")) return "AD";
        if (oldIndex.equals("AE")) return "AE";
        if (oldIndex.equals("AF")) return "AF";
        if (oldIndex.equals("AG")) return "AG";
        if (oldIndex.equals("AH")) return "AH";
        if (oldIndex.equals("AI")) return "AI";
        if (oldIndex.equals("AJ")) return "AJ";
        if (oldIndex.equals("AK")) return "AK";
        if (oldIndex.equals("AL")) return "AL";
        if (oldIndex.equals("AM")) return "AM";
        if (oldIndex.equals("AN")) return "AN";
        if (oldIndex.equals("AO")) return "AO";
        if (oldIndex.equals("AP")) return "AP";
        if (oldIndex.equals("AQ")) return "AQ";
        if (oldIndex.equals("AR")) return "AR";
        if (oldIndex.equals("AS")) return "AS";
        if (oldIndex.equals("AT")) return "AT";
        if (oldIndex.equals("AU")) return "AU";
        if (oldIndex.equals("AV")) return "AV";
        if (oldIndex.equals("AW")) return "AW";
        if (oldIndex.equals("AX")) return "AX";
        if (oldIndex.equals("AY")) return "AY";
        if (oldIndex.equals("AZ")) return "AZ";
        else return null;
    }
}
