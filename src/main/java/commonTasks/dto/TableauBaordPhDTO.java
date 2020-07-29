/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import org.json.JSONPropertyName;
import util.DateConverter;

/**
 *
 * @author DICI
 */
public class TableauBaordPhDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean vente;
    private String dateOperation;
//    @JsonProperty("Total vente")
    private Integer montantTTC = 0;
    // @JsonProperty("Ca net")
    private Integer montantNet = 0;
    //  @JsonProperty("Remise")
    private Integer montantRemise = 0;
    //  @JsonProperty("Comptant")
    private Integer montantEsp = 0;
    //  @JsonProperty("Credit")
    private Integer montantCredit = 0;
//    @JsonProperty("Nbre Clients")
    private Integer nbreVente = 0;
//    @JsonProperty("LABOREX")
    private Integer montantAchatOne = 0;
//    @JsonProperty("DPCI")
    private Integer montantAchatTwo = 0;
//    @JsonProperty("COPHARMED")
    private Integer montantAchatThree = 0;
//    @JsonProperty("TEDIS PHARMA")
    private Integer montantAchatFour = 0;
//    @JsonProperty("AUTRES")
    private Integer montantAchatFive = 0;
//    @JsonProperty("Achat net")
    private Integer montantAchat = 0;
    private Integer montantAchatNet = 0;
//    @JsonProperty("AVOIR")
    private Integer montantAvoir = 0;
//    @JsonProperty("RATIOVA")
    private double ratioVA = 0.0;
//    @JsonProperty("RATIOACHV")
    private double rationAV = 0.0;
    private LocalDate mvtDate;

    public String getDateOperation() {
        try {
            this.dateOperation = mvtDate.format(dateFormat);
        } catch (Exception e) {
        }
        return dateOperation;
    }

    @JSONPropertyName("AVOIR")
    public Integer getMontantAvoir() {
        return montantAvoir;
    }

    public void setMontantAvoir(Integer montantAvoir) {
        this.montantAvoir = montantAvoir;
    }

    public LocalDate getMvtDate() {
        return mvtDate;
    }

    public boolean isVente() {
        return vente;
    }

    public void setVente(boolean vente) {
        this.vente = vente;
    }

    public void setMvtDate(LocalDate mvtDate) {
        /*    try {
              this.dateOperation=mvtDate.format(dateFormat);
        } catch (Exception e) {
        }
         */
        this.mvtDate = mvtDate;
    }

    @JSONPropertyName("RATIOVA")
    public double getRatioVA() {
        return ratioVA;
    }

    public void setRatioVA(double ratioVA) {
        this.ratioVA = ratioVA;
    }

    @JSONPropertyName("RATIOACHV")
    public double getRationAV() {
        return rationAV;
    }

    public void setRationAV(double rationAV) {
        this.rationAV = rationAV;
    }

    @JSONPropertyName("Achat net")
    public Integer getMontantAchat() {
        return montantAchat;
    }

    public void setMontantAchat(Integer montantAchat) {
        this.montantAchat = montantAchat;
    }

    public Integer getMontantAchatNet() {
        return montantAchatNet;
    }

    public void setMontantAchatNet(Integer montantAchatNet) {
        this.montantAchatNet = montantAchatNet;
    }

    public Integer getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(Integer montantTTC) {
        this.montantTTC = montantTTC;
    }

    @JSONPropertyName("Ca net")
    public Integer getMontantNet() {
        return montantNet;
    }

    public void setMontantNet(Integer montantNet) {
        this.montantNet = montantNet;
    }

    @JSONPropertyName("Remise")
    public Integer getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(Integer montantRemise) {
        this.montantRemise = montantRemise;
    }

    @JSONPropertyName("Comptant")
    public Integer getMontantEsp() {
        return montantEsp;
    }

    public void setMontantEsp(Integer montantEsp) {
        this.montantEsp = montantEsp;
    }

    @JSONPropertyName("Nbre Clients")
    public Integer getNbreVente() {
        return nbreVente;
    }

    public void setNbreVente(Integer nbreVente) {
        this.nbreVente = nbreVente;
    }

    @JSONPropertyName("LABOREX")
    public Integer getMontantAchatOne() {
        return montantAchatOne;
    }

    public void setMontantAchatOne(Integer montantAchatOne) {
        this.montantAchatOne = montantAchatOne;
    }

    @JSONPropertyName("DPCI")
    public Integer getMontantAchatTwo() {
        return montantAchatTwo;
    }

    public void setMontantAchatTwo(Integer montantAchatTwo) {
        this.montantAchatTwo = montantAchatTwo;
    }

    @JSONPropertyName("COPHARMED")
    public Integer getMontantAchatThree() {
        return montantAchatThree;
    }

    public void setMontantAchatThree(Integer montantAchatThree) {
        this.montantAchatThree = montantAchatThree;
    }

    @JSONPropertyName("TEDIS PHARMA")
    public Integer getMontantAchatFour() {
        return montantAchatFour;
    }

    public void setMontantAchatFour(Integer montantAchatFour) {
        this.montantAchatFour = montantAchatFour;
    }

    @JSONPropertyName("AUTRES")
    public Integer getMontantAchatFive() {
        return montantAchatFive;
    }

    public void setMontantAchatFive(Integer montantAchatFive) {
        this.montantAchatFive = montantAchatFive;
    }

    @Override
    public String toString() {
        return "TableauBaordPhDTO{" + "dateOperation=" + dateOperation + ", mvtDate=" + mvtDate + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.mvtDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableauBaordPhDTO other = (TableauBaordPhDTO) obj;
        return Objects.equals(this.mvtDate, other.mvtDate);
    }

    @JSONPropertyName("Credit")
    public Integer getMontantCredit() {
        return montantCredit;
    }

    public void setMontantCredit(Integer montantCredit) {
        this.montantCredit = montantCredit;
    }

    public TableauBaordPhDTO() {

    }

    public TableauBaordPhDTO(long montantTTC, long montantRemise, Date dateOp, long nbreVente, long  customPart,String typeVente) {
        this.montantRemise = (int) montantRemise;
        this.montantTTC = (int) montantTTC;
        this.nbreVente = (int) nbreVente;
        this.mvtDate = DateConverter.convertDateToLocalDate(dateOp);
        this.montantNet = (int) (montantTTC - montantRemise);
        if (typeVente.contains(DateConverter.VENTE_ASSURANCE)) {
         this.montantCredit=(int)(montantTTC-customPart);
        }
        this.vente = true;
    }

    public TableauBaordPhDTO(long montantEsp, Date dateOp) {
        this.montantEsp = (int) montantEsp;
        this.mvtDate = DateConverter.convertDateToLocalDate(dateOp);
    }

    public TableauBaordPhDTO(Date dateOp, long montantEsp) {
        this.montantEsp -= (int) montantEsp;
        this.mvtDate = DateConverter.convertDateToLocalDate(dateOp);
        this.vente = true;
    }

    public TableauBaordPhDTO(Date dateOp, long montantTTC, long montantRemise, long montantEsp, long montantCredit, long montantDiff) {
//        System.out.println("montantTTC -- "+montantTTC+" montantRemise --"+montantRemise+" "+montantEsp+" montantCredit "+montantCredit);
        this.montantRemise -= (int) montantRemise;
        this.montantTTC -= (int) montantTTC;
        this.montantCredit -= (int) (montantCredit + montantDiff);
        this.montantEsp = -(int) montantEsp;
        this.mvtDate = DateConverter.convertDateToLocalDate(dateOp);
        this.montantNet -= (int) (montantTTC - montantRemise);
        this.vente = false;
    }

}
