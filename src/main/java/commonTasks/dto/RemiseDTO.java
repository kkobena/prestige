/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TRemise;
import dal.TTypeRemise;
import java.io.Serializable;

/**
 *
 * @author Kobena
 */
public class RemiseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String lgREMISEID, strNAME, strCODE, lgTYPEREMISEID, libelleType;
    private double dblTAUX;

    public String getLgREMISEID() {
        return lgREMISEID;
    }

    public void setLgREMISEID(String lgREMISEID) {
        this.lgREMISEID = lgREMISEID;
    }

    public String getStrNAME() {
        return strNAME;
    }

    public void setStrNAME(String strNAME) {
        this.strNAME = strNAME;
    }

    public String getStrCODE() {
        return strCODE;
    }

    public void setStrCODE(String strCODE) {
        this.strCODE = strCODE;
    }

    public String getLgTYPEREMISEID() {
        return lgTYPEREMISEID;
    }

    public void setLgTYPEREMISEID(String lgTYPEREMISEID) {
        this.lgTYPEREMISEID = lgTYPEREMISEID;
    }

    public String getLibelleType() {
        return libelleType;
    }

    public void setLibelleType(String libelleType) {
        this.libelleType = libelleType;
    }

    public double getDblTAUX() {
        return dblTAUX;
    }

    public void setDblTAUX(double dblTAUX) {
        this.dblTAUX = dblTAUX;
    }

    public RemiseDTO() {
    }

    public RemiseDTO(TRemise remise) {
        this.lgREMISEID = remise.getLgREMISEID();
        this.strNAME = remise.getStrNAME();
        this.strCODE = remise.getStrCODE();
        TTypeRemise tTypeRemise = remise.getLgTYPEREMISEID();
        this.lgTYPEREMISEID = tTypeRemise.getLgTYPEREMISEID();
        this.libelleType = tTypeRemise.getStrNAME();
        this.dblTAUX = remise.getDblTAUX();
    }

    public RemiseDTO(String lgREMISEID, String strNAME) {
        this.lgREMISEID = lgREMISEID;
        this.strNAME = strNAME;
    }

}
