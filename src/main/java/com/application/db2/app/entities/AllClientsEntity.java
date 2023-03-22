package com.application.db2.app.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import com.application.db2.app.dtos.AllClientsDtoTest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Data
@Table(schema = "DEV_ADF_APPL", name = "UTILIZATORI_RARES")
@Entity
@AllArgsConstructor
public class AllClientsEntity {
    @Column(name = "NUME")
    private String nume;
    @Column(name = "PRENUME\t")
    private String prenume;
    @Column(name = "CNP")
    @Id
    private String cnp;
    @Column(name = "DATA_RIDICARE_PORTIE")
    private Date dataRidicarePortie;
    @Column(name = "DATA_SCADENTA_RIDICARE_PORTIE")
    private Date dataScadentaRidicarePortie;
    @Column(name = "CANTITATE_TOTALA")
    private BigDecimal cantitateTotala;
    @Column(name = "UM")
    private String um;
    @Column(name = "IMPUTERNICIT")
    private String imputernicit;
    @Column(name = "PORTIE_AN")
    private BigDecimal portieAn;
    @Column(name = "PORTIE_RAMASA_ANII_ANTERIORI")
    private BigDecimal portieRamasaAniiAnteriori;
    @Column(name = "HECTARE_DETINUTE")
    private BigDecimal hectareDetinute;
    @Column(name = "PORTIE_RESTANTA")
    private BigDecimal portieRestanta;
    @Column(name = "CNP_IMPUTERNICIT")
    private String cnpImputernicit;
    @Column(name = "ANII_RESTANTA")
    private String aniiRestanta;

    public AllClientsEntity() {
        }

    public AllClientsEntity(String nume,
                String prenume,
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

    public AllClientsEntity(AllClientsDtoTest entry) {

    }
//
//    public AllClientsEntity(AllClientsDtoTest dto) {
//        if (Objects.nonNull(dto.getNume())) {
//            this.setNume(dto.getNume());
//        }
//        if (Objects.nonNull(dto.getPrenume())) {
//            this.setPrenume(dto.getPrenume());
//        }
//        if (Objects.nonNull(dto.getDataRidicarePortie())) {
//            this.setDataRidicarePortie(dto.getDataRidicarePortie());
//        }
//        if (Objects.nonNull(dto.getDataScadentaRidicarePortie())) {
//            this.setDataScadentaRidicarePortie(dto.getDataScadentaRidicarePortie());
//        }
//        if (Objects.nonNull(dto.getCantitateTotala())) {
//            this.setCantitateTotala(dto.getCantitateTotala());
//        }
//        if (Objects.nonNull(dto.getUm())) {
//            this.setUm(dto.getUm());
//        }
//        if (Objects.nonNull(dto.getImputernicit())) {
//            this.setImputernicit(dto.getImputernicit());
//        }
//        if (Objects.nonNull(dto.getPortieAn())) {
//            this.setPortieAn(dto.getPortieAn());
//        }
//        if (Objects.nonNull(dto.getPortieRamasaAniiAnteriori())) {
//            this.setPortieRamasaAniiAnteriori(dto.getPortieRamasaAniiAnteriori());
//        }
//        if (Objects.nonNull(dto.getHectareDetinute())) {
//            this.setHectareDetinute(dto.getHectareDetinute());
//        }
//        if (Objects.nonNull(dto.getPortieRestanta())) {
//            this.setPortieRestanta(dto.getPortieRestanta());
//        }
//        if (Objects.nonNull(dto.getCnpImputernicit())) {
//            this.setCnpImputernicit(dto.getCnpImputernicit());
//        }
//        if (Objects.nonNull(dto.getAniiRestanta())) {
//            this.setAniiRestanta(dto.getAniiRestanta());
//        }
//    }

    public Boolean isValid() {
        return
                Objects.isNull(this.getNume()) ||
                        Objects.isNull(this.getPrenume()) ||
                        Objects.isNull(this.getDataRidicarePortie()) ||
                        Objects.isNull(this.getDataScadentaRidicarePortie()) ||
                        Objects.isNull(this.getCantitateTotala()) ||
                        Objects.isNull(this.getUm()) ||
                        Objects.isNull(this.getImputernicit()) ||
                        Objects.isNull(this.getPortieAn()) ||
                        Objects.isNull(this.getPortieRamasaAniiAnteriori()) ||
                        Objects.isNull(this.getHectareDetinute()) ||
                        Objects.isNull(this.getPortieRestanta()) ||
                        Objects.isNull(this.getCnpImputernicit()) ||
                        Objects.isNull(this.getAniiRestanta());
    }
}

