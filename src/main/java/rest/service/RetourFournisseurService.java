/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import commonTasks.dto.ErpAvoir;
import commonTasks.dto.RetourDetailsDTO;
import commonTasks.dto.RetourFournisseurDTO;
import dal.TUser;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author koben
 */
@Local
public interface RetourFournisseurService {

    List<RetourDetailsDTO> loadDetailRetourFournisseur(String retourId) ;

    RetourFournisseurDTO createRetour(RetourFournisseurDTO params);

    RetourDetailsDTO addItem(RetourDetailsDTO params);

    RetourDetailsDTO updateItem(RetourDetailsDTO params);

    void removeItem(String params);

    void cloture(RetourFournisseurDTO params);
    
      List<ErpAvoir> erpAvoirsFournisseurs(String dtStart, String dtEnd) ;
      
      void returnFullBonLivraison(String bonId, String motifId,TUser user) throws CloneNotSupportedException;
}
