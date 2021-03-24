/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author MKABOU
 */
@Entity
@Table(name = "t_user", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"str_LOGIN"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TUser.findAll", query = "SELECT t FROM TUser t"),
    @NamedQuery(name = "TUser.findByLgUSERID", query = "SELECT t FROM TUser t WHERE t.lgUSERID = :lgUSERID"),
    @NamedQuery(name = "TUser.findByStrIDS", query = "SELECT t FROM TUser t WHERE t.strIDS = :strIDS"),
    @NamedQuery(name = "TUser.findByStrLOGIN", query = "SELECT t FROM TUser t WHERE t.strLOGIN = :strLOGIN"),
    @NamedQuery(name = "TUser.findByStrTYPE", query = "SELECT t FROM TUser t WHERE t.strTYPE = :strTYPE"),
    @NamedQuery(name = "TUser.findByStrPASSWORD", query = "SELECT t FROM TUser t WHERE t.strPASSWORD = :strPASSWORD"),
    @NamedQuery(name = "TUser.findByStrCODE", query = "SELECT t FROM TUser t WHERE t.strCODE = :strCODE"),
    @NamedQuery(name = "TUser.findByDtCREATED", query = "SELECT t FROM TUser t WHERE t.dtCREATED = :dtCREATED"),
    @NamedQuery(name = "TUser.findByDtUPDATED", query = "SELECT t FROM TUser t WHERE t.dtUPDATED = :dtUPDATED"),
    @NamedQuery(name = "TUser.findByStrCREATEDBY", query = "SELECT t FROM TUser t WHERE t.strCREATEDBY = :strCREATEDBY"),
    @NamedQuery(name = "TUser.findByStrUPDATEDBY", query = "SELECT t FROM TUser t WHERE t.strUPDATEDBY = :strUPDATEDBY"),
    @NamedQuery(name = "TUser.findByStrLASTCONNECTIONDATE", query = "SELECT t FROM TUser t WHERE t.strLASTCONNECTIONDATE = :strLASTCONNECTIONDATE"),
    @NamedQuery(name = "TUser.findByLgSKINID", query = "SELECT t FROM TUser t WHERE t.lgSKINID = :lgSKINID"),
    @NamedQuery(name = "TUser.findByStrSTATUT", query = "SELECT t FROM TUser t WHERE t.strSTATUT = :strSTATUT"),
    @NamedQuery(name = "TUser.findByDtLASTACTIVITY", query = "SELECT t FROM TUser t WHERE t.dtLASTACTIVITY = :dtLASTACTIVITY"),
    @NamedQuery(name = "TUser.findByStrFUNCTION", query = "SELECT t FROM TUser t WHERE t.strFUNCTION = :strFUNCTION"),
    @NamedQuery(name = "TUser.findByStrPHONE", query = "SELECT t FROM TUser t WHERE t.strPHONE = :strPHONE"),
    @NamedQuery(name = "TUser.findByStrMAIL", query = "SELECT t FROM TUser t WHERE t.strMAIL = :strMAIL"),
    @NamedQuery(name = "TUser.findByIntCONNEXION", query = "SELECT t FROM TUser t WHERE t.intCONNEXION = :intCONNEXION"),
    @NamedQuery(name = "TUser.findByBCHANGEPASSWORD", query = "SELECT t FROM TUser t WHERE t.bCHANGEPASSWORD = :bCHANGEPASSWORD"),
    @NamedQuery(name = "TUser.findByBIsConnected", query = "SELECT t FROM TUser t WHERE t.bIsConnected = :bIsConnected"),
    @NamedQuery(name = "TUser.findByStrPIC", query = "SELECT t FROM TUser t WHERE t.strPIC = :strPIC")})
public class TUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "lg_USER_ID", nullable = false, length = 40)
    private String lgUSERID;
    @Column(name = "str_IDS")
    private Integer strIDS;
    @Basic(optional = false)
    @Column(name = "str_LOGIN", nullable = false, length = 40)
    private String strLOGIN;
    @Basic(optional = false)
    @Column(name = "str_TYPE", nullable = false, length = 50)
    private String strTYPE;
    @Column(name = "str_PASSWORD", length = 40)
    private String strPASSWORD;
    @Column(name = "str_CODE", length = 20)
    private String strCODE;
    @Column(name = "dt_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCREATED;
    @Column(name = "dt_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtUPDATED;
    @Column(name = "str_CREATED_BY", length = 20)
    private String strCREATEDBY;
    @Column(name = "str_UPDATED_BY", length = 20)
    private String strUPDATEDBY;
   
    @Column(name = "str_FIRST_NAME", length = 255)
    private String strFIRSTNAME;
  
    @Column(name = "str_LAST_NAME", length = 255)
    private String strLASTNAME;
    @Column(name = "str_LAST_CONNECTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date strLASTCONNECTIONDATE;
    @Column(name = "lg_SKIN_ID", length = 20)
    private String lgSKINID;
    @Column(name = "str_STATUT", length = 40)
    private String strSTATUT;
    @Column(name = "dt_LAST_ACTIVITY")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLASTACTIVITY;
    @Column(name = "str_FUNCTION", length = 20)
    private String strFUNCTION;
    @Column(name = "str_PHONE", length = 20)
    private String strPHONE;
    @Column(name = "str_MAIL", length = 20)
    private String strMAIL;
    @Column(name = "int_CONNEXION")
    private Integer intCONNEXION;
    @Column(name = "b_CHANGE_PASSWORD")
    private Boolean bCHANGEPASSWORD;
    @Column(name = "b_is_connected")
    private Boolean bIsConnected;
    @Column(name = "str_PIC", length = 50)
    private String strPIC;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TBonLivraison> tBonLivraisonCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TOrder> tOrderCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TSnapShopDalyStat> tSnapShopDalyStatCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TResumeCaisse> tResumeCaisseCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TCaisse> tCaisseCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TRetourdepot> tRetourdepotCollection;
    @OneToMany(mappedBy = "lgUSERID",fetch = FetchType.EAGER)
    private Collection<TRoleUser> tRoleUserCollection;
    @OneToMany( mappedBy = "lgUSERID")
    private Collection<TInventaire> tInventaireCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TRetourFournisseur> tRetourFournisseurCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TAjustement> tAjustementCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TBilletage> tBilletageCollection;
    @OneToMany( mappedBy = "lgUSERID")
    private Collection<TWarehouse> tWarehouseCollection;
    @OneToMany(mappedBy = "lgUSERIDOUT")
    private Collection<TNotification> tNotificationCollection;
    @OneToMany(mappedBy = "lgUSERIDIN")
    private Collection<TNotification> tNotificationCollection1;
    @OneToMany( mappedBy = "lgUSERID")
    private Collection<TUserImprimante> tUserImprimanteCollection;
    @OneToMany( mappedBy = "lgUSERID")
    private Collection<TUserFone> tUserFoneCollection;
    @OneToMany(mappedBy = "strCREATEDBY")
    private Collection<TMvtCaisse> tMvtCaisseCollection;
    @OneToMany(mappedBy = "lgUSERCAISSIERID")
    private Collection<TPreenregistrement> tPreenregistrementCollection;
    @OneToMany(mappedBy = "lgUSERVENDEURID")
    private Collection<TPreenregistrement> tPreenregistrementCollection1;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TPreenregistrement> tPreenregistrementCollection2;
    @OneToMany( mappedBy = "lgUSERID")
    private Collection<TLot> tLotCollection;
    @JoinColumn(name = "lg_EMPLACEMENT_ID", referencedColumnName = "lg_EMPLACEMENT_ID", nullable = false)
    @ManyToOne(optional = false)
    private TEmplacement lgEMPLACEMENTID;
    @JoinColumn(name = "lg_Language_ID", referencedColumnName = "lg_Language_ID")
    @ManyToOne
    private TLanguage lgLanguageID;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TReglement> tReglementCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TPreenregistrementCompteClient> tPreenregistrementCompteClientCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TCodeGestion> tCodeGestionCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TCashTransaction> tCashTransactionCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TDossierReglement> tDossierReglementCollection;
    @OneToMany( mappedBy = "lgUSERID")
    private Collection<TMouvementprice> tMouvementpriceCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TRetrocession> tRetrocessionCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TPreenregistrementCompteClientTiersPayent> tPreenregistrementCompteClientTiersPayentCollection;
    @OneToMany( mappedBy = "lgUSERID")
    private Collection<TMouvement> tMouvementCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TCoffreCaisse> tCoffreCaisseCollection;
    @OneToMany(mappedBy = "lgUSERID")
    private Collection<TDeconditionnement> tDeconditionnementCollection;
   @OneToMany(mappedBy = "lgUSERID")
    private Collection<TEventLog> tEventLogCollection;

    public TUser() {
    }

    public TUser(String lgUSERID) {
        this.lgUSERID = lgUSERID;
    }
    public TUser(String lgUSERID, String strLOGIN, String strTYPE) {
        this.lgUSERID = lgUSERID;
        this.strLOGIN = strLOGIN;
        this.strTYPE = strTYPE;
    }

    public String getLgUSERID() {
        return lgUSERID;
    }

    public void setLgUSERID(String lgUSERID) {
        this.lgUSERID = lgUSERID;
    }

    public Integer getStrIDS() {
        return strIDS;
    }

    public void setStrIDS(Integer strIDS) {
        this.strIDS = strIDS;
    }

    public String getStrLOGIN() {
        return strLOGIN;
    }

    public void setStrLOGIN(String strLOGIN) {
        this.strLOGIN = strLOGIN;
    }

    public String getStrTYPE() {
        return strTYPE;
    }

    public void setStrTYPE(String strTYPE) {
        this.strTYPE = strTYPE;
    }

    public String getStrPASSWORD() {
        return strPASSWORD;
    }

    public void setStrPASSWORD(String strPASSWORD) {
        this.strPASSWORD = strPASSWORD;
    }

    public String getStrCODE() {
        return strCODE;
    }

    public void setStrCODE(String strCODE) {
        this.strCODE = strCODE;
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

    public String getStrCREATEDBY() {
        return strCREATEDBY;
    }

    public void setStrCREATEDBY(String strCREATEDBY) {
        this.strCREATEDBY = strCREATEDBY;
    }

    public String getStrUPDATEDBY() {
        return strUPDATEDBY;
    }

    public void setStrUPDATEDBY(String strUPDATEDBY) {
        this.strUPDATEDBY = strUPDATEDBY;
    }

    public String getStrFIRSTNAME() {
        return strFIRSTNAME;
    }

    public void setStrFIRSTNAME(String strFIRSTNAME) {
        this.strFIRSTNAME = strFIRSTNAME;
    }

    public String getStrLASTNAME() {
        return strLASTNAME;
    }

    public void setStrLASTNAME(String strLASTNAME) {
        this.strLASTNAME = strLASTNAME;
    }

    public Date getStrLASTCONNECTIONDATE() {
        return strLASTCONNECTIONDATE;
    }

    public void setStrLASTCONNECTIONDATE(Date strLASTCONNECTIONDATE) {
        this.strLASTCONNECTIONDATE = strLASTCONNECTIONDATE;
    }

    public String getLgSKINID() {
        return lgSKINID;
    }

    public void setLgSKINID(String lgSKINID) {
        this.lgSKINID = lgSKINID;
    }

    public String getStrSTATUT() {
        return strSTATUT;
    }

    public void setStrSTATUT(String strSTATUT) {
        this.strSTATUT = strSTATUT;
    }

    public Date getDtLASTACTIVITY() {
        return dtLASTACTIVITY;
    }

    public void setDtLASTACTIVITY(Date dtLASTACTIVITY) {
        this.dtLASTACTIVITY = dtLASTACTIVITY;
    }

    public String getStrFUNCTION() {
        return strFUNCTION;
    }

    public void setStrFUNCTION(String strFUNCTION) {
        this.strFUNCTION = strFUNCTION;
    }

    public String getStrPHONE() {
        return strPHONE;
    }

    public void setStrPHONE(String strPHONE) {
        this.strPHONE = strPHONE;
    }

    public String getStrMAIL() {
        return strMAIL;
    }

    public void setStrMAIL(String strMAIL) {
        this.strMAIL = strMAIL;
    }

    public Integer getIntCONNEXION() {
        return intCONNEXION;
    }

    public void setIntCONNEXION(Integer intCONNEXION) {
        this.intCONNEXION = intCONNEXION;
    }

    public Boolean getBCHANGEPASSWORD() {
        return bCHANGEPASSWORD;
    }

    public void setBCHANGEPASSWORD(Boolean bCHANGEPASSWORD) {
        this.bCHANGEPASSWORD = bCHANGEPASSWORD;
    }

    public Boolean getBIsConnected() {
        return bIsConnected;
    }

    public void setBIsConnected(Boolean bIsConnected) {
        this.bIsConnected = bIsConnected;
    }

    public String getStrPIC() {
        return strPIC;
    }

    public void setStrPIC(String strPIC) {
        this.strPIC = strPIC;
    }

    @XmlTransient
    public Collection<TBonLivraison> getTBonLivraisonCollection() {
        return tBonLivraisonCollection;
    }

    public void setTBonLivraisonCollection(Collection<TBonLivraison> tBonLivraisonCollection) {
        this.tBonLivraisonCollection = tBonLivraisonCollection;
    }

    @XmlTransient
    public Collection<TOrder> getTOrderCollection() {
        return tOrderCollection;
    }

    public void setTOrderCollection(Collection<TOrder> tOrderCollection) {
        this.tOrderCollection = tOrderCollection;
    }

    @XmlTransient
    public Collection<TSnapShopDalyStat> getTSnapShopDalyStatCollection() {
        return tSnapShopDalyStatCollection;
    }

    public void setTSnapShopDalyStatCollection(Collection<TSnapShopDalyStat> tSnapShopDalyStatCollection) {
        this.tSnapShopDalyStatCollection = tSnapShopDalyStatCollection;
    }

    @XmlTransient
    public Collection<TResumeCaisse> getTResumeCaisseCollection() {
        return tResumeCaisseCollection;
    }

    public void setTResumeCaisseCollection(Collection<TResumeCaisse> tResumeCaisseCollection) {
        this.tResumeCaisseCollection = tResumeCaisseCollection;
    }

    @XmlTransient
    public Collection<TCaisse> getTCaisseCollection() {
        return tCaisseCollection;
    }

    public void setTCaisseCollection(Collection<TCaisse> tCaisseCollection) {
        this.tCaisseCollection = tCaisseCollection;
    }

    @XmlTransient
    public Collection<TRetourdepot> getTRetourdepotCollection() {
        return tRetourdepotCollection;
    }

    public void setTRetourdepotCollection(Collection<TRetourdepot> tRetourdepotCollection) {
        this.tRetourdepotCollection = tRetourdepotCollection;
    }

    @XmlTransient
    public Collection<TRoleUser> getTRoleUserCollection() {
        return tRoleUserCollection;
    }

    public void setTRoleUserCollection(Collection<TRoleUser> tRoleUserCollection) {
        this.tRoleUserCollection = tRoleUserCollection;
    }

    @XmlTransient
    public Collection<TInventaire> getTInventaireCollection() {
        return tInventaireCollection;
    }

    public void setTInventaireCollection(Collection<TInventaire> tInventaireCollection) {
        this.tInventaireCollection = tInventaireCollection;
    }

    @XmlTransient
    public Collection<TRetourFournisseur> getTRetourFournisseurCollection() {
        return tRetourFournisseurCollection;
    }

    public void setTRetourFournisseurCollection(Collection<TRetourFournisseur> tRetourFournisseurCollection) {
        this.tRetourFournisseurCollection = tRetourFournisseurCollection;
    }

    @XmlTransient
    public Collection<TAjustement> getTAjustementCollection() {
        return tAjustementCollection;
    }

    public void setTAjustementCollection(Collection<TAjustement> tAjustementCollection) {
        this.tAjustementCollection = tAjustementCollection;
    }

    @XmlTransient
    public Collection<TBilletage> getTBilletageCollection() {
        return tBilletageCollection;
    }

    public void setTBilletageCollection(Collection<TBilletage> tBilletageCollection) {
        this.tBilletageCollection = tBilletageCollection;
    }

    @XmlTransient
    public Collection<TWarehouse> getTWarehouseCollection() {
        return tWarehouseCollection;
    }

    public void setTWarehouseCollection(Collection<TWarehouse> tWarehouseCollection) {
        this.tWarehouseCollection = tWarehouseCollection;
    }

    @XmlTransient
    public Collection<TNotification> getTNotificationCollection() {
        return tNotificationCollection;
    }

    public void setTNotificationCollection(Collection<TNotification> tNotificationCollection) {
        this.tNotificationCollection = tNotificationCollection;
    }

    @XmlTransient
    public Collection<TNotification> getTNotificationCollection1() {
        return tNotificationCollection1;
    }

    public void setTNotificationCollection1(Collection<TNotification> tNotificationCollection1) {
        this.tNotificationCollection1 = tNotificationCollection1;
    }

    @XmlTransient
    public Collection<TUserImprimante> getTUserImprimanteCollection() {
        return tUserImprimanteCollection;
    }

    public void setTUserImprimanteCollection(Collection<TUserImprimante> tUserImprimanteCollection) {
        this.tUserImprimanteCollection = tUserImprimanteCollection;
    }

    @XmlTransient
    public Collection<TUserFone> getTUserFoneCollection() {
        return tUserFoneCollection;
    }

    public void setTUserFoneCollection(Collection<TUserFone> tUserFoneCollection) {
        this.tUserFoneCollection = tUserFoneCollection;
    }

    @XmlTransient
    public Collection<TMvtCaisse> getTMvtCaisseCollection() {
        return tMvtCaisseCollection;
    }

    public void setTMvtCaisseCollection(Collection<TMvtCaisse> tMvtCaisseCollection) {
        this.tMvtCaisseCollection = tMvtCaisseCollection;
    }

    @XmlTransient
    public Collection<TPreenregistrement> getTPreenregistrementCollection() {
        return tPreenregistrementCollection;
    }

    public void setTPreenregistrementCollection(Collection<TPreenregistrement> tPreenregistrementCollection) {
        this.tPreenregistrementCollection = tPreenregistrementCollection;
    }

    @XmlTransient
    public Collection<TPreenregistrement> getTPreenregistrementCollection1() {
        return tPreenregistrementCollection1;
    }

    public void setTPreenregistrementCollection1(Collection<TPreenregistrement> tPreenregistrementCollection1) {
        this.tPreenregistrementCollection1 = tPreenregistrementCollection1;
    }

    @XmlTransient
    public Collection<TPreenregistrement> getTPreenregistrementCollection2() {
        return tPreenregistrementCollection2;
    }

    public void setTPreenregistrementCollection2(Collection<TPreenregistrement> tPreenregistrementCollection2) {
        this.tPreenregistrementCollection2 = tPreenregistrementCollection2;
    }

    @XmlTransient
    public Collection<TLot> getTLotCollection() {
        return tLotCollection;
    }

    public void setTLotCollection(Collection<TLot> tLotCollection) {
        this.tLotCollection = tLotCollection;
    }

    public TEmplacement getLgEMPLACEMENTID() {
        return lgEMPLACEMENTID;
    }

    public void setLgEMPLACEMENTID(TEmplacement lgEMPLACEMENTID) {
        this.lgEMPLACEMENTID = lgEMPLACEMENTID;
    }

    public TLanguage getLgLanguageID() {
        return lgLanguageID;
    }

    public void setLgLanguageID(TLanguage lgLanguageID) {
        this.lgLanguageID = lgLanguageID;
    }

    @XmlTransient
    public Collection<TReglement> getTReglementCollection() {
        return tReglementCollection;
    }

    public void setTReglementCollection(Collection<TReglement> tReglementCollection) {
        this.tReglementCollection = tReglementCollection;
    }

    @XmlTransient
    public Collection<TPreenregistrementCompteClient> getTPreenregistrementCompteClientCollection() {
        return tPreenregistrementCompteClientCollection;
    }

    public void setTPreenregistrementCompteClientCollection(Collection<TPreenregistrementCompteClient> tPreenregistrementCompteClientCollection) {
        this.tPreenregistrementCompteClientCollection = tPreenregistrementCompteClientCollection;
    }

    @XmlTransient
    public Collection<TCodeGestion> getTCodeGestionCollection() {
        return tCodeGestionCollection;
    }

    public void setTCodeGestionCollection(Collection<TCodeGestion> tCodeGestionCollection) {
        this.tCodeGestionCollection = tCodeGestionCollection;
    }

    @XmlTransient
    public Collection<TCashTransaction> getTCashTransactionCollection() {
        return tCashTransactionCollection;
    }

    public void setTCashTransactionCollection(Collection<TCashTransaction> tCashTransactionCollection) {
        this.tCashTransactionCollection = tCashTransactionCollection;
    }

    @XmlTransient
    public Collection<TDossierReglement> getTDossierReglementCollection() {
        return tDossierReglementCollection;
    }

    public void setTDossierReglementCollection(Collection<TDossierReglement> tDossierReglementCollection) {
        this.tDossierReglementCollection = tDossierReglementCollection;
    }

    @XmlTransient
    public Collection<TDeconditionnement> getTDeconditionnementCollection() {
        return tDeconditionnementCollection;
    }

    public void setTDeconditionnementCollection(Collection<TDeconditionnement> tDeconditionnementCollection) {
        this.tDeconditionnementCollection = tDeconditionnementCollection;
    }
    
    @XmlTransient
    public Collection<TMouvementprice> getTMouvementpriceCollection() {
        return tMouvementpriceCollection;
    }

    public void setTMouvementpriceCollection(Collection<TMouvementprice> tMouvementpriceCollection) {
        this.tMouvementpriceCollection = tMouvementpriceCollection;
    }

    @XmlTransient
    public Collection<TRetrocession> getTRetrocessionCollection() {
        return tRetrocessionCollection;
    }

    public void setTRetrocessionCollection(Collection<TRetrocession> tRetrocessionCollection) {
        this.tRetrocessionCollection = tRetrocessionCollection;
    }

    @XmlTransient
    public Collection<TPreenregistrementCompteClientTiersPayent> getTPreenregistrementCompteClientTiersPayentCollection() {
        return tPreenregistrementCompteClientTiersPayentCollection;
    }

    public void setTPreenregistrementCompteClientTiersPayentCollection(Collection<TPreenregistrementCompteClientTiersPayent> tPreenregistrementCompteClientTiersPayentCollection) {
        this.tPreenregistrementCompteClientTiersPayentCollection = tPreenregistrementCompteClientTiersPayentCollection;
    }

    @XmlTransient
    public Collection<TMouvement> getTMouvementCollection() {
        return tMouvementCollection;
    }

    public void setTMouvementCollection(Collection<TMouvement> tMouvementCollection) {
        this.tMouvementCollection = tMouvementCollection;
    }

    @XmlTransient
    public Collection<TCoffreCaisse> getTCoffreCaisseCollection() {
        return tCoffreCaisseCollection;
    }

    public void setTCoffreCaisseCollection(Collection<TCoffreCaisse> tCoffreCaisseCollection) {
        this.tCoffreCaisseCollection = tCoffreCaisseCollection;
    }

     @XmlTransient
    public Collection<TEventLog> getTEventLogs() {
        return tEventLogCollection;
    }

    public void setTEventLogCollection(Collection<TEventLog> tEventLogCollection) {
        this.tEventLogCollection = tEventLogCollection;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lgUSERID != null ? lgUSERID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TUser)) {
            return false;
        }
        TUser other = (TUser) object;
        if ((this.lgUSERID == null && other.lgUSERID != null) || (this.lgUSERID != null && !this.lgUSERID.equals(other.lgUSERID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dal.TUser[ lgUSERID=" + lgUSERID + " ]";
    }
    
}
