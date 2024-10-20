/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.Objects;

/**
 *
 * @author koben
 */
public class StringUtils {

    public static String subStringData(String texte, int begin, int end) {
        if (Objects.isNull(texte)) {
            return "";
        }
        if (texte.length() > end) {
            texte = texte.substring(begin, end);
        }
        return texte;
    }
}
