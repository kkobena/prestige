package rest.service;

import javax.ejb.Local;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author airman
 */

@Local
public interface GroupeInvoiceService {

    JSONObject getGroupeInvoices(String dtStart, String dtEnd, String searchValue, Integer lgGroupeId,
            String codeGroupe, boolean actionReglerFacture, int start, int limit) throws JSONException;

    JSONObject getGroupeInvoiceDetails(String codeGroupe, String lgTP, String searchValue, int start, int limit)
            throws JSONException;
}
