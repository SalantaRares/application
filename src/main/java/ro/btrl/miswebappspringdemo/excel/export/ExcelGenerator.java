package ro.btrl.miswebappspringdemo.excel.export;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelCustomColumnName;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelFormatOptions;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelIgnoreParam;
import ro.btrl.miswebappspringdemo.exceptions.CustomException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExcelGenerator extends AbstractXlsxStreamingView {

    //CONSTANTS
    public static final String EXPORT_LIST_NAME = "objects";
    public static final String EXPORT_CUSTOM_ATTRIBUTES = "ATTRIBUTES_MAPPING";
    public static final String EXPORT_SMALL_SIZE_COLUMNS = "SMALL_COLUMNS";
    public static final String CENTER_ALIGNMENT = "CENTER";
    public static final String LEFT_ALIGNMENT = "LEFT";
    public static final String RIGHT_ALIGNMENT = "RIGHT";

    public static final int DEFAULT_DATA_FORMAT_STYLE = 0;
    public static final int STRING_DATA_FORMAT_STYLE = 1;
    public static final int DATE_DATA_FORMAT_STYLE = 2;
    public static final int INTEGER_DATA_FORMAT_STYLE = 3;
    public static final int BIGDECIMAL_DATA_FORMAT_STYLE = 4;

    private final String COMPOSED_ID_ANNOTATION = javax.persistence.EmbeddedId.class.getName();
    private final String EXCEL_IGNORED_PARAMETER_ANNOTATION = ExcelIgnoreParam.class.getName();
    private final String EXCEL_CUSTOM_COLUMN_NAME_ANNOTATION = ExcelCustomColumnName.class.getName();
    private final Class<ExcelFormatOptions> EXCEL_FORMAT_OPTIONS_ANNOTATION = ExcelFormatOptions.class;

    private final short HEIGHT_FACTOR = 20;
    private final short HEADER_ROW_HEIGHT = 32 * HEIGHT_FACTOR;
    private final short ROW_HEIGHT = 15 * HEIGHT_FACTOR;

    private final short WIDTH_FACTOR = 263;
    private final short DEFAULT_COLUMN_WIDTH = 30 * WIDTH_FACTOR;
    private final short SMALL_COLUMN_WIDTH = 11 * WIDTH_FACTOR;


    private final int HEADER_ROW_INDEX = 0;
    private final int DATA_START_ROW_INDEX = 1;

    private final HorizontalAlignment ALIGN_CENTER = HorizontalAlignment.CENTER;
    private final VerticalAlignment ALIGN_CENTER_VERTICAL = VerticalAlignment.CENTER;
    private final HorizontalAlignment ALIGN_LEFT = HorizontalAlignment.LEFT;
    private final HorizontalAlignment ALIGN_RIGHT = HorizontalAlignment.RIGHT;


    //REPORT DATA OBJECTS
    private List<Object> dataList = null;
    private List<String> customAttributes = null;

    //EXCEL RELATED ATTRIBUTES
    private Workbook workbook = null;
    private int sheetsNo = 0;
    private final int MAXROWSHEET = 1000000;
    private final List<FieldOptions> referenceObjectFieldsOptions = new ArrayList<>();
    private final List<String> header = new ArrayList<>();
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

    private static List<String> SMALL_SIZE_COLUMNS;

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
        if (model.get(EXPORT_CUSTOM_ATTRIBUTES) != null) {
            customAttributes = (List<String>) model.get(EXPORT_CUSTOM_ATTRIBUTES);
        }
        if (model.get(EXPORT_SMALL_SIZE_COLUMNS) != null) {
            SMALL_SIZE_COLUMNS = (List<String>) model.get(EXPORT_SMALL_SIZE_COLUMNS);
        } else {
            SMALL_SIZE_COLUMNS = new ArrayList<>();
        }
    }

    private void extractDetailsFromData() {
        this.sheetsNo = (this.dataList.size() / MAXROWSHEET) + 1;
        //LIST HAS ELEMENTS
        if (this.dataList.size() > 0) {
            setFieldsOptionsAndHeader(this.dataList.get(0).getClass().getDeclaredFields(), false);
        }
    }

    private void setFieldsOptionsAndHeader(Field[] fields, boolean isInComposite) {
        for (Field field : fields) {
            field.setAccessible(true);
            if (isFieldAnnotated(field, EXCEL_IGNORED_PARAMETER_ANNOTATION)) continue;
            if (!isFieldAnnotated(field, COMPOSED_ID_ANNOTATION)) {
                if (!isAttributeOfInterest(field.getName())) {
                    continue;
                }
                setFieldsOptionsAndHeader(field, isInComposite);
            } else {
                setFieldsOptionsAndHeader(field.getType().getDeclaredFields(), true);
            }
        }
    }

    private boolean isAttributeOfInterest(String fieldName) {
        return customAttributes == null || customAttributes.isEmpty() || customAttributes.contains(fieldName);
    }

    private void setFieldsOptionsAndHeader(Field field, boolean isInComposite) {
        processFieldsOptions(field, isInComposite);
        header.add(getHeaderEntryFromField(field));
    }

    private void processFieldsOptions(Field field, boolean isInComposite) {
        ExcelFormatOptions excelFormatOptionsAnnotation = (ExcelFormatOptions) getAnnotation(field, EXCEL_FORMAT_OPTIONS_ANNOTATION.getName());
        FieldOptions fieldOptions = getCellStyleFromOptions(excelFormatOptionsAnnotation, field.getType());
        fieldOptions.setInComposite(isInComposite);
        fieldOptions.setFieldName(field.getName());
        this.referenceObjectFieldsOptions.add(fieldOptions);
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
                } else {
                    if (objectType.equals(double.class) || objectType.equals(float.class)
                            || objectType.equals(short.class) || objectType.equals(int.class) || objectType.equals(long.class)) {
                        fieldOptions.setStyle(getAlignedSimpleNumberStyle(alignment));
                    } else {
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
     * Gets the values from  attribute of interest from object
     * If an attribute is a composite id, gets the values from it
     * And associates a style
     *
     * @param object - from where to get the attributes values
     * @return - list of object values and cell styles
     */
    private List<RowDetails> getValuesFromObject(Object object) {
        List<RowDetails> rowDetails = new ArrayList<>();
        for (FieldOptions fieldOptions : referenceObjectFieldsOptions) {
            RowDetails rowValue = new RowDetails();
            try {
                if (fieldOptions.isInComposite) {
                    Object composite = getCompositeIdFromOject(object);
                    rowValue.setObject(getObjectValueByField(composite.getClass().getDeclaredField(fieldOptions.fieldName), composite, fieldOptions.isNrGroupSeparation()));
                } else {
                    rowValue.setObject(getObjectValueByField(object.getClass().getDeclaredField(fieldOptions.fieldName), object, fieldOptions.isNrGroupSeparation()));
                }
                rowValue.setStyle(fieldOptions.getStyle());
                rowDetails.add(rowValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new CustomException("Eroare la extragerea valorilor din obiecte!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return rowDetails;
    }

    private Object getCompositeIdFromOject(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (isFieldAnnotated(field, COMPOSED_ID_ANNOTATION)) {
                return field.get(object);
            }
        }
        return null;

    }


    private Object getObjectValueByField(Field field, Object object, boolean nrGroupSeparation) {
        try {
            field.setAccessible(true);
            if (field.get(object) instanceof Number) {
                if (globalNrGroupSeparation && nrGroupSeparation) {
                    return field.get(object);
                } else {
                    return field.get(object).toString();
                }
            } else {
                return field.get(object);
            }
        } catch (IllegalAccessException e) {
            throw new CustomException("Eroare la extragerea valorilor din obiecte!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addRow(List<RowDetails> rowElements) {
        if (rowElements == null || rowElements.isEmpty()) return;
        if ((this.currentRowIndex - MAXROWSHEET) == 0) {
            this.currentSheetIndex++;
            setColumnsWidth();
            this.currentRowIndex = DATA_START_ROW_INDEX;
        }

        int column = 0;
        Row row = this.workbook.getSheetAt(this.currentSheetIndex).createRow(this.currentRowIndex++);
        row.setHeight(ROW_HEIGHT);
        for (RowDetails element : rowElements) {
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
    static class RowDetails {
        private CellStyle style;
        Object object;
    }

    @Getter
    @Setter
    class FieldOptions {
        String fieldName;
        private CellStyle style;
        private boolean nrGroupSeparation;
        private boolean isInComposite;

        private FieldOptions() {
            this.nrGroupSeparation = globalNrGroupSeparation;
        }
    }
}
