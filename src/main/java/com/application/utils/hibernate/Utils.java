package com.application.utils.hibernate;

import com.application.excel.inport.ExcelToObjectMapper;
import com.application.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public static Object extractFromExcel(MultipartFile excelFile, Class classToExtarct, boolean duplicatesCheck) {
        if (excelFile == null) {
            throw new CustomException(Messages.FILE_NOT_NULL, HttpStatus.BAD_REQUEST);
        }
        ExcelToObjectMapper excel;
        try {
            excel = new ExcelToObjectMapper(excelFile.getInputStream(), duplicatesCheck);
            return excel.getDataFromXlsx(classToExtarct, 0);
        } catch (IOException e) {
            throw new CustomException("Eroare la extragerea fisierului din MultipartFile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static String formatStringtoCamelcase(String value) {
        value = value.replaceAll("[!\"#$%&'()*+.,/:;<=>?@\\^_`{|}~-]", " ");
        String[] arr = value.split(" ");
        String newValue = "";
        for (String ss : arr) {
            if (ss != null && !ss.isEmpty()) {
                if (!newValue.isEmpty()) {
                    newValue = newValue + ss.substring(0, 1).toUpperCase() + ss.substring(1).toLowerCase();
                } else {
                    newValue = ss.toLowerCase();
                }
            }
        }
        return newValue;
    }
}
