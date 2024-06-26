/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.MedecinDTO;
import dal.Medecin;
import javax.ejb.Local;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author koben
 */
@Local
public interface MedecinService {

    JSONObject create(MedecinDTO dTO) throws JSONException;

    JSONObject update(MedecinDTO dTO) throws JSONException;

    Medecin save(MedecinDTO dTO);

    Medecin findByNumOrder(String numOrder);

    JSONObject findAllByNonOrNumOrder(String query) throws JSONException;
}
