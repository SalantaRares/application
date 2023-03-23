package com.application.db2.app.repository.query.forestryEngineer;


public class ForestryEngineerQueries {
    public static final String GET_ALL_INFO = "SELECT NUME,\n" +
            "        PRENUME,\n" +
            "        CNP,\n" +
            "        DATA_RIDICARE_PORTIE,\n" +
            "        DATA_SCADENTA_RIDICARE_PORTIE,\n" +
            "        CANTITATE_TOTALA,\n" +
            "        UM,\n" +
            "        IMPUTERNICIT,\n" +
            "        PORTIE_AN,\n" +
            "        PORTIE_RAMASA_ANII_ANTERIORI,\n" +
            "        HECTARE_DETINUTE,\n" +
            "        PORTIE_RESTANTA,\n" +
            "        CNP_IMPUTERNICIT,\n" +
            "        ANII_RESTANTA\n" +
            "FROM UTILIZATORI_RARES";

    public static final String GET_EXPIRY_TODAY_DATE = "SELECT NUME,\n" +
            "        PRENUME,\n" +
            "        CNP,\n" +
            "        DATA_RIDICARE_PORTIE,\n" +
            "        DATA_SCADENTA_RIDICARE_PORTIE,\n" +
            "        CANTITATE_TOTALA,\n" +
            "        UM,\n" +
            "        IMPUTERNICIT,\n" +
            "        PORTIE_AN,\n" +
            "        PORTIE_RAMASA_ANII_ANTERIORI,\n" +
            "        HECTARE_DETINUTE,\n" +
            "        PORTIE_RESTANTA,\n" +
            "        CNP_IMPUTERNICIT,\n" +
            "        ANII_RESTANTA\n" +
            "FROM UTILIZATORI_RARES where TO_DATE(DATA_SCADENTA_RIDICARE_PORTIE)=TO_DATE(SYSDATE)";
    public static final String P_DATE = "p_date";
    public static final String GET_EXPIRY_BY_GIVEN_DATE = "SELECT NUME,\n" +
            "        PRENUME,\n" +
            "        CNP,\n" +
            "        DATA_RIDICARE_PORTIE,\n" +
            "        DATA_SCADENTA_RIDICARE_PORTIE,\n" +
            "        CANTITATE_TOTALA,\n" +
            "        UM,\n" +
            "        IMPUTERNICIT,\n" +
            "        PORTIE_AN,\n" +
            "        PORTIE_RAMASA_ANII_ANTERIORI,\n" +
            "        HECTARE_DETINUTE,\n" +
            "        PORTIE_RESTANTA,\n" +
            "        CNP_IMPUTERNICIT,\n" +
            "        ANII_RESTANTA\n" +
            "FROM UTILIZATORI_RARES where DATA_SCADENTA_RIDICARE_PORTIE>=:" + P_DATE + " ";
    public static final String SEQ_LOGS_UTILIZATORI_RARES = "SELECT SEQ_LOGS_UTILIZATORI_RARES.NEXTVAL FROM DUAL";
    public static final String P_CNP = "p_cnp";
    public static final String GET_ANII_RESTANTA = "SELECT ANII_RESTANTA FROM utilizatori_rares WHERE CNP = :" + P_CNP + "";
}

