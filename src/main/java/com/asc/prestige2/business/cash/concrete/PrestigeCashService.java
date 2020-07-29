/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asc.prestige2.business.cash.concrete;

import bll.bllBase;
import bll.common.Parameter;
import bll.entity.EntityData;
import bll.userManagement.privilege;
import bll.utils.TparameterManager;
import com.asc.prestige2.business.cash.CashService;
import dal.TParameters;
import dal.TUser;
import dal.dataManager;
import dal.jconnexion;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import toolkits.utils.logger;

/**
 *
 * @author JZAGO
 */
public class PrestigeCashService extends bllBase implements CashService {

    public PrestigeCashService(dataManager odataManager, TUser oTUser) {
        this.setOdataManager(odataManager);
        this.setOTUser(oTUser);
        this.checkDatamanager();
    }

    @Override
    public int getMontantAnnule(String lg_USER_ID, String dt_date_debut, String dt_date_fin) {

        List<EntityData> lstEntityData = new ArrayList<>();
        EntityData OEntityData = null;

        privilege Oprivilege = new privilege(this.getOdataManager(), this.getOTUser());
        TparameterManager OTparameterManager = new TparameterManager(this.getOdataManager());
        
        int value = 0;
        try {
            jconnexion Ojconnexion = new jconnexion();
            Ojconnexion.initConnexion();
          
            String sql = "SELECT SUM(t.int_AMOUNT_DEBIT) AS int_AMOUNT_ANNULE\n"
                    + "FROM t_cash_transaction t, t_preenregistrement p, t_reglement r \n"
                    + "WHERE t.lg_REGLEMENT_ID = r.lg_REGLEMENT_ID \n"
                    + "AND p.lg_REGLEMENT_ID = r.lg_REGLEMENT_ID\n"
                    + "AND p.int_PRICE > 0 \n"
                    + "AND p.b_IS_CANCEL = true\n"
                    + "AND p.str_STATUT = 'is_Closed'\n"
                    + "AND t.str_TASK = 'ANNULE_VENTE'\n"
                    + "AND p.lg_USER_ID LIKE '" + lg_USER_ID + "'\n"
                    + "AND DATE(p.dt_UPDATED ) >='" + dt_date_debut + "' \n"
                    + "AND DATE(p.dt_UPDATED ) <='" + dt_date_fin + "'";
            new logger().OCategory.info("qry -- " + sql);
            Ojconnexion.set_Request(sql);
            ResultSet resultSet = Ojconnexion.get_resultat();
            
            ResultSetMetaData rsmd = resultSet.getMetaData();
            System.out.println("querying SELECT * FROM XXX");
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) {
                        System.out.print(",  ");
                    }
                    String columnValue = resultSet.getString(i);
                    if(columnValue != null){
                       value = Integer.parseInt(columnValue);
                    }
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }

            //ResultSetMetaData rsmddatas = Ojconnexion.get_resultat().getMetaData();
            //int count = rsmddatas.getColumnCount();
//           if(count  <= 0){
//              throw new Exception("No rows found");
//           }else{
            //value = Ojconnexion.get_resultat().getInt("int_AMOUNT_ANNULE");
            System.out.println("VALUE: " + value);
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

}
