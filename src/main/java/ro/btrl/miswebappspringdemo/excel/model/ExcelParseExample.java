package ro.btrl.miswebappspringdemo.excel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ExcelParseExample {
    String name;
    Date birthDate;
    BigDecimal salary;
}
