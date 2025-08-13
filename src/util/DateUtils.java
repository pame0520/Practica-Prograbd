/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.time.LocalDate;

/**
 *
 * @author pame
 */
public class DateUtils {
    private static final String PATTERN = "yyyy-MM-dd";
    public static DateFormatter getFormatter() {
        return new DateFormatter(new SimpleDateFormat(PATTERN));
    }
    public static LocalDate parse(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return LocalDate.parse(s.trim()); }
        catch (DateTimeParseException e) { return null; }
    }
}