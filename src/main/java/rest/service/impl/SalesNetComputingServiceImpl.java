package rest.service.impl;

import commonTasks.dto.MontantAPaye;
import commonTasks.dto.MontantTp;
import commonTasks.dto.SalesParams;
import commonTasks.dto.TiersPayantParams;
import dal.Caution;
import dal.PrixReferenceType;
import dal.Rate;
import dal.TCompteClientTiersPayant;
import dal.TFamille;
import dal.TGrilleRemise;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementDetail;
import dal.TRemise;
import dal.TTiersPayant;
import dal.TTypeVente;
import dal.TWorkflowRemiseArticle;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import rest.service.PrixReferenceService;
import rest.service.SalesNetComputingService;
import rest.service.calculation.TiersPayantCalculationService;
import rest.service.calculation.dto.CalculationInput;
import rest.service.calculation.dto.CalculationResult;
import rest.service.calculation.dto.NatureVente;
import rest.service.calculation.dto.SaleItemInput;
import rest.service.calculation.dto.TiersPayantInput;
import rest.service.calculation.dto.TiersPayantLineOutput;
import rest.service.calculation.dto.TiersPayantPrixInput;
import rest.service.dto.NetComputingDTO;
import util.Constant;
import util.NumberUtils;

/**
 *
 * @author koben
 */
@Stateless
public class SalesNetComputingServiceImpl implements SalesNetComputingService {

    private static final Logger LOG = Logger.getLogger(SalesNetComputingServiceImpl.class.getName());

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private PrixReferenceService prixReferenceService;
    @EJB
    private TiersPayantCalculationService tiersPayantCalculationService;

    /*
     * deduire le montant de reference à partir du tiers payant principal c-a-d celui qui le taux le plus grand sur la
     * vente
     */
    private int comoutePrixReferenceAmount(List<MontantTp> montantTps, String cmpteClientId) {
        return montantTps.stream().filter(m -> m.getCompteClientTiersPayantId().equals(cmpteClientId))
                .mapToInt(MontantTp::getMontant).sum();
    }

    private NetComputingDTO computeMontantPlafondAvecOptionTaux(TiersPayantParams tierspayant, int montantTiersPayant,
            boolean asRestrictions) {

        int plafondTiersPayantParVente = 0;
        long plafondGlobal = 0;
        String message = null;
        TTiersPayant payant = null;

        int montantTiersPayantCopie = montantTiersPayant;
        if (asRestrictions) {
            TCompteClientTiersPayant tc = em.find(TCompteClientTiersPayant.class, tierspayant.getCompteTp());
            payant = tc.getLgTIERSPAYANTID();

            plafondTiersPayantParVente = getPlafondTiersPayantParVente(tc);

            montantTiersPayant = computeCustomerOutstanding(tc, montantTiersPayant);
            if (plafondTiersPayantParVente > 0) {
                if (montantTiersPayant > plafondTiersPayantParVente) {
                    montantTiersPayant = plafondTiersPayantParVente;
                }
            }
        }
        if (Objects.nonNull(payant) && montantTiersPayant != montantTiersPayantCopie) {
            message = "Le plafond du tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'> "
                    + payant.getStrFULLNAME() + "</span>? car son plafond est atteint <br>";
        }

        NetComputingDTO netComputing = new NetComputingDTO();
        netComputing.setIdCompteClientTiersPayant(tierspayant.getCompteTp());
        netComputing.setPlafondVente(plafondTiersPayantParVente);
        netComputing.setMontantTiersPayant(montantTiersPayant);
        netComputing.setPlafondGlobal(plafondGlobal);
        netComputing.setMessage(message);
        netComputing.setNumBon(tierspayant.getNumBon());
        return netComputing;

    }

    private MontantAPaye computeAmountByPrixOptionTaux(TPreenregistrement op, List<TiersPayantParams> tierspayants,
            MontantAPaye aPaye, boolean asPlafondActivated) {
        MontantAPaye montantAPaye = new MontantAPaye();
        tierspayants.sort(Comparator.comparing(TiersPayantParams::getTaux, Comparator.reverseOrder()));
        TiersPayantParams tiersPayantParamsRo = tierspayants.get(0);
        int montantVente = op.getIntPRICE();
        int montantTiersPayantBase = comoutePrixReferenceAmount(aPaye.getMontantTierspayants(),
                tierspayants.get(0).getCompteTp());/* aPaye.getTiersPayantBaseAmount(); */// montant de base du RO

        List<NetComputingDTO> datas = new ArrayList<>();

        NetComputingDTO netComputed = computeMontantPlafondAvecOptionTaux(tiersPayantParamsRo, montantTiersPayantBase,
                asPlafondActivated);
        int montantTotalTp = netComputed.getMontantTiersPayant();
        int montantACharge = montantTiersPayantBase - montantTotalTp;
        netComputed.setPercentage(
                (int) Math.ceil((Double.valueOf(netComputed.getMontantTiersPayant()) * 100) / montantTiersPayantBase));
        datas.add(netComputed);
        // second tp
        if (tierspayants.size() > 1) {
            NetComputingDTO netComputedC0 = computeMontantPlafondAvecOptionTaux(tierspayants.get(1),
                    (montantVente - montantTotalTp) - montantACharge, asPlafondActivated);
            montantTotalTp += netComputedC0.getMontantTiersPayant();
            netComputedC0.setPercentage((int) Math
                    .ceil((Double.valueOf(netComputedC0.getMontantTiersPayant()) * 100) / montantTiersPayantBase));
            datas.add(netComputedC0);
        }
        int custPart = (montantVente - montantTotalTp) - op.getIntPRICEREMISE();

        op.setIntCUSTPART(custPart >= 0 ? custPart : 0);
        em.merge(op);

        montantAPaye.setRemise(op.getIntPRICEREMISE());
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

    private MontantAPaye calculAssuranceNet(SalesParams params, boolean asPlafondActivated) {
        MontantAPaye montantAPaye = new MontantAPaye();
        List<TPreenregistrementDetail> items = getItems(params.getVenteId());
        TPreenregistrement op = items.get(0).getLgPREENREGISTREMENTID();
        boolean isCarnet = Constant.VENTE_AVEC_CARNET.equals(op.getLgTYPEVENTEID().getLgTYPEVENTEID());
        boolean isCarnetATauxZero = params.getTierspayants().stream().mapToInt(TiersPayantParams::getTaux).sum() == 0;
        TRemise remise = op.getRemise();
        remise = remise != null ? remise : op.getClient().getRemise();
        MontantAPaye aPaye = computeRemise(op, remise, items);

        if (Objects.nonNull(aPaye.getType()) && aPaye.getType() != PrixReferenceType.PRIX_REFERENCE) {
            return computeAmountByPrixOptionTaux(op, params.getTierspayants(), aPaye, asPlafondActivated);
        }

        if (isCarnet) {
            TCompteClientTiersPayant tc = em.find(TCompteClientTiersPayant.class,
                    params.getTierspayants().get(0).getCompteTp());
            TTiersPayant tTiersPayant = tc.getLgTIERSPAYANTID();
            if (tTiersPayant.hasCaution()) {
                return calculNetAvecCaution(aPaye, op, params, tTiersPayant);
            }

        }
        List<TiersPayantParams> tierspayants = params.getTierspayants();
        tierspayants.sort(Comparator.comparing(TiersPayantParams::getTaux, Comparator.reverseOrder()));
        int montantVente = op.getIntPRICE();
        int montantTiersPayantBase = Objects.nonNull(aPaye.getType()) ? aPaye.getTiersPayantBaseAmount() : montantVente;
        int montantRestant = 0;
        int montantTotalTp = 0;
        List<NetComputingDTO> datas = new ArrayList<>();

        int counter = 0;
        int bonsSize = tierspayants.size();

        int montantTpFinalTierspayant = 0;
        for (TiersPayantParams tiersPayantParams : tierspayants) {
            counter++;
            int amountToCompute = montantTiersPayantBase;

            NetComputingDTO netComputed = computeTiesrPayantNetAmount(tiersPayantParams, amountToCompute,
                    asPlafondActivated);

            montantTotalTp += netComputed.getMontantTiersPayant();

            if (bonsSize > 1 && counter == bonsSize && montantTotalTp > amountToCompute) {

                netComputed.setMontantTiersPayant(montantRestant);

            }

            int reelTaux = (int) Math
                    .ceil((Double.valueOf(netComputed.getMontantTiersPayant()) * 100) / amountToCompute);
            if (reelTaux >= tiersPayantParams.getTaux()) {
                netComputed.setPercentage(tiersPayantParams.getTaux());
            } else {
                if (counter == bonsSize) {
                    netComputed.setPercentage((int) Math
                            .ceil((Double.valueOf(netComputed.getMontantTiersPayant()) * 100) / amountToCompute));
                }
            }

            montantTpFinalTierspayant += netComputed.getMontantTiersPayant();
            montantRestant = amountToCompute - montantTpFinalTierspayant;
            datas.add(netComputed);

        }
        int custPart;
        if (isCarnet) {
            if (isCarnetATauxZero) {
                custPart = montantVente - op.getIntPRICEREMISE();
                montantTpFinalTierspayant = 0;
            } else {
                custPart = montantVente - montantTpFinalTierspayant;
                montantTpFinalTierspayant = montantTpFinalTierspayant - op.getIntPRICEREMISE();
            }

            datas.get(0).setMontantTiersPayant(montantTpFinalTierspayant);

        } else {
            custPart = (montantVente - montantTpFinalTierspayant) - op.getIntPRICEREMISE();
        }

        op.setIntCUSTPART(custPart);

        em.merge(op);

        montantAPaye.setRemise(op.getIntPRICEREMISE());
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

        List<MontantTp> montantTps = new ArrayList<>();
        MontantAPaye aPaye = new MontantAPaye();
        for (TPreenregistrementDetail x : lstTPreenregistrementDetail) {
            // updateMontantTps(x, montantTps, aPaye);
            totalAmount += x.getIntPRICE();
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
        aPaye.setMontant(totalAmount);
        aPaye.setMontantNet(montantNet);
        aPaye.setMontantTp(0);
        aPaye.setRemise(totalRemise);
        aPaye.setMarge(marge);
        aPaye.setMontantTva(montantTva);
        aPaye.setMontantTierspayants(montantTps);
        return aPaye;

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
        return calcule(params);
        // return calculAssuranceNet(params, asPlafondActivated);

    }

    private NetComputingDTO computeTiesrPayantNetAmount(TiersPayantParams tierspayant, int amountToCompute,
            boolean asRestrictions) {

        int plafondTiersPayantParVente = 0;
        long plafondGlobal = 0;
        String message = null;
        TTiersPayant payant = null;
        float taux = Float.valueOf(tierspayant.getTaux()) / 100;
        double montantTp = amountToCompute * taux;
        int montantTiersPayant = (int) Math.ceil(montantTp);
        int montantTiersPayantCopie = montantTiersPayant;
        if (asRestrictions) {
            TCompteClientTiersPayant tc = em.find(TCompteClientTiersPayant.class, tierspayant.getCompteTp());
            payant = tc.getLgTIERSPAYANTID();

            plafondTiersPayantParVente = getPlafondTiersPayantParVente(tc);

            montantTiersPayant = computeCustomerOutstanding(tc, montantTiersPayant);
            if (plafondTiersPayantParVente > 0) {
                if (montantTiersPayant > plafondTiersPayantParVente) {
                    montantTiersPayant = plafondTiersPayantParVente;
                }
            }
        }
        if (Objects.nonNull(payant) && montantTiersPayant != montantTiersPayantCopie) {
            message = "Le plafond du tierspayant: <span style='font-weight:900;color:blue;text-decoration: underline;'> "
                    + payant.getStrFULLNAME() + "</span>? car son plafond est atteint <br>";
        }

        NetComputingDTO netComputing = new NetComputingDTO();
        netComputing.setIdCompteClientTiersPayant(tierspayant.getCompteTp());
        netComputing.setPlafondVente(plafondTiersPayantParVente);
        netComputing.setMontantTiersPayant(montantTiersPayant);
        netComputing.setPlafondGlobal(plafondGlobal);
        netComputing.setMessage(message);
        netComputing.setNumBon(tierspayant.getNumBon());
        return netComputing;

    }

    private int computeCustomerOutstanding(TCompteClientTiersPayant tc, int partTiersPayant) {
        var encoursClient = tc.getDbPLAFONDENCOURS() != null && tc.getDbPLAFONDENCOURS() > 0 ? tc.getDbPLAFONDENCOURS()
                : 0;
        if (encoursClient == 0) {
            return partTiersPayant;
        }
        var consoMensuelleClient = tc.getDbCONSOMMATIONMENSUELLE() != null && tc.getDbCONSOMMATIONMENSUELLE() > 0
                ? tc.getDbCONSOMMATIONMENSUELLE() : 0;
        var conso = consoMensuelleClient + partTiersPayant;
        if (conso <= encoursClient) {
            return partTiersPayant;
        }
        return encoursClient - consoMensuelleClient;

    }

    private int getPlafondTiersPayantParVente(TCompteClientTiersPayant tc) {
        return (tc.getDblPLAFOND() == null || tc.getDblPLAFOND() <= 0 ? 0 : tc.getDblPLAFOND().intValue());
    }

    private NetComputingDTO computeCaution(TiersPayantParams tierspayant, TTiersPayant payant, TPreenregistrement op) {
        int montantTp;
        Caution c = payant.getCaution();
        int caution = c.getMontant();
        int montantPaye = op.getIntPRICE() - op.getIntPRICEREMISE();
        if (caution >= montantPaye) {
            montantTp = montantPaye;
        } else {
            montantTp = caution;
        }
        NetComputingDTO netComputing = new NetComputingDTO();
        netComputing.setIdCompteClientTiersPayant(tierspayant.getCompteTp());
        netComputing.setPlafondVente(0);
        netComputing.setMontantTiersPayant(montantTp);
        netComputing.setPlafondGlobal(0);
        netComputing.setNumBon(tierspayant.getNumBon());
        op.setCaution(c);
        return netComputing;

    }

    private MontantAPaye calculNetAvecCaution(MontantAPaye aPaye, TPreenregistrement op, SalesParams params,
            TTiersPayant payant) {
        MontantAPaye montantAPaye = new MontantAPaye();
        TiersPayantParams tierspayant = params.getTierspayants().get(0);
        NetComputingDTO netComputing = computeCaution(tierspayant, payant, op);
        int custPart = (op.getIntPRICE() - op.getIntPRICEREMISE()) - netComputing.getMontantTiersPayant();
        op.setIntCUSTPART(custPart);
        em.merge(op);
        montantAPaye.setRemise(op.getIntPRICEREMISE());
        montantAPaye.setMontantNet(NumberUtils.arrondiModuloOfNumber(op.getIntCUSTPART(), 5));
        montantAPaye.setMontant(op.getIntPRICE());
        montantAPaye.setMarge(aPaye.getMarge());
        montantAPaye.setMontantTva(aPaye.getMontantTva());
        montantAPaye.setMontantTp(netComputing.getMontantTiersPayant());
        TiersPayantParams tp = new TiersPayantParams();
        tp.setCompteTp(tierspayant.getCompteTp());
        tp.setTaux(100);
        tp.setNumBon(tierspayant.getNumBon());
        tp.setTpnet(montantAPaye.getMontantTp());
        montantAPaye.getTierspayants().add(tp);
        return montantAPaye;
    }

    private MontantAPaye calcule(SalesParams params) {
        TPreenregistrement op = em.find(TPreenregistrement.class, params.getVenteId());
        boolean isCarnet = Constant.VENTE_AVEC_CARNET.equals(op.getLgTYPEVENTEID().getLgTYPEVENTEID());

        TRemise remise = op.getRemise();
        remise = remise != null ? remise : op.getClient().getRemise();
        List<TPreenregistrementDetail> items = new ArrayList<>(op.getTPreenregistrementDetailCollection());
        MontantAPaye montantAPaye = computeRemise(op, remise, items);
        List<TPreenregistrementCompteClientTiersPayent> compteClientTiersPayents = new ArrayList<>(
                op.getTPreenregistrementCompteClientTiersPayentCollection());

        if (isCarnet) {
            TCompteClientTiersPayant tc = compteClientTiersPayents.get(0).getLgCOMPTECLIENTTIERSPAYANTID();
            TTiersPayant tTiersPayant = tc.getLgTIERSPAYANTID();
            if (tTiersPayant.hasCaution()) {
                return calculNetAvecCaution(montantAPaye, op, params, tTiersPayant);
            }

        }
        Map<String, List<TiersPayantParams>> tpsBons = params.getTierspayants().stream()
                .collect(Collectors.groupingBy(TiersPayantParams::getCompteTp));
        CalculationInput input = buildCalculationInput(op, items, compteClientTiersPayents, tpsBons);
        input.setDiscountAmount(BigDecimal.valueOf(Objects.requireNonNullElse(montantAPaye.getMontantAccount(), 0)));
        CalculationResult output = tiersPayantCalculationService.calculate(input);

        Map<String, List<TPreenregistrementCompteClientTiersPayent>> maps = compteClientTiersPayents.stream().collect(
                Collectors.groupingBy(e -> e.getLgCOMPTECLIENTTIERSPAYANTID().getLgCOMPTECLIENTTIERSPAYANTID()));

        int totalPatientShare = output.getTotalPatientShare().intValue();
        op.setIntCUSTPART(totalPatientShare);

        for (TiersPayantLineOutput lineResult : output.getTiersPayantLines()) {
            TPreenregistrementCompteClientTiersPayent saleLine = maps.get(lineResult.getClientTiersPayantId()).get(0);

            if (output.isHasPriceOption()) {
                TCompteClientTiersPayant tcctp = saleLine.getLgCOMPTECLIENTTIERSPAYANTID();
                saleLine.setIntPERCENT(tcctp.getIntPOURCENTAGE());
            } else {
                saleLine.setIntPERCENT(lineResult.getFinalTaux());
            }
            saleLine.setIntPRICE(lineResult.getMontant().intValue());
            saleLine.setStrREFBON(lineResult.getNumBon());

            em.merge(saleLine);
            TiersPayantParams tp = new TiersPayantParams();
            tp.setTaux(saleLine.getIntPERCENT());
            tp.setCompteTp(lineResult.getClientTiersPayantId());
            tp.setNumBon(saleLine.getStrREFBON());
            tp.setTpnet(saleLine.getIntPRICE());
            montantAPaye.getTierspayants().add(tp);

        }
        for (TPreenregistrementDetail saleLine : items) {

            output.getItemShares().stream()
                    .filter(s -> s.getSaleLineId().equals(saleLine.getLgPREENREGISTREMENTDETAILID())).findFirst()
                    .ifPresent(itemShare -> {
                        saleLine.setCalculationBasePrice(itemShare.getCalculationBasePrice());
                        em.merge(saleLine);
                        itemShare.getRates().forEach(em::persist);
                    });
        }
        op.setHasPriceOption(output.isHasPriceOption());
        em.merge(op);
        montantAPaye.setMontantTp(output.getTotalTiersPayant().intValue());

        montantAPaye.setMontantNet(NumberUtils.arrondiModuloOfNumber(op.getIntCUSTPART(), 5));
        montantAPaye.setRestructuring(StringUtils.isNoneEmpty(output.getWarningMessage()));
        montantAPaye.setMessage(output.getWarningMessage());

        return montantAPaye;

    }

    private List<SaleItemInput> buildSaleItemInputs(List<TPreenregistrementDetail> items, CalculationInput input,
            Set<String> tiersPayantIds, List<TiersPayantInput> tiersPayantInputs) {

        return items.stream().map(sl -> {
            SaleItemInput si = new SaleItemInput();
            TFamille produit = sl.getLgFAMILLEID();
            si.setSalesLineId(sl.getLgPREENREGISTREMENTDETAILID());
            si.setTotalSalesAmount(BigDecimal.valueOf(sl.getIntPRICE()));
            si.setQuantity(sl.getIntQUANTITY());
            si.setDiscountAmount(BigDecimal.valueOf(Objects.requireNonNullElse(sl.getIntPRICEREMISE(), 0)));
            si.setRegularUnitPrice(BigDecimal.valueOf(sl.getIntPRICEUNITAIR()));
            input.setTotalSalesAmount(Objects.requireNonNullElse(input.getTotalSalesAmount(), BigDecimal.ZERO)
                    .add(si.getTotalSalesAmount()));
            this.prixReferenceService.getActifByProduitIdAndTiersPayantIds(produit.getLgFAMILLEID(), tiersPayantIds)
                    .forEach(prixRef -> {

                        TTiersPayant pt = prixRef.getTiersPayant();
                        tiersPayantInputs.forEach(cl -> {
                            if (cl.getTiersPayantId().equals(pt.getLgTIERSPAYANTID())) {
                                TiersPayantPrixInput pi = new TiersPayantPrixInput();
                                pi.setCompteTiersPayantId(cl.getClientTiersPayantId());
                                pi.setPrice(prixRef.getValeur());
                                if (Objects.isNull(prixRef.getValeurTaux())) {
                                    pi.setRate(100.0f);
                                } else {
                                    pi.setRate(prixRef.getValeurTaux());
                                }

                                pi.setOptionPrixType(prixRef.getType());
                                si.getPrixAssurances().add(pi);
                            }

                        });

                    });

            return si;
        }).collect(Collectors.toList());
    }

    private CalculationInput buildCalculationInput(TPreenregistrement sale, List<TPreenregistrementDetail> items,
            List<TPreenregistrementCompteClientTiersPayent> compteClientTiersPayents,
            Map<String, List<TiersPayantParams>> tpsBons) {
        TTypeVente tTypeVente = sale.getLgTYPEVENTEID();
        CalculationInput input = new CalculationInput();
        input.setNatureVente(Constant.VENTE_ASSURANCE_ID.equals(tTypeVente.getLgTYPEVENTEID()) ? NatureVente.ASSURANCE
                : NatureVente.CARNET);
        input.setDiscountAmount(BigDecimal.valueOf(Objects.requireNonNullElse(sale.getIntPRICEREMISE(), 0)));

        Set<String> tiersPayantIds = new HashSet<>();
        List<TiersPayantInput> tiersPayantInputs = buildTiersPayantInputs(compteClientTiersPayents, tiersPayantIds,
                tpsBons);
        input.setTiersPayants(tiersPayantInputs);
        input.setSaleItems(buildSaleItemInputs(items, input, tiersPayantIds, tiersPayantInputs));

        return input;
    }

    private List<TiersPayantInput> buildTiersPayantInputs(
            List<TPreenregistrementCompteClientTiersPayent> compteClientTiersPayents, Set<String> tiersPayantIds,
            Map<String, List<TiersPayantParams>> tpsBons) {
        if (CollectionUtils.isEmpty(compteClientTiersPayents)) {
            return Collections.emptyList();
        }
        return compteClientTiersPayents.stream().map(it -> {
            TCompteClientTiersPayant ctp = it.getLgCOMPTECLIENTTIERSPAYANTID();
            TiersPayantParams tiersPayantParams = tpsBons.get(ctp.getLgCOMPTECLIENTTIERSPAYANTID()).get(0);
            TiersPayantInput ti = new TiersPayantInput();
            TTiersPayant tiersPayant = ctp.getLgTIERSPAYANTID();
            tiersPayantIds.add(tiersPayant.getLgTIERSPAYANTID());
            ti.setClientTiersPayantId(ctp.getLgCOMPTECLIENTTIERSPAYANTID());
            ti.setTiersPayantId(tiersPayant.getLgTIERSPAYANTID());
            ti.setTiersPayantFullName(tiersPayant.getStrFULLNAME());
            // ti.setTaux(ctp.getIntPOURCENTAGE() / 100.0f);// apres les retours de franck, on prends le taux sais du
            // front
            if (Objects.nonNull(it.getIntPERCENT())) {
                ti.setTaux(it.getIntPERCENT() / 100.0f);
            } else {
                ti.setTaux(tiersPayantParams.getTaux() / 100.0f);
            }
            ti.setNumBon(tiersPayantParams.getNumBon());
            ti.setPriorite(ctp.getIntPRIORITY());
            // Optional.ofNullable(tiersPayant.getDblPLAFONDCREDIT()).ifPresent(v ->
            // ti.setPlafondConso(BigDecimal.valueOf(v))); // A voir sil faut ajouter les plafond sur la fiche du TP
            Optional.ofNullable(ctp.getDbPLAFONDENCOURS()).ifPresent(v -> ti.setPlafondConso(BigDecimal.valueOf(v)));
            Optional.ofNullable(ctp.getDbCONSOMMATIONMENSUELLE())
                    .ifPresent(v -> ti.setConsoMensuelle(BigDecimal.valueOf(v)));
            Optional.ofNullable(ctp.getDblPLAFOND())
                    .ifPresent(v -> ti.setPlafondJournalierClient(BigDecimal.valueOf(v)));
            return ti;
        }).collect(Collectors.toList());

    }

}
