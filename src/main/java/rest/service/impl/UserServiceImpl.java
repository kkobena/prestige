/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import com.kstruct.gethostname4j.Hostname;
import commonTasks.dto.ManagedUserVM;
import dal.*;
import dal.enumeration.TypeLog;
import org.apache.commons.lang3.StringUtils;
import rest.service.LogService;
import rest.service.UserService;
import rest.service.dto.AccountInfoDTO;
import toolkits.utils.StringComplexUtils.DataStringManager;
import util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import toolkits.security.Md5;

/**
 * @author koben
 */
@Stateless
public class UserServiceImpl implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private LogService logService;

    @Override
    public TUser connexion(ManagedUserVM managedUser, HttpServletRequest request) {
        try {
            TypedQuery<TUser> q = getEm()
                    .createQuery(
                            "SELECT t FROM TUser t  WHERE t.strLOGIN = ?1 AND t.strPASSWORD = ?2 AND t.strSTATUT =?3 ",
                            TUser.class)
                    .setParameter(1, managedUser.getLogin()).setParameter(2, Md5.encode(managedUser.getPassword()))
                    .setParameter(3, DateConverter.STATUT_ENABLE).setMaxResults(1);
            TUser OTUser = q.getSingleResult();
            OTUser.setStrLASTCONNECTIONDATE(new Date());
            OTUser.setIntCONNEXION(OTUser.getIntCONNEXION() + 1);
            OTUser.setBIsConnected(true);
            getEm().merge(OTUser);
            String desc = "Authentification de " + OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME()
                    + " à partir de l'adresse " + request.getRemoteAddr() + " : nom poste " + getHostName(request);
            logService.updateLogFile(OTUser, OTUser.getStrLOGIN(), desc, TypeLog.AUTHENTIFICATION, OTUser,
                    getHostName(request), request.getRemoteAddr());
            afficheur("Caisse: " + OTUser.getStrLASTNAME());
            return OTUser;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }
    }

    public boolean afficheurActif() {

        try {
            TParameters tp = getEm().find(TParameters.class, "KEY_ACTIVATE_DISPLAYER");
            return (tp != null && tp.getStrVALUE().trim().equals("1"));
        } catch (Exception e) {
            return false;
        }

    }

    public EntityManager getEm() {
        return em;
    }

    void afficheur(String test) {
        if (afficheurActif()) {
            try {
                Afficheur afficheur = Afficheur.getInstance();
                afficheur.affichage(DataStringManager.subStringData(getOfficine().getStrNOMABREGE(), 0, 20));
                afficheur.affichage(DataStringManager.subStringData(test, 0, 20));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }

    }

    @Override
    public TOfficine getOfficine() {
        return getEm().find(TOfficine.class, DateConverter.OFFICINE);

    }

    @Override
    public boolean deConnexion(HttpServletRequest request, TUser OTUser) {
        try {
            OTUser.setBIsConnected(false);
            getEm().merge(OTUser);
            HttpSession hs = request.getSession();
            hs.invalidate();
            String desc = " Déconnection de " + OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME()
                    + " à partir de l'adresse " + request.getRemoteAddr() + " : nom poste " + getHostName(request);
            logService.updateLogFile(OTUser, OTUser.getStrLOGIN(), desc, TypeLog.DECONNECTION, OTUser,
                    getHostName(request), request.getRemoteAddr());
            afficheur("   CAISSE FERMEE");
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public TRoleUser getTRoleUser(String lg_USER_ID) {

        try {
            TypedQuery<TRoleUser> q = getEm().createQuery(
                    "SELECT t FROM TRoleUser t WHERE t.lgUSERID.lgUSERID = ?1 AND t.lgUSERID.strSTATUT = ?2 AND t.lgROLEID.strSTATUT = ?2",
                    TRoleUser.class).setParameter(1, lg_USER_ID).setParameter(2, DateConverter.STATUT_ENABLE)
                    .setMaxResults(1);
            return q.getSingleResult();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            return null;
        }

    }

    private List<TRoleUser> loadRoleUser(String userId) {
        TypedQuery<TRoleUser> q = this.getEm().createQuery("SELECT o FROM TRoleUser o WHERE o.lgUSERID.lgUSERID=?1 ",
                TRoleUser.class);
        q.setParameter(1, userId);
        return q.getResultList();
    }

    private List<TRolePrivelege> loadTRolePrivelege(String roleId) {
        TypedQuery<TRolePrivelege> q = this.getEm().createQuery(
                "SELECT o FROM  TRolePrivelege o WHERE o.lgROLEID.lgROLEID =?1 AND o.lgPRIVILEGEID.strSTATUT='enable'",
                TRolePrivelege.class);
        q.setParameter(1, roleId);
        return q.getResultList();
    }

    @Override
    public List<TPrivilege> getAllPrivilege(TUser oTUser) {

        List<TPrivilege> LstTPrivilege = new ArrayList<>();
        try {

            List<TRoleUser> CollTRoleUser = loadRoleUser(oTUser.getLgUSERID());

            Iterator iteraror = CollTRoleUser.iterator();
            while (iteraror.hasNext()) {
                Object el = iteraror.next();
                TRoleUser OTRoleUser = (TRoleUser) el;
                List<TRolePrivelege> CollTRolePrivelege = loadTRolePrivelege(OTRoleUser.getLgROLEID().getLgROLEID());
                Iterator iterarorTRolePrivelege = CollTRolePrivelege.iterator();
                while (iterarorTRolePrivelege.hasNext()) {
                    Object elTRolePrivelege = iterarorTRolePrivelege.next();
                    TRolePrivelege OTRolePrivelege = (TRolePrivelege) elTRolePrivelege;
                    LstTPrivilege.add(OTRolePrivelege.getLgPRIVILEGEID());

                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

        return LstTPrivilege;
    }

    @Override
    public AccountInfoDTO getAccount(TUser tu) {

        if (Objects.isNull(tu)) {
            throw new RuntimeException();
        }
        TRoleUser roleUser = this.getTRoleUser(tu.getLgUSERID());
        TRole role = roleUser.getLgROLEID();

        String roleName = (role != null ? role.getStrDESIGNATION() : "");
        String xtypeload = "mainmenumanager";
        if (role != null && (role.getStrNAME().equalsIgnoreCase(Constant.ROLE_SUPERADMIN)
                || role.getStrNAME().equalsIgnoreCase(Constant.ROLE_PHARMACIEN))) {
            xtypeload = "dashboard";
        }

        TLanguage tLanguage = tu.getLgLanguageID();
        return new AccountInfoDTO().setLgUSERID(tu.getLgUSERID()).setStrLOGIN(tu.getStrLOGIN())
                .setStrFIRSTNAME(tu.getStrFIRSTNAME()).setStrLASTNAME(tu.getStrLASTNAME())
                .setStrLASTCONNECTIONDATE(
                        DateUtil.convertDate(tu.getStrLASTCONNECTIONDATE(), new SimpleDateFormat("yyyy/MM/dd")))
                .setStrSTATUT(tu.getStrSTATUT())
                .setLgLanguageID(Objects.nonNull(tLanguage) ? tLanguage.getStrDescription() : "").setRole(roleName)
                .setXtypeload(xtypeload);

    }

    private String getHostName(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        if (remoteAddr.equals(localAddr)) {
            return Hostname.getHostname();
        }
        return request.getRemoteHost();
    }

    @Override
    public TUser updateProfilUser(AccountInfoDTO accountInfo) {
        TUser usr = this.em.find(TUser.class, accountInfo.getLgUSERID());
        usr.setStrFIRSTNAME(accountInfo.getStrFIRSTNAME());
        usr.setStrLASTNAME(accountInfo.getStrLASTNAME());
        if (StringUtils.isNotEmpty(accountInfo.getStrPASSWORD())) {
            usr.setStrPASSWORD(Md5.encode(accountInfo.getStrPASSWORD()));

        }

        return this.em.merge(usr);

    }
}
