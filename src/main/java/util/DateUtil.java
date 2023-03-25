/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author koben
 */
public final class DateUtil {

    public static LocalDate getLastMonthFromDate(LocalDate date) {
        Objects.requireNonNull(date, "Le parametre de doit pas être null");
        LocalDate lastMonth = date.minusMonths(1);
        return LocalDate.of(lastMonth.getYear(), lastMonth.getMonth(), lastMonth.lengthOfMonth());
    }

    public static LocalDate getLastMonthFromNow() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        return LocalDate.of(lastMonth.getYear(), lastMonth.getMonth(), lastMonth.lengthOfMonth());
    }

    public static LocalDate getNthLastMonthFromNow(int nthnMoth) {
        LocalDate lastMonth = LocalDate.now().minusMonths(nthnMoth);
        return LocalDate.of(lastMonth.getYear(), lastMonth.getMonth(), 1);
    }

    public static String convertDateToDD_MM_YYYY_HH_mm(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (date != null) {
            return dateFormat.format(date);
        } else {
            return "";
        }
    }
    private DateUtil() {
    }

}
