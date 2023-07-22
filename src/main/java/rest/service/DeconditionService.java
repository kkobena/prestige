/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.SalesParams;
import javax.ejb.Local;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Kobena
 */
@Local

public interface DeconditionService {

    public JSONObject deconditionnementVente(SalesParams params) throws JSONException;
}
