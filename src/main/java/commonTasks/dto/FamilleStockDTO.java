/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.TEmplacement;
import dal.TFamille;
import dal.TFamilleStock;
import dal.TPreenregistrementDetail;
import java.util.Date;

/**
 *
 * @author koben
 */
public class FamilleStockDTO {

    private String lgFAMILLESTOCKID;
    private Integer intUG;
    private Integer intNUMBER;
    private Integer intNUMBERAVAILABLE;
    private Date dtCREATED;
    private Date dtUPDATED;
    private String strSTATUT;
    private String lgFAMILLEID;
    private String lgEMPLACEMENTID;

    public String getLgFAMILLESTOCKID() {
        return lgFAMILLESTOCKID;
    }

    public void setLgFAMILLESTOCKID(String lgFAMILLESTOCKID) {
        this.lgFAMILLESTOCKID = lgFAMILLESTOCKID;
    }

    public Integer getIntUG() {
        return intUG;
    }

    public void setIntUG(Integer intUG) {
        this.intUG = intUG;
    }

    public Integer getIntNUMBER() {
        return intNUMBER;
    }

    public void setIntNUMBER(Integer intNUMBER) {
        this.intNUMBER = intNUMBER;
    }

    public Integer getIntNUMBERAVAILABLE() {
        return intNUMBERAVAILABLE;
    }

    public void setIntNUMBERAVAILABLE(Integer intNUMBERAVAILABLE) {
        this.intNUMBERAVAILABLE = intNUMBERAVAILABLE;
    }

    public Date getDtCREATED() {
        return dtCREATED;
    }

    public void setDtCREATED(Date dtCREATED) {
        this.dtCREATED = dtCREATED;
    }

    public Date getDtUPDATED() {
        return dtUPDATED;
    }

    public void setDtUPDATED(Date dtUPDATED) {
        this.dtUPDATED = dtUPDATED;
    }

    public String getStrSTATUT() {
        return strSTATUT;
    }

    public void setStrSTATUT(String strSTATUT) {
        this.strSTATUT = strSTATUT;
    }

    public String getLgFAMILLEID() {
        return lgFAMILLEID;
    }

    public void setLgFAMILLEID(String lgFAMILLEID) {
        this.lgFAMILLEID = lgFAMILLEID;
    }

    public String getLgEMPLACEMENTID() {
        return lgEMPLACEMENTID;
    }

    public void setLgEMPLACEMENTID(String lgEMPLACEMENTID) {
        this.lgEMPLACEMENTID = lgEMPLACEMENTID;
    }

    public FamilleStockDTO() {
    }

    private TFamille familleFromId(String id) {
        return new TFamille(id);
    }

    private TEmplacement fromId(String id) {
        return new TEmplacement(id);
    }

    public FamilleStockDTO(TFamilleStock o) {
        this.lgFAMILLESTOCKID = o.getLgFAMILLESTOCKID();
        this.intNUMBER = o.getIntNUMBER();
        this.intNUMBERAVAILABLE = o.getIntNUMBERAVAILABLE();
        this.dtCREATED = o.getDtCREATED();
        this.dtUPDATED = o.getDtUPDATED();
        this.strSTATUT = o.getStrSTATUT();
        TFamille f = o.getLgFAMILLEID();
        this.lgFAMILLEID = f.getLgFAMILLEID();
        this.intUG = o.getIntUG();
        TEmplacement e = o.getLgEMPLACEMENTID();
        this.lgEMPLACEMENTID = e.getLgEMPLACEMENTID();
    }

    public FamilleStockDTO(TPreenregistrementDetail d, TFamilleStock o) {
        this.lgFAMILLESTOCKID = o.getLgFAMILLESTOCKID();
        this.intNUMBER = d.getIntQUANTITYSERVED();
        this.intNUMBERAVAILABLE = d.getIntQUANTITYSERVED();
        this.dtCREATED = o.getDtCREATED();
        this.dtUPDATED = o.getDtUPDATED();
        this.strSTATUT = o.getStrSTATUT();
        TFamille f = d.getLgFAMILLEID();
        this.lgFAMILLEID = f.getLgFAMILLEID();
        this.intUG = d.getIntUG();
        TEmplacement e = o.getLgEMPLACEMENTID();
        this.lgEMPLACEMENTID = e.getLgEMPLACEMENTID();
    }

    public static TFamilleStock build(FamilleStockDTO o) {
        TFamilleStock t = new TFamilleStock();
        t.setLgFAMILLESTOCKID(o.getLgFAMILLESTOCKID());
        t.setIntNUMBER(o.getIntNUMBER());
        t.setIntNUMBERAVAILABLE(o.getIntNUMBERAVAILABLE());
        t.setDtCREATED(new Date());
        t.setDtUPDATED(new Date());
        t.setStrSTATUT(o.getStrSTATUT());
        t.setIntUG(o.getIntUG());
        return t;
    }

    public static TFamilleStock build(FamilleStockDTO o, TFamilleStock t) {
        t.setIntNUMBERAVAILABLE(t.getIntNUMBERAVAILABLE() + o.getIntNUMBERAVAILABLE());
        t.setIntNUMBER(t.getIntNUMBERAVAILABLE());
        t.setDtUPDATED(new Date());
        t.setStrSTATUT(o.getStrSTATUT());
        t.setIntUG(t.getIntUG() + o.getIntUG());
        return t;
    }
}
