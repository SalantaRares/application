package com.application.db2.app.repository.query.users;

public class UsersQueries {
    public static final String P_USERNAME = "p_username";
    public static final String P_PASSWORD = "p_password";
    public static final String GET_USER_BY_USERNAME_AND_PASS = "\n" +
            "select USERNAME,\n" +
            "PASSWORD,\n" +
            "USER_TYPE,\n" +
            "EMAIL_CONTACT,\n" +
            "VALIDATION,\n" +
            "VALABILITY,\n" +
            "APPROVER_EMAIL,\n" +
            "FIRST_NAME,\n" +
            "LAST_NAME,\n" +
            "LOCATION\n" +
            "from DEV_ADF_APPL.useri_utilizatori_rares where username=:" + P_USERNAME + " and password=:" + P_PASSWORD + "";
    public static final String GET_EXISTING_USER = "select username from  DEV_ADF_APPL.useri_utilizatori_rares where username=:" + P_USERNAME + "";
}
