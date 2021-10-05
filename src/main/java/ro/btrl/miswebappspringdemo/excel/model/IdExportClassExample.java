package ro.btrl.miswebappspringdemo.excel.model;

import lombok.Data;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelFormatOptions;
import ro.btrl.miswebappspringdemo.excel.export.annotations.ExcelIgnoreParam;
import ro.btrl.miswebappspringdemo.excel.export.ExcelGenerator;

import java.io.Serializable;
import java.util.Date;

@Data
public class IdExportClassExample implements Serializable {
    Date attributeDate;
    @ExcelFormatOptions(alignment = ExcelGenerator.LEFT_ALIGNMENT)
    String attributeString;
    @ExcelIgnoreParam
    String attributeString2;
}
