/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "PVenteItem", uniqueConstraints = {
    @UniqueConstraint(name = "UNQ_PVenteItem_prventeId", columnNames = {"lg_FAMILLE_ID", "venteId"})},
        indexes = {
            @Index(name = "venteItemIdex", columnList = "str_STATUT")
            ,
            @Index(name = "venteItemIdex4", columnList = "dt_CREATED")
            ,
            @Index(name = "venteItemIdex5", columnList = "dt_UPDATED")
        }
)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PVenteItem.findByVente", query = "SELECT o FROM PVenteItem o WHERE o.vente=:vente  ORDER BY o.dtUPDATED DESC ")
    ,
@NamedQuery(name = "PVenteItem.findByVenteAndStatut", query = "SELECT o FROM PVenteItem o WHERE o.vente=:vente AND o.strSTATUT=:statut  ORDER BY o.dtUPDATED DESC ")
    ,
@NamedQuery(name = "PVenteItem.findByVenteAndArticle", query = "SELECT o FROM PVenteItem o WHERE o.vente=:vente AND o.lgFAMILLEID.lgFAMILLEID =:idProduit  ORDER BY o.dtUPDATED DESC ")

})
public class PVenteItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "uuid", nullable = false)
    private String uuid = UUID.randomUUID().toString();
    @Column(name = "int_QUANTITY")
    private Integer intQUANTITY = 0;
    @Column(name = "int_QUANTITY_SERVED")
    private Integer intQUANTITYSERVED = 0;
    @Column(name = "int_AVOIR")
    private Integer intAVOIR = 0;
    @Column(name = "int_PRICE")
    private Integer intPRICE;
    @Column(name = "int_PRICE_UNITAIR")
    private Integer intPRICEUNITAIR;
    @Column(name = "int_NUMBER")
    private Integer intNUMBER;
    @Column(name = "str_STATUT")
    @Enumerated(EnumType.ORDINAL)
    private Statut strSTATUT = Statut.ENABLE;
    @Column(name = "dt_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCREATED;
    @Column(name = "dt_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtUPDATED;
    @Column(name = "int_PRICE_REMISE")
    private Integer intPRICEREMISE;
    @Basic(optional = false)
    @Column(name = "b_IS_AVOIR", nullable = false)
    private boolean bISAVOIR = false;
    @JoinColumn(name = "lg_FAMILLE_ID", referencedColumnName = "lg_FAMILLE_ID")
    @ManyToOne
    private TFamille lgFAMILLEID;
    @JoinColumn(name = "venteId", referencedColumnName = "uuid", nullable = false)
    @ManyToOne(optional = false)
    private PVente vente;
    @Column(name = "bool_ACCOUNT")
    private Boolean boolACCOUNT = true;
    @Column(name = "int_UG")
    private Integer intUG;
    @Version
    private int version;

    public PVenteItem() {
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getIntQUANTITY() {
        return intQUANTITY;
    }

    public void setIntQUANTITY(Integer intQUANTITY) {
        this.intQUANTITY = intQUANTITY;
    }

    public Integer getIntQUANTITYSERVED() {
        return intQUANTITYSERVED;
    }

    public void setIntQUANTITYSERVED(Integer intQUANTITYSERVED) {
        this.intQUANTITYSERVED = intQUANTITYSERVED;
    }

    public Integer getIntAVOIR() {
        return intAVOIR;
    }

    public void setIntAVOIR(Integer intAVOIR) {
        this.intAVOIR = intAVOIR;
    }

    public Integer getIntPRICE() {
        return intPRICE;
    }

    public void setIntPRICE(Integer intPRICE) {
        this.intPRICE = intPRICE;
    }

    public Integer getIntPRICEUNITAIR() {
        return intPRICEUNITAIR;
    }

    public void setIntPRICEUNITAIR(Integer intPRICEUNITAIR) {
        this.intPRICEUNITAIR = intPRICEUNITAIR;
    }

    public Integer getIntNUMBER() {
        return intNUMBER;
    }

    public void setIntNUMBER(Integer intNUMBER) {
        this.intNUMBER = intNUMBER;
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

    public Integer getIntPRICEREMISE() {
        return intPRICEREMISE;
    }

    public void setIntPRICEREMISE(Integer intPRICEREMISE) {
        this.intPRICEREMISE = intPRICEREMISE;
    }

    public boolean isbISAVOIR() {
        return bISAVOIR;
    }

    public void setbISAVOIR(boolean bISAVOIR) {
        this.bISAVOIR = bISAVOIR;
    }

    public TFamille getLgFAMILLEID() {
        return lgFAMILLEID;
    }

    public void setLgFAMILLEID(TFamille lgFAMILLEID) {
        this.lgFAMILLEID = lgFAMILLEID;
    }

    public PVente getVente() {
        return vente;
    }

    public void setVente(PVente vente) {
        this.vente = vente;
    }

    public Boolean getBoolACCOUNT() {
        return boolACCOUNT;
    }

    public void setBoolACCOUNT(Boolean boolACCOUNT) {
        this.boolACCOUNT = boolACCOUNT;
    }

    public Integer getIntUG() {
        return intUG;
    }

    public void setIntUG(Integer intUG) {
        this.intUG = intUG;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.uuid);
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
        final PVenteItem other = (PVenteItem) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PVenteItem{" + "uuid=" + uuid + ", intQUANTITY=" + intQUANTITY + ", intQUANTITYSERVED=" + intQUANTITYSERVED + ", intAVOIR=" + intAVOIR + ", intPRICE=" + intPRICE + ", intPRICEUNITAIR=" + intPRICEUNITAIR + ", intNUMBER=" + intNUMBER + ", dtCREATED=" + dtCREATED + ", dtUPDATED=" + dtUPDATED + '}';
    }

}
