package com.application.db2.app.dtos.forestryEngineer;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AllClientsDto {
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

    public AllClientsDto() {
    }

    public AllClientsDto(String nume,
                         String prenume,
                         String cnp,
                         Date dataRidicarePortie,
                         Date dataScadentaRidicarePortie,
                         BigDecimal cantitateTotala,
                         String um,
                         String imputernicit,
                         BigDecimal portieAn,
                         BigDecimal portieRamasaAniiAnteriori,
                         BigDecimal hectareDetinute,
                         BigDecimal portieRestanta,
                         String cnpImputernicit,
                         String aniiRestanta) {
        this.nume = nume;
        this.prenume = prenume;
        this.cnp = cnp;
        this.dataRidicarePortie = dataRidicarePortie;
        this.dataScadentaRidicarePortie = dataScadentaRidicarePortie;
        this.cantitateTotala = cantitateTotala;
        this.um = um;
        this.imputernicit = imputernicit;
        this.portieAn = portieAn;
        this.portieRamasaAniiAnteriori = portieRamasaAniiAnteriori;
        this.hectareDetinute = hectareDetinute;
        this.portieRestanta = portieRestanta;
        this.cnpImputernicit = cnpImputernicit;
        this.aniiRestanta = aniiRestanta;
    }
}
