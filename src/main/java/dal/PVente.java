/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dal.enumeration.NatureVente;
import dal.enumeration.Statut;
import dal.enumeration.TypeVente;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Kobena
 */
@Entity
@Table(name = "PVente",
        indexes = {
            @Index(name = "venteIdex1", columnList = "str_REF")
            ,
            @Index(name = "venteIdex2", columnList = "str_REF_TICKET")
            ,
            @Index(name = "venteIdex3", columnList = "str_STATUT")
            ,
            @Index(name = "venteIdex4", columnList = "dt_CREATED")
            ,
            @Index(name = "venteIdex5", columnList = "dt_UPDATED")
            ,
            @Index(name = "venteIdex6", columnList = "str_REF_BON")
            ,
            @Index(name = "venteIdex7", columnList = "dt_OPERATION")
            ,
            @Index(name = "venteIdex8", columnList = "dt_ANNULER")
            ,
            @Index(name = "venteIdex9", columnList = "natureVente")
            ,
            @Index(name = "venteIdex10", columnList = "typeVente")
            ,
            @Index(name = "venteIdex11", columnList = "differe")

        }
)
@XmlRootElement
public class PVente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "uuid", nullable = false)
    private String uuid = UUID.randomUUID().toString();
    @Column(name = "str_REF", length = 30)
    private String strREF;
    @Column(name = "str_REF_TICKET", length = 10)
    private String strREFTICKET;
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
    @Column(name = "str_REF_BON", length = 80)
    private String strREFBON;
    @Column(name = "dt_OPERATION")
    @Temporal(TemporalType.DATE)
    private Date dtOPERATION = new Date();
    @Column(name = "dt_ANNULER")
    @Temporal(TemporalType.DATE)
    private Date dtANNULER;
    @Basic(optional = false)
    @Column(name = "b_IS_AVOIR", nullable = false)
    private boolean bISAVOIR = false;
    @Basic(optional = false)
    @Column(name = "b_WITHOUT_BON", nullable = false)
    private boolean bWITHOUTBON = false;
    @Column(name = "int_PRICE_OTHER")
    private Integer intPRICEOTHER = 0;
    @JoinColumn(name = "lg_USER_VENDEUR_ID", referencedColumnName = "lg_USER_ID", nullable = false)
    @ManyToOne
    private TUser lgUSERVENDEURID;
    @Column(name = "typeVente")
    @Enumerated(EnumType.STRING)
    private TypeVente typeVente = TypeVente.VNO;
    @Column(name = "natureVente")
    private NatureVente natureVente = NatureVente.VNO;
    @JoinColumn(name = "lg_USER_ID", referencedColumnName = "lg_USER_ID", nullable = false)
    @ManyToOne
    private TUser lgUSERID;
    @OneToMany(mappedBy = "vente")
    private Collection<PVenteVOItems> pVenteVOItemses = new ArrayList<>();
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "vente")
    private Collection<PVenteItem> pVenteItems = new ArrayList<>();
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
    @JoinColumn(name = "clientID", referencedColumnName = "lg_CLIENT_ID", nullable = true)
    @ManyToOne
    private TClient client;
    @JoinColumn(name = "ayantDroitID", referencedColumnName = "lg_AYANTS_DROITS_ID", nullable = true)
    @ManyToOne
    private TAyantDroit ayantDroit;
    @Version
    private int version;
    
    @JoinColumn(name = "lg_REGLEMENT_ID", referencedColumnName = "lg_REGLEMENT_ID")
    @ManyToOne
    private TReglement lgREGLEMENTID;
    public TReglement getLgREGLEMENTID() {
        return lgREGLEMENTID;
    }

    public void setLgREGLEMENTID(TReglement lgREGLEMENTID) {
        this.lgREGLEMENTID = lgREGLEMENTID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStrREF() {
        return strREF;
    }

    public void setStrREF(String strREF) {
        this.strREF = strREF;
    }

    public TClient getClient() {
        return client;
    }

    public void setClient(TClient client) {
        this.client = client;
    }

    public TAyantDroit getAyantDroit() {
        return ayantDroit;
    }

    public void setAyantDroit(TAyantDroit ayantDroit) {
        this.ayantDroit = ayantDroit;
    }

    public String getStrREFTICKET() {
        return strREFTICKET;
    }

    public void setStrREFTICKET(String strREFTICKET) {
        this.strREFTICKET = strREFTICKET;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public String getStrREFBON() {
        return strREFBON;
    }

    public void setStrREFBON(String strREFBON) {
        this.strREFBON = strREFBON;
    }

    public Date getDtOPERATION() {
        return dtOPERATION;
    }

    public void setDtOPERATION(Date dtOPERATION) {
        this.dtOPERATION = dtOPERATION;
    }

    public Date getDtANNULER() {
        return dtANNULER;
    }

    public void setDtANNULER(Date dtANNULER) {
        this.dtANNULER = dtANNULER;
    }

    public boolean isbISAVOIR() {
        return bISAVOIR;
    }

    public void setbISAVOIR(boolean bISAVOIR) {
        this.bISAVOIR = bISAVOIR;
    }

    public boolean isbWITHOUTBON() {
        return bWITHOUTBON;
    }

    public void setbWITHOUTBON(boolean bWITHOUTBON) {
        this.bWITHOUTBON = bWITHOUTBON;
    }

    public Integer getIntPRICEOTHER() {
        return intPRICEOTHER;
    }

    public void setIntPRICEOTHER(Integer intPRICEOTHER) {
        this.intPRICEOTHER = intPRICEOTHER;
    }

    public TUser getLgUSERVENDEURID() {
        return lgUSERVENDEURID;
    }

    public void setLgUSERVENDEURID(TUser lgUSERVENDEURID) {
        this.lgUSERVENDEURID = lgUSERVENDEURID;
    }

    public TypeVente getTypeVente() {
        return typeVente;
    }

    public void setTypeVente(TypeVente typeVente) {
        this.typeVente = typeVente;
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

    public Collection<PVenteVOItems> getpVenteVOItemses() {
        return pVenteVOItemses;
    }

    public void setpVenteVOItemses(Collection<PVenteVOItems> pVenteVOItemses) {
        this.pVenteVOItemses = pVenteVOItemses;
    }

    @XmlTransient
    public Collection<PVenteItem> getpVenteItems() {
        return pVenteItems;
    }

    public void setpVenteItems(Collection<PVenteItem> pVenteItems) {
        this.pVenteItems = pVenteItems;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.uuid);
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
        final PVente other = (PVente) obj;
        return Objects.equals(this.uuid, other.uuid);
    }

    @Override
    public String toString() {
        return "PVente{" + "uuid=" + uuid + ", intPRICE=" + intPRICE + '}';
    }

    public PVente() {
    }

}
