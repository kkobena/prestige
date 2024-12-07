package rest.service.impl;

import dal.Caution;
import dal.CautionHistorique;
import dal.TTiersPayant;
import java.util.Objects;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;
import rest.service.CautionTiersPayantService;
import rest.service.SessionHelperService;

/**
 *
 * @author koben
 */
@Stateless
public class CautionTiersPayantServiceImpl implements CautionTiersPayantService {

    private static final Logger LOG = Logger.getLogger(CautionTiersPayantServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private SessionHelperService sessionHelperService;

    @Override
    public void addCaution(String idTiersPayant, int amount) {
        TTiersPayant tTiersPayant = this.em.find(TTiersPayant.class, idTiersPayant);
        Caution caution = tTiersPayant.getCaution();
        if (Objects.isNull(caution)) {
            createNewCaution(tTiersPayant, amount);
        } else {
            addCautionHistorique(caution, amount);
            caution.setMontant(caution.getMontant() + amount);
            em.merge(caution);
        }

    }

    @Override
    public JSONObject update(String idCaution, int amount) {
        JSONObject json = new JSONObject();
        if (amount == 0) {
            return json.put("success", false).put("msg", "Le montant 0 n'est pas permis");
        }
        Caution caution = this.em.find(Caution.class, idCaution);
        int montant = caution.getMontant() + amount;

        if (!canModifyCaution(montant, amount, caution.getConso())) {
            return json.put("success", false).put("msg", "Impossible de reduire le montant");

        }

        addCautionHistorique(caution, amount);
        caution.setMontant(montant);
        em.merge(caution);
        return json.put("success", true).put("msg", "Opération effectuée");
    }

    private boolean canModifyCaution(int montant, int newAmount, int conso) {
        return newAmount < 0 && (montant < conso);

    }

    private void addCautionHistorique(Caution caution, int amount) {
        CautionHistorique cautionHistorique = new CautionHistorique();
        cautionHistorique.setCaution(caution);
        cautionHistorique.setMontant(amount);
        cautionHistorique.setUser(sessionHelperService.getCurrentUser());
        em.persist(cautionHistorique);
    }

    private void createNewCaution(TTiersPayant tTiersPayant, int amount) {
        if (amount > 0) {
            Caution caution = new Caution();
            caution.setId(tTiersPayant.getLgTIERSPAYANTID());
            caution.setTiersPayant(tTiersPayant);
            caution.setMontant(amount);
            caution.setConso(0);
            caution.setUser(sessionHelperService.getCurrentUser());

            CautionHistorique cautionHistorique = new CautionHistorique();
            cautionHistorique.setCaution(caution);
            cautionHistorique.setUser(caution.getUser());
            cautionHistorique.setMontant(caution.getMontant());
            caution.getHistoriques().add(cautionHistorique);
            em.persist(caution);
        }

    }

    @Override
    public JSONObject supprimerHistorique(String cautionHistoriqueId) {
        JSONObject json = new JSONObject();
        CautionHistorique cautionHistorique = this.em.find(CautionHistorique.class, cautionHistoriqueId);
        Caution caution = cautionHistorique.getCaution();
        int newAmount = (-1) * cautionHistorique.getMontant();
        int montant = caution.getMontant() + newAmount;
        if (!canModifyCaution(montant, newAmount, caution.getConso())) {
            return json.put("success", false).put("msg", "Impossible de reduire le montant");

        }
        cautionHistorique.setCaution(null);
        this.em.remove(cautionHistorique);
        caution.setMontant(montant);
        em.merge(caution);
        return json.put("success", true).put("msg", "Opération effectuée");
    }

    @Override
    public JSONObject supprimerCaution(String idCaution) {
        JSONObject json = new JSONObject();
        Caution caution = this.em.find(Caution.class, idCaution);
        if (caution.getConso() > 0) {
            return json.put("success", false).put("msg", "Impossible de supprimer il y a des ventes liées");
        }
        em.remove(caution);
        return json.put("success", true).put("msg", "Opération effectuée");

    }

    @Override
    public void updateCaution(Caution caution, int saleAmount) {
        caution.setMontant(caution.getMontant() - saleAmount);
        caution.setConso(caution.getConso() + saleAmount);
        em.merge(caution);
    }

}
