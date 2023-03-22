package com.application.utils.hibernate;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String dateToYYYYMMDD(Long date) {
        if (date == null) return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(new Date(date));
    }

    public static String getUserNameFromPrincipal(Principal principal) {
        String username = principal.getName();
        return username.substring(0, username.indexOf("@"));
    }
}
