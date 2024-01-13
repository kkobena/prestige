package rest.service.impl;

import commonTasks.dto.VenteReglementDTO;
import dal.MvtTransaction;
import dal.TPreenregistrement;
import dal.TTypeReglement;
import dal.VenteReglement;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rest.service.VenteReglementService;
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

    @Override
    public void createNew(VenteReglement venteReglement) {
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
        this.createNew(venteReglement);
    }

    @Override
    public void createVenteReglement(TPreenregistrement tp, VenteReglementDTO p, TTypeReglement typeReglement,
            LocalDateTime mvtDate, int montantTtcUg, int montantNetUg) {
        VenteReglement venteReglement = new VenteReglement();
        venteReglement.setMontant(p.getMontant());
        venteReglement.setTypeReglement(typeReglement);
        venteReglement.setPreenregistrement(tp);
        venteReglement.setMvtDate(mvtDate);
        venteReglement.setMontantAttentu(p.getMontantAttentu());
        venteReglement.setUgAmount(montantTtcUg);
        venteReglement.setUgNetAmount(montantNetUg);
        this.createNew(venteReglement);
    }
}
