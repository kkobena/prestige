package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
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

    public static String convertDateToDD_MM_YYYY(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (date != null) {
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String convertDateToDD_MM_YYYY_HH_mm(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (date != null) {
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String convertDateToISO(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String convertDate(Date date, SimpleDateFormat simpleDateFormat) {

        if (date != null) {
            return simpleDateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String convertToString(Date date, SimpleDateFormat simpleDateFormat) {

        if (Objects.isNull(date)) {
            date = new Date();
        }
        return simpleDateFormat.format(date);
    }

    public static String convertToString(LocalDate date, DateTimeFormatter dateTimeFormatter) {

        if (Objects.isNull(date)) {
            date = LocalDate.now();
        }
        return dateTimeFormatter.format(date);
    }

    public static LocalDate convertStringToLocalDate(String date, DateTimeFormatter dateTimeFormatter) {

        if (StringUtils.isNotEmpty(date)) {
            return LocalDate.parse(date, dateTimeFormatter);
        }
        return LocalDate.now();
    }

    private DateUtil() {
    }

}
