package rest.service.impl;

import dal.TPreenregistrement;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import util.Constant;

/**
 * Une vente se modifie tant qu'elle n'est pas terminee (retour du 14/09 : la proforma refusait son deuxieme produit en
 * se disant cloturee, comme la prevente le 12/09). Le controle refuse les statuts qui terminent une vente, et eux
 * seuls.
 */
class ProformaModifiableTest {

    private static TPreenregistrement vente(String statut) {
        TPreenregistrement tp = new TPreenregistrement();
        tp.setStrSTATUT(statut);
        return tp;
    }

    @Test
    void touteVenteEnCoursDeCompositionSeModifie() {
        assertTrue(SalesServiceImpl.venteEnCours(vente(Constant.STATUT_IS_PROGRESS)), "vente ordinaire");
        assertTrue(SalesServiceImpl.venteEnCours(vente(Constant.STATUT_PENDING)), "prevente");
        assertTrue(SalesServiceImpl.venteEnCours(vente(Constant.STATUT_IS_DEVIS)), "proforma");
        // un parcours qui utiliserait demain un autre statut de composition ne doit pas etre refuse
        assertTrue(SalesServiceImpl.venteEnCours(vente("nouveau_parcours")), "statut de composition inconnu");
    }

    @Test
    void venteTermineeOuAnnuleeNeSeModifiePlus() {
        assertFalse(SalesServiceImpl.venteEnCours(vente(Constant.STATUT_IS_CLOSED)), "vente cloturee");
        assertFalse(SalesServiceImpl.venteEnCours(vente(Constant.STATUT_CANCEL)), "vente annulee");
        assertFalse(SalesServiceImpl.venteEnCours(vente(Constant.STATUT_DELETE)), "vente supprimee");
        assertFalse(SalesServiceImpl.venteEnCours(vente(null)), "vente sans statut");
        assertFalse(SalesServiceImpl.venteEnCours(null), "vente absente");
    }

    @Test
    void leMessageDitLaBonneRaison() {
        Calendar c = Calendar.getInstance();
        c.set(2026, Calendar.SEPTEMBER, 14, 11, 32, 0);
        Date quand = c.getTime();
        String cloturee = SalesServiceImpl.messageVenteNonModifiable("260914_00001", quand, Constant.STATUT_IS_CLOSED);
        assertTrue(cloturee.contains("clôturée le 14/09/2026 11:32"), cloturee);
        assertTrue(cloturee.contains("Ventes terminées"), cloturee);

        String annulee = SalesServiceImpl.messageVenteNonModifiable("260914_00002", quand, Constant.STATUT_CANCEL);
        assertTrue(annulee.contains("annulée ou supprimée"), annulee);
        assertFalse(annulee.contains("clôturée"), annulee);
        assertTrue(annulee.contains("260914_00002"), annulee);
        assertFalse(annulee.contains("null"), annulee);
    }
}
