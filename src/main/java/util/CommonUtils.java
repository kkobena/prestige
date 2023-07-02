
package util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
 public static String encodeString(String key) {
  byte[] uniqueKey = key.getBytes();

  byte[] hash;
  try {
   hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
  } catch (NoSuchAlgorithmException var6) {
   throw new Error("no MD5 support in this VM");
  }

  StringBuffer hashString = new StringBuffer();

  for(int i = 0; i < hash.length; ++i) {
   String hex = Integer.toHexString(hash[i]);
   if (hex.length() == 1) {
    hashString.append('0');
    hashString.append(hex.charAt(hex.length() - 1));
   } else {
    hashString.append(hex.substring(hex.length() - 2));
   }
  }

  return hashString.toString();
 }
}
