/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TGroupeTierspayant;
import dal.TTiersPayant;
import java.io.Serializable;

/**
 *
 * @author koben
 */
public class ErpTiersPayant implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  tiersPayantId, tiersPayantLibelle,adresse,telephone ,groupeLibelle;
    private Integer groupeId;

    public ErpTiersPayant(TTiersPayant p) {
        this.tiersPayantId = p.getLgTIERSPAYANTID();
        this.tiersPayantLibelle = p.getStrFULLNAME();
        this.adresse = p.getStrADRESSE();
        this.telephone = p.getStrTELEPHONE();
        TGroupeTierspayant g=p.getLgGROUPEID();
        if(g!=null){
          this.groupeId = g.getLgGROUPEID();  
          this.groupeLibelle=g.getStrLIBELLE();
        }
        
    }

    public String getTiersPayantId() {
        return tiersPayantId;
    }

    public void setTiersPayantId(String tiersPayantId) {
        this.tiersPayantId = tiersPayantId;
    }

    public String getTiersPayantLibelle() {
        return tiersPayantLibelle;
    }

    public void setTiersPayantLibelle(String tiersPayantLibelle) {
        this.tiersPayantLibelle = tiersPayantLibelle;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getGroupeLibelle() {
        return groupeLibelle;
    }

    public void setGroupeLibelle(String groupeLibelle) {
        this.groupeLibelle = groupeLibelle;
    }

    public Integer getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Integer groupeId) {
        this.groupeId = groupeId;
    }

   
    
    
    
    
    
}
