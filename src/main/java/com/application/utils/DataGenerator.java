package com.application.utils;

import org.springframework.http.HttpStatus;
import com.application.exceptions.CustomException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataGenerator {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static List generatePopulatedObjects(Class type, int elementsNo) {
        if (type == null) {
            throw new CustomException("Tipul obiectelor trebuie specificat!", HttpStatus.BAD_REQUEST);
        }
        List list = new ArrayList();
        for (int i = 0; i < elementsNo; i++) {
            list.add(createObject(type));
        }

        return list;
    }

    private static Object createObject(Class type) {
        Object o;
        try {
            o = type.newInstance();

            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(short.class)) {
                    field.setShort(o, (short) generateRandomInt(0, 10));
                } else if (field.getType().equals(byte.class)) {
                    field.setByte(o, (byte) generateRandomInt(0, 10));
                } else {
                    field.set(o, returnFieldValue(field));
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CustomException("Eroare la generarea obiectelor", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return o;
    }

    private static Object returnFieldValue(Field field) {
        int maxNr = 10000;
        if (field.getType().equals(String.class)) {
            return generateRandomString();
        } else if (Number.class.isAssignableFrom(field.getType())) {
            if (field.getType().equals(BigDecimal.class)) {
                return new BigDecimal(generateRandomInt(1, maxNr));
            } else if (field.getType().equals(Float.class)) {
                return (float) generateRandomInt(1, maxNr);
            } else if (field.getType().equals(Double.class)) {
                return (double) generateRandomInt(1, maxNr);
            } else if (field.getType().equals(Long.class)) {
                return (long) generateRandomInt(1, maxNr);
            }
            return generateRandomInt(1, maxNr);
        } else if (field.getType().equals(Date.class)) {
            return new Date(System.currentTimeMillis() + generateRandomInt(-100000, 100000));
        } else if (field.getType().equals(Boolean.class)) {
            return generateRandomInt(0, 1) != 0;
        } else if (field.getType().isPrimitive()) {
            if (field.getType().equals(int.class) || field.getType().equals(double.class) || field.getType().equals(long.class)
                    || field.getType().equals(float.class)) {
                return generateRandomInt(1, maxNr);
            } else if (field.getType().equals(boolean.class)) {
                return generateRandomInt(0, 1) != 0;
            } else if (field.getType().equals(char.class)) {
                return AB.charAt(generateRandomInt(0, AB.length() - 1));
            }
            return null;
        } else if (field.getType() == byte[].class) {
            return populateBytesArray(new byte[generateRandomInt(1, 8)]);
        } else {
            return createObject(field.getType());
        }

    }

    private static byte[] populateBytesArray(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) generateRandomInt(1, 9);
        }
        return array;
    }

    private static String generateRandomString() {
        int lenght = 8;
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(lenght);
        for (int i = 0; i < lenght; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private static int generateRandomInt(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }


}
