package com.application.util;

public class ApplicationQueries {
    public static String GET_LABELS_FOR_MENU = "" +
            "select " +
            "ID," +
            "LABEL AS NAME " +
            "from BT_ADF_APPL.ADF_PORTAL_MENUS " +
            "where menu_type ='MENU' order by label asc";

    public static String GET_LABELS_BY_MENU_ID = "select " +
            "ID," +
            "LABEL AS NAME, " +
            "APP_LINK AS URL " +
            "from BT_ADF_APPL.ADF_PORTAL_MENUS " +
            "where menu_type ='ITEM' " +
            "AND MENU_ID = :p_meniu_id order by label asc";
}
