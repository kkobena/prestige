/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bll.teller;

import bll.report.JournalVente;
import dal.TUser;
import dal.dataManager;

/**
 *
 * @author MKABOU
 */
public class NewMain {

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        dataManager OdataManager = new dataManager();
        OdataManager.initEntityManager();
        JournalVente OJournalVente = new JournalVente(OdataManager,
                OdataManager.getEm().find(TUser.class, "52141743589144800410"));
        OJournalVente.getTotalAmountCashTransaction(
                OJournalVente.getListeCaisse("2016-09-01", "2016-09-30", "00:00", "23:59", "%%", "%%", 6650, 20));
        // OJournalVente.getTotalListeCaisse("2016-09-01", "2016-09-30", "00:00", "23:59", "%%", "%%");
    }

}
