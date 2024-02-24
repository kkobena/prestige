package rest.service.impl;

import commonTasks.dto.VenteReglementDTO;
import dal.MvtTransaction;
import dal.TPreenregistrement;
import dal.TPreenregistrementDetail;
import dal.TTypeReglement;
import dal.VenteReglement;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rest.service.VenteReglementService;
import util.Constant;
import util.DateCommonUtils;

/**
 *
 * @author koben
 */
@Stateless
public class VenteReglementServiceImpl implements VenteReglementService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public List<VenteReglement> getByVenteId(String venteId) {
        TypedQuery<VenteReglement> query = em
                .createQuery("SELECT o FROM VenteReglement o WHERE o.preenregistrement.lgPREENREGISTREMENTID=?1",
                        VenteReglement.class)
                .setParameter(1, venteId);
        return query.getResultList();
    }

    private void createNew(VenteReglement venteReglement) {
        em.persist(venteReglement);
    }

    @Override
    public void createNew(TPreenregistrement preenregistrement, TTypeReglement typeReglement, MvtTransaction mt) {
        VenteReglement venteReglement = new VenteReglement();
        venteReglement.setMontant(mt.getMontantPaye());
        venteReglement.setTypeReglement(typeReglement);
        venteReglement.setPreenregistrement(preenregistrement);
        venteReglement.setMvtDate(DateCommonUtils.convertDateToLocalDateTime(preenregistrement.getDtUPDATED()));
        venteReglement.setMontantAttentu(mt.getMontantRegle());
        venteReglement.setUgAmount(mt.getMontantttcug());
        venteReglement.setUgNetAmount(mt.getMontantnetug());
        venteReglement.setAmountNonCa(computeSumOfExclusVenteItemFromCa(preenregistrement));
        em.persist(venteReglement);
    }

    @Override
    public void createCopyVenteReglement(TPreenregistrement tp, VenteReglement venteReglement) {
        VenteReglement copy = new VenteReglement();
        copy.setMontant(venteReglement.getMontant() * (-1));
        copy.setTypeReglement(venteReglement.getTypeReglement());
        copy.setPreenregistrement(tp);
        copy.setMvtDate(DateCommonUtils.convertDateToLocalDateTime(tp.getDtUPDATED()));
        copy.setMontantAttentu(venteReglement.getMontantAttentu() * (-1));
        copy.setUgAmount(venteReglement.getUgAmount() * (-1));
        copy.setUgNetAmount(venteReglement.getUgNetAmount() * (-1));
        copy.setAmountNonCa(venteReglement.getAmountNonCa() * (-1));
        copy.setFlagedAmount(venteReglement.getFlagedAmount()* (-1));
        this.createNew(copy);
    }

    @Override
    public void createVenteReglement(TPreenregistrement tp, VenteReglementDTO p, TTypeReglement typeReglement,
            LocalDateTime mvtDate) {
        VenteReglement venteReglement = new VenteReglement();
        venteReglement.setMontant(p.getMontant());
        venteReglement.setAmountNonCa(p.getAmountNonCa());
        venteReglement.setTypeReglement(typeReglement);
        venteReglement.setPreenregistrement(tp);
        venteReglement.setMvtDate(mvtDate);
        venteReglement.setMontantAttentu(p.getMontantAttentu());
        venteReglement.setUgAmount(p.getMontantTttcug());
        venteReglement.setUgNetAmount(p.getMontantnetug());
        this.createNew(venteReglement);
    }

    private int computeSumOfExclusVenteItemFromCa(TPreenregistrement tp) {
        return tp.getTPreenregistrementDetailCollection().stream().filter(
                e -> !e.getBoolACCOUNT() && tp.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Constant.VENTE_COMPTANT_ID))
                .mapToInt(TPreenregistrementDetail::getIntPRICE).sum();
    }
}
