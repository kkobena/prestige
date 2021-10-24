/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.CaisseParamsDTO;
import commonTasks.dto.FlagDTO;
import commonTasks.dto.GenericDTO;
import commonTasks.dto.MvtCaisseDTO;
import commonTasks.dto.Params;
import commonTasks.dto.RapportDTO;
import commonTasks.dto.ResumeCaisseDTO;
import commonTasks.dto.SumCaisseDTO;
import commonTasks.dto.TableauBaordPhDTO;
import commonTasks.dto.TableauBaordSummary;
import commonTasks.dto.TvaDTO;
import commonTasks.dto.VenteDetailsDTO;
import commonTasks.dto.VisualisationCaisseDTO;
import dal.MvtTransaction;
import dal.TCashTransaction;
import dal.TOfficine;
import dal.TPreenregistrement;
import dal.TUser;
import dal.Typemvtproduit;
import dal.enumeration.TypeTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import javax.persistence.EntityManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Kobena
 */
@Local
//@Remote
public interface CaisseService {

    /*
	 * Expression<javax.sql.Date> date = cb.currentDate(); // date only
	 * 
	 * // Create current time expression: Expression<javax.sql.Time> time =
	 * cb.currentTime(); // time only
	 * 
	 * // Create current date & time expression: Expression<javax.sql.Timestamp> ts
	 * = cb.currentTimestamp(); // both // Create expressions that extract date
	 * parts: Expression<Integer> year = cb.function("year", Integer.class, date);
	 * Expression<Integer> month = cb.function("month", Integer.class, date);
	 * Expression<Integer> day = cb.function("day", Integer.class, ts);
	 * 
	 * // Create expressions that extract time parts: Expression<Integer> hour =
	 * cb.function("hour", Integer.class, time); Expression<Integer> minute =
	 * cb.function("minute", Integer.class, time); Expression<Integer> second =
	 * cb.function("second", Integer.class, ts);
    qry.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.
BYPASS);
    EntityGraph entityGraph = entityManager
.getEntityGraph("author-books-graph");
Map<String, Object> properties = new HashMap<>();
properties.put("javax.persistence.fetchgraph", entityGraph);
Author author = entityManager.find(Author.class, id, properties);
     */
    List<VisualisationCaisseDTO> listCaisses(CaisseParamsDTO caisseParams, boolean all);

    List<VisualisationCaisseDTO> mouvementsCaisses(CaisseParamsDTO caisseParams, boolean all);

    List<VisualisationCaisseDTO> visualisationsCaisses(CaisseParamsDTO caisseParams, boolean all);

    List<VisualisationCaisseDTO> gestionsCaisses(CaisseParamsDTO caisseParams, boolean all);

    List<VisualisationCaisseDTO> recaptilatifsCaisses(CaisseParamsDTO caisseParams, boolean all);

    List<TCashTransaction> cashTransactions(CaisseParamsDTO caisseParams, boolean all);

    long countcashTransactions(CaisseParamsDTO caisseParams);

    List<SumCaisseDTO> getCaisse(CaisseParamsDTO caisseParams);

    List<SumCaisseDTO> montantCaisseAnnule(CaisseParamsDTO caisseParams);

    SumCaisseDTO cumul(CaisseParamsDTO caisseParams, boolean all);

    GenericDTO balanceVenteCaisseReport(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId);

    JSONObject balanceVenteCaisse(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId)
            throws JSONException;

    List<VisualisationCaisseDTO> findAllMvtCaisse(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId);

    JSONObject resumeCaisse(LocalDate dtStart, LocalDate dtEnd, TUser u, boolean cancel, Boolean allActivite, int start,
            int limit, boolean all, String userId) throws JSONException;

    List<ResumeCaisseDTO> resumeCaisse(LocalDate dtStart, LocalDate dtEnd, TUser u, Boolean allActivite, int start,
            int limit, boolean cancel, String userId, boolean all);

    JSONObject rollbackcloseCaisse(TUser o, String idCaisse) throws JSONException;

    JSONObject closeCaisse(TUser o, String idCaisse) throws JSONException;

    JSONObject tableauBoardDatas(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user, int start, int limit,
            boolean all) throws JSONException;

    JSONObject tableauBoardDatasOld(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user, int start, int limit,
            boolean all) throws JSONException;

    Map<TableauBaordSummary, List<TableauBaordPhDTO>> tableauBoardDatas(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user,
            int ration, int start, int limit, boolean all);

    List<Typemvtproduit> findAllTypeMvtProduit();

    JSONObject createMvt(MvtCaisseDTO caisseDTO, TUser user) throws JSONException;

    JSONObject removeMvt(MvtCaisseDTO caisseDTO, TUser user) throws JSONException;

    boolean checkCaisse(TUser user, EntityManager emg);

    JSONObject validerFondDeCaisse(String id, TUser user) throws JSONException;

    JSONObject attribuerFondDeCaisse(String idUser, TUser operateur, Integer amount) throws JSONException;

    JSONObject rapportGestionViewData(Params params) throws JSONException;

    Map<Params, List<RapportDTO>> rapportGestion(Params params);

    Integer margeAchatVente(LocalDate dtStart, LocalDate dtEnd, TUser user, String empl);

    JSONObject donneeCaisses(CaisseParamsDTO caisseParams, boolean all);

    Map<TableauBaordSummary, List<TableauBaordPhDTO>> tableauBoardDatasOld(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user,
            int ration, int start, int limit, boolean all);

    Integer totalVenteDepot(LocalDate dtStart, LocalDate dtEnd, String empl);

    List<MvtTransaction> venteDepot(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId);

    List<VisualisationCaisseDTO> mouvementCaisses(CaisseParamsDTO caisseParams, boolean all);

    JSONObject mouvementCaisses(CaisseParamsDTO caisseParams) throws JSONException;

    List<TPreenregistrement> getTtVente(String dt_start, String dt_end, String lgEmp);

    Integer montantCa(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId, TypeTransaction transaction, String typrReglement);

    MvtTransaction findByVenteId(String venteId);

     boolean key_Params();

     boolean key_Take_Into_Account();

    long montantAccount(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId, TypeTransaction transaction, String typrReglement);

    TOfficine findOfficine();

    JSONObject venteUg(LocalDate dtStart, LocalDate dtEnd, String query);

    JSONObject balanceVenteCaisseVersion2(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId)
            throws JSONException;

    GenericDTO balanceVenteCaisseReportVersion2(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId);

    List<VenteDetailsDTO> venteUgDTO(LocalDate dtStart, LocalDate dtEnd, String query);

    JSONObject balancePara(LocalDate dtStart, LocalDate dtEnd, String emplacementId)
            throws JSONException;

    GenericDTO balanceVenteCaisseReportPara(LocalDate dtStart, LocalDate dtEnd, String emplacementId);

    JSONObject tableauBoardDatasGroupByMonth(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user, int start,
            int limit, boolean all) throws JSONException;

    Map<TableauBaordSummary, List<TableauBaordPhDTO>> tableauBoardDatasMonthly(LocalDate dtStart, LocalDate dtEnd, Boolean checked, TUser user,
            int ration, int start, int limit, boolean all);

    long montantAccount(LocalDate dtStart, LocalDate dtEnd, boolean checked, String emplacementId, TypeTransaction transaction, String typrReglement, String typeMvtCaisse);
  
}
