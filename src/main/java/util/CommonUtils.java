
package util;


import java.time.Year;

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
