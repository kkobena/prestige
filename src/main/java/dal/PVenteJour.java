/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dal.enumeration.NatureVente;
import dal.enumeration.Statut;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kobena
 */
@Entity
@Table(name = "PVenteJour", uniqueConstraints = {
  
@UniqueConstraint(name = "UNQ_PVenteJour_ctrsdnl", columnNames = {"str_STATUT", "dt_OPERATION", "natureVente", "lg_USER_ID"})

},
        indexes = {
            @Index(name = "PVenteJour3", columnList = "str_STATUT")
            ,
            @Index(name = "PVenteJour7", columnList = "dt_OPERATION")
            ,
            @Index(name = "PVenteJour9", columnList = "natureVente")
            ,
            @Index(name = "PVenteJour11", columnList = "differe")

        }
)
@XmlRootElement
public class PVenteJour implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "uuid", nullable = false)
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "int_PRICE")
    private Integer intPRICE = 0;
    @Column(name = "int_PRICE_REMISE")
    private Integer intPRICEREMISE = 0;
    @Column(name = "int_CUST_PART")
    private Integer intCUSTPART = 0;
    @Column(name = "str_STATUT")
    @Enumerated(EnumType.ORDINAL)
    private Statut strSTATUT = Statut.ENABLE;
    @Column(name = "dt_CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCREATED = new Date();
    @Column(name = "dt_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtUPDATED = new Date();

    @Column(name = "dt_OPERATION", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dtOPERATION =  new Date();
//    @Column(name = "typeVente")
//    @Enumerated(EnumType.STRING)
//    private TypeVente typeVente = TypeVente.VNO;
     @Enumerated(EnumType.STRING)
    @Column(name = "natureVente", nullable = false)
    private NatureVente natureVente = NatureVente.VO;
    
    @JoinColumn(name = "lg_USER_ID", referencedColumnName = "lg_USER_ID", nullable = false)
    @ManyToOne
    private TUser lgUSERID;

    @Column(name = "int_ACCOUNT")
    private Integer intACCOUNT = 0;
    @Column(name = "int_REMISE_PARA")
    private Integer intREMISEPARA = 0;
    @Column(name = "PK_BRAND")
    private String pkBrand;
    @Column(name = "differe")
    private Boolean differe = false;
    @Column(name = "tauxremise", precision = 3, scale = 1)
    private Double tauxremise = 0.0;
    @Column(name = "montantNet")
    private Integer montantNet = 0;
    @Column(name = "montantPaye")
    private Integer montantPaye = 0;
    @Column(name = "montantRestant")
    private Integer montantRestant = 0;
    @Column(name = "montantTp")
    private Integer montantTp = 0;
    @Version
    private int version;
    @JoinColumn(name = "lg_EMPLACEMENT_ID", referencedColumnName = "lg_EMPLACEMENT_ID", nullable = false)
    @ManyToOne
    private TEmplacement emplacement;

    public String getUuid() {
        return uuid;
    }

    public TEmplacement getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(TEmplacement emplacement) {
        this.emplacement = emplacement;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getIntPRICE() {
        return intPRICE;
    }

    public void setIntPRICE(Integer intPRICE) {
        this.intPRICE = intPRICE;
    }

    public Integer getIntPRICEREMISE() {
        return intPRICEREMISE;
    }

    public void setIntPRICEREMISE(Integer intPRICEREMISE) {
        this.intPRICEREMISE = intPRICEREMISE;
    }

    public Integer getIntCUSTPART() {
        return intCUSTPART;
    }

    public void setIntCUSTPART(Integer intCUSTPART) {
        this.intCUSTPART = intCUSTPART;
    }

    public Statut getStrSTATUT() {
        return strSTATUT;
    }

    public void setStrSTATUT(Statut strSTATUT) {
        this.strSTATUT = strSTATUT;
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

    public Date getDtOPERATION() {
        return dtOPERATION;
    }

   

    public NatureVente getNatureVente() {
        return natureVente;
    }

    public void setNatureVente(NatureVente natureVente) {
        this.natureVente = natureVente;
    }

    public TUser getLgUSERID() {
        return lgUSERID;
    }

    public void setLgUSERID(TUser lgUSERID) {
        this.lgUSERID = lgUSERID;
    }

    public Integer getIntACCOUNT() {
        return intACCOUNT;
    }

    public void setIntACCOUNT(Integer intACCOUNT) {
        this.intACCOUNT = intACCOUNT;
    }

    public Integer getIntREMISEPARA() {
        return intREMISEPARA;
    }

    public void setIntREMISEPARA(Integer intREMISEPARA) {
        this.intREMISEPARA = intREMISEPARA;
    }

    public String getPkBrand() {
        return pkBrand;
    }

    public void setPkBrand(String pkBrand) {
        this.pkBrand = pkBrand;
    }

    public Boolean getDiffere() {
        return differe;
    }

    public void setDiffere(Boolean differe) {
        this.differe = differe;
    }

    public Double getTauxremise() {
        return tauxremise;
    }

    public void setTauxremise(Double tauxremise) {
        this.tauxremise = tauxremise;
    }

    public Integer getMontantNet() {
        return montantNet;
    }

    public void setMontantNet(Integer montantNet) {
        this.montantNet = montantNet;
    }

    public Integer getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(Integer montantPaye) {
        this.montantPaye = montantPaye;
    }

    public Integer getMontantRestant() {
        return montantRestant;
    }

    public void setMontantRestant(Integer montantRestant) {
        this.montantRestant = montantRestant;
    }

    public Integer getMontantTp() {
        return montantTp;
    }

    public void setMontantTp(Integer montantTp) {
        this.montantTp = montantTp;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.uuid);
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
        final PVenteJour other = (PVenteJour) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PVenteJour{" + "uuid=" + uuid + ", strSTATUT=" + strSTATUT + ", dtOPERATION=" + dtOPERATION + ", natureVente=" + natureVente + '}';
    }

    public void setDtOPERATION(Date dtOPERATION) {
        this.dtOPERATION = dtOPERATION;
    }

}
