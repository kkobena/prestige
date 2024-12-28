/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service;

import javax.ejb.Local;
import org.json.JSONObject;

/**
 *
 * @author koben
 */
@Local
public interface TiersPayantService {
    JSONObject fetchList(int start, int limit, String search, String typeTierspayant, boolean btnDesactive,
            boolean delete);
}
