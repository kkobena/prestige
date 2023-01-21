package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author koben
 */
public final class DateCommonUtils {

    private static final String DATE_FORMAT_DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";

    public static String formatDate(@NotNull Date date) {
        Objects.requireNonNull(date);

        return new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_HH_MM_SS).format(date);
    }

    public static String formatDate(Date date,@NotBlank String dateFormat) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(dateFormat);
        return new SimpleDateFormat(dateFormat).format(date);

    }

    private DateCommonUtils() {
    }

}
