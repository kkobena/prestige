package rest.service.fne;

import javax.ejb.Local;
import rest.service.exception.FneExeception;

/**
 *
 * @author koben
 */
@Local
public interface FneService {

    void createInvoice(String idFacture, TypeInvoice typeInvoice) throws FneExeception;

    // void createGroupeInvoice(Integer idFacture, TypeInvoice typeInvoice);

    void createGroupeInvoice(Integer idFacture, String codeFacture, TypeInvoice typeInvoice);
}
