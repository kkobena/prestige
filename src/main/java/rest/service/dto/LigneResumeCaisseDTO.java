package rest.service.dto;

import dal.LigneResumeCaisse;
import dal.TTypeReglement;
import java.math.BigDecimal;
import javax.persistence.Tuple;

/**
 *
 * @author koben
 */
public class LigneResumeCaisseDTO {

    private final long montant;
    private final String libelleReglement;
    private final String idRegelement;

    public LigneResumeCaisseDTO(LigneResumeCaisse ligneResumeCaisse) {
        this.montant = ligneResumeCaisse.getMontant();
        TTypeReglement reglement = ligneResumeCaisse.getTypeReglement();
        this.libelleReglement = reglement.getStrDESCRIPTION();
        this.idRegelement = reglement.getLgTYPEREGLEMENTID();
    }

    public LigneResumeCaisseDTO(Tuple tuple) {
        this.montant = tuple.get("montantRegle", BigDecimal.class).longValue();
        this.idRegelement = tuple.get("type_regelement", String.class);
        this.libelleReglement = null;

    }

    public long getMontant() {
        return montant;
    }

    public String getIdRegelement() {
        return idRegelement;
    }

    public String getLibelleReglement() {
        return libelleReglement;
    }

}
