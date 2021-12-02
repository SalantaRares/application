package ro.btrl.miswebappspringdemo.excel.model;

import lombok.Data;
import ro.btrl.miswebappspringdemo.excel.export.ExcelGenerator;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelCustomColumnName;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelFormatOptions;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelIgnoreParam;

import javax.persistence.EmbeddedId;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExportClassExample {
    @EmbeddedId
    IdExportClassExample compositeId;
    String attributeOneString;
    @ExcelIgnoreParam
    String attributeTwoString;
    Integer attributeThreeInteger;
    @ExcelCustomColumnName(name = "Custom Name For Attribute")
    @ExcelFormatOptions(alignment = ExcelGenerator.RIGHT_ALIGNMENT)
    String attributeFourString;
    boolean attributeFiveBoolean;
    @ExcelFormatOptions(format = ExcelGenerator.DATE_TIME_DATA_FORMAT_STYLE)
    Date attributeSixDate;
    @ExcelFormatOptions(format = ExcelGenerator.STRING_DATA_FORMAT_STYLE)
    Integer attributeSevenInteger;
    @ExcelFormatOptions(format = ExcelGenerator.INTEGER_DATA_FORMAT_STYLE)
    String attributeEightString;
    long attributeNineLong;
    int attributeTenInt;
    short attributeElevenShort;
    byte attributeTwelveByte;
    double attributeThirteenDouble;
    float attributeFourteenFloat;
    @ExcelFormatOptions(nrGroupSeparation = false)
    double attributeFifteenDouble;
    Boolean attributeSixteenBoolean;
    @ExcelFormatOptions(format = ExcelGenerator.CUSTOM_DATA_FORMAT_STYLE, customFormatStyle ="###,###,###,##0.00%", alignment = ExcelGenerator.RIGHT_ALIGNMENT)
    BigDecimal attributeSeventeenBigDecimal;
}
