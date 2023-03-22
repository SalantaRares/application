package com.application.excel.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import com.application.exceptions.CustomException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

public class ExcelParser {

    Map<String, Integer> header;
    Iterator rows;
    final ObjectMapper mapper;
    Workbook wb = null;
    boolean checkForDuplicates = true;
    private static final String EMPTY_STRING = "";

    public static final int COLUMN_NAME = 0;
    public static final int COLUMN_ORDER = 1;


    Map<String, Integer> classTypeMap = null;
    Class<?> objectType = null;


    public ExcelParser(MultipartFile file) throws IOException {
        mapper = new ObjectMapper();
        if (file.getContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            wb = new XSSFWorkbook(file.getInputStream());
        }
        if (file.getContentType().equalsIgnoreCase("application/vnd.ms-excel")) {
            wb = new HSSFWorkbook(file.getInputStream());
        }
        if (wb == null) {
            throw new CustomException("Fisierul nu a putut fi procesat. Acesta nu este tip Microsoft Excel", HttpStatus.BAD_REQUEST);
        }
    }


    public List getData(Class<?> objectType) {
        extractInfoFromObjectType(objectType);
        List data = new ArrayList();
        int sheetsNumber = wb.getNumberOfSheets();
        for (int i = 0; i < sheetsNumber; i++) {
            data.addAll(getDataFromSheet(wb.getSheetAt(i)));
        }
        return data;
    }

    private void extractInfoFromObjectType(Class<?> objectType) {
        this.classTypeMap = new HashMap<>();
        this.objectType = objectType;
        int fieldIndex = 0;
        for (Field field : objectType.getDeclaredFields()) {
            field.setAccessible(true);
            this.classTypeMap.put(field.getName(), fieldIndex++);
        }
    }

    private List getDataFromSheet(Sheet sheet) {
        List data = new ArrayList();
        rows = sheet.rowIterator();
        if (rows.hasNext()) {
            rows.next(); // skip header
        }
        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            Object obj = getDataFromRow(row, this.classTypeMap);
            if (obj != null)
                data.add(obj);
        }
        return data;
    }

    private Object getDataFromRow(Row row, Map<String, Integer> typeMap) {
        Object objectT;
        Map object = new HashMap();
        for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
            Object obj = getDataFromCell(row.getCell(entry.getValue()));
            object.put(entry.getKey(), obj);
        }

        try {
            //String json = mapper.writeValueAsString(object);
            //System.out.println(json);
            //objectT = mapper.convertValue(object, this.objectType);
            ///objectT = mapper.readValue(json, this.objectType);
            //System.out.println(objectT);
            objectT = convertMapToObject(object);
            if (!isObjectEmpty(object)) {
                return objectT;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CustomException("Date invalide! Asigurati-va ca tipul de date de pe fiecare coloana corespunde cu tipul de date cerut!", HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    private Object getDataFromCell(Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                cell.setCellType(CellType.STRING);     // change the cell type from numeric to string to get correctly the telephone nr
                return cell.getStringCellValue();
            }
        }
        return null;
    }

    private Object convertMapToObject(Map object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> ctor = this.objectType.getConstructor();
        Object objectT = ctor.newInstance();

        for (Field field : this.objectType.getDeclaredFields()) {
            field.setAccessible(true);
            Object attributeValue = object.get(field.getName());
            final Class<?> type = field.getType();
            if (attributeValue != null) {
                if (type == BigDecimal.class) {
                    field.set(objectT, new BigDecimal((String) attributeValue));
                } else
                    field.set(objectT, attributeValue);
            }
        }
        return objectT;
    }

    private boolean isObjectEmpty(Map object) {
        boolean flag = true;
        final Set set = object.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            final Object obj = object.get(it.next());
            //check for at least one field that contains info
            if (obj != null && !obj.toString().trim().equals(EMPTY_STRING)) {
                return false;
            }
        }
        return flag;
    }
}

