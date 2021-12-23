package ro.btrl.miswebappspringdemo.excel.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExcelParserExample2 {
    private Date histDate;
    private String customerId;
    private String isCustomer;
    private String custCategory;
    private String unitId;
    private String productId;
    private String refNo;
    private String purchaseCcy;
    private BigDecimal purchaseAmount;
    private String saleCcy;
    private BigDecimal saleAmount;
    private String marginForex;
    private Date valueDate;
    private String origination;
    private BigDecimal negociated;
    private BigDecimal xPurchase;
}
