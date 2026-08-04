package rest;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dal.ModelFactureDynamique;
import dal.ModelFactureDynamiqueColonne;
import dal.TFacture;
import dal.TFactureDetail;
import dal.TOfficine;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TTiersPayant;
import dal.TUser;
import dal.dataManager;
import java.io.ByteArrayOutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import toolkits.parameters.commonparameter;
import toolkits.utils.conversion;
import toolkits.utils.date;

/**
 * Createur de modeles de facture dynamiques : l'utilisateur definit lui-meme les colonnes a afficher (parmi toutes les
 * informations disponibles d'un dossier), leur libelle, leur ordre et le tri, puis rattache des tiers payants au
 * modele. L'edition PDF est generee en Java (iText) sans fichier Jasper : les tiers payants non rattaches gardent le
 * circuit Jasper historique inchange.
 */
@Path("v1/model-facture-dynamique")
@Produces("application/json")
@Consumes("application/json")
public class ModelFactureDynamiqueRessource {

    private static final Logger LOG = Logger.getLogger(ModelFactureDynamiqueRessource.class.getName());

    /** Registre des colonnes disponibles : code -> [libelle par defaut, colonne numerique totalisee]. */
    private static final Map<String, Object[]> COLONNES_DISPONIBLES = new LinkedHashMap<>();

    static {
        COLONNES_DISPONIBLES.put("NUMERO", new Object[] { "N°", false });
        COLONNES_DISPONIBLES.put("DATE_BON", new Object[] { "Date", false });
        COLONNES_DISPONIBLES.put("REF_BON", new Object[] { "N° Bon", false });
        COLONNES_DISPONIBLES.put("NOM_CLIENT", new Object[] { "Nom", false });
        COLONNES_DISPONIBLES.put("PRENOM_CLIENT", new Object[] { "Prénom(s)", false });
        COLONNES_DISPONIBLES.put("NOM_COMPLET", new Object[] { "Nom et prénom(s)", false });
        COLONNES_DISPONIBLES.put("MATRICULE", new Object[] { "Matricule", false });
        COLONNES_DISPONIBLES.put("REF_VENTE", new Object[] { "Réf. vente", false });
        COLONNES_DISPONIBLES.put("TAUX", new Object[] { "Taux (%)", false });
        COLONNES_DISPONIBLES.put("MONTANT_BRUT", new Object[] { "Montant brut", true });
        COLONNES_DISPONIBLES.put("REMISE", new Object[] { "Remise", true });
        COLONNES_DISPONIBLES.put("PART_CLIENT", new Object[] { "Part client", true });
        COLONNES_DISPONIBLES.put("PART_TIERS_PAYANT", new Object[] { "Part tiers payant", true });
    }

    @Inject
    private HttpServletRequest servletRequest;

    private TUser utilisateurSession() {
        return (TUser) servletRequest.getSession().getAttribute(commonparameter.AIRTIME_USER);
    }

    private Response reponseJson(JSONObject json) {
        return Response.ok().entity(json.toString()).build();
    }

    private Response reponseDeconnecte() {
        return reponseJson(new JSONObject().put("success", "0").put("errors", util.Constant.DECONNECTED_MESSAGE)
                .put("data", new JSONArray()).put("total", 0));
    }

    /** Colonnes proposees au choix dans le createur de modeles. */
    @GET
    @Path("colonnes")
    public Response colonnes() {
        JSONArray data = new JSONArray();
        for (Map.Entry<String, Object[]> e : COLONNES_DISPONIBLES.entrySet()) {
            data.put(new JSONObject().put("champ", e.getKey()).put("libelle", (String) e.getValue()[0]).put("numerique",
                    (Boolean) e.getValue()[1]));
        }
        return reponseJson(new JSONObject().put("data", data).put("total", data.length()));
    }

    /** Liste des modeles dynamiques avec leurs colonnes et le nombre de tiers payants rattaches. */
    @GET
    @Path("list")
    public Response list(@DefaultValue("") @QueryParam("query") String query) {
        if (utilisateurSession() == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            EntityManager em = odm.getEm();
            List<ModelFactureDynamique> modeles = em.createQuery(
                    "SELECT m FROM ModelFactureDynamique m WHERE m.statut = 'enable' AND m.nom LIKE ?1 ORDER BY m.nom",
                    ModelFactureDynamique.class).setParameter(1, StringUtils.defaultString(query) + "%")
                    .getResultList();
            JSONArray data = new JSONArray();
            for (ModelFactureDynamique m : modeles) {
                Long nbTp = em.createQuery("SELECT COUNT(t) FROM TTiersPayant t WHERE t.modelFactureDynamiqueId = ?1",
                        Long.class).setParameter(1, m.getId()).getSingleResult();
                JSONArray cols = new JSONArray();
                for (ModelFactureDynamiqueColonne c : m.getColonnes()) {
                    cols.put(new JSONObject().put("champ", c.getChamp()).put("libelle", c.getLibelle()).put("ordre",
                            c.getOrdre()));
                }
                data.put(new JSONObject().put("id", m.getId()).put("nom", m.getNom())
                        .put("description", StringUtils.defaultString(m.getDescription()))
                        .put("modeTri", m.getModeTri()).put("colonnes", cols).put("nbColonnes", cols.length())
                        .put("nbTiersPayants", nbTp));
            }
            return reponseJson(new JSONObject().put("data", data).put("total", data.length()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "liste modeles dynamiques", e);
            return reponseJson(new JSONObject().put("data", new JSONArray()).put("total", 0));
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Creation / modification d'un modele (id vide = creation) avec ses colonnes (tableau JSON). */
    @POST
    @Path("save")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response save(@DefaultValue("") @FormParam("id") String idParam,
            @DefaultValue("") @FormParam("nom") String nom,
            @DefaultValue("") @FormParam("description") String description,
            @DefaultValue("TIERS_PAYANT") @FormParam("modeTri") String modeTri,
            @DefaultValue("[]") @FormParam("colonnes") String colonnesParam) {
        if (utilisateurSession() == null) {
            return reponseDeconnecte();
        }
        if (StringUtils.isBlank(nom)) {
            return reponseJson(new JSONObject().put("success", "0").put("errors", "Le nom du modèle est obligatoire"));
        }
        JSONArray colonnes = new JSONArray(colonnesParam);
        if (colonnes.length() == 0) {
            return reponseJson(
                    new JSONObject().put("success", "0").put("errors", "Choisissez au moins une colonne à afficher"));
        }
        for (int i = 0; i < colonnes.length(); i++) {
            String champ = colonnes.getJSONObject(i).optString("champ");
            if (!COLONNES_DISPONIBLES.containsKey(champ)) {
                return reponseJson(new JSONObject().put("success", "0").put("errors", "Colonne inconnue : " + champ));
            }
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            EntityManager em = odm.getEm();
            em.getTransaction().begin();
            ModelFactureDynamique modele;
            if (StringUtils.isNotBlank(idParam)) {
                modele = em.find(ModelFactureDynamique.class, Integer.valueOf(idParam));
                if (modele == null) {
                    em.getTransaction().rollback();
                    return reponseJson(new JSONObject().put("success", "0").put("errors", "Modèle introuvable"));
                }
                modele.setDtUpdated(new Date());
                modele.getColonnes().clear();
            } else {
                modele = new ModelFactureDynamique();
                modele.setDtCreated(new Date());
            }
            modele.setNom(nom.trim());
            modele.setDescription(StringUtils.trimToNull(description));
            modele.setModeTri(normaliserModeTri(modeTri));
            for (int i = 0; i < colonnes.length(); i++) {
                JSONObject o = colonnes.getJSONObject(i);
                ModelFactureDynamiqueColonne col = new ModelFactureDynamiqueColonne();
                col.setModele(modele);
                col.setChamp(o.getString("champ"));
                String libelle = StringUtils.trimToNull(o.optString("libelle"));
                col.setLibelle(libelle != null ? libelle : (String) COLONNES_DISPONIBLES.get(o.getString("champ"))[0]);
                col.setOrdre(o.optInt("ordre", i));
                modele.getColonnes().add(col);
            }
            if (modele.getId() == null) {
                em.persist(modele);
            } else {
                em.merge(modele);
            }
            em.getTransaction().commit();
            return reponseJson(new JSONObject().put("success", "1").put("errors", "Modèle enregistré"));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "sauvegarde modele dynamique", e);
            return reponseJson(
                    new JSONObject().put("success", "0").put("errors", "Impossible d'enregistrer le modèle"));
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Suppression d'un modele : les tiers payants rattaches retournent au circuit Jasper historique. */
    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response delete(@FormParam("id") int id) {
        if (utilisateurSession() == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            EntityManager em = odm.getEm();
            em.getTransaction().begin();
            ModelFactureDynamique modele = em.find(ModelFactureDynamique.class, id);
            if (modele == null) {
                em.getTransaction().rollback();
                return reponseJson(new JSONObject().put("success", "0").put("errors", "Modèle introuvable"));
            }
            em.createQuery(
                    "UPDATE TTiersPayant t SET t.modelFactureDynamiqueId = NULL WHERE t.modelFactureDynamiqueId = ?1")
                    .setParameter(1, id).executeUpdate();
            em.remove(modele);
            em.getTransaction().commit();
            return reponseJson(new JSONObject().put("success", "1").put("errors", "Modèle supprimé"));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "suppression modele dynamique", e);
            return reponseJson(new JSONObject().put("success", "0").put("errors", "Impossible de supprimer le modèle"));
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Tiers payants rattaches a un modele. */
    @GET
    @Path("tiers-payants")
    public Response tiersPayants(@QueryParam("modelId") int modelId) {
        if (utilisateurSession() == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            List<TTiersPayant> tps = odm.getEm()
                    .createQuery(
                            "SELECT t FROM TTiersPayant t WHERE t.modelFactureDynamiqueId = ?1 ORDER BY t.strFULLNAME",
                            TTiersPayant.class)
                    .setParameter(1, modelId).getResultList();
            JSONArray data = new JSONArray();
            for (TTiersPayant tp : tps) {
                data.put(new JSONObject().put("lg_TIERS_PAYANT_ID", tp.getLgTIERSPAYANTID()).put("str_FULLNAME",
                        tp.getStrFULLNAME()));
            }
            return reponseJson(new JSONObject().put("data", data).put("total", data.length()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "tiers payants du modele", e);
            return reponseJson(new JSONObject().put("data", new JSONArray()).put("total", 0));
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Rattache (modelId > 0) ou detache (modelId vide) un tiers payant d'un modele dynamique. */
    @POST
    @Path("assigner")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response assigner(@FormParam("lg_TIERS_PAYANT_ID") String tiersPayantId,
            @DefaultValue("") @FormParam("modelId") String modelIdParam) {
        if (utilisateurSession() == null) {
            return reponseDeconnecte();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            EntityManager em = odm.getEm();
            TTiersPayant tp = em.find(TTiersPayant.class, StringUtils.defaultString(tiersPayantId));
            if (tp == null) {
                return reponseJson(new JSONObject().put("success", "0").put("errors", "Tiers payant introuvable"));
            }
            Integer modelId = null;
            if (StringUtils.isNotBlank(modelIdParam)) {
                modelId = Integer.valueOf(modelIdParam);
                if (em.find(ModelFactureDynamique.class, modelId) == null) {
                    return reponseJson(new JSONObject().put("success", "0").put("errors", "Modèle introuvable"));
                }
            }
            em.getTransaction().begin();
            tp.setModelFactureDynamiqueId(modelId);
            em.merge(tp);
            em.getTransaction().commit();
            return reponseJson(new JSONObject().put("success", "1").put("errors",
                    modelId != null ? "Tiers payant rattaché au modèle" : "Tiers payant détaché du modèle"));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "assignation modele dynamique", e);
            return reponseJson(new JSONObject().put("success", "0").put("errors", "Opération impossible"));
        } finally {
            odm.closeEntityManager();
        }
    }

    /** Edition PDF d'une facture selon le modele dynamique du tiers payant. */
    @GET
    @Path("pdf/{factureId}")
    @Produces("application/pdf")
    public Response pdf(@PathParam("factureId") String factureId) {
        TUser sessionUser = utilisateurSession();
        if (sessionUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        dataManager odm = new dataManager();
        try {
            odm.initEntityManager();
            EntityManager em = odm.getEm();
            TFacture facture = em.find(TFacture.class, factureId);
            if (facture == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            TTiersPayant tiersPayant = em.find(TTiersPayant.class, facture.getStrCUSTOMER());
            ModelFactureDynamique modele = tiersPayant != null && tiersPayant.getModelFactureDynamiqueId() != null
                    ? em.find(ModelFactureDynamique.class, tiersPayant.getModelFactureDynamiqueId()) : null;
            if (modele == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Aucun modèle de facture dynamique pour ce tiers payant").type("text/plain").build();
            }
            byte[] pdf = genererPdf(em, facture, tiersPayant, modele);
            return Response.ok(pdf, "application/pdf").header("Content-Disposition", "inline; filename=Facture_"
                    + StringUtils.defaultString(facture.getStrCODEFACTURE()).replaceAll("[^A-Za-z0-9_-]", "_") + ".pdf")
                    .build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "pdf modele dynamique", e);
            return Response.serverError().entity("Erreur lors de la génération de la facture").type("text/plain")
                    .build();
        } finally {
            odm.closeEntityManager();
        }
    }

    // =============================================================================================
    // Generation PDF (iText) : memes donnees que le bordereau historique (TFactureDetail +
    // TPreenregistrementCompteClientTiersPayent), colonnes/libelles/ordre/tri pilotes par le modele.
    // =============================================================================================

    private static class LigneFacture {
        TFactureDetail detail;
        TPreenregistrementCompteClientTiersPayent dossier;
    }

    private byte[] genererPdf(EntityManager em, TFacture facture, TTiersPayant tiersPayant,
            ModelFactureDynamique modele) throws Exception {
        List<TFactureDetail> details = em
                .createQuery("SELECT t FROM TFactureDetail t WHERE t.lgFACTUREID.lgFACTUREID = ?1",
                        TFactureDetail.class)
                .setParameter(1, facture.getLgFACTUREID()).getResultList();
        List<LigneFacture> lignes = new ArrayList<>();
        for (TFactureDetail d : details) {
            TPreenregistrementCompteClientTiersPayent p = em.find(TPreenregistrementCompteClientTiersPayent.class,
                    d.getStrREF());
            if (p == null) {
                continue;
            }
            LigneFacture l = new LigneFacture();
            l.detail = d;
            l.dossier = p;
            lignes.add(l);
        }
        lignes.sort(comparateur(modeTriEffectif(modele, tiersPayant)));

        List<ModelFactureDynamiqueColonne> colonnes = new ArrayList<>(modele.getColonnes());
        colonnes.sort(Comparator.comparing(ModelFactureDynamiqueColonne::getOrdre,
                Comparator.nullsLast(Comparator.naturalOrder())));

        TOfficine officine = em.find(TOfficine.class, "1");

        Font fontTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontEntete = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font fontCellule = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font fontInfoGras = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font fontPied = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 28, 28, 28, 40);
        PdfWriter.getInstance(document, out);
        document.open();

        if (officine != null) {
            Paragraph institution = new Paragraph(StringUtils.defaultString(officine.getStrNOMABREGE()), fontInfoGras);
            document.add(institution);
            if (StringUtils.isNotBlank(officine.getStrADRESSSEPOSTALE())) {
                document.add(new Paragraph(officine.getStrADRESSSEPOSTALE(), fontInfo));
            }
        }
        Paragraph titre = new Paragraph("FACTURE N° " + StringUtils.defaultString(facture.getStrCODEFACTURE()),
                fontTitre);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(10);
        document.add(titre);
        Paragraph infoTp = new Paragraph(
                (tiersPayant != null ? StringUtils.defaultString(tiersPayant.getStrFULLNAME()) : "") + " — PERIODE DU "
                        + date.formatterShort.format(facture.getDtDEBUTFACTURE()) + " AU "
                        + date.formatterShort.format(facture.getDtFINFACTURE()),
                fontInfoGras);
        infoTp.setAlignment(Element.ALIGN_CENTER);
        infoTp.setSpacingAfter(10);
        document.add(infoTp);

        PdfPTable table = new PdfPTable(colonnes.size());
        table.setWidthPercentage(100);
        for (ModelFactureDynamiqueColonne c : colonnes) {
            PdfPCell cell = new PdfPCell(new Phrase(c.getLibelle(), fontEntete));
            cell.setHorizontalAlignment(estNumerique(c.getChamp()) ? Element.ALIGN_RIGHT : Element.ALIGN_CENTER);
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 230, 230));
            table.addCell(cell);
        }
        Map<String, Long> totaux = new LinkedHashMap<>();
        int numero = 1;
        for (LigneFacture l : lignes) {
            for (ModelFactureDynamiqueColonne c : colonnes) {
                String champ = c.getChamp();
                String valeur = valeurChamp(champ, l, numero);
                PdfPCell cell = new PdfPCell(new Phrase(valeur, fontCellule));
                if (estNumerique(champ)) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totaux.merge(champ, valeurNumerique(champ, l), Long::sum);
                }
                table.addCell(cell);
            }
            numero++;
        }
        // ligne des totaux (uniquement si au moins une colonne numerique est presente)
        boolean auMoinsUneNumerique = colonnes.stream().anyMatch(c -> estNumerique(c.getChamp()));
        if (auMoinsUneNumerique) {
            for (int i = 0; i < colonnes.size(); i++) {
                ModelFactureDynamiqueColonne c = colonnes.get(i);
                String texte = "";
                if (i == 0 && !estNumerique(c.getChamp())) {
                    texte = "TOTAUX";
                } else if (estNumerique(c.getChamp())) {
                    texte = conversion.AmountFormat(totaux.getOrDefault(c.getChamp(), 0L).intValue());
                }
                PdfPCell cell = new PdfPCell(new Phrase(texte, fontEntete));
                cell.setHorizontalAlignment(estNumerique(c.getChamp()) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
                table.addCell(cell);
            }
        }
        document.add(table);

        double montantNet = facture.getDblMONTANTCMDE() != null ? facture.getDblMONTANTCMDE() : 0d;
        Paragraph arrete = new Paragraph(
                "Arrêtée la présente facture à la somme de " + conversion.GetNumberTowords(montantNet).toUpperCase()
                        + " (" + conversion.AmountFormat((int) montantNet) + " FCFA)",
                fontInfoGras);
        arrete.setSpacingBefore(12);
        document.add(arrete);
        document.add(new Paragraph("Nombre de dossiers : " + lignes.size(), fontInfo));

        if (officine != null) {
            StringBuilder pied = new StringBuilder();
            if (officine.getStrREGISTRECOMMERCE() != null) {
                pied.append("RC N° ").append(officine.getStrREGISTRECOMMERCE());
            }
            if (officine.getStrCOMPTECONTRIBUABLE() != null) {
                pied.append(" - CC N° ").append(officine.getStrCOMPTECONTRIBUABLE());
            }
            Paragraph piedParagraphe = new Paragraph(pied.toString(), fontPied);
            piedParagraphe.setSpacingBefore(15);
            document.add(piedParagraphe);
        }

        document.close();
        return out.toByteArray();
    }

    private static String normaliserModeTri(String modeTri) {
        if (ModelFactureDynamique.TRI_ALPHABETIQUE.equalsIgnoreCase(StringUtils.trimToEmpty(modeTri))) {
            return ModelFactureDynamique.TRI_ALPHABETIQUE;
        }
        if (ModelFactureDynamique.TRI_DATE_BON.equalsIgnoreCase(StringUtils.trimToEmpty(modeTri))) {
            return ModelFactureDynamique.TRI_DATE_BON;
        }
        return ModelFactureDynamique.TRI_SELON_TIERS_PAYANT;
    }

    /** Tri effectif : celui du modele, ou celui de la fiche du tiers payant quand le modele suit la fiche. */
    private static String modeTriEffectif(ModelFactureDynamique modele, TTiersPayant tiersPayant) {
        if (!ModelFactureDynamique.TRI_SELON_TIERS_PAYANT.equals(modele.getModeTri())) {
            return modele.getModeTri();
        }
        String triTp = tiersPayant != null ? tiersPayant.getStrMODETRIFACTURE() : null;
        return "DATE_BON".equalsIgnoreCase(StringUtils.trimToEmpty(triTp)) ? ModelFactureDynamique.TRI_DATE_BON
                : ModelFactureDynamique.TRI_ALPHABETIQUE;
    }

    private static Comparator<LigneFacture> comparateur(String modeTri) {
        if (ModelFactureDynamique.TRI_DATE_BON.equals(modeTri)) {
            return Comparator.comparing(l -> l.dossier.getDtCREATED(), Comparator.nullsLast(Comparator.naturalOrder()));
        }
        return Comparator.comparing(ModelFactureDynamiqueRessource::nomCompletNormalise);
    }

    private static String nomCompletNormalise(LigneFacture l) {
        TPreenregistrement pre = l.dossier.getLgPREENREGISTREMENTID();
        String nom = (StringUtils.defaultString(pre.getStrFIRSTNAMECUSTOMER()) + " "
                + StringUtils.defaultString(pre.getStrLASTNAMECUSTOMER())).trim();
        return Normalizer.normalize(nom, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase();
    }

    private static boolean estNumerique(String champ) {
        Object[] def = COLONNES_DISPONIBLES.get(champ);
        return def != null && (Boolean) def[1];
    }

    private static long valeurNumerique(String champ, LigneFacture l) {
        TPreenregistrement pre = l.dossier.getLgPREENREGISTREMENTID();
        switch (champ) {
        case "MONTANT_BRUT":
            return pre.getIntPRICE() != null ? pre.getIntPRICE() : 0L;
        case "REMISE":
            long remise = l.detail.getDblMONTANTREMISE() != null ? l.detail.getDblMONTANTREMISE().longValue() : 0L;
            if (remise == 0 && pre.getIntPRICEREMISE() != null) {
                remise = pre.getIntPRICEREMISE();
            }
            return remise;
        case "PART_CLIENT":
            return pre.getIntCUSTPART() != null ? pre.getIntCUSTPART() : 0L;
        case "PART_TIERS_PAYANT":
            return l.detail.getDblMONTANT() != null ? l.detail.getDblMONTANT().longValue() : 0L;
        default:
            return 0L;
        }
    }

    private static String valeurChamp(String champ, LigneFacture l, int numero) {
        TPreenregistrement pre = l.dossier.getLgPREENREGISTREMENTID();
        switch (champ) {
        case "NUMERO":
            return String.valueOf(numero);
        case "DATE_BON":
            return l.dossier.getDtCREATED() != null ? date.formatterShort.format(l.dossier.getDtCREATED()) : "";
        case "REF_BON":
            return StringUtils.defaultString(l.dossier.getStrREFBON());
        case "NOM_CLIENT":
            return StringUtils.defaultString(pre.getStrFIRSTNAMECUSTOMER());
        case "PRENOM_CLIENT":
            return StringUtils.defaultString(pre.getStrLASTNAMECUSTOMER());
        case "NOM_COMPLET":
            return (StringUtils.defaultString(pre.getStrFIRSTNAMECUSTOMER()) + " "
                    + StringUtils.defaultString(pre.getStrLASTNAMECUSTOMER())).trim();
        case "MATRICULE":
            return StringUtils.defaultString(pre.getStrNUMEROSECURITESOCIAL());
        case "REF_VENTE":
            return StringUtils.defaultString(pre.getStrREF());
        case "TAUX":
            return l.dossier.getIntPERCENT() != null ? String.valueOf(l.dossier.getIntPERCENT()) : "";
        default:
            return conversion.AmountFormat((int) valeurNumerique(champ, l));
        }
    }
}
