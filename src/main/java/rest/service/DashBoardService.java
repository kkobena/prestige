/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.Params;
import commonTasks.dto.RecapActiviteCreditDTO;
import commonTasks.dto.RecapActiviteDTO;
import dal.TUser;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author DICI
 */
@Local
public interface DashBoardService {

    JSONObject donneesRecapActiviteView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query) throws JSONException;

    List<Params> donneesReglementsTp(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all);

    List<RecapActiviteCreditDTO> donneesCreditAccordes(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all);

    JSONObject donneesReglementsTpView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all) throws JSONException;

    JSONObject donneesCreditAccordesView(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu, String query, int start, int limit, boolean all) throws JSONException;

    RecapActiviteDTO donneesRecapActivite(LocalDate dtStart, LocalDate dtEnd, String emplacementId, TUser tu);

    RecapActiviteCreditDTO donneesRecapTotataux(LocalDate dtStart, LocalDate dtEnd,  TUser tu, String query);

}
