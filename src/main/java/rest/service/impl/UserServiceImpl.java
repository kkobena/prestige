/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import bll.userManagement.authentification;
import com.kstruct.gethostname4j.Hostname;
import commonTasks.dto.ManagedUserVM;
import dal.TOfficine;
import dal.TParameters;
import dal.TPrivilege;
import dal.TRolePrivelege;
import dal.TRoleUser;
import dal.TUser;
import dal.enumeration.TypeLog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import rest.service.LogService;
import rest.service.UserService;
import toolkits.parameters.commonparameter;
import toolkits.security.Md5;
import toolkits.utils.StringComplexUtils.DataStringManager;
import util.Afficheur;
import util.DateConverter;

/**
 *
 * @author koben
 */
@Stateless
public class UserServiceImpl implements UserService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
    @EJB
    private LogService logService;

    @Override
    public TUser connexion(ManagedUserVM managedUser, HttpServletRequest request) {
        try {
            TypedQuery<TUser> q = getEm().createQuery("SELECT t FROM TUser t WHERE t.strLOGIN = ?1 AND t.strPASSWORD = ?2 AND t.strSTATUT =?3 ", TUser.class).
                    setParameter(1, managedUser.getLogin()).
                    setParameter(2, Md5.encode(managedUser.getPassword())).
                    setParameter(3, DateConverter.STATUT_ENABLE).setMaxResults(1);
            TUser OTUser = q.getSingleResult();
            OTUser.setStrLASTCONNECTIONDATE(new Date());
            OTUser.setIntCONNEXION(OTUser.getIntCONNEXION() + 1);
            OTUser.setBIsConnected(true);
            getEm().merge(OTUser);

            String desc = "Authentification de " + OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME() + " à partir de l'adresse " + request.getRemoteAddr() + " : nom poste " + getHostName(request);
            logService.updateLogFile(OTUser, OTUser.getStrLOGIN(), desc, TypeLog.AUTHENTIFICATION, OTUser, getHostName(request), request.getRemoteAddr());
            afficheur("Caisse: " + OTUser.getStrLASTNAME());
            return OTUser;
        } catch (Exception e) {
            e.printStackTrace(System.err);
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
            }
        }

    }

    @Override
    public TOfficine getOfficine() {
        return getEm().find(TOfficine.class, DateConverter.OFFICINE);

    }

    private TUser findByLogin(String login) {
        try {
            TypedQuery<TUser> q = getEm().createQuery("SELECT t FROM TUser t WHERE t.strLOGIN = ?1", TUser.class).
                    setParameter(1, login)
                    .setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deConnexion(HttpServletRequest request, TUser OTUser) {
        try {
            OTUser.setBIsConnected(false);
            getEm().merge(OTUser);
            HttpSession hs = request.getSession();
            hs.invalidate();
            String desc = " Déconnection de " + OTUser.getStrFIRSTNAME() + " " + OTUser.getStrLASTNAME() + " à partir de l'adresse " + request.getRemoteAddr() + " : nom poste " + getHostName(request);
            logService.updateLogFile(OTUser, OTUser.getStrLOGIN(), desc, TypeLog.DECONNECTION, OTUser, getHostName(request), request.getRemoteAddr());
            afficheur("   CAISSE FERMEE");
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public TRoleUser getTRoleUser(String lg_USER_ID) {

        try {
            TypedQuery<TRoleUser> q = getEm().createQuery("SELECT t FROM TRoleUser t WHERE t.lgUSERID.lgUSERID = ?1 AND t.lgUSERID.strSTATUT = ?2 AND t.lgROLEID.strSTATUT = ?2", TRoleUser.class)
                    .setParameter(1, lg_USER_ID).setParameter(2, DateConverter.STATUT_ENABLE)
                    .setMaxResults(1);
            return q.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<TPrivilege> getAllPrivilege(TUser oTUser) {

        List<TPrivilege> LstTPrivilege = new ArrayList<>();
        try {

            Collection<TRoleUser> CollTRoleUser = oTUser.getTRoleUserCollection();
            Iterator iteraror = CollTRoleUser.iterator();
            while (iteraror.hasNext()) {
                Object el = iteraror.next();
                TRoleUser OTRoleUser = (TRoleUser) el;
                Collection<TRolePrivelege> CollTRolePrivelege = OTRoleUser.getLgROLEID().getTRolePrivelegeCollection();
                Iterator iterarorTRolePrivelege = CollTRolePrivelege.iterator();
                while (iterarorTRolePrivelege.hasNext()) {
                    Object elTRolePrivelege = iterarorTRolePrivelege.next();
                    TRolePrivelege OTRolePrivelege = (TRolePrivelege) elTRolePrivelege;
                    LstTPrivilege.add(OTRolePrivelege.getLgPRIVILEGEID());

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        return LstTPrivilege;
    }

    private String getHostName(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        if (remoteAddr.equals(localAddr)) {
            return Hostname.getHostname();
        }
        return request.getRemoteHost();
    }
}
