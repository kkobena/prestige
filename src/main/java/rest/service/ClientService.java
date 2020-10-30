/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.AyantDroitDTO;
import commonTasks.dto.ClientDTO;
import commonTasks.dto.ClientLambdaDTO;
import commonTasks.dto.TiersPayantDTO;
import commonTasks.dto.TiersPayantParams;
import dal.TClient;
import dal.TCompteClientTiersPayant;
import dal.TTiersPayant;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Kobena
 */
@Local
//@Remote
public interface ClientService {

    TClient createClient(ClientLambdaDTO clientLambda);

    List<ClientLambdaDTO> findClientLambda(String query);

    List<ClientDTO> clientDifferes(String query, String empl);

    List<ClientDTO> clientDiffere(String query, String empl);

    List<ClientDTO> findClientAssurance(String query, String typeClientId);

    List<TiersPayantParams> findTiersPayantByClientId(String clientId);

    List<TiersPayantParams> findTiersPayantByClientIdExcludeRo(String clientId);

    List<TiersPayantDTO> findTiersPayants(String query, String type);

    @Deprecated
    JSONObject updateClientAssurance(ClientDTO client) throws JSONException;

    List<AyantDroitDTO> findAyantDroitByClientId(String clientId, String query);

    JSONObject addAyantDroitToClient(AyantDroitDTO ayantDroit) throws JSONException;

    JSONObject findClientAssuranceById(String clientId, String venteId) throws JSONException;

    @Deprecated
    JSONObject updateClientCarnet(ClientDTO client) throws JSONException;

    JSONObject updateCreateClientCarnet(ClientDTO client) throws JSONException;

    JSONObject createClient(ClientLambdaDTO clientLambda, String venteId) throws JSONException;

    JSONObject updateOrCreateClientAssurance(ClientDTO client) throws JSONException;

    TCompteClientTiersPayant updateOrCreateClientAssurance(TClient client, TTiersPayant tpId, int taux) throws Exception;

    void updateCompteClientTiersPayantEncourAndPlafond(String venteId);

    JSONObject addNewTiersPayantToClient(TiersPayantDTO tiersPayantDTO, String clientId, String typeTiersPayantId, int taux) throws JSONException;
}
