/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bll.transaction;

import dal.TRecettes;
import java.time.LocalDate;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Kobena
 */
public interface Transaction {

    List<TRecettes> getRecetteses(LocalDate start, LocalDate end);

    Double getMontantRecetteses(LocalDate start, LocalDate end) throws Exception;

    JSONObject getDataActivites(String dt_start, String dt_end, String emp, int start, int limit);

    Integer getBalanceRegl(String dt_start, String dt_end, String typevente, String lgEmp, String lgTYPEREGLEMENTID);

    Integer getBalanceRegl(String dt_start, String dt_end, String lgEmp, String lgTYPEREGLEMENTID);
}
