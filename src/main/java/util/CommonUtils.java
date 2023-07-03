
package util;


import dal.TParameters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import toolkits.utils.date;
import toolkits.utils.logger;

import java.time.Year;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.IntStream;

/**
 *
 * @author koben
 */
public final class CommonUtils {
    private static final int BEGIN=2015;
     public static  int[] getYears(){
      return IntStream.rangeClosed(Year.of(BEGIN).getValue(), Year.now().getValue()).toArray();
     }
     private CommonUtils(){
         
     }


}
