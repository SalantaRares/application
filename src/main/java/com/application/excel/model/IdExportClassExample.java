package com.application.excel.model;

import com.application.excel.export.ExcelGenerator;
import com.application.excel.export.annotations.ExcelFormatOptions;
import com.application.excel.export.annotations.ExcelIgnoreParam;
import lombok.Data;

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
