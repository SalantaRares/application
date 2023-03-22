package com.application.db2.app.entities;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "LOGS_UTILIZATORI_RARES", schema = "DEV_ADF_APPL")
public class LogsUtilizatoriRaresEntity {
    @Id
    @Column(name = "PK_COLUMN")
    private BigDecimal pkColumn;
    @Column(name = "COLUMN_NAME")
    private String columnName;
    @Column(name = "OLD_VALUE")
    private String oldValue;
    @Column(name = "NEW_VALUE")
    private String newValue;
    @Column(name = "OPERATION")
    private String operation;
    @Column(name = "BY_USER")
    private String byUser;
    @Column(name = "OPERATION_DATE")
    private String operationDate;
    @Column(name = "TABLE_NAME")
    private String tableName;

    public LogsUtilizatoriRaresEntity() {
    }

    public LogsUtilizatoriRaresEntity(BigDecimal pkColumn,
                                      String columnName,
                                      String oldValue,
                                      String newValue,
                                      String operation,
                                      String byUser,
                                      String operationDate,
                                      String tableName) {
        this.pkColumn = pkColumn;
        this.columnName = columnName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.operation = operation;
        this.byUser = byUser;
        this.operationDate = operationDate;
        this.tableName = tableName;
    }
}

