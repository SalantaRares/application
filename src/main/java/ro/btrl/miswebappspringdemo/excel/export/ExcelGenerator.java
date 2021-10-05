package ro.btrl.miswebappspringdemo.excel.export;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelCustomColumnName;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelFormatOptions;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelIgnoreParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class ExcelGenerator extends AbstractXlsxStreamingView {

    //CONSTANTS
    public static final String EXPORT_LIST_NAME = "objects";
    public static final String EXPORT_CUSTOM_HEADER = "HEADER_MAPPING";
    public static final String CENTER_ALIGNMENT = "CENTER";
    public static final String LEFT_ALIGNMENT = "LEFT";
    public static final String RIGHT_ALIGNMENT = "RIGHT";

    public static final int DEFAULT_DATA_FORMAT_STYLE = 0;
    public static final int STRING_DATA_FORMAT_STYLE = 1;
    public static final int DATE_DATA_FORMAT_STYLE = 2;
    public static final int INTEGER_DATA_FORMAT_STYLE = 3;
    public static final int BIGDECIMAL_DATA_FORMAT_STYLE = 4;

    private String COMPOSED_ID_ANNOTATION = javax.persistence.EmbeddedId.class.getName();
    private String EXCEL_IGNORED_PARAMETER_ANNOTATION = ExcelIgnoreParam.class.getName();
    private String EXCEL_CUSTOM_COLUMN_NAME_ANNOTATION = ExcelCustomColumnName.class.getName();
    private Class EXCEL_FORMAT_OPTIONS_ANNOTATION = ExcelFormatOptions.class;

    private static final short HEIGHT_FACTOR = 20;
    private static final short HEADER_ROW_HEIGHT = 32 * HEIGHT_FACTOR;
    private static final short ROW_HEIGHT = 15 * HEIGHT_FACTOR;

    private static final short WIDTH_FACTOR = 263;
    private static final short DEFAULT_COLUMN_WIDTH = 30 * WIDTH_FACTOR;
    private static final short SMALL_COLUMN_WIDTH = 11 * WIDTH_FACTOR;


    private static final int HEADER_ROW_INDEX = 0;
    private static final int DATA_START_ROW_INDEX = 1;

    private static final HorizontalAlignment ALIGN_CENTER = HorizontalAlignment.CENTER;
    private static final VerticalAlignment ALIGN_CENTER_VERTICAL = VerticalAlignment.CENTER;
    private static final HorizontalAlignment ALIGN_LEFT = HorizontalAlignment.LEFT;
    private static final HorizontalAlignment ALIGN_RIGHT = HorizontalAlignment.RIGHT;


    //REPORT DATA OBJECTS
    private List<Object> dataList = null;
    private List<String> customHeader = null;
    private int dataListSize = 0;

    //EXCEL RELATED ATTRIBUTES
    private Workbook workbook = null;
    private int sheetsNo = 0;
    private static final int MAXROWSHEET = 1000000;
    private Map<String, FieldOptions> referenceObjectFieldsOptions = new HashMap<>();
    private Boolean isUsingCustomHeader;
    private List<String> header = new ArrayList<>();
    private int currentRowIndex = DATA_START_ROW_INDEX;
    private int currentSheetIndex = 0;
    private static String EMPTY = "";

    private boolean globalNrGroupSeparation = true;

    private CellStyle STYLE_ALIGNED_CENTER;
    private CellStyle STYLE_ALIGNED_LEFT;
    private CellStyle STYLE_ALIGNED_RIGHT;

    private CellStyle STYLE_ALIGNED_CENTER_DATA_FORMAT;
    private CellStyle STYLE_ALIGNED_LEFT_DATA_FORMAT;
    private CellStyle STYLE_ALIGNED_RIGHT_DATA_FORMAT;

    private CellStyle STYLE_ALIGNED_CENTER_BIGDECIMAL_FORMAT;
    private CellStyle STYLE_ALIGNED_LEFT_BIGDECIMAL_FORMAT;
    private CellStyle STYLE_ALIGNED_RIGHT_BIGDECIMAL_FORMAT;

    private CellStyle STYLE_ALIGNED_CENTER_INTEGER_FORMAT;
    private CellStyle STYLE_ALIGNED_LEFT_INTEGER_FORMAT;
    private CellStyle STYLE_ALIGNED_RIGHT_INTEGER_FORMAT;


    private CellStyle headerStyle;

    private short DATE_DATA_FORMAT;
    private short INTEGER_DATA_FORMAT;
    private short BIGDECIMAL_DATA_FORMAT;

    private static final List<String> SMALL_SIZE_COLUMNS = new ArrayList<>(Arrays.asList("DATA HIST", "HIST DATE", "CIF", "CIF CLIENT", "CATEGORIE CLIENT", "COD PRODUS", "DATA DESCHIDERE", "DATA INCHIDERE", "ID UNIT", "ID SUCU", "ID SUCURSALA"));

    public ExcelGenerator() {
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        this.workbook = workbook;
        fetchDataFromInput(model);
        if (this.dataList != null && !this.dataList.isEmpty()) {
            initializeCellStyles();
            extractDetailsFromData();
            workbook = generateReport();
        } else {
            workbook.createSheet("Sheet");
        }
    }

    private void fetchDataFromInput(Map<String, Object> model) {
        this.dataList = (List<Object>) model.get(EXPORT_LIST_NAME);
        if (model.get(EXPORT_CUSTOM_HEADER) != null) {
            customHeader = (List<String>) model.get(EXPORT_CUSTOM_HEADER);
        }
    }

    private void extractDetailsFromData() {
        this.dataListSize = this.dataList.size();
        this.sheetsNo = (this.dataListSize / MAXROWSHEET) + 1;
        this.isUsingCustomHeader = this.customHeader != null;
        //LIST HAS ELEMENTS
        if (this.dataList != null && this.dataListSize > 0) {
            extractInfoForReferenceObjectFieldsAndHeader(this.dataList.get(0).getClass());
        }
        if (this.isUsingCustomHeader) {
            this.header = this.customHeader;
        }

    }

    private void extractInfoForReferenceObjectFieldsAndHeader(Class referenceObject) {
        final Field[] declaredFields = referenceObject.getDeclaredFields();
        setFieldsOptionsAndHeader(declaredFields);
    }

    private void setFieldsOptionsAndHeader(Field[] fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            if (isFieldAnnotated(field, EXCEL_IGNORED_PARAMETER_ANNOTATION)) continue;
            if (!isFieldAnnotated(field, COMPOSED_ID_ANNOTATION)) {
                setFieldsOptionsAndHeader(field);
            } else {
                setFieldsOptionsAndHeader(field.getType().getDeclaredFields());
            }
        }
    }

    private void setFieldsOptionsAndHeader(Field field) {
        processFieldsOptions(field);
        header.add(getHeaderEntryFromField(field));
    }

    private void processFieldsOptions(Field field) {
        ExcelFormatOptions excelFormatOptionsAnnotation = (ExcelFormatOptions) getAnnotation(field, EXCEL_FORMAT_OPTIONS_ANNOTATION.getName());
        this.referenceObjectFieldsOptions.put(field.getName(), getCellStyleFromOptions(excelFormatOptionsAnnotation, field.getType()));
    }

    private FieldOptions getCellStyleFromOptions(ExcelFormatOptions excelFormatOptions, Class objectType) {
        FieldOptions fieldOptions = new FieldOptions();
        String alignment = "";
        if (excelFormatOptions != null) {
            alignment = excelFormatOptions.alignment();
            fieldOptions.setNrGroupSeparation(excelFormatOptions.nrGroupSeparation());
        }

        if (excelFormatOptions != null && excelFormatOptions.format() != DEFAULT_DATA_FORMAT_STYLE) {
            if (excelFormatOptions.format() == BIGDECIMAL_DATA_FORMAT_STYLE) {
                fieldOptions.setStyle(getAlignedBigDecimalStyle(alignment));
            } else if (excelFormatOptions.format() == DATE_DATA_FORMAT_STYLE) {
                fieldOptions.setStyle(getAlignedDateStyle(alignment));
            } else if (excelFormatOptions.format() == INTEGER_DATA_FORMAT_STYLE) {
                fieldOptions.setStyle(getAlignedIntegerStyle(alignment));
            } else {
                fieldOptions.setNrGroupSeparation(false);
                fieldOptions.setStyle(getAlignedSimpleStyle(alignment));
            }
        } else {
            if (objectType.isPrimitive()) {
                if (fieldOptions.isNrGroupSeparation()) {
                    if (objectType.equals(double.class) || objectType.equals(float.class)) {
                        fieldOptions.setStyle(getAlignedBigDecimalStyle(alignment));
                    } else if (objectType.equals(short.class) || objectType.equals(int.class) || objectType.equals(long.class)) {
                        fieldOptions.setStyle(getAlignedIntegerStyle(alignment));
                    } else {
                        fieldOptions.setStyle(getAlignedSimpleStyle(alignment));
                    }
                }else{
                    if (objectType.equals(double.class) || objectType.equals(float.class)
                    ||objectType.equals(short.class) || objectType.equals(int.class) || objectType.equals(long.class)) {
                        fieldOptions.setStyle(getAlignedSimpleNumberStyle(alignment));
                    }  else {
                        fieldOptions.setStyle(getAlignedSimpleStyle(alignment));
                    }
                }
            } else if (objectType.getSuperclass().equals(Number.class)) {
                if (fieldOptions.isNrGroupSeparation()) {
                    if (objectType.equals(Integer.class) || objectType.equals(Long.class) || objectType.equals(Short.class)) {
                        fieldOptions.setStyle(getAlignedIntegerStyle(alignment));
                    } else {
                        fieldOptions.setStyle(getAlignedBigDecimalStyle(alignment));
                    }
                } else {
                    fieldOptions.setStyle(getAlignedSimpleNumberStyle(alignment));
                }

            } else if (objectType.equals(Date.class)) {
                fieldOptions.setStyle(getAlignedDateStyle(alignment));
            } else {
                fieldOptions.setStyle(getAlignedSimpleStyle(alignment));
            }
        }
        return fieldOptions;
    }


    private CellStyle getAlignedSimpleStyle(String alignment) {
        if (alignment != null && alignment.equals(RIGHT_ALIGNMENT)) {
            return STYLE_ALIGNED_RIGHT;
        } else if (alignment != null && alignment.equals(LEFT_ALIGNMENT)) {
            return STYLE_ALIGNED_LEFT;
        } else {
            return STYLE_ALIGNED_CENTER;
        }
    }

    private CellStyle getAlignedSimpleNumberStyle(String alignment) {
        if (alignment != null && alignment.equals(CENTER_ALIGNMENT)) {
            return STYLE_ALIGNED_CENTER;
        } else if (alignment != null && alignment.equals(LEFT_ALIGNMENT)) {
            return STYLE_ALIGNED_LEFT;
        } else {
            return STYLE_ALIGNED_RIGHT;
        }
    }

    private CellStyle getAlignedBigDecimalStyle(String alignment) {
        if (alignment != null && alignment.equals(CENTER_ALIGNMENT)) {
            return STYLE_ALIGNED_CENTER_BIGDECIMAL_FORMAT;
        } else if (alignment != null && alignment.equals(LEFT_ALIGNMENT)) {
            return STYLE_ALIGNED_LEFT_BIGDECIMAL_FORMAT;
        } else {
            return STYLE_ALIGNED_RIGHT_BIGDECIMAL_FORMAT;
        }
    }

    private CellStyle getAlignedIntegerStyle(String alignment) {
        if (alignment != null && alignment.equals(CENTER_ALIGNMENT)) {
            return STYLE_ALIGNED_CENTER_INTEGER_FORMAT;
        } else if (alignment != null && alignment.equals(LEFT_ALIGNMENT)) {
            return STYLE_ALIGNED_LEFT_INTEGER_FORMAT;
        } else {
            return STYLE_ALIGNED_RIGHT_INTEGER_FORMAT;
        }
    }


    private CellStyle getAlignedDateStyle(String alignment) {
        if (alignment != null && alignment.equals(RIGHT_ALIGNMENT)) {
            return STYLE_ALIGNED_RIGHT_DATA_FORMAT;
        } else if (alignment != null && alignment.equals(LEFT_ALIGNMENT)) {
            return STYLE_ALIGNED_LEFT_DATA_FORMAT;
        } else {
            return STYLE_ALIGNED_CENTER_DATA_FORMAT;
        }
    }


    private Workbook generateReport() {
        for (int i = 0; i < this.sheetsNo; i++) {
            Sheet sheet = this.workbook.createSheet("Sheet" + (i + 1));
            setHeader(sheet);
        }
        for (Object obj : this.dataList) {
            addRow(getValuesFromObject(obj));
        }
        setColumnsWidth();
        return this.workbook;
    }

    /**
     * Gets the values from every attribute from object
     * If an attribute is a compose id, gets the values from it
     * And associates a style
     *
     * @param object - from where to get the attributes values
     * @return - list of object values
     */
    @SneakyThrows
    private List<FormatObject> getValuesFromObject(Object object) {
        List<FormatObject> objects = new ArrayList<>();
        if (object == null || object.getClass().getDeclaredFields().length == 0) return objects;
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            boolean isComposeId = isFieldAnnotated(field, COMPOSED_ID_ANNOTATION);
            if (isFieldAnnotated(field, EXCEL_IGNORED_PARAMETER_ANNOTATION)) {
                continue;
            }

            FormatObject objectField = new FormatObject();
            if (field.get(object) instanceof Number) {
                if (globalNrGroupSeparation && this.referenceObjectFieldsOptions.get(field.getName()).isNrGroupSeparation()) {
                    objectField.setObject(field.get(object));
                } else {
                    objectField.setObject(field.get(object).toString());
                }
            } else {
                objectField.setObject(field.get(object));
            }

            if (!isComposeId) {
                objectField.setStyle(this.referenceObjectFieldsOptions.get(field.getName()).getStyle());
                objects.add(objectField);
            } else {
                objects.addAll(getValuesFromObject(objectField.getObject()));
            }
        }
        return objects;
    }


    private void addRow(List<FormatObject> rowElements) {
        if (rowElements == null || rowElements.isEmpty()) return;
        if ((this.currentRowIndex - MAXROWSHEET) == 0) {
            this.currentSheetIndex++;
            setColumnsWidth();
            this.currentRowIndex = DATA_START_ROW_INDEX;
        }

        int column = 0;
        Row row = this.workbook.getSheetAt(this.currentSheetIndex).createRow(this.currentRowIndex++);
        row.setHeight(ROW_HEIGHT);
        for (FormatObject element : rowElements) {
            createAndAddCell(element.getObject(), row, column++, element.getStyle());
        }
    }

    private void createAndAddCell(Object input, Row row, int column, CellStyle cellStyle) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(cellStyle);
        if (input == null) {
            cell.setCellValue(EMPTY);
        } else {
            if (input instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) input).doubleValue());
            } else if (input instanceof Integer) {
                cell.setCellValue((Integer) input);
            } else if (input instanceof Double) {
                cell.setCellValue((Double) input);
            } else if (input instanceof Long) {
                cell.setCellValue((Long) input);
            } else if (input instanceof Float) {
                cell.setCellValue((Float) input);
            } else if (input instanceof Short) {
                cell.setCellValue((Short) input);
            } else if (input instanceof Date) {
                cell.getCellStyle().setDataFormat(DATE_DATA_FORMAT);
                cell.setCellValue((Date) input);
            } else {
                cell.setCellValue(String.valueOf(input));
            }
        }
    }

    private void setHeader(Sheet sheet) {
        Row row = sheet.createRow(HEADER_ROW_INDEX);
        row.setHeight(HEADER_ROW_HEIGHT);
        for (int column = 0; column < this.header.size(); column++) {
            createAndAddCell(header.get(column), row, column, headerStyle);
        }
        sheet.createFreezePane(0, 1); //FREEZE ONLY FIRST ROW WITH HEADER
    }

    private void initializeCellStyles() {
        CreationHelper createHelper = workbook.getCreationHelper();
        DATE_DATA_FORMAT = createHelper.createDataFormat().getFormat("dd/mm/yyyy");
        INTEGER_DATA_FORMAT = createHelper.createDataFormat().getFormat("#,##0");
        BIGDECIMAL_DATA_FORMAT = createHelper.createDataFormat().getFormat("#,##0.00");

        STYLE_ALIGNED_CENTER = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_CENTER, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_LEFT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_LEFT, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_RIGHT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_RIGHT, ALIGN_CENTER_VERTICAL);

        STYLE_ALIGNED_CENTER_DATA_FORMAT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_CENTER, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_CENTER_DATA_FORMAT.setDataFormat(DATE_DATA_FORMAT);
        STYLE_ALIGNED_LEFT_DATA_FORMAT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_LEFT, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_LEFT_DATA_FORMAT.setDataFormat(DATE_DATA_FORMAT);
        STYLE_ALIGNED_RIGHT_DATA_FORMAT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_RIGHT, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_RIGHT_DATA_FORMAT.setDataFormat(DATE_DATA_FORMAT);


        STYLE_ALIGNED_CENTER_BIGDECIMAL_FORMAT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_CENTER, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_CENTER_BIGDECIMAL_FORMAT.setDataFormat(BIGDECIMAL_DATA_FORMAT);
        STYLE_ALIGNED_LEFT_BIGDECIMAL_FORMAT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_LEFT, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_LEFT_BIGDECIMAL_FORMAT.setDataFormat(BIGDECIMAL_DATA_FORMAT);
        STYLE_ALIGNED_RIGHT_BIGDECIMAL_FORMAT = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.WHITE.getIndex(), false, false, ALIGN_RIGHT, ALIGN_CENTER_VERTICAL);
        STYLE_ALIGNED_RIGHT_BIGDECIMAL_FORMAT.setDataFormat(BIGDECIMAL_DATA_FORMAT);

        STYLE_ALIGNED_CENTER_INTEGER_FORMAT = STYLE_ALIGNED_CENTER;
        STYLE_ALIGNED_CENTER_INTEGER_FORMAT.setDataFormat(INTEGER_DATA_FORMAT);
        STYLE_ALIGNED_LEFT_INTEGER_FORMAT = STYLE_ALIGNED_LEFT;
        STYLE_ALIGNED_LEFT_INTEGER_FORMAT.setDataFormat(INTEGER_DATA_FORMAT);
        STYLE_ALIGNED_RIGHT_INTEGER_FORMAT = STYLE_ALIGNED_RIGHT;
        STYLE_ALIGNED_RIGHT_INTEGER_FORMAT.setDataFormat(INTEGER_DATA_FORMAT);

        headerStyle = ExcelUtils.createFormat(this.workbook, (short) 11, IndexedColors.GREY_25_PERCENT.getIndex(), false, true, ALIGN_CENTER, ALIGN_CENTER_VERTICAL);
    }

    /**
     * set every column width  from sheet
     */
    private void setColumnsWidth() {
        for (int i = 0; i <= header.size() - 1; i++) {
            if (header.get(i) != null && SMALL_SIZE_COLUMNS.contains(header.get(i).trim())) {
                this.workbook.getSheetAt(currentSheetIndex).setColumnWidth(i, SMALL_COLUMN_WIDTH);
            } else {
                this.workbook.getSheetAt(currentSheetIndex).setColumnWidth(i, DEFAULT_COLUMN_WIDTH);
            }
        }
    }


    private String getHeaderEntryFromField(Field field) {
        String name = field.getName();
        String columnName = formatCamelCaseString(name);
        if (isFieldAnnotated(field, EXCEL_CUSTOM_COLUMN_NAME_ANNOTATION)) {
            columnName = getCustomColumnName(field);
        }
        return columnName;
    }

    private boolean isFieldAnnotated(Field field, String annotationFullPath) {
        if (getAnnotation(field, annotationFullPath) != null) {
            return true;
        } else {
            return false;
        }
    }

    private Annotation getAnnotation(Field field, String annotationFullPath) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(annotationFullPath)) {
                return annotation;
            }
        }
        return null;
    }


    private String getCustomColumnName(Field field) {
        final ExcelCustomColumnName annotation = field.getAnnotation(ExcelCustomColumnName.class);
        return annotation.name();
    }

    public void setGlobalNrGroupSeparation(boolean globalNrGroupSeparation) {
        this.globalNrGroupSeparation = globalNrGroupSeparation;
    }

    public static String formatCamelCaseString(String string) {
        String splitString = "";
        String[] splitVector = string.split("(?<=[a-z])(?=[A-Z])");
        for (String word : splitVector) {
            splitString = splitString + word.toUpperCase() + " ";
        }
        return splitString;
    }

    @Getter
    @Setter
    class FormatObject {
        Object object;
        CellStyle style;
    }

    @Getter
    @Setter
    class FieldOptions {
        private CellStyle style;
        private boolean nrGroupSeparation;

        private FieldOptions() {
            this.nrGroupSeparation = globalNrGroupSeparation;
        }
    }
}
