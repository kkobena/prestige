/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TGroupeTierspayant;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TTiersPayant;
import dal.TTypeTiersPayant;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author koben
 */
public class VenteTiersPayantsDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String libelleGroupe;
    private Integer groupeId;
    private String tiersPayantId;
    private String libelleTiersPayant;
    private String codeTiersPayant;
    private int nbreDossier;
    private long montant;
    private long montantRemise;
    private String typeTiersPayant;
    private String typeTiersPayantId;

    public String getLibelleGroupe() {
        return libelleGroupe;
    }

    public String getTypeTiersPayant() {
        return typeTiersPayant;
    }

    public void setTypeTiersPayant(String typeTiersPayant) {
        this.typeTiersPayant = typeTiersPayant;
    }

    public String getTypeTiersPayantId() {
        return typeTiersPayantId;
    }

    public void setTypeTiersPayantId(String typeTiersPayantId) {
        this.typeTiersPayantId = typeTiersPayantId;
    }

    public void setLibelleGroupe(String libelleGroupe) {
        this.libelleGroupe = libelleGroupe;
    }

    public String getLibelleTiersPayant() {
        return libelleTiersPayant;
    }

    public void setLibelleTiersPayant(String libelleTiersPayant) {
        this.libelleTiersPayant = libelleTiersPayant;
    }

    public String getCodeTiersPayant() {
        return codeTiersPayant;
    }

    public void setCodeTiersPayant(String codeTiersPayant) {
        this.codeTiersPayant = codeTiersPayant;
    }

    public int getNbreDossier() {
        return nbreDossier;
    }

    public void setNbreDossier(int nbreDossier) {
        this.nbreDossier = nbreDossier;
    }

    public long getMontant() {
        return montant;
    }

    public void setMontant(long montant) {
        this.montant = montant;
    }

    public VenteTiersPayantsDTO(TTiersPayant payant, List<TPreenregistrementCompteClientTiersPayent> clientTiersPayents) {
      
            this.tiersPayantId = payant.getLgTIERSPAYANTID();
            this.codeTiersPayant = payant.getStrCODEORGANISME();
            this.libelleTiersPayant = payant.getStrFULLNAME();
            TGroupeTierspayant groupe = payant.getLgGROUPEID();
            if (groupe != null) {
                this.groupeId = groupe.getLgGROUPEID();
                this.libelleGroupe = groupe.getStrLIBELLE();
            }
          
            clientTiersPayents.forEach(e -> {
                this.nbreDossier++;
                this.montant += e.getIntPRICE();
                this.montantRemise += e.getIntPRICERESTE();
            });
        }

   
    public Integer getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Integer groupeId) {
        this.groupeId = groupeId;
    }

    public String getTiersPayantId() {
        return tiersPayantId;
    }

    public void setTiersPayantId(String tiersPayantId) {
        this.tiersPayantId = tiersPayantId;
    }

    public long getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(long montantRemise) {
        this.montantRemise = montantRemise;
    }
    
       public VenteTiersPayantsDTO(TTiersPayant payant, long nbreDossier,long montant,long montantRemise ) {
      
            this.tiersPayantId = payant.getLgTIERSPAYANTID();
            this.codeTiersPayant = payant.getStrCODEORGANISME();
            this.libelleTiersPayant = payant.getStrFULLNAME();
            TTypeTiersPayant typeTiersPayant=payant.getLgTYPETIERSPAYANTID();
            if(typeTiersPayant!=null){
                this.typeTiersPayant=typeTiersPayant.getStrLIBELLETYPETIERSPAYANT();
                this.typeTiersPayantId=typeTiersPayant.getLgTYPETIERSPAYANTID();
            }
            TGroupeTierspayant groupe = payant.getLgGROUPEID();
            if (groupe != null) {
                this.groupeId = groupe.getLgGROUPEID();
                this.libelleGroupe = groupe.getStrLIBELLE();
            }
          
                this.nbreDossier=(int)nbreDossier;
                this.montant =montant;
                this.montantRemise =montantRemise;
          
        }

}
