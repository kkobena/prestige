/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "PVenteVOItems", uniqueConstraints = {
  
@UniqueConstraint(name = "UNQ_PVenteVOItems_vncmptp",columnNames = {"venteID", "lg_COMPTE_CLIENT_TIERS_PAYANT_ID"})

},
        indexes = {
            @Index(name = "PVenteVOItems3", columnList = "str_REF_BON")
            ,
            @Index(name = "PVenteVOItems7", columnList = "dt_UPDATED")

        }
)
@XmlRootElement
public class PVenteVOItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "uuid", nullable = false)
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "dt_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCREATED=new Date();
    @Column(name = "dt_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtUPDATED=new Date();
    @Column(name = "int_PERCENT")
    private int intPERCENT = 0;
    @Column(name = "int_PRICE")
    private Integer intPRICE;
    @Column(name = "str_REF_BON", length = 50)
    private String strREFBON;
    @JoinColumn(name = "lg_USER_ID", referencedColumnName = "lg_USER_ID")
    @ManyToOne
    private TUser lgUSERID;
    @JoinColumn(name = "lg_COMPTE_CLIENT_TIERS_PAYANT_ID", referencedColumnName = "lg_COMPTE_CLIENT_TIERS_PAYANT_ID")
    @ManyToOne
    private TCompteClientTiersPayant lgCOMPTECLIENTTIERSPAYANTID;
    @JoinColumn(name = "venteID", referencedColumnName = "uuid")
    @ManyToOne
    private PVente vente;
    @Version
    private int version;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public int getIntPERCENT() {
        return intPERCENT;
    }

    public void setIntPERCENT(int intPERCENT) {
        this.intPERCENT = intPERCENT;
    }

    public Integer getIntPRICE() {
        return intPRICE;
    }

    public void setIntPRICE(Integer intPRICE) {
        this.intPRICE = intPRICE;
    }

    public String getStrREFBON() {
        return strREFBON;
    }

    public void setStrREFBON(String strREFBON) {
        this.strREFBON = strREFBON;
    }

    public TUser getLgUSERID() {
        return lgUSERID;
    }

    public void setLgUSERID(TUser lgUSERID) {
        this.lgUSERID = lgUSERID;
    }

    public TCompteClientTiersPayant getLgCOMPTECLIENTTIERSPAYANTID() {
        return lgCOMPTECLIENTTIERSPAYANTID;
    }

    public void setLgCOMPTECLIENTTIERSPAYANTID(TCompteClientTiersPayant lgCOMPTECLIENTTIERSPAYANTID) {
        this.lgCOMPTECLIENTTIERSPAYANTID = lgCOMPTECLIENTTIERSPAYANTID;
    }

    public PVente getVente() {
        return vente;
    }

    public void setVente(PVente vente) {
        this.vente = vente;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.uuid);
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
        final PVenteVOItems other = (PVenteVOItems) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PVenteVOItems{" + "uuid=" + uuid + ", strREFBON=" + strREFBON + ", version=" + version + '}';
    }

}
