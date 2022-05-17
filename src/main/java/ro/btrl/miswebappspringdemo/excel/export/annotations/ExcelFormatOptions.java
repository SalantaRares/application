package ro.btrl.miswebappspringdemo.excel.export.annotations;

import ro.btrl.miswebappspringdemo.excel.export.ExcelGenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelFormatOptions {
    boolean nrGroupSeparation() default true;

    String alignment() default "";

    int format() default ExcelGenerator.DEFAULT_DATA_FORMAT_STYLE;

    String customFormatStyle() default "";

    short dimension() default ExcelGenerator.DEFAULT_COLUMN_WIDTH;
}
