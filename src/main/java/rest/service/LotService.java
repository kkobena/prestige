/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest.service;

import javax.ejb.Local;
import org.json.JSONObject;

/**
 *
 * @author Hermann N'ZI
 */
@Local
public interface LotService {

    JSONObject getAllLots(String dtStart, String dtEnd, int limit, int start);
    
    JSONObject getAllLots();

    JSONObject getLots();
}
