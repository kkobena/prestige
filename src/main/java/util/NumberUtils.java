
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
}
