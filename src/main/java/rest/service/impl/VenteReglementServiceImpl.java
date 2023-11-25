package rest.service.impl;

import dal.TPreenregistrement;
import dal.TTypeReglement;
import dal.VenteReglement;
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
    public void createNew(TPreenregistrement preenregistrement, TTypeReglement typeReglement, int montant,
            int montantAttendu) {
        VenteReglement venteReglement = new VenteReglement();
        venteReglement.setMontant(montant);
        venteReglement.setTypeReglement(typeReglement);
        venteReglement.setPreenregistrement(preenregistrement);
        venteReglement.setMvtDate(DateCommonUtils.convertDateToLocalDateTime(preenregistrement.getDtUPDATED()));
        venteReglement.setMontantAttentu(montantAttendu);
        em.persist(venteReglement);
    }

    @Override
    public List<VenteReglement> getByVenteId(String venteId) {
        TypedQuery<VenteReglement> query = em
                .createQuery("SELECT o FROM VenteReglement o WHERE o.preenregistrement.lgPREENREGISTREMENTID=?1",
                        VenteReglement.class)
                .setParameter(1, venteId);
        return query.getResultList();
    }

}
