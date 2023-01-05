package com.application.app.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AllClients {
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
