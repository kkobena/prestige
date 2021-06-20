/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import commonTasks.dto.FournisseurProduitDTO;
import commonTasks.dto.ProduitDTO;
import dal.GammeProduit;
import dal.Laboratoire;
import dal.TCodeTva;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TFamillearticle;
import dal.TFormeArticle;
import dal.TGrossiste;
import dal.TTypeetiquette;
import dal.TZoneGeographique;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import util.DateConverter;

/**
 *
 * @author koben
 */
@Stateless
public class DataExportService {

    private static final Logger LOG = Logger.getLogger(DataExportService.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    private TFamille detail(String idParent) {
        try {
            TypedQuery<TFamille> q = getEntityManager().createQuery("SELECT o FROM TFamille o WHERE o.strSTATUT <> 'delete' AND o.lgFAMILLEPARENTID=?1", TFamille.class);
            q.setMaxResults(1);
            q.setParameter(1, idParent);
            return q.getSingleResult();
        } catch (Exception e) {
//            LOG.log(Level.SEVERE, "factures =====>>", e);
            return null;
        }
    }

    private List<TFamille> familles() {
        return getEntityManager().createQuery("SELECT o FROM TFamille o WHERE o.strSTATUT <> 'delete' AND o.lgFAMILLEPARENTID=''", TFamille.class).getResultList();
    }

    private ProduitDTO build(TFamille f) {
        ProduitDTO o = new ProduitDTO();
        o.setItemQty(1);
        o.setTypeProduit(1);
        if (f.getBoolDECONDITIONNE().compareTo((short) 1) == 0) {
            o.setTypeProduit(0);
        }
        o.setItemCostAmount(f.getIntPAF());
        o.setItemRegularUnitPrice(f.getIntPRICE());
        TGrossiste gros = f.getLgGROSSISTEID();
        o.setStatus(f.getStrSTATUT().equals("enable") ? 0 : 1);
        f.getTFamilleGrossisteCollection().stream().map(FournisseurProduitDTO::new).forEach(a -> {
            if (a.getFournisseurLibelle().equalsIgnoreCase(gros.getStrLIBELLE())) {
                a.setPrincipal(true);
            }
            o.getFournisseurProduits().add(a);
        });

        Optional<TFamilleStock> familleStock = f.getTFamilleStockCollection().stream().filter(s -> s.getLgEMPLACEMENTID().getLgEMPLACEMENTID().equals(DateConverter.OFFICINE)).findFirst();
        if (familleStock.isPresent()) {
            TFamilleStock s = familleStock.get();
            o.setTotalQuantity(s.getIntNUMBERAVAILABLE());
            o.setQtyUG(s.getIntUG());
        }

        if (f.getBoolDECONDITIONNEEXIST().compareTo((short) 1) == 0) {
            TFamille d = detail(f.getLgFAMILLEID());
            o.setDeconditionnable(Boolean.TRUE);
            o.setItemQty(f.getIntNUMBERDETAIL());
            if (d != null) {
                o.setItemCostAmount(d.getIntPAF());
                o.setItemRegularUnitPrice(d.getIntPRICE());
                o.getProduits().add(build(d));
            }

        }
        o.setChiffre(f.getBoolACCOUNT());
        o.setCodeEan(f.getIntEAN13());
        o.setCostAmount(f.getIntPAF());
        o.setRegularUnitPrice(f.getIntPRICE());
        o.setDateperemption(f.getDtPEREMPTION() != null);
        o.setPerimeAt(DateConverter.convertDateToLocalDateAndReturnNull(f.getDtPEREMPTION()));
        o.setLibelle(f.getStrNAME());
        try {
            o.setPrixMnp(f.getDblPRIXMOYENPONDERE().intValue());
        } catch (Exception e) {
        }
        o.setQtyAppro(f.getIntQTEREAPPROVISIONNEMENT());
        o.setQtySeuilMini(f.getIntSEUILMIN());
        TFamillearticle tf = f.getLgFAMILLEARTICLEID();
        if (tf != null) {
            o.setFamilleLibelle(tf.getStrLIBELLE());
        }
        TFormeArticle fr = f.getLgFORMEID();
        if (fr != null) {
            o.setFormeLibelle(fr.getStrLIBELLE());
        }
        TTypeetiquette e = f.getLgTYPEETIQUETTEID();
        if (e != null) {
            o.setTypeEtyquetteLibelle(e.getStrNAME());
        }
        GammeProduit gammeProduit = f.getGamme();
        if (gammeProduit != null) {
            o.setGammeLibelle(gammeProduit.getLibelle());
        }
        Laboratoire l = f.getLaboratoire();
        if (l != null) {
            o.setLaboratoireLibelle(l.getLibelle());
        }
        TCodeTva codeTva = f.getLgCODETVAID();
        if (codeTva != null) {
            o.setTvaTaux(codeTva.getIntVALUE());
        }
        TZoneGeographique ra = f.getLgZONEGEOID();
        if (ra != null) {
            o.setRayonLibelle(ra.getStrLIBELLEE());
        }
        return o;
    }

    public List<ProduitDTO> listProduits() {
        List<ProduitDTO> datas = new ArrayList<>();
        List<TFamille> familles = familles();
        familles.forEach(f -> {
            datas.add(build(f));
        });

        return datas;
    }
}
