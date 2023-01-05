package com.application.util.excel;

import com.application.util.CustomException;
import com.application.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


public class Excel {

    Map<String, Integer> header;
    Iterator rows;
    final ObjectMapper mapper;
    Workbook wb;
    int currentSheet = 0;
    int totalSheetNr;
    Set encountered;
    boolean checkForDuplicates = true;
    private static final String EMPTY_STRING = "";

    public static final int COLUMN_NAME = 0;
    public static final int COLUMN_ORDER = 1;

    /**
     * Gets the excel data, and the header from it.
     * Creates ObjectMapper
     *
     * @param file - excel file to be processed
     * @throws IOException
     */
    public Excel(InputStream file) throws IOException {
        mapper = new ObjectMapper();
        wb = new XSSFWorkbook(file);
        totalSheetNr = wb.getNumberOfSheets();
        encountered = new HashSet<>();
        initializeSheetAndRows();
    }

    public Excel(MultipartFile file) throws IOException {
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
        totalSheetNr = wb.getNumberOfSheets();
        encountered = new HashSet<>();
        initializeSheetAndRows();
    }

    public Excel(InputStream file, boolean checkForDuplicates) throws IOException {
        mapper = new ObjectMapper();
        wb = new XSSFWorkbook(file);
        totalSheetNr = wb.getNumberOfSheets();
        encountered = new HashSet<>();
        this.checkForDuplicates = checkForDuplicates;
        initializeSheetAndRows();
    }

    /**
     * Extracts the data from Excel file
     *
     * @param objectType - The type of object to which data is being converted
     * @return - a list of specified objects
     */

    public List getDataFromXlsx(Class<?> objectType) {
        setHeader(objectType);
        List objects = new ArrayList<>();
        getDataFromSheet(objects, objectType);

        while (currentSheet < totalSheetNr - 1) {
            currentSheet = currentSheet + 1;
            initializeSheetAndRows();
            if (rows.hasNext()) {
                rows.next();
            }
            getDataFromSheet(objects, objectType);
        }
        return objects;
    }

    public List getDataFromXlsx(Class<?> objectType, int extractionOption) {
        setHeader(objectType);
        List objects = new ArrayList<>();
        if (extractionOption == COLUMN_ORDER) {
            getDataFromSheetWithColumnOrder(objects, objectType);
        } else {
            getDataFromSheet(objects, objectType);
        }

        while (currentSheet < totalSheetNr - 1) {
            currentSheet = currentSheet + 1;
            initializeSheetAndRows();
            if (rows.hasNext()) {
                rows.next();
            }
            if (extractionOption == COLUMN_ORDER) {
                getDataFromSheetWithColumnOrder(objects, objectType);
            } else {
                getDataFromSheet(objects, objectType);
            }
        }
        return objects;
    }

    /**
     * Extracts data from current sheet and converts every row intro the specified object
     *
     * @param objects    - a list of specified objects
     * @param objectType - The type of object to which data is being converted
     */
    private void getDataFromSheet(List objects, Class<?> objectType) {
        Cell cell;
        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            Map object = new HashMap();

            for (Map.Entry<String, Integer> entry : header.entrySet()) {
                cell = row.getCell(entry.getValue());
                if (cell != null) {

                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        object.put(entry.getKey(), cell.getStringCellValue());
                    } else if (DateUtil.isCellDateFormatted(cell)) {
                        object.put(entry.getKey(), cell.getDateCellValue());
                    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);     // change the cell type from numeric to string to get correctly the telephone nr
                        object.put(entry.getKey(), cell.getStringCellValue());
                    }
                }
            }
            try {
                Object objectT = mapper.convertValue(object, objectType);
                if (!checkEmptyObject(object)) {
                    if (checkForDuplicates) {
                        final boolean first = encountered.add(objectT);
                        if (first) {
                            // add to final list after conversion of the map into the specified object (the object must be unique in list)
                            objects.add(objectT);
                        }
                    } else {
                        objects.add(objectT);
                    }
                }

            } catch (IllegalArgumentException e) {
                throw new CustomException("Date invalide! Asigurati-va ca tipul de date de pe fiecare coloana corespunde cu tipul de date cerut!", HttpStatus.BAD_REQUEST);
            }
        }
    }


    private void getDataFromSheetWithColumnOrder(List objects, Class<?> objectType) {
        Map<Integer, String> classMapping = new HashMap<>();
        int fieldIndex = 0;
        for (Field field : objectType.getDeclaredFields()) {
            field.setAccessible(true);
            classMapping.put(fieldIndex++, field.getName());
        }

        Cell cell;
        while (rows.hasNext()) {
            Row row = (Row) rows.next();
            Map object = new HashMap();

            for (Integer index : classMapping.keySet()) {
                cell = row.getCell(index);
                if (cell != null) {
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        object.put(classMapping.get(index), cell.getStringCellValue());
                    } else if (DateUtil.isCellDateFormatted(cell)) {
                        object.put(classMapping.get(index), cell.getDateCellValue());
                    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);     // change the cell type from numeric to string to get correctly the telephone nr
                        object.put(classMapping.get(index), cell.getStringCellValue());
                    }
                } else {
                    object.put(classMapping.get(index), null);
                }
            }

            try {
                Object objectT = mapper.convertValue(object, objectType);
                if (!checkEmptyObject(object)) {
                    if (checkForDuplicates) {
                        final boolean first = encountered.add(objectT);
                        if (first) {
                            // add to final list after conversion of the map into the specified object (the object must be unique in list)
                            objects.add(objectT);
                        }
                    } else {
                        objects.add(objectT);
                    }
                }

            } catch (IllegalArgumentException e) {
                throw new CustomException("Date invalide! Asigurati-va ca tipul de date de pe fiecare coloana corespunde cu tipul de date cerut!", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean checkEmptyObject(Map object) {
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


    /**
     * Gets the header from Excel file
     *
     * @param objectType
     */
    private void setHeader(Class objectType) {
        Row row;
        Cell cell;
        header = new HashMap<>();

        if (rows.hasNext()) {
            row = (Row) rows.next();
            Iterator cells = row.cellIterator();
            while (cells.hasNext()) {
                cell = (Cell) cells.next();
                if (cell.getStringCellValue() != null && !cell.getStringCellValue().equals(EMPTY_STRING)) {
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        String headerColumnValue = formatHeaderValue(cell.getStringCellValue());
                        header.put(headerColumnValue, cell.getColumnIndex());
                    } else {
                        throw new CustomException("Cap de tabel invalid! Valorile trebuie sa fie sir de caractere!", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
        }
    }

    /**
     * @param field              - field to check for annotation
     * @param annotationFullPath - annotation to search ( package path + annotation name; ex: javax.persistence.EmbeddedId )
     * @return - true if field contains annotation , false if the field don't contains annotation
     */
    private boolean isFieldAnnotated(Field field, String annotationFullPath) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(annotationFullPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the current sheet and rows
     */
    private void initializeSheetAndRows() {
        Sheet sheet = wb.getSheetAt(currentSheet);
        rows = sheet.rowIterator();
    }

    /**
     * Format the header values to match the object parameters (camel case)
     * Each value from header must be a string that contains words separated by space or other caracters from: [!"#$%&'()*+,./:;<=>?@\^_`{|}~-]
     *
     * @param value - The value from header
     * @return - the new value formatted
     */
    private String formatHeaderValue(String value) {
        return Utils.formatStringtoCamelcase(value);
    }
}

