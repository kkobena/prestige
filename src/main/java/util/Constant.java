package util;

import dal.TPrivilege;
import java.util.List;

/**
 * @author koben
 */
public final class Constant {

    public static final String DEPOT_EXTENSION = "5";
    public static final String DECONNECTED_MESSAGE = "Veuillez vous connecter";
    public static final String STATUT_AUTO = "auto";
    public static final String STATUT_ENABLE = "enable";
    public static final String STATUT_IS_PROGRESS = "is_Process";
    public static final String STATUT_IS_CLOSED = "is_Closed";
    public static final String AIRTIME_USER = "AIRTIME_USER";
    public static final String STATUT_PENDING = "pending";
    public static final String STATUT_PASSED = "passed";
    public static final String STATUT_PHARMA = "pharma";
    public static final String ROLE_SUPERADMIN = "Super Administrateur";
    public static final String PARAMETER_ADMIN = "ADMIN";
    public static final String ROLE_PHARMACIEN = "Pharmacien";
    public static final String ROLE_ADMIN = "Administrateur";
    public static final String STATUT_WAITING = "is_Waiting";
    public static final String VENTE_COMPTANT = "VNO";
    public static final String VENTE_ASSURANCE = "VO";
    public static final String USER_LIST_PRIVILEGE = "USER_LIST_PRIVILEGE";
    public static final String str_SHOW_VENTE = "str_SHOW_VENTE";
    public static final String P_SHOW_ALL_ACTIVITY = "P_SHOW_ALL_ACTIVITY";
    public static final String STATUT_DELETE = "delete";
    public static final String STATUT_CANCEL = "cancel";
    public static final String DEFAUL_TYPEETIQUETTE = "2";
    public static final String STATUT_IS_USING = "is_Using";
    public static final String ACTION_DESACTIVE_PRODUIT = "ACTION_DESACTIVE_PRODUIT";
    public static final String KEY_ACTIVATE_CLOTURE_CAISSE_AUTO = "KEY_ACTIVATE_CLOTURE_CAISSE_AUTO";
    public static final String STATUT_IS_WAITING_VALIDATION = "is_Waiting_validation";
    public static final String STATUT_IS_ASSIGN = "is_assign";
    public static final String MODE_ESP = "1";
    public static String ACTION_OTHER = "OTHER";
    public static final String MVT_SORTIE_CAISSE = "4";

    private Constant() {
    }

    public static boolean hasAuthorityByName(List<TPrivilege> lstTPrivilege, String authorityName) {
        java.util.function.Predicate<TPrivilege> p = e -> e.getStrNAME().equalsIgnoreCase(authorityName);
        return lstTPrivilege.stream().anyMatch(p);
    }
}
