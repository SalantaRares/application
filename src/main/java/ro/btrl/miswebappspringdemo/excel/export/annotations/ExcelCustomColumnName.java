package ro.btrl.miswebappspringdemo.excel.export.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCustomColumnName {
    String name() default "-";
}
