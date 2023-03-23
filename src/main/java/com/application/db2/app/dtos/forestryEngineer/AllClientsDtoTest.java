package com.application.db2.app.dtos.forestryEngineer;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AllClientsDtoTest {
    private String nume;
    private String prenume;
    private String cnp;
    private Date dataRidicarePortie;
    private Date dataScadentaRidicarePortie;
    private BigDecimal cantitateTotala;
    private String um;
    private String imputernicit;
    private BigDecimal portieAn;
    private BigDecimal portieRamasaAniiAnteriori;
    private BigDecimal hectareDetinute;
    private BigDecimal portieRestanta;
    private String cnpImputernicit;
    private String aniiRestanta;
}