/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bll.stock;

import dal.TFamille;
import dal.TMouvement;

import java.time.LocalDate;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public interface MouvementService {

    Integer entreeStock(TFamille famille, LocalDate debut, LocalDate fin);

    Integer stockDeconditionne(TFamille famille, LocalDate debut, LocalDate fin, String empl);

    Integer stockVente(TFamille famille, LocalDate debut, LocalDate fin, String empl);

    Integer stockPerime(TFamille famille, LocalDate debut, LocalDate fin, String empl);

    Integer stockRetour(TFamille famille, LocalDate debut, LocalDate fin, String empl);

    Integer stockAjusteNegatitf(TFamille famille, LocalDate debut, LocalDate fin, String empl);

    Integer stockAjustePositif(TFamille famille, LocalDate debut, LocalDate fin, String empl);

    Integer stockInventaire(TFamille famille, LocalDate debut, LocalDate fin, String empl);

  //  List<TFamille> listArticle(String lgFamille, String zoneID, String criteria, String dateDebut, String dateEnd, String empl, String LgFamilleArticle);
     List<TMouvement> listMvt(String lgFamille, String zoneID, String criteria, String dateDebut, String dateEnd, String empl, String LgFamilleArticle,boolean all,int start, int limit );

    JSONObject listMvt(JSONArray data, int count, String empl);

    JSONArray listMvt(JSONArray data, String empl);

    Integer stockDeconditionnenegatif(TFamille famille, LocalDate debut, LocalDate fin, String empl);
}
