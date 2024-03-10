package rest.service.impl;

import commonTasks.dto.MontantAPaye;
import commonTasks.dto.SalesParams;
import commonTasks.dto.TiersPayantParams;
import dal.TCompteClientTiersPayant;
import dal.TFamille;
import dal.TGrilleRemise;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TRemise;
import dal.TTiersPayant;
import dal.TWorkflowRemiseArticle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import rest.service.SalesNetComputingService;
import rest.service.dto.NetComputingDTO;
import util.Constant;
import util.NumberUtils;

/**
 *
 * @author koben
 */
@Stateless
public class SalesNetComputingServiceImpl implements SalesNetComputingService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    private int computeEncour(TCompteClientTiersPayant tc, int amountToCompute) {
        var encoursClient = tc.getDbPLAFONDENCOURS() != null && tc.getDbPLAFONDENCOURS() > 0 ? tc.getDbPLAFONDENCOURS()
                : 0;
        if (encoursClient == 0) {
            return amountToCompute;
        }
        var consoMensuelleClient = tc.getDbCONSOMMATIONMENSUELLE() != null && tc.getDbCONSOMMATIONMENSUELLE() > 0
                ? tc.getDbCONSOMMATIONMENSUELLE() : 0;
        var conso = consoMensuelleClient + amountToCompute;
        if (conso <= encoursClient) {
            return amountToCompute;
        }
        return encoursClient - consoMensuelleClient;

    }

    private int computeEncourTiersPayant(TTiersPayant tc, int amountToCompute) {

        var plafondTierPayant = (tc.getDblPLAFONDCREDIT() == null || tc.getDblPLAFONDCREDIT() <= 0 ? 0
                : tc.getDblPLAFONDCREDIT().intValue());
        if (plafondTierPayant == 0) {
            return amountToCompute;
        }
        var consoMensuelleTierPayant = tc.getDbCONSOMMATIONMENSUELLE() == null || tc.getDbCONSOMMATIONMENSUELLE() < 0
                ? 0 : tc.getDbCONSOMMATIONMENSUELLE();
        var conso = consoMensuelleTierPayant + amountToCompute;
        if (conso <= plafondTierPayant) {
            return amountToCompute;
        }
        return plafondTierPayant - consoMensuelleTierPayant;

    }

    private NetComputingDTO computeTiesrPayantNet(TiersPayantParams tierspayant, int amountToCompute,
            boolean asRestrictions) {

        int plafondVente = 0;
        long plafondGlobal = 0;

        int reelAmount = amountToCompute;
        String message = null;

        TTiersPayant payant = null;
        if (asRestrictions) {
            TCompteClientTiersPayant tc = em.find(TCompteClientTiersPayant.class, tierspayant.getCompteTp());
            payant = tc.getLgTIERSPAYANTID();
            plafondVente = (tc.getDblPLAFOND() == null || tc.getDblPLAFOND() <= 0 ? 0 : tc.getDblPLAFOND().intValue());
            if (plafondVente > 0 && plafondVente < amountToCompute) {
                amountToCompute = plafondVente;

            }
            amountToCompute = computeEncour(tc, amountToCompute);
            amountToCompute = computeEncourTiersPayant(payant, amountToCompute);
        }
        float taux = Float.valueOf(tierspayant.getTaux()) / 100;
        double montantTp = amountToCompute * taux;
        int montantTiersPayant = (int) Math.ceil(montantTp);
        if (Objects.nonNull(payant) && reelAmount != amountToCompute) {
            message = "Le tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'> "
                    + payant.getStrFULLNAME()
                    + "</span> ne peut prendre <span style='font-weight:900;color:red;text-decoration: underline;'>"
                    + NumberUtils.formatIntToString(montantTiersPayant) + "</span><br>";
        }

        NetComputingDTO netComputing = new NetComputingDTO();
        netComputing.setIdCompteClientTiersPayant(tierspayant.getCompteTp());
        netComputing.setPlafondVente(plafondVente);
        netComputing.setMontantTiersPayant(montantTiersPayant);
        netComputing.setPlafondGlobal(plafondGlobal);
        netComputing.setMessage(message);
        netComputing.setNumBon(tierspayant.getNumBon());
        return netComputing;

    }

    private MontantAPaye calculAssuranceNet(SalesParams params, boolean asPlafondActivated) {
        MontantAPaye montantAPaye = new MontantAPaye();
        List<TPreenregistrementDetail> items = getItems(params.getVenteId());
        TPreenregistrement op = items.get(0).getLgPREENREGISTREMENTID();
        TRemise remise = op.getRemise();
        remise = remise != null ? remise : op.getClient().getRemise();
        MontantAPaye aPaye = computeRemise(op, remise, items);

        int montantVente = op.getIntPRICE();
        int cmuAmount = op.getCmuAmount();
        int montantRestant;
        int montantTotalTp = 0;
        List<NetComputingDTO> datas = new ArrayList<>();
        for (TiersPayantParams tiersPayantParams : params.getTierspayants()) {
            boolean isCmu = tiersPayantParams.isCmu() && (cmuAmount != montantVente);
            int amountToCompute = isCmu ? cmuAmount : montantVente;

            NetComputingDTO netComputed = computeTiesrPayantNet(tiersPayantParams, amountToCompute, asPlafondActivated);
            montantTotalTp += netComputed.getMontantTiersPayant();
            montantRestant = amountToCompute - montantTotalTp;
            if (montantTotalTp == netComputed.getMontantTiersPayant()) {
                netComputed.setPercentage(tiersPayantParams.getTaux());

            } else {
                if (netComputed.getMontantTiersPayant() <= montantRestant) {
                    netComputed.setPercentage(tiersPayantParams.getTaux());

                } else {
                    int montantTp = montantRestant;
                    int reelTaux = (int) Math.ceil((Double.valueOf(montantTp) * 100) / amountToCompute);
                    netComputed.setPercentage(reelTaux);
                    netComputed.setMontantTiersPayant(montantTp);
                }
            }
            datas.add(netComputed);

        }
        int custPart = (montantVente - montantTotalTp) - op.getIntPRICEREMISE();
        op.setIntCUSTPART(custPart);
        em.merge(op);
        montantAPaye.setCmuAmount(cmuAmount);
        montantAPaye.setRemise(NumberUtils.arrondiModuloOfNumber(op.getIntPRICEREMISE(), 5));
        montantAPaye.setMontantNet(NumberUtils.arrondiModuloOfNumber(op.getIntCUSTPART(), 5));
        montantAPaye.setMontant(op.getIntPRICE());
        montantAPaye.setMarge(aPaye.getMarge());
        montantAPaye.setMontantTva(aPaye.getMontantTva());
        boolean asPlafond = false;
        String message = null;
        for (NetComputingDTO o : datas) {
            TiersPayantParams tp = new TiersPayantParams();
            montantAPaye.setMontantTp(montantAPaye.getMontantTp() + o.getMontantTiersPayant());
            tp.setTaux(o.getPercentage());
            tp.setCompteTp(o.getIdCompteClientTiersPayant());
            tp.setNumBon(o.getNumBon());
            tp.setTpnet(o.getMontantTiersPayant());
            if (StringUtils.isNotEmpty(o.getMessage())) {
                asPlafond = true;
                tp.setMessage(o.getMessage());
                if (StringUtils.isEmpty(message)) {
                    message = o.getMessage();
                } else {
                    message += o.getMessage();
                }
            }

            montantAPaye.getTierspayants().add(tp);

        }
        montantAPaye.setRestructuring(asPlafond);
        montantAPaye.setMessage(message);

        return montantAPaye;
    }

    private List<TPreenregistrementDetail> getItems(String venteId) {

        try {

            TypedQuery<TPreenregistrementDetail> q = em.createQuery(
                    "SELECT t FROM TPreenregistrementDetail t WHERE  t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1",
                    TPreenregistrementDetail.class).setParameter(1, venteId);

            return q.getResultList();

        } catch (Exception ex) {
            return Collections.emptyList();
        }

    }

    private MontantAPaye computeRemise(TPreenregistrement op, TRemise oTRemise,
            List<TPreenregistrementDetail> lstTPreenregistrementDetail) {

        int montantNet;
        int totalRemise = 0;
        int totalRemisePara = 0;
        int totalAmount = 0;
        int marge = 0;
        int montantTva = 0;
        int montantAccount = 0;
        int montantCMU = 0;

        for (TPreenregistrementDetail x : lstTPreenregistrementDetail) {
            totalAmount += x.getIntPRICE();
            if (Objects.nonNull(x.getCmuPrice()) && x.getCmuPrice() != 0) {
                montantCMU += (x.getCmuPrice() * x.getIntQUANTITY());
            } else {
                montantCMU += x.getIntPRICE();
            }
            montantTva += x.getMontantTva();
            TFamille famille = x.getLgFAMILLEID();

            int remise = 0;
            if (Objects.nonNull(oTRemise) && !StringUtils.isEmpty(famille.getStrCODEREMISE())
                    && !famille.getStrCODEREMISE().equals("2") && !famille.getStrCODEREMISE().equals("3")) {

                TGrilleRemise grilleRemise = grilleRemiseRemiseFromWorkflow(x.getLgPREENREGISTREMENTID(), famille,
                        oTRemise.getLgREMISEID());
                if (grilleRemise != null) {
                    remise = (int) ((x.getIntPRICE() * grilleRemise.getDblTAUX()) / 100);
                    if (!x.getBoolACCOUNT()) {
                        totalRemisePara += remise;
                    }
                    totalRemise += remise;
                    x.setLgGRILLEREMISEID(grilleRemise.getLgGRILLEREMISEID());
                }
                x.setIntPRICEREMISE(remise);
                em.merge(x);
            }

            if (x.getLgFAMILLEID().getBoolACCOUNT()) {
                int thatMarge = (x.getIntPRICE() - remise - x.getMontantTva())
                        - (x.getIntQUANTITY() * famille.getIntPAF());
                marge += thatMarge;
                montantAccount += x.getIntPRICE();
                montantTva += x.getMontantTva();

            }
        }

        montantNet = totalAmount - totalRemise;
        op.setIntPRICE(totalAmount);
        op.setIntACCOUNT(montantAccount);
        op.setIntPRICEREMISE(totalRemise);
        op.setIntREMISEPARA(totalRemisePara);
        op.setMontantTva(montantTva);
        if (totalRemise > 0 && oTRemise == null) {
            op.setRemise(oTRemise);
        }
        op.setCmuAmount(montantCMU);

        return new MontantAPaye(montantNet, totalAmount, 0, totalRemise, marge, montantTva).cmuAmount(montantCMU);
    }

    private TGrilleRemise grilleRemiseRemiseFromWorkflow(TPreenregistrement op, TFamille familleP, String remiseId) {
        int intCodeRemise;
        TGrilleRemise oTGrilleRemise;
        try {
            TWorkflowRemiseArticle oRemiseArticle = findByArticleRemise(familleP.getStrCODEREMISE());
            if (oRemiseArticle == null) {
                return null;
            }
            if ((op.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Constant.VENTE_ASSURANCE_ID))
                    || (op.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Constant.VENTE_AVEC_CARNET))) {
                intCodeRemise = oRemiseArticle.getStrCODEGRILLEVO();
                oTGrilleRemise = (TGrilleRemise) em.createQuery(
                        "SELECT t FROM TGrilleRemise t WHERE t.strCODEGRILLE = ?1  AND t.strSTATUT = ?2  AND t.lgREMISEID.lgREMISEID = ?3 ")
                        .setParameter(1, intCodeRemise).setParameter(2, Constant.STATUT_ENABLE)
                        .setParameter(3, remiseId).getSingleResult();

                return oTGrilleRemise;
            } else {
                intCodeRemise = oRemiseArticle.getStrCODEGRILLEVNO();
                oTGrilleRemise = (TGrilleRemise) em.createQuery(
                        "SELECT t FROM TGrilleRemise t WHERE t.strCODEGRILLE  = ?1  AND t.strSTATUT = ?2 AND t.lgREMISEID.lgREMISEID = ?3 ")
                        .setParameter(1, intCodeRemise).setParameter(2, Constant.STATUT_ENABLE)
                        .setParameter(3, remiseId).getSingleResult();
                return oTGrilleRemise;
            }

        } catch (Exception e) {
            return null;
        }

    }

    private TWorkflowRemiseArticle findByArticleRemise(String strCODEREMISE) {
        if (StringUtils.isEmpty(strCODEREMISE)) {
            return null;
        }
        try {
            TypedQuery<TWorkflowRemiseArticle> q = em.createQuery(
                    "SELECT t FROM TWorkflowRemiseArticle t WHERE t.strCODEREMISEARTICLE = ?1  AND t.strSTATUT = ?2 ",
                    TWorkflowRemiseArticle.class);
            q.setParameter(1, strCODEREMISE).setParameter(2, Constant.STATUT_ENABLE);
            q.setMaxResults(1);
            return q.getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public MontantAPaye computeVONet(SalesParams params, boolean asPlafondActivated) {

        return calculAssuranceNet(params, asPlafondActivated);

    }
}
