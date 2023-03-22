package com.application.excel.inport;

import com.application.exceptions.CustomException;
import com.application.utils.hibernate.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


public class ExcelToObjectMapper {

    Map<Integer, String> header;
    Iterator rows;
    final ObjectMapper mapper;
    XSSFWorkbook wb;
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

    public ExcelToObjectMapper(InputStream file, boolean checkForDuplicates) throws IOException {
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
        getDataFromSheet(objects, objectType, header);
    }


    private void getDataFromSheetWithColumnOrder(List objects, Class<?> objectType) {
        Map<Integer, String> classMapping = new HashMap<>();
        int fieldIndex = 0;
        for (Field field : objectType.getDeclaredFields()) {
            field.setAccessible(true);
            classMapping.put(fieldIndex++, field.getName());
        }
        getDataFromSheet(objects, objectType, classMapping);

    }

    private void getDataFromSheet(List objects, Class<?> objectType, Map<Integer, String> classMapping) {
        XSSFCell cell;
        while (rows.hasNext()) {
            XSSFRow row = (XSSFRow) rows.next();
            Map object = new HashMap();

            for (Integer index : classMapping.keySet()) {
                cell = row.getCell(index);
                if (cell != null) {
                    if (cell.getCellType() == CellType.STRING) {
                        object.put(classMapping.get(index), cell.getStringCellValue());
                    } else if (DateUtil.isCellDateFormatted(cell)) {
                        object.put(classMapping.get(index), cell.getDateCellValue());
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        cell.setCellType(CellType.STRING);     // change the cell type from numeric to string to get correctly the telephone nr
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
                        } else {
                            throw new CustomException("Exista date cu identificatori identici in fisier!Verificati randul " + (row.getRowNum() + 1), HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        objects.add(objectT);
                    }
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                e.getMessage();
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
        XSSFRow row;
        XSSFCell cell;
        header = new HashMap<>();

        if (rows.hasNext()) {
            row = (XSSFRow) rows.next();
            Iterator cells = row.cellIterator();
            while (cells.hasNext()) {
                cell = (XSSFCell) cells.next();
                if (cell.getStringCellValue() != null && !cell.getStringCellValue().equals(EMPTY_STRING)) {
                    if (cell.getCellType() == CellType.STRING) {
                        String headerColumnValue = formatHeaderValue(cell.getStringCellValue());
                        //UtilsHeader.checkHeaderPosition(objectType, headerColumnValue, (cell.getColumnIndex()));
                        header.put(cell.getColumnIndex(), headerColumnValue);
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
        XSSFSheet sheet = wb.getSheetAt(currentSheet);
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
