package rest.service.impl;

import dal.TFacture;
import dal.TTiersPayant;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.GroupeInvoiceService;
import javax.persistence.Query; // <-- ajoute cet import en haut

/**
 *
 * @author airman
 */

@Stateless
public class GroupeInvoiceServiceImpl implements GroupeInvoiceService {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    private static final SimpleDateFormat DF_MYSQL = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DF_SHORT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * EXACTEMENT comme ws_groupeInvoices.jsp : group by (lg_GROUPE_ID, str_CODE_FACTURE)
     */
    @Override
    public JSONObject getGroupeInvoices(String dtStart, String dtEnd, String searchValue, Integer lgGroupeId,
            String codeGroupe, boolean actionReglerFacture, int start, int limit) throws JSONException {

        Date dStart = parseMysql(dtStart);
        Date dEnd = endOfDay(parseMysql(dtEnd));

        String sv = StringUtils.defaultString(searchValue).trim();
        String cg = StringUtils.defaultString(codeGroupe).trim();
        if ("undefined".equalsIgnoreCase(cg)) {
            cg = "";
        }

        // ✅ Query agrégée : NBFACTURES, AMOUNT, AMOUNTPAYE, MONTANTRESTANT, DATECREATION
        String jpql = "SELECT " + " g.lgGROUPEID.lgGROUPEID AS lgGroupeId, " + " g.lgGROUPEID.strLIBELLE AS libelle, "
                + " g.strCODEFACTURE AS codeFacture, " + " COUNT(DISTINCT f.lgFACTUREID) AS nbFactures, "
                + " COALESCE(SUM(f.dblMONTANTCMDE), 0) AS amount, "
                + " COALESCE(SUM(f.dblMONTANTPAYE), 0) AS amountPaye, "
                + " COALESCE(SUM(f.dblMONTANTRESTANT), 0) AS montantRestant, " + " MAX(g.dtCREATED) AS dateCreation "
                + "FROM TGroupeFactures g " + "JOIN g.lgFACTURESID f " + "WHERE 1=1 "
                + " AND (:dtStart IS NULL OR g.dtCREATED >= :dtStart) "
                + " AND (:dtEnd IS NULL OR g.dtCREATED <= :dtEnd) "
                + " AND (:lgGroupeId IS NULL OR g.lgGROUPEID.lgGROUPEID = :lgGroupeId) "
                + " AND (:codeGroupe = '' OR g.strCODEFACTURE = :codeGroupe) "
                + " AND (:sv = '' OR (g.strCODEFACTURE LIKE :svLike OR g.lgGROUPEID.strLIBELLE LIKE :svLike)) "
                + "GROUP BY g.lgGROUPEID.lgGROUPEID, g.lgGROUPEID.strLIBELLE, g.strCODEFACTURE "
                + "ORDER BY MAX(g.dtCREATED) DESC";

        TypedQuery<Tuple> q = em.createQuery(jpql, Tuple.class);
        q.setParameter("dtStart", dStart);
        q.setParameter("dtEnd", dEnd);
        q.setParameter("lgGroupeId", lgGroupeId);
        q.setParameter("codeGroupe", cg);
        q.setParameter("sv", sv);
        q.setParameter("svLike", "%" + sv + "%");
        q.setFirstResult(start);
        q.setMaxResults(limit);

        List<Tuple> rows = q.getResultList();

        JSONArray data = new JSONArray();
        for (Tuple t : rows) {
            JSONObject o = new JSONObject();

            Integer gid = getInt(t, "lgGroupeId");
            String lib = getString(t, "libelle");
            String codeFacture = getString(t, "codeFacture");

            long nbFactures = getLong(t, "nbFactures");
            long amount = getLong(t, "amount");
            long amountPaye = getLong(t, "amountPaye");
            long montantRestant = getLong(t, "montantRestant");

            Date dc = (Date) t.get("dateCreation");

            // ✅ STATUT exactement exploitable dans ton grid
            String statut = (montantRestant <= 0) ? "paid" : "enable";

            o.put("lg_GROUPE_ID", gid);
            o.put("NBFACTURES", nbFactures);
            o.put("STATUT", statut);
            o.put("ACTION_REGLER_FACTURE", actionReglerFacture);
            o.put("CODEFACTURE", codeFacture);
            o.put("AMOUNT", amount);
            o.put("str_LIB", lib);
            o.put("MONTANTRESTANT", montantRestant);
            o.put("AMOUNTPAYE", amountPaye);
            o.put("DATECREATION", dc != null ? DF_SHORT.format(dc) : "");

            data.put(o);
        }

        int total = countGroupeInvoicesGrouped(dStart, dEnd, sv, lgGroupeId, cg);

        return new JSONObject().put("total", total).put("data", data);
    }

    /**
     * EXACTEMENT comme ws_invoiceDetails.jsp (détails d'un code facture groupe)
     */
    @Override
    public JSONObject getGroupeInvoiceDetails(String codeGroupe, String lgTP, String searchValue, int start, int limit)
            throws JSONException {

        String cg = StringUtils.defaultString(codeGroupe).trim();
        String tp = StringUtils.defaultString(lgTP).trim();
        String sv = StringUtils.defaultString(searchValue).trim();

        String jpql = "SELECT DISTINCT f " + "FROM TGroupeFactures gf " + "JOIN gf.lgFACTURESID f "
                + "LEFT JOIN f.tiersPayant tp2 " + "WHERE gf.strCODEFACTURE = :codeGroupe "
                + " AND (:lgTP = '' OR tp2.lgTIERSPAYANTID = :lgTP) "
                + " AND (:sv = '' OR (f.strCODEFACTURE LIKE :svLike OR tp2.strFULLNAME LIKE :svLike)) "
                + "ORDER BY f.dtDATEFACTURE DESC";

        TypedQuery<TFacture> q = em.createQuery(jpql, TFacture.class);
        q.setParameter("codeGroupe", cg);
        q.setParameter("lgTP", tp);
        q.setParameter("sv", sv);
        q.setParameter("svLike", "%" + sv + "%");
        q.setFirstResult(start);
        q.setMaxResults(limit);

        List<TFacture> rows = q.getResultList();

        JSONArray data = new JSONArray();
        for (TFacture f : rows) {
            JSONObject json = new JSONObject();

            TTiersPayant tp2 = null;
            try {
                tp2 = f.getTiersPayant();
                if (tp2 == null && StringUtils.isNotBlank(f.getStrCUSTOMER())) {
                    tp2 = em.find(TTiersPayant.class, f.getStrCUSTOMER());
                }
            } catch (Exception e) {
                tp2 = null;
            }

            json.put("lg_FACTURE_ID", f.getLgFACTUREID());
            json.put("str_CODE_FACTURE", StringUtils.defaultString(f.getStrCODEFACTURE()));
            json.put("int_NB_DOSSIER", f.getIntNBDOSSIER() != null ? f.getIntNBDOSSIER() : 0);
            json.put("dt_CREATED", f.getDtDATEFACTURE() != null ? DF_SHORT.format(f.getDtDATEFACTURE()) : "");
            json.put("str_STATUT", StringUtils.defaultString(f.getStrSTATUT()));
            json.put("str_CUSTOMER_NAME", tp2 != null ? StringUtils.defaultString(tp2.getStrFULLNAME()) : "");

            String periode = "Du " + (f.getDtDEBUTFACTURE() != null ? DF_SHORT.format(f.getDtDEBUTFACTURE()) : "")
                    + " Au " + (f.getDtFINFACTURE() != null ? DF_SHORT.format(f.getDtFINFACTURE()) : "");
            json.put("str_PERIODE", periode);

            json.put("dbl_MONTANT_CMDE", nvlDouble(f.getDblMONTANTCMDE()));
            json.put("dbl_MONTANT_RESTANT", nvlDouble(f.getDblMONTANTRESTANT()));
            json.put("dbl_MONTANT_PAYE", nvlDouble(f.getDblMONTANTPAYE()));

            json.put("MONTANTREMISE", f.getDblMONTANTREMISE() != null ? f.getDblMONTANTREMISE() : BigDecimal.ZERO);
            json.put("MONTANTFORFETAIRE",
                    f.getDblMONTANTFOFETAIRE() != null ? f.getDblMONTANTFOFETAIRE() : BigDecimal.ZERO);
            json.put("MONTANTBRUT", f.getDblMONTANTBrut() != null ? f.getDblMONTANTBrut() : BigDecimal.ZERO);

            json.put("isChecked", false);

            data.put(json);
        }

        int total = countGroupeInvoiceDetails(cg, tp, sv);

        return new JSONObject().put("total", total).put("data", data);
    }

    // -------------------- COUNT : total des groupes (lgGROUPEID, strCODEFACTURE) --------------------

    private int countGroupeInvoicesGrouped(Date dStart, Date dEnd, String sv, Integer lgGroupeId, String codeGroupe) {

        String cg = StringUtils.defaultString(codeGroupe).trim();
        String svSafe = StringUtils.defaultString(sv).trim();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM (");
        sql.append("  SELECT gf.lg_GROUPE_ID, gf.str_CODE_FACTURE ");
        sql.append("  FROM t_groupe_factures gf ");
        sql.append("  JOIN t_groupe_tierspayant gt ON gt.lg_GROUPE_ID = gf.lg_GROUPE_ID ");
        sql.append("  WHERE 1=1 ");
        sql.append("    AND (?1 IS NULL OR gf.dt_CREATED >= ?1) ");
        sql.append("    AND (?2 IS NULL OR gf.dt_CREATED <= ?2) ");
        sql.append("    AND (?3 IS NULL OR gf.lg_GROUPE_ID = ?3) ");
        sql.append("    AND (?4 = '' OR gf.str_CODE_FACTURE = ?4) ");
        sql.append("    AND (?5 = '' OR (gf.str_CODE_FACTURE LIKE ?6 OR gt.str_LIBELLE LIKE ?6)) ");
        sql.append("  GROUP BY gf.lg_GROUPE_ID, gf.str_CODE_FACTURE ");
        sql.append(") x");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter(1, dStart);
        q.setParameter(2, dEnd);
        q.setParameter(3, lgGroupeId);
        q.setParameter(4, cg);
        q.setParameter(5, svSafe);
        q.setParameter(6, "%" + svSafe + "%");

        Number n = (Number) q.getSingleResult();
        return n != null ? n.intValue() : 0;
    }

    private int countGroupeInvoiceDetails(String codeGroupe, String lgTP, String sv) {
        String jpql = "SELECT COUNT(DISTINCT f.lgFACTUREID) " + "FROM TGroupeFactures gf " + "JOIN gf.lgFACTURESID f "
                + "LEFT JOIN f.tiersPayant tp2 " + "WHERE gf.strCODEFACTURE = :codeGroupe "
                + " AND (:lgTP = '' OR tp2.lgTIERSPAYANTID = :lgTP) "
                + " AND (:sv = '' OR (f.strCODEFACTURE LIKE :svLike OR tp2.strFULLNAME LIKE :svLike))";

        TypedQuery<Long> q = em.createQuery(jpql, Long.class);
        q.setParameter("codeGroupe", codeGroupe);
        q.setParameter("lgTP", StringUtils.defaultString(lgTP));
        q.setParameter("sv", StringUtils.defaultString(sv));
        q.setParameter("svLike", "%" + StringUtils.defaultString(sv) + "%");

        Long c = q.getSingleResult();
        return c == null ? 0 : c.intValue();
    }

    // -------------------- UTILS --------------------

    private Date parseMysql(String s) {
        try {
            if (StringUtils.isBlank(s)) {
                return null;
            }
            return DF_MYSQL.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Date endOfDay(Date d) {
        if (d == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private double nvlDouble(Double v) {
        return v == null ? 0d : v.doubleValue();
    }

    private String getString(Tuple t, String alias) {
        Object v = t.get(alias);
        return v == null ? "" : String.valueOf(v);
    }

    private Integer getInt(Tuple t, String alias) {
        Object v = t.get(alias);
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return Integer.valueOf(String.valueOf(v));
    }

    private long getLong(Tuple t, String alias) {
        Object v = t.get(alias);
        if (v == null) {
            return 0L;
        }
        if (v instanceof BigDecimal) {
            return ((BigDecimal) v).longValue();
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        return Long.parseLong(String.valueOf(v));
    }
}
