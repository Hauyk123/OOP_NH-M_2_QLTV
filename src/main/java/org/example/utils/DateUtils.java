package org.example.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final String FORMAT = "dd/MM/yyyy";

    public static String format(Date date) {
        return new SimpleDateFormat(FORMAT).format(date);
    }

    public static Date parse(String str) {
        try {
            return new SimpleDateFormat(FORMAT).parse(str);
        } catch (Exception e) {
            return null;
        }
    }
} 