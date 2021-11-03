/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.GenererFactureDTO;
import commonTasks.dto.ReglementCarnetDTO;
import commonTasks.dto.TiersPayantExclusDTO;
import commonTasks.dto.VenteTiersPayantsDTO;
import dal.TUser;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import org.json.JSONObject;

/**
 *
 * @author koben
 */
@Local
public interface TiersPayantExclusService {

    List<TiersPayantExclusDTO> all(int start, int size, String query, boolean all, Boolean exclude);

    JSONObject all(int start, int size, String query, Boolean exclude);

    void exclure(GenererFactureDTO datas);

    void exclure(String id);

    void inclure(String id);

    void update(String id, boolean toExclure);

    JSONObject fetchVenteByTiersPayant(String tiersPayantId, String dtStart, String dtEnd, int start, int size);

    List<TiersPayantExclusDTO> fetchVenteByTiersPayant(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd);

    List<VenteTiersPayantsDTO> fetchVente(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd, int start, int size, boolean all);

    TiersPayantExclusDTO fetchVenteSummary(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd);

    JSONObject reglementsCarnet(String tiersPayantId, String dtStart, String dtEnd, int start, int size);

    List<ReglementCarnetDTO> reglementsCarnet(String tiersPayantId, String dtStart, String dtEnd, int start, int size, boolean all);

    ReglementCarnetDTO reglementsCarnetSummary(String tiersPayantId, LocalDate dtStart, LocalDate dtEnd);

    JSONObject faireReglement(ReglementCarnetDTO reglementCarnetDTO, TUser user);

    String getTiersPayantName(String tiersPayantId);

}
