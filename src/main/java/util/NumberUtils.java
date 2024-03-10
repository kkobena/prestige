package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author koben
 */
public final class NumberUtils {

    public static String formatLongToString(long number) {
        String result = "0";
        try {

            DecimalFormatSymbols amountSymbols = new DecimalFormatSymbols();

            amountSymbols.setGroupingSeparator(' ');

            DecimalFormat amountFormat = new DecimalFormat("###,###", amountSymbols);
            result = amountFormat.format(number);
        } catch (NumberFormatException ex) {

        }
        return result;
    }

    public static String formatIntToString(int number) {
        String result = "0";
        try {

            DecimalFormatSymbols amountSymbols = new DecimalFormatSymbols();

            amountSymbols.setGroupingSeparator(' ');

            DecimalFormat amountFormat = new DecimalFormat("###,###", amountSymbols);
            result = amountFormat.format(number);
        } catch (NumberFormatException ex) {

        }
        return result;
    }

    public static Integer arrondiModuloOfNumber(Integer nbre, Integer modulo) {
        int result = 0;
        int tempModulo;
        float part;
        String tempV;
        try {

            tempV = String.valueOf(nbre).substring(String.valueOf(nbre).length() - 1, String.valueOf(nbre).length());

            tempModulo = Integer.valueOf(tempV) % modulo;

            part = (modulo - 1) / 2;

            result = ((part >= tempModulo) ? (nbre - tempModulo) : ((nbre - tempModulo) + modulo));

        } catch (NumberFormatException e) {

        }
        return result;
    }
}
