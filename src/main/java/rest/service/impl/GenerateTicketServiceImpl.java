/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import bll.common.Parameter;
import bll.preenregistrement.Preenregistrement;
import com.itextpdf.text.pdf.Barcode128;
import commonTasks.dto.ClotureVenteParams;
import commonTasks.dto.Params;
import commonTasks.dto.TicketDTO;
import commonTasks.dto.VenteDetailsDTO;
import dal.MvtTransaction;
import dal.TAyantDroit;
import dal.TCashTransaction;
import dal.TClient;
import dal.TDossierReglement;
import dal.TDossierReglementDetail;
import dal.TEmplacement;
import dal.TImprimante;
import dal.TOfficine;
import dal.TParameters;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClient;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementDetail;
import dal.TTypeMvtCaisse;
import dal.TTypeReglement;
import dal.TUser;
import dal.dataManager;
import dal.enumeration.TypeTransaction;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import org.json.JSONException;
import org.json.JSONObject;
import rest.report.ReportUtil;
import rest.service.GenerateTicketService;
import rest.service.SalesStatsService;
import toolkits.parameters.commonparameter;
import toolkits.utils.Maths;
import toolkits.utils.StringComplexUtils.DataStringManager;
import toolkits.utils.jdom;
import util.Afficheur;

import util.DateConverter;
import util.TicketTemplate;

/**
 *
 * @author Kobena
 */
@Stateless
public class GenerateTicketServiceImpl implements GenerateTicketService {

    private static final Logger LOG = Logger.getLogger(GenerateTicketServiceImpl.class.getName());
    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
//    @EJB
//    CommonService commonService;
    @EJB
    ReportUtil reportUtil;
    @EJB
    SalesStatsService salesStatsService;

    @Override
    public File buildBarecode(String data) {
        try {
            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(data);
            barcode128.setBaseline(10);
            barcode128.setBarHeight(50);
            barcode128.setCodeType(Barcode128.CODE128);
            java.awt.Image img = barcode128.createAwtImage(Color.BLACK, Color.WHITE);
            BufferedImage bi = new BufferedImage(100, 70, BufferedImage.BITMASK);
            Graphics2D gd = bi.createGraphics();
            gd.drawImage(img, 4, 2, null);
            gd.setColor(Color.BLACK);
            gd.drawString(data, 10, 65);
            gd.dispose();
            File f = new File(jdom.barecode_file + "" + data + ".png");

            ImageIO.write(bi, "png", f);
            return f;

        } catch (IOException e) {
            LOG.log(Level.INFO, "Le lien des barcode n'est pas correcte dans le fichier de configuration ----------- {0}", jdom.barecode_file);
            e.printStackTrace(System.err);
            return null;

        }
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String buildLineBarecode(String data) {
        String file = null;
        try {

            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(data);
            barcode128.setBaseline(10);
            barcode128.setBarHeight(50);
            barcode128.setCodeType(Barcode128.CODE128);
            java.awt.Image img = barcode128.createAwtImage(Color.BLACK, Color.WHITE);
            BufferedImage bi = new BufferedImage(100, 70, BufferedImage.BITMASK);
            Graphics2D gd = bi.createGraphics();
            gd.drawImage(img, 4, 2, null);
            gd.setColor(Color.BLACK);
            gd.drawString(data, 10, 65);
            gd.dispose();
            file = jdom.barecode_file + data + ".png";
            File f = new File(file);
            ImageIO.write(bi, "png", f);
//            file = f.getAbsolutePath();
        } catch (IOException ex) {
//            LOG.log(Level.SEVERE, null, ex);
        }
        return file;

    }

    private List<TPreenregistrement> venteLiees(String originalVenteId) {
        try {
            TypedQuery<TPreenregistrement> q = getEntityManager().createQuery("SELECT o FROM TPreenregistrement o WHERE o.lgPARENTID=?1 ORDER BY o.dtCREATED ASC ", TPreenregistrement.class);
            q.setParameter(1, originalVenteId);
            return q.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public JSONObject lunchPrinterForTicket(String id) throws JSONException {
        JSONObject json = new JSONObject();
        int counter = 40, copies = 1, k = 0, page, pageCurrent = 0, diff, counter_constante = 40;
        String title;
        List<String> lstDataFinal = new ArrayList<>();
        List<String> infoClientAvoir = new ArrayList<>();

        try {
            TPreenregistrement oTPreenregistrement = getEntityManager().find(TPreenregistrement.class, id);
            String _id = id;
            MvtTransaction mvtTransaction = findByPkey(_id);
            String fileBarecode = buildLineBarecode(oTPreenregistrement.getStrREFTICKET());
            TEmplacement te = oTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
            boolean voirNumTicket = voirNumeroTicket();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            if (voirNumTicket) {
                title = oTPreenregistrement.getStrREF();
            } else {
                title = oTPreenregistrement.getStrREFTICKET();
            }
            List<String> datas = generateData(oTPreenregistrement);
            List<String> infoSellers = generateDataSeller(oTPreenregistrement);
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTitle("Ticket N° " + title);
            imp.setTypeTicket(DateConverter.TICKET_VENTE);
            imp.setShowCodeBar(true);
            imp.setEmplacement(te);
            imp.setOperation(oTPreenregistrement.getDtUPDATED());
            imp.setIntBegin(0);
            if (oTPreenregistrement.getStrSTATUTVENTE().equalsIgnoreCase(commonparameter.statut_differe) || oTPreenregistrement.getBISAVOIR() == true) {
                infoClientAvoir = generateDataTiersPayant(oTPreenregistrement);
            }
            if (datas.size() <= counter) {
                imp.buildTicket(datas, infoSellers, infoClientAvoir, generateDataSummaryVno(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                imp.printTicketVente(copies);
            } else {
                page = datas.size() / counter;
                while (page != pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infoClientAvoir, Collections.emptyList(), Collections.emptyList(), fileBarecode);
                    imp.printTicketVente(copies);
                    k = counter;
                    diff = datas.size() - counter;
                    if (diff > counter_constante) {
                        counter = counter + counter_constante;
                    } else {
                        counter = counter + diff;
                    }

                    pageCurrent++;
                    lstDataFinal.clear();
                }
                if (page == pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infoClientAvoir, generateDataSummaryVno(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                }
                imp.printTicketVente(copies);

            }
            json.put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }
        return json;
    }

    @Override
    public JSONObject lunchPrinterForTicketVo(String id) throws JSONException {
        JSONObject json = new JSONObject();
        int counter = 40, k = 0, page, pageCurrent = 0, diff, counter_constante = 40;
        String title;
        List<String> lstDataFinal = new ArrayList<>();
        List<String> infotiersPayants;

        try {
            TPreenregistrement oTPreenregistrement = getEntityManager().find(TPreenregistrement.class, id);
            String _id = id;
            MvtTransaction mvtTransaction = findByPkey(_id);
            String fileBarecode = buildLineBarecode(oTPreenregistrement.getStrREFTICKET());
            TEmplacement te = oTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
            boolean voirNumTicket = voirNumeroTicket();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            if (voirNumTicket) {
                title = oTPreenregistrement.getStrREF();
            } else {
                title = oTPreenregistrement.getStrREFTICKET();
            }
            List<String> datas = generateData(oTPreenregistrement);
            List<TPreenregistrementCompteClientTiersPayent> listeVenteTiersPayants = listeVenteTiersPayantsByIdVente(oTPreenregistrement.getLgPREENREGISTREMENTID());
            List<String> infoSellers = generateDataSeller(oTPreenregistrement);
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTypeTicket(commonparameter.str_ACTION_VENTE);
            imp.setShowCodeBar(true);
            imp.setEmplacement(te);
            imp.setOperation(oTPreenregistrement.getDtUPDATED());
            imp.setIntBegin(0);
            infotiersPayants = this.generateDataTiersPayant(oTPreenregistrement, listeVenteTiersPayants);
            title = title + " | Bon N°:: " + oTPreenregistrement.getStrREFBON();
            imp.setTitle("Ticket N° " + title);

            if (datas.size() <= counter) {
                imp.buildTicket(datas, infoSellers, infotiersPayants, generateDataSummaryVo(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                if (oTPreenregistrement.getIntPRICE() < 0) {
                    imp.printTicketVente(1);
                } else {
//                    for (TPreenregistrementCompteClientTiersPayent b : listeVenteTiersPayants) {
//                        imp.printTicketVente(1);
//                    }
                    imp.printTicketVente(1);
                }
            } else {
                page = datas.size() / counter;
                while (page != pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infotiersPayants, Collections.emptyList(), Collections.emptyList(), fileBarecode);
                    if (oTPreenregistrement.getIntPRICE() < 0) {
                        imp.printTicketVente(1);
                    } else {
//                        for (TPreenregistrementCompteClientTiersPayent b : listeVenteTiersPayants) {
//                            imp.printTicketVente(1);
//                        }
                        imp.printTicketVente(1);
                    }

                    k = counter;
                    diff = datas.size() - counter;
                    if (diff > counter_constante) {
                        counter = counter + counter_constante;
                    } else {
                        counter = counter + diff;
                    }

                    pageCurrent++;
                    lstDataFinal.clear();
                }
                if (page == pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infotiersPayants, generateDataSummaryVo(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                }
                if (oTPreenregistrement.getIntPRICE() < 0) {
                    imp.printTicketVente(1);
                } else {
//                    for (TPreenregistrementCompteClientTiersPayent b : listeVenteTiersPayants) {
//                        imp.printTicketVente(1);
//                    }
                    imp.printTicketVente(1);
                }

            }
            json.put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }
        return json;

    }

    @Override
    public List<TPreenregistrementDetail> listeVenteByIdVente(String id) {
        return getEntityManager().createQuery("SELECT o FROM TPreenregistrementDetail o WHERE o.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID=?1 ").setParameter(1, id).getResultList();
    }

    @Override
    public List<TPreenregistrementCompteClientTiersPayent> listeVenteTiersPayantsByIdVente(String id) {

        return getEntityManager().createQuery("SELECT t FROM TPreenregistrementCompteClientTiersPayent t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1   ORDER BY t.intPERCENT DESC ").setParameter(1, id).getResultList();
    }

    @Override
    public List<String> generateDataSeller(TPreenregistrement OTPreenregistrement) {
        List<String> datas = new ArrayList<>();
        try {
            datas.add("Caissier(e):: " + DataStringManager.subStringData(OTPreenregistrement.getLgUSERCAISSIERID().getStrFIRSTNAME(), 0, 1) + "." + OTPreenregistrement.getLgUSERCAISSIERID().getStrLASTNAME() + "   |   " + "Vendeur:: " + DataStringManager.subStringData(OTPreenregistrement.getLgUSERVENDEURID().getStrFIRSTNAME(), 0, 1) + "." + OTPreenregistrement.getLgUSERVENDEURID().getStrLASTNAME());
            if (OTPreenregistrement.getBISCANCEL() || OTPreenregistrement.getIntPRICE() < 0) {
                datas.add("Annulée par :: " + DataStringManager.subStringData(OTPreenregistrement.getLgUSERID().getStrFIRSTNAME(), 0, 1) + "." + OTPreenregistrement.getLgUSERID().getStrLASTNAME());
            }
            if (OTPreenregistrement.getLgNATUREVENTEID().getLgNATUREVENTEID().equalsIgnoreCase(Parameter.KEY_NATURE_VENTE_DEPOT)) {
                TEmplacement OTEmplacement = getEntityManager().find(TEmplacement.class, OTPreenregistrement.getPkBrand());
                datas.add(OTEmplacement != null ? "Dépôt: " + OTEmplacement.getStrDESCRIPTION() : " ");
                datas.add("Client(e):: " + (OTEmplacement != null ? OTEmplacement.getStrFIRSTNAME() + " " + OTEmplacement.getStrLASTNAME() : OTPreenregistrement.getStrFIRSTNAMECUSTOMER() + " " + OTPreenregistrement.getStrLASTNAMECUSTOMER()));

            }
        } catch (Exception e) {

        }
        return datas;
    }

    @Override
    public List<String> generateData(TPreenregistrement OTPreenregistrement) {

        if (OTPreenregistrement.getIntPRICE() < 0) {
            return generateDataVenteSupprime(OTPreenregistrement);
        }
        List<String> datas = new ArrayList<>();
        List<TPreenregistrementDetail> lstTPreenregistrementDetail = listeVenteByIdVente(OTPreenregistrement.getLgPREENREGISTREMENTID());
        lstTPreenregistrementDetail.forEach((OTPreenregistrementDetail) -> {
            datas.add(" " + OTPreenregistrementDetail.getIntQUANTITY() + "; *;" + DataStringManager.subStringData(OTPreenregistrementDetail.getLgFAMILLEID().getStrDESCRIPTION().toUpperCase(), 0, 20) + ";" + DateConverter.amountFormat(OTPreenregistrementDetail.getIntPRICEUNITAIR()) + ";" + DateConverter.amountFormat(OTPreenregistrementDetail.getIntPRICE()));
        });

        datas.add(";;;;------");
        datas.add(";;;;" + DateConverter.amountFormat((OTPreenregistrement.getIntPRICE() >= 0) ? OTPreenregistrement.getIntPRICE() : (-1) * OTPreenregistrement.getIntPRICE()));
        return datas;
    }

    @Override
    public List<String> generateDataVenteSupprime(TPreenregistrement OTPreenregistrement) {
        List<String> datas = new ArrayList<>();
        List<TPreenregistrementDetail> lstTPreenregistrementDetail = listeVenteByIdVente(OTPreenregistrement.getLgPREENREGISTREMENTID());
        lstTPreenregistrementDetail.forEach((OTPreenregistrementDetail) -> {
            datas.add(" " + (-1 * OTPreenregistrementDetail.getIntQUANTITY()) + ";*;" + DataStringManager.subStringData(OTPreenregistrementDetail.getLgFAMILLEID().getStrDESCRIPTION().toUpperCase(), 0, 16) + ";" + DateConverter.amountFormat(OTPreenregistrementDetail.getIntPRICEUNITAIR()) + ";" + DateConverter.amountFormat(OTPreenregistrementDetail.getIntPRICE() * (-1)));
        });
        datas.add(";;;;------");
        datas.add(";;;;" + DateConverter.amountFormat((OTPreenregistrement.getIntPRICE() >= 0) ? OTPreenregistrement.getIntPRICE() : (-1) * OTPreenregistrement.getIntPRICE()));
        return datas;
    }

    @Override
    public List<String> generateDataTiersPayant(TPreenregistrement op) {
        List<String> datas = new ArrayList<>();
        TClient client = op.getClient();
        datas.add("Nom du Client:: " + client.getStrFIRSTNAME() + " " + client.getStrLASTNAME());
        return datas;
    }

    private TClient findClientById(String id) {
        try {
            return getEntityManager().find(TClient.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> generateDataTiersPayant(TPreenregistrement p, List<TPreenregistrementCompteClientTiersPayent> lstT) {
        List<String> datas = new ArrayList<>();
        TClient c = p.getClient();
        String bb = c.getStrFIRSTNAME() + " " + c.getStrLASTNAME();
        TAyantDroit ayantDroit = p.getAyantDroit();
        if (ayantDroit != null) {
            bb = ayantDroit.getStrFIRSTNAME() + " " + ayantDroit.getStrLASTNAME();
        }
        datas.add("Matricule Assuré:: " + c.getStrNUMEROSECURITESOCIAL());
        datas.add("Bénéficiaire:: " + bb);
        int partClt = p.getIntCUSTPART();
        if (partClt < 0) {
            partClt = (-1) * DateConverter.arrondiModuloOfNumber(Math.abs(partClt), 5);
        } else {
            partClt = DateConverter.arrondiModuloOfNumber(partClt, 5);
        }
        datas.add("Part du Client:: " + DateConverter.amountFormat(partClt) + "  CFA");
        for (int i = 0; i < lstT.size(); i++) {
            /* if (p.getLgTYPEVENTEID().getLgTYPEVENTEID().equals(Parameter.VENTE_AVEC_CARNET)) {
                datas.add(lstT.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME() + "   " + lstT.get(i).getIntPERCENT() + "%" + " :: " + DateConverter.amountFormat(p.getIntPRICE() - p.getIntPRICEREMISE()) + "  CFA");
            } else {
                datas.add(lstT.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME() + "   " + lstT.get(i).getIntPERCENT() + "%" + " :: " + DateConverter.amountFormat(lstT.get(i).getIntPRICE()) + "  CFA");
            }*/
            datas.add(lstT.get(i).getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME() + "   " + lstT.get(i).getIntPERCENT() + "%" + " :: " + DateConverter.amountFormat(lstT.get(i).getIntPRICE()) + "  CFA");
        }
        return datas;
    }

    private MvtTransaction findByPkey(String pkey) {
        try {
            TypedQuery<MvtTransaction> query = getEntityManager().createQuery("SELECT o FROM MvtTransaction o WHERE o.pkey=?1 ", MvtTransaction.class);
            query.setMaxResults(1);
            query.setParameter(1, pkey);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> generateDataSummaryVno(TPreenregistrement p) {
        TTypeReglement tTypeReglement = p.getLgREGLEMENTID().getLgMODEREGLEMENTID().getLgTYPEREGLEMENTID();
        String id = p.getLgPREENREGISTREMENTID();
        MvtTransaction mvtTransaction = findByPkey(id);
        List<String> datas = new ArrayList<>();
        if (mvtTransaction.getMontantRemise() > 0) {
            datas.add("* ;(-) " + DateConverter.amountFormat(mvtTransaction.getMontantRemise()) + "; F CFA;1");
        }
        Integer vente_net = mvtTransaction.getMontantNet();
        if (vente_net > 0) {
            vente_net = DateConverter.arrondiModuloOfNumber(vente_net, 5);
        } else {
            vente_net = (-1) * DateConverter.arrondiModuloOfNumber(((-1) * vente_net), 5);
        }
        datas.add("Net à payer: ;     " + DateConverter.amountFormat(vente_net) + "; F CFA;1");
        datas.add("Règlement: ;     " + tTypeReglement.getStrNAME() + "; ;0");

        if (vente_net > 0) {

            datas.add("Montant Versé: ;     " + DateConverter.amountFormat(mvtTransaction.getMontantVerse()) + "; F CFA;0");
            datas.add("Monnaie: ;     " + DateConverter.amountFormat((mvtTransaction.getMontantVerse() - mvtTransaction.getMontantPaye() > 0 ? mvtTransaction.getMontantVerse() - mvtTransaction.getMontantPaye() : 0)) + "; F CFA;0");
        }
        return datas;
    }

    @Override
    public List<String> generateDataSummaryVno(TPreenregistrement p, MvtTransaction mvtTransaction) {

        TTypeReglement tTypeReglement = p.getLgREGLEMENTID().getLgMODEREGLEMENTID().getLgTYPEREGLEMENTID();
        List<String> datas = new ArrayList<>();
        int remise = Math.abs(mvtTransaction.getMontantRemise());
        if (remise > 0) {
            datas.add("* ;(-) " + DateConverter.amountFormat(remise) + "; F CFA;1");
        }
        Integer vente_net = mvtTransaction.getMontantNet();
        if (vente_net > 0) {
            vente_net = DateConverter.arrondiModuloOfNumber(vente_net, 5);
        } else {
            vente_net = (-1) * DateConverter.arrondiModuloOfNumber(((-1) * vente_net), 5);
        }
        datas.add("Net à payer: ;     " + DateConverter.amountFormat(vente_net) + "; F CFA;1");
        datas.add("Règlement: ;     " + tTypeReglement.getStrNAME() + "; ;0");
        if (p.getIntPRICE() > 0) {
            datas.add("Montant Versé: ;     " + DateConverter.amountFormat(Math.abs(mvtTransaction.getMontantVerse())) + "; F CFA;0");
            datas.add("Monnaie: ;     " + DateConverter.amountFormat((Math.abs(mvtTransaction.getMontantVerse()) - vente_net > 0 ? Math.abs(mvtTransaction.getMontantVerse()) - Math.abs(mvtTransaction.getMontantPaye()) : 0)) + "; F CFA;0");

        }
        return datas;
    }

    @Override
    public List<String> generateDataSummaryVo(TPreenregistrement OTPreenregistrement) {
        List<String> datas = new ArrayList<>();
        Optional<TCashTransaction> OTCashTransaction = cashTransactionsByVenteId(OTPreenregistrement.getLgPREENREGISTREMENTID()).stream().findFirst();
        TTypeReglement reglement = OTPreenregistrement.getLgREGLEMENTID().getLgMODEREGLEMENTID().getLgTYPEREGLEMENTID();
        if (OTPreenregistrement.getIntCUSTPART() == 0) {
            if (OTPreenregistrement.getIntPRICEREMISE() > 0) {
                datas.add("* ;(-) " + DateConverter.amountFormat(OTPreenregistrement.getIntPRICEREMISE()) + "; F CFA;1");
            }
            String lgTyvente = OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID();
            OTCashTransaction.ifPresent(c -> {
                if (lgTyvente.equals(Parameter.VENTE_ASSURANCE) || lgTyvente.equals(Parameter.VENTE_AVEC_CARNET)) {
                    datas.add("Vente à terme: ;    " + DateConverter.amountFormat((OTPreenregistrement.getIntPRICE() >= 0) ? c.getIntAMOUNTDEBIT() : (-1) * c.getIntAMOUNTCREDIT()) + "; F CFA;1");
                } else {
                    datas.add("Vente à terme: ;    " + DateConverter.amountFormat((OTPreenregistrement.getIntPRICE() >= 0) ? c.getIntAMOUNTDEBIT() : OTPreenregistrement.getIntPRICE()) + "; F CFA;1");
                }
            });

        } else {
            Integer vente_net = OTPreenregistrement.getIntCUSTPART();
            if (vente_net >= 0) {
                vente_net = DateConverter.arrondiModuloOfNumber(vente_net, 5);
            } else {
                vente_net = (-1) * DateConverter.arrondiModuloOfNumber((-1) * vente_net, 5);
            }

            if (OTPreenregistrement.getIntPRICEREMISE() > 0) {
                datas.add("* ;(-) " + DateConverter.amountFormat(OTPreenregistrement.getIntPRICEREMISE()) + "; F CFA;1");
            }
            datas.add("Net à payer: ;     " + DateConverter.amountFormat(Maths.arrondiModuloOfNumber((vente_net - OTPreenregistrement.getIntPRICEREMISE()), 5)) + "; F CFA;1");
            datas.add("Règlement: ;     " + reglement.getStrNAME() + "; ;0");

            if (OTPreenregistrement.getIntPRICE() >= 0) {
                OTCashTransaction.ifPresent(c -> {
                    datas.add("Montant Versé: ;     " + DateConverter.amountFormat(Maths.arrondiModuloOfNumber(c.getIntAMOUNTRECU(), 5)) + "; F CFA;0");
                    final Integer change = c.getIntAMOUNTRECU() - (DateConverter.arrondiModuloOfNumber(OTPreenregistrement.getIntCUSTPART(), 5) - OTPreenregistrement.getIntPRICEREMISE());
                    datas.add("Monnaie: ;     " + DateConverter.amountFormat((change >= 0 ? change : 0)) + "; F CFA;0");
                });

            }
        }
        return datas;
    }

    private List<TCashTransaction> cashTransactionsByVenteId(String id) {
        TypedQuery<TCashTransaction> query = getEntityManager().createQuery("SELECT o FROM TCashTransaction o WHERE o.strRESSOURCEREF=?1 ", TCashTransaction.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    @Override
    public List<String> generateCommentaire(TPreenregistrement p, MvtTransaction mvtTransaction) {
        TOfficine officine = findOfficine();
        List<String> datas = new ArrayList<>();
        if (p.getBISAVOIR()) {
            List<TPreenregistrementDetail> _lstPreenregistrementDetail = listeVenteByIdVente(p.getLgPREENREGISTREMENTID());
            List<TPreenregistrementDetail> lstPreenregistrementDetail = _lstPreenregistrementDetail.stream().filter(v -> v.getBISAVOIR()).collect(Collectors.toList());
            Integer int_AMOUNT_ACCOMPTE = lstPreenregistrementDetail.stream().map((OTPreenregistrementDetail) -> OTPreenregistrementDetail.getIntAVOIR() * OTPreenregistrementDetail.getIntPRICEUNITAIR()).reduce(0, Integer::sum);
            datas.add(" ;0");
            datas.add("ACOMPTE: " + DateConverter.amountFormat(int_AMOUNT_ACCOMPTE, ' ') + " F CFA;1");
            datas.add(" ;0");
        }

        if (p.getStrSTATUTVENTE().equalsIgnoreCase(commonparameter.statut_differe) && p.getIntPRICE() > 0) {
            datas.add(" ;0");
            datas.add("MONTANT RESTANT: " + DateConverter.amountFormat(Maths.arrondiModuloOfNumber(mvtTransaction.getMontantRestant(), 5), ' ') + " F CFA;1");
            datas.add(" ;0");
        }

        if (!officine.getStrCOMMENTAIRE1().equals("") && officine.getStrCOMMENTAIRE1() != null) {
            datas.add(officine.getStrCOMMENTAIRE1() + ";0");
        }
        if (!officine.getStrCOMMENTAIRE2().equals("") && officine.getStrCOMMENTAIRE2() != null) {
            datas.add(officine.getStrCOMMENTAIRE2() + ";0");
        }

        return datas;

    }

    public TPreenregistrementCompteClient getTPreenregistrementCompteClient(String lg_PREENREGISTREMENT_ID) {
        TPreenregistrementCompteClient OTPreenregistrementCompteClient = null;
        try {
            OTPreenregistrementCompteClient = (TPreenregistrementCompteClient) getEntityManager().createQuery("SELECT t FROM TPreenregistrementCompteClient t WHERE t.lgPREENREGISTREMENTID.lgPREENREGISTREMENTID = ?1")
                    .setParameter(1, lg_PREENREGISTREMENT_ID).getSingleResult();

        } catch (Exception e) {
//            e.printStackTrace();
        }
        return OTPreenregistrementCompteClient;
    }

    private List<String> generateCommentaire0(TPreenregistrement p, MvtTransaction mvtTransaction) {

        List<String> datas = new ArrayList<>();
        if (p.getBISAVOIR()) {
            List<TPreenregistrementDetail> _lstPreenregistrementDetail = listeVenteByIdVente(p.getLgPREENREGISTREMENTID());
            List<TPreenregistrementDetail> lstPreenregistrementDetail = _lstPreenregistrementDetail.stream().filter(v -> v.getBISAVOIR()).collect(Collectors.toList());
            Integer int_AMOUNT_ACCOMPTE = lstPreenregistrementDetail.stream().map((OTPreenregistrementDetail) -> OTPreenregistrementDetail.getIntAVOIR() * OTPreenregistrementDetail.getIntPRICEUNITAIR()).reduce(0, Integer::sum);
            datas.add(" ;0");
            datas.add("ACOMPTE: " + DateConverter.amountFormat(int_AMOUNT_ACCOMPTE, ' ') + " F CFA;1");
            datas.add(" ;0");
        }

        if (p.getStrSTATUTVENTE().equalsIgnoreCase(commonparameter.statut_differe) && p.getIntPRICE() > 0) {
            datas.add(" ;0");
            datas.add("MONTANT RESTANT: " + DateConverter.amountFormat(mvtTransaction.getMontantRestant(), ' ') + " F CFA;1");
            datas.add(" ;0");
        }

        return datas;

    }

    @Override
    public JSONObject lunchPrinterForTicket(ClotureVenteParams clotureVenteParams) throws JSONException {
        JSONObject json = new JSONObject();
        int counter = 40, copies = 1, k = 0, page, pageCurrent = 0, diff, counter_constante = 40;
        String title;
        List<String> lstDataFinal = new ArrayList<>();
        List<String> infoClientAvoir = new ArrayList<>();
        try {
            TPreenregistrement oTPreenregistrement = getEntityManager().find(TPreenregistrement.class, clotureVenteParams.getVenteId());
            String _id = oTPreenregistrement.getLgPREENREGISTREMENTID();
            MvtTransaction mvtTransaction = findByPkey(_id);
            String fileBarecode = buildLineBarecode(oTPreenregistrement.getStrREFTICKET());
            TEmplacement te = oTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
            boolean voirNumTicket = voirNumeroTicket();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            if (voirNumTicket) {
                title = oTPreenregistrement.getStrREF();
            } else {
                title = oTPreenregistrement.getStrREFTICKET();
            }
            List<String> datas = generateData(oTPreenregistrement);
            List<String> infoSellers = generateDataSeller(oTPreenregistrement);
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTitle("Ticket N° " + title);
            imp.setTypeTicket(commonparameter.str_ACTION_VENTE);
            imp.setShowCodeBar(true);
            imp.setEmplacement(te);
            imp.setOperation(oTPreenregistrement.getDtUPDATED());
            imp.setIntBegin(0);
            List<String> generateDataSummaryVno = generateDataSummaryVno(oTPreenregistrement, mvtTransaction);
            List<String> generateCommentaire = generateCommentaire(oTPreenregistrement, mvtTransaction);
            if (oTPreenregistrement.getStrSTATUTVENTE().equalsIgnoreCase(commonparameter.statut_differe) || oTPreenregistrement.getBISAVOIR() == true) {
                infoClientAvoir = generateDataTiersPayant(oTPreenregistrement);
            }
            if (datas.size() <= counter) {
                imp.buildTicket(datas, infoSellers, infoClientAvoir, generateDataSummaryVno, generateCommentaire, fileBarecode);
                imp.printTicketVente(copies);
            } else {
                page = datas.size() / counter;
                while (page != pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infoClientAvoir, Collections.emptyList(), Collections.emptyList(), fileBarecode);
                    imp.printTicketVente(copies);
                    k = counter;
                    diff = datas.size() - counter;
                    if (diff > counter_constante) {
                        counter = counter + counter_constante;
                    } else {
                        counter = counter + diff;
                    }

                    pageCurrent++;
                    lstDataFinal.clear();
                }
                if (page == pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infoClientAvoir, generateDataSummaryVno, generateCommentaire, fileBarecode);
                }
                imp.printTicketVente(copies);

            }
            json.put("success", true);
            afficheurWellComeMessage();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }
        return json;
    }

    private boolean printUniqueTicket() {
        try {
            TParameters p = getEntityManager().find(TParameters.class, DateConverter.KEY_NOMBRE_TICKETS_VNO);
            return Integer.valueOf(p.getStrVALUE()).compareTo(0) == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void print(ImpressionServiceImpl imp, TPreenregistrement oTPreenregistrement,
            List<TPreenregistrementCompteClientTiersPayent> listeVenteTiersPayants,
            boolean printUniqueTicket
    ) throws PrinterException {
        if (oTPreenregistrement.getIntPRICE() < 0) {
            imp.printTicketVente(1);
        } else {
            if (!printUniqueTicket) {
                for (TPreenregistrementCompteClientTiersPayent b : listeVenteTiersPayants) {
                    imp.printTicketVente(1);
                }
            }

            imp.printTicketVente(1);
        }
    }

    @Override
    public JSONObject lunchPrinterForTicketVo(ClotureVenteParams clotureVenteParams) throws JSONException {
        JSONObject json = new JSONObject();
        int counter = 40, k = 0, page, pageCurrent = 0, diff, counter_constante = 40;
        String title;
        List<String> lstDataFinal = new ArrayList<>();
        List<String> infotiersPayants;

        try {
            TPreenregistrement oTPreenregistrement = getEntityManager().find(TPreenregistrement.class, clotureVenteParams.getVenteId());
            String _id = clotureVenteParams.getVenteId();
            MvtTransaction mvtTransaction = findByPkey(_id);
            String fileBarecode = buildLineBarecode(oTPreenregistrement.getStrREFTICKET());
            TEmplacement te = oTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
            boolean voirNumTicket = voirNumeroTicket();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            if (voirNumTicket) {
                title = oTPreenregistrement.getStrREF();
            } else {
                title = oTPreenregistrement.getStrREFTICKET();
            }
            List<String> datas = generateData(oTPreenregistrement);
            List<TPreenregistrementCompteClientTiersPayent> listeVenteTiersPayants = listeVenteTiersPayantsByIdVente(oTPreenregistrement.getLgPREENREGISTREMENTID());
            List<String> infoSellers = generateDataSeller(oTPreenregistrement);
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTypeTicket(commonparameter.str_ACTION_VENTE);
            imp.setShowCodeBar(true);
            imp.setEmplacement(te);
            imp.setOperation(oTPreenregistrement.getDtUPDATED());
            imp.setIntBegin(0);
            infotiersPayants = this.generateDataTiersPayant(oTPreenregistrement, listeVenteTiersPayants);
            title = title + " | Bon N°:: " + oTPreenregistrement.getStrREFBON();
            imp.setTitle("Ticket N° " + title);
            List<String> generateDataSummarys = generateDataSummaryVo(oTPreenregistrement, clotureVenteParams);
            List<String> commentaires = generateCommentaire(oTPreenregistrement, mvtTransaction);
            if (datas.size() <= counter) {
                imp.buildTicket(datas, infoSellers, infotiersPayants, generateDataSummarys, commentaires, fileBarecode);

                print(imp, oTPreenregistrement, listeVenteTiersPayants, printUniqueTicket());
                /* 
                if (oTPreenregistrement.getIntPRICE() < 0) {
                    imp.printTicketVente(1);
                } else {

                    for (TPreenregistrementCompteClientTiersPayent b : listeVenteTiersPayants) {
                        imp.printTicketVente(1);
                    }
                    imp.printTicketVente(1);
                }*/

            } else {
                page = datas.size() / counter;
                while (page != pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infotiersPayants, Collections.emptyList(), Collections.emptyList(), fileBarecode);
                    print(imp, oTPreenregistrement, listeVenteTiersPayants, printUniqueTicket());

                    k = counter;
                    diff = datas.size() - counter;
                    if (diff > counter_constante) {
                        counter = counter + counter_constante;
                    } else {
                        counter = counter + diff;
                    }

                    pageCurrent++;
                    lstDataFinal.clear();
                }
                if (page == pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infotiersPayants, generateDataSummarys, commentaires, fileBarecode);
                }
                print(imp, oTPreenregistrement, listeVenteTiersPayants, printUniqueTicket());

            }
            json.put("success", true);
            afficheurWellComeMessage();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }
        return json;

    }

    @Override
    public List<String> generateDataSummaryVo(TPreenregistrement OTPreenregistrement, ClotureVenteParams clotureVenteParams) {
        List<String> datas = new ArrayList<>();
        TTypeReglement reglement = OTPreenregistrement.getLgREGLEMENTID().getLgMODEREGLEMENTID().getLgTYPEREGLEMENTID();
        if (OTPreenregistrement.getIntCUSTPART() == 0) {
            if (OTPreenregistrement.getIntPRICEREMISE() > 0) {
                datas.add("* ;(-) " + DateConverter.amountFormat(OTPreenregistrement.getIntPRICEREMISE()) + "; F CFA;1");
            }
            String lgTyvente = OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID();

            if (lgTyvente.equals(Parameter.VENTE_ASSURANCE) || lgTyvente.equals(Parameter.VENTE_AVEC_CARNET)) {
                datas.add("Vente à terme: ;    " + DateConverter.amountFormat(clotureVenteParams.getPartTP()) + "; F CFA;1");
            } else {
                datas.add("Vente à terme: ;    " + DateConverter.amountFormat(clotureVenteParams.getTotalRecap()) + "; F CFA;1");
            }

        } else {
            Integer vente_net = OTPreenregistrement.getIntCUSTPART();
            if (vente_net > 0) {
                vente_net = DateConverter.arrondiModuloOfNumber(OTPreenregistrement.getIntCUSTPART(), 5);
            } else {
                vente_net = (-1) * DateConverter.arrondiModuloOfNumber(Math.abs(OTPreenregistrement.getIntCUSTPART()), 5);
            }

            if (OTPreenregistrement.getIntPRICEREMISE() > 0) {
                datas.add("* ;(-) " + DateConverter.amountFormat(OTPreenregistrement.getIntPRICEREMISE()) + "; F CFA;1");
            }
            datas.add("Net à payer: ;     " + DateConverter.amountFormat(Maths.arrondiModuloOfNumber((vente_net - OTPreenregistrement.getIntPRICEREMISE()), 5)) + "; F CFA;1");
            datas.add("Règlement: ;     " + reglement.getStrNAME() + "; ;0");

            if (OTPreenregistrement.getIntPRICE() >= 0) {

                datas.add("Montant Versé: ;     " + DateConverter.amountFormat(Maths.arrondiModuloOfNumber(clotureVenteParams.getMontantRecu(), 5)) + "; F CFA;0");
                final Integer change = clotureVenteParams.getMontantRecu() - (DateConverter.arrondiModuloOfNumber(OTPreenregistrement.getIntCUSTPART(), 5) - OTPreenregistrement.getIntPRICEREMISE());
                datas.add("Monnaie: ;     " + DateConverter.amountFormat((change >= 0 ? change : 0)) + "; F CFA;0");

            }
        }
        return datas;
    }

    public List<String> generateDataSummaryVo(TPreenregistrement OTPreenregistrement, MvtTransaction clotureVenteParams) {
        List<String> datas = new ArrayList<>();
        TTypeReglement reglement = OTPreenregistrement.getLgREGLEMENTID().getLgMODEREGLEMENTID().getLgTYPEREGLEMENTID();
        int remise = clotureVenteParams.getMontantRemise();

        if (OTPreenregistrement.getIntCUSTPART() == 0) {

            remise = Math.abs(remise);
            if (remise > 0) {
                datas.add("* ;(-) " + DateConverter.amountFormat(remise) + "; F CFA;1");
            }
            String lgTyvente = OTPreenregistrement.getLgTYPEVENTEID().getLgTYPEVENTEID();

            if (lgTyvente.equals(Parameter.VENTE_ASSURANCE) || lgTyvente.equals(Parameter.VENTE_AVEC_CARNET)) {

                datas.add("Vente à terme: ;    " + DateConverter.amountFormat(clotureVenteParams.getMontantCredit()) + "; F CFA;1");
            } else {
                datas.add("Vente à terme: ;    " + DateConverter.amountFormat(clotureVenteParams.getMontantNet()) + "; F CFA;1");
            }

        } else {
            Integer vente_net = OTPreenregistrement.getIntCUSTPART();
            if (remise > 0) {
                datas.add("* ;(-) " + DateConverter.amountFormat(remise) + "; F CFA;1");
            }
            if (vente_net >= 0) {
                vente_net = DateConverter.arrondiModuloOfNumber(vente_net, 5);
            } else {
                vente_net = (-1) * DateConverter.arrondiModuloOfNumber((-1) * vente_net, 5);
            }
            datas.add("Net à payer: ;     " + DateConverter.amountFormat(Maths.arrondiModuloOfNumber((vente_net - remise), 5)) + "; F CFA;1");
            datas.add("Règlement: ;     " + reglement.getStrNAME() + "; ;0");

            if (OTPreenregistrement.getIntPRICE() >= 0) {

                datas.add("Montant Versé: ;     " + DateConverter.amountFormat(Maths.arrondiModuloOfNumber(Math.abs(clotureVenteParams.getMontantVerse()), 5)) + "; F CFA;0");
                final Integer change = Math.abs(clotureVenteParams.getMontantVerse()) - Math.abs(clotureVenteParams.getMontantPaye());
                datas.add("Monnaie: ;     " + DateConverter.amountFormat((change >= 0 ? change : 0)) + "; F CFA;0");

            }
        }
        return datas;
    }

    @Override
    public JSONObject printerForTicket(String p, TUser user) throws JSONException {
        dataManager manager = new dataManager();
        manager.initEntityManager();
        TPreenregistrement tp = manager.getEm().find(TPreenregistrement.class, p);
        File f = buildBarecode(tp.getStrREFTICKET());
        Preenregistrement preenregistrement = new Preenregistrement(manager, user);
        try {
            preenregistrement.lunchPrinterForTicket(tp, (f != null) ? f.getAbsolutePath() : "");
        } catch (Exception e) {
        }
        return new JSONObject().put("success", true);
    }

    List<MvtTransaction> ticketZData(Params params) {
        try {
            TypedQuery<MvtTransaction> q;
            if (params.getDescription().equals("ALL")) {
                q = getEntityManager().createQuery("SELECT o FROM MvtTransaction o WHERE o.mvtDate =:dtStart AND o.checked=TRUE AND o.magasin.lgEMPLACEMENTID=:empl AND  o.typeTransaction IN :typetransac", MvtTransaction.class);
                q.setParameter("typetransac", EnumSet.of(TypeTransaction.VENTE_COMPTANT, TypeTransaction.VENTE_CREDIT, TypeTransaction.SORTIE, TypeTransaction.ENTREE));
            } else {
                q = getEntityManager().createQuery("SELECT o FROM MvtTransaction o WHERE o.mvtDate =:dtStart AND o.checked=TRUE AND o.magasin.lgEMPLACEMENTID=:empl AND  o.typeTransaction IN :typetransac", MvtTransaction.class);
                q.setParameter("typetransac", EnumSet.of(TypeTransaction.VENTE_COMPTANT, TypeTransaction.VENTE_CREDIT));
            }
            q.setParameter("empl", params.getOperateur().getLgEMPLACEMENTID().getLgEMPLACEMENTID());
            q.setParameter("dtStart", LocalDate.parse(params.getDtStart(), DateTimeFormatter.ISO_DATE));
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    List<String> generateTicketZForPrintMVT(Params params) {
        Map<TUser, List<MvtTransaction>> mapuser = ticketZData(params).parallelStream().collect(Collectors.groupingBy(MvtTransaction::getCaisse));
        List<String> lstData = new ArrayList<>();
        LongAdder _totalEsp = new LongAdder();
        LongAdder _totalCredit = new LongAdder();
        LongAdder _totalCheque = new LongAdder();
        LongAdder _totalVirement = new LongAdder();
        LongAdder _totalCB = new LongAdder();
        if (mapuser.isEmpty()) {
            return lstData;
        }
        mapuser.forEach((k, v) -> {
            lstData.add("RECAPITULATIF DE CAISSE: " + k.getStrFIRSTNAME().substring(0, 1).toUpperCase() + " " + k.getStrLASTNAME() + " ; ; ; ;1;0");
            lstData.add(" ; ; ; ; ; ;");
            LongAdder utotalEsp = new LongAdder();
            LongAdder utotalCredit = new LongAdder();
            LongAdder utotalCheque = new LongAdder();
            LongAdder utotalVirement = new LongAdder();
            LongAdder utotalCB = new LongAdder();

            LongAdder rtotalEsp = new LongAdder();
            LongAdder rtotalCheque = new LongAdder();
            LongAdder rtotalVirement = new LongAdder();
            LongAdder rtotalCB = new LongAdder();
            LongAdder etotalEsp = new LongAdder();
            LongAdder etotalCheque = new LongAdder();
            LongAdder etotalVirement = new LongAdder();
            LongAdder etotalCB = new LongAdder();
            LongAdder stotalEsp = new LongAdder();
            LongAdder stotalCheque = new LongAdder();
            LongAdder stotalVirement = new LongAdder();
            LongAdder stotalCB = new LongAdder();
            LongAdder retotalEsp = new LongAdder();
            LongAdder rretotalCheque = new LongAdder();
            LongAdder rretotalVirement = new LongAdder();
            LongAdder retotalCB = new LongAdder();
            Map<TypeTransaction, List<MvtTransaction>> mapTypeRegle = v.parallelStream().collect(Collectors.groupingBy(MvtTransaction::getTypeTransaction));
            mapTypeRegle.forEach((t, r) -> {

                switch (t) {
                    case VENTE_COMPTANT:
                        r.forEach(b -> {
                            switch (b.getReglement().getLgTYPEREGLEMENTID()) {
                                case DateConverter.MODE_ESP:
                                    utotalEsp.add(b.getMontantRegle());
                                    rtotalEsp.add(b.getMontantRegle());
                                    utotalCredit.add(b.getMontantRestant());
                                    break;
                                case DateConverter.MODE_VIREMENT:
                                    utotalVirement.add(b.getMontantRegle());
                                    rtotalVirement.add(b.getMontantRegle());
                                    break;
                                case DateConverter.MODE_CHEQUE:
                                    utotalCheque.add(b.getMontantRegle());
                                    rtotalCheque.add(b.getMontantRegle());
                                    break;
                                case DateConverter.MODE_CB:
                                    utotalCB.add(b.getMontantRegle());
                                    rtotalCB.add(b.getMontantRegle());
                                    break;
                                default:
                                    break;

                            }

                        });

                        break;
                    case VENTE_CREDIT:
                        r.forEach(b -> {
                            utotalCredit.add(b.getMontantCredit());
                            switch (b.getReglement().getLgTYPEREGLEMENTID()) {
                                case DateConverter.MODE_ESP:
                                    utotalEsp.add(b.getMontantRegle());
                                    utotalCredit.add(b.getMontantRestant());
                                    rtotalEsp.add(b.getMontantRegle());
                                    break;
                                case DateConverter.MODE_VIREMENT:
                                    utotalVirement.add(b.getMontantRegle());
                                    rtotalVirement.add(b.getMontantRegle());
                                    break;
                                case DateConverter.MODE_CHEQUE:
                                    utotalCheque.add(b.getMontantRegle());
                                    rtotalCheque.add(b.getMontantRegle());
                                    break;
                                case DateConverter.MODE_CB:
                                    utotalCB.add(b.getMontantRegle());
                                    rtotalCB.add(b.getMontantRegle());
                                    break;
                                default:
                                    break;

                            }

                        });
                        break;
                    case ENTREE:
                        r.forEach(b -> {
                            TTypeMvtCaisse mvtCaisse = b.gettTypeMvtCaisse();
                            switch (b.getReglement().getLgTYPEREGLEMENTID()) {

                                case DateConverter.MODE_ESP:
                                    utotalEsp.add(b.getMontant());
                                    if (mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_TP) || mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_DIFF)) {
                                        retotalEsp.add(b.getMontant());
                                    } else {
                                        etotalEsp.add(b.getMontant());
                                    }

                                    break;
                                case DateConverter.MODE_VIREMENT:
                                    utotalVirement.add(b.getMontant());
                                    if (mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_TP) || mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_DIFF)) {
                                        rretotalVirement.add(b.getMontant());
                                    } else {
                                        etotalVirement.add(b.getMontant());
                                    }

                                    break;
                                case DateConverter.MODE_CHEQUE:
                                    utotalCheque.add(b.getMontant());
                                    if (mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_TP) || mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_DIFF)) {
                                        rretotalCheque.add(b.getMontant());
                                    } else {
                                        etotalCheque.add(b.getMontant());
                                    }

                                    break;
                                case DateConverter.MODE_CB:
                                    utotalCB.add(b.getMontant());
                                    if (mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_TP) || mvtCaisse.getLgTYPEMVTCAISSEID().equals(DateConverter.MVT_REGLE_DIFF)) {
                                        retotalCB.add(b.getMontant());
                                    } else {
                                        etotalCB.add(b.getMontant());
                                    }

                                    break;
                                default:
                                    break;

                            }

                        });
                        break;
                    case SORTIE:
                        r.forEach(b -> {
                            switch (b.getReglement().getLgTYPEREGLEMENTID()) {
                                case DateConverter.MODE_ESP:
                                    utotalEsp.add(b.getMontant());
                                    stotalEsp.add(b.getMontant());
                                    break;
                                case DateConverter.MODE_VIREMENT:
                                    utotalVirement.add(b.getMontant());
                                    stotalVirement.add(b.getMontant());
                                    break;
                                case DateConverter.MODE_CHEQUE:
                                    utotalCheque.add(b.getMontant());
                                    stotalCheque.add(b.getMontant());
                                    break;
                                case DateConverter.MODE_CB:
                                    utotalCB.add(b.getMontant());
                                    stotalCB.add(b.getMontant());
                                    break;
                                default:
                                    break;

                            }

                        });
                        break;
                    default:
                        break;
                }

            });

            Integer usp = rtotalEsp.intValue();
            Integer ucredit = utotalCredit.intValue();
            Integer uCheque = rtotalCheque.intValue();
            Integer entreeEsp = etotalEsp.intValue();
            Integer sEsp = stotalEsp.intValue();
            Integer reEsp = retotalEsp.intValue();
            Integer cbv = rtotalCB.intValue();
            Integer virv = rtotalVirement.intValue();
            Integer entreCh = etotalCheque.intValue();
            Integer entreCB = etotalCB.intValue();
            Integer entreVir = etotalVirement.intValue();
            Integer teglech = rretotalCheque.intValue();
            Integer teglcb = retotalCB.intValue();
            Integer teglVir = rretotalVirement.intValue();

            _totalEsp.add(utotalEsp.intValue());
            _totalCredit.add(ucredit);
            _totalCheque.add(utotalCheque.intValue());
            _totalCB.add(utotalCB.intValue());
            _totalVirement.add(utotalVirement.intValue());

            if (usp.compareTo(0) != 0) {
                lstData.add("Espèce(vno/vo):" + ";" + DateConverter.amountFormat(usp) + "; F CFA;;0;1");
            }
            if (ucredit.compareTo(0) != 0) {
                lstData.add("Crédit(vno/vo):" + ";" + DateConverter.amountFormat(ucredit) + "; F CFA;;0;1");
            }
            if (entreeEsp.compareTo(0) != 0) {
                lstData.add("Espèce Entrée:" + ";" + DateConverter.amountFormat(entreeEsp) + "; F CFA;;0;1");
            }
            if (reEsp.compareTo(0) != 0) {
                lstData.add("Espèce Regl:" + ";" + DateConverter.amountFormat(reEsp) + "; F CFA;;0;1");
            }
            if (sEsp.compareTo(0) != 0) {
                lstData.add("Espèce Sortie:" + ";" + DateConverter.amountFormat(sEsp) + "; F CFA;;0;1");
            }
            lstData.add("Total espèce: ;" + DateConverter.amountFormat(utotalEsp.intValue()) + ";F CFA;;1;1");
            if (uCheque.compareTo(0) != 0) {
                lstData.add("Total Ch (vno/vo): ;" + DateConverter.amountFormat(uCheque) + ";F CFA;;1;1");
            }
            if (cbv.compareTo(0) != 0) {
                lstData.add("Total CB (vno/vo): ;" + DateConverter.amountFormat(cbv) + ";F CFA;;1;1");
            }
            if (virv.compareTo(0) != 0) {
                lstData.add("Total Vir. (vno/vo): ;" + DateConverter.amountFormat(virv) + ";F CFA;;1;1");
            }
            if (entreCh.compareTo(0) != 0) {
                lstData.add("Total Entrée Chèque : ;" + DateConverter.amountFormat(entreCh) + ";F CFA;;1;1");
            }
            if (entreCB.compareTo(0) != 0) {
                lstData.add("Total Entrée CB : ;" + DateConverter.amountFormat(entreCB) + ";F CFA;;1;1");
            }
            if (entreVir.compareTo(0) != 0) {
                lstData.add("Total entrée.Vir: ;" + DateConverter.amountFormat(entreVir) + ";F CFA;;1;1");
            }
            if (teglech.compareTo(0) != 0) {
                lstData.add("Total Regl Ch: ;" + DateConverter.amountFormat(teglech) + ";F CFA;;1;1");
            }
            if (teglcb.compareTo(0) != 0) {
                lstData.add("Total Regl CB: ;" + DateConverter.amountFormat(teglcb) + ";F CFA;;1;1");
            }
            if (teglVir.compareTo(0) != 0) {
                lstData.add("Total Regl Vir: ;" + DateConverter.amountFormat(teglVir) + ";F CFA;;1;1");
            }
            Integer sortieChe = stotalCheque.intValue();
            Integer sortieCb = stotalCB.intValue();
            Integer sortieVir = stotalVirement.intValue();
            if (sortieChe.compareTo(0) != 0) {
                lstData.add("Total Sortie Ch: ;" + DateConverter.amountFormat(sortieChe) + ";F CFA;;1;1");
            }
            if (sortieCb.compareTo(0) != 0) {
                lstData.add("Total Sortie CB: ;" + DateConverter.amountFormat(sortieCb) + ";F CFA;;1;1");
            }
            if (sortieVir.compareTo(0) != 0) {
                lstData.add("Total Sortie Vir: ;" + DateConverter.amountFormat(sortieVir) + ";F CFA;;1;1");
            }
            lstData.add(" ; ; ; ; ; ;");
        });

        int _tes = _totalEsp.intValue();
        if (_tes != 0) {
            // lstData.add("TOTAL GENERAL ESP: ; " + DateConverter.amountFormat(_tes) + "; F CFA;;1;1;G");
            lstData.add("TOTAL ESP: ; " + DateConverter.amountFormat(_tes) + "; F CFA;;1;1;G");
        }
        int cr = _totalCredit.intValue();
        if (cr != 0) {
//             lstData.add("TOTAL GENERAL (VNO/VO): ;" + DateConverter.amountFormat(cr) + "; F CFA;;1;1;G");
            lstData.add("TOTAL (VNO/VO): ;" + DateConverter.amountFormat(cr) + "; F CFA;;1;1;G");
        }
        int ch = _totalCheque.intValue();
        if (ch != 0) {
            lstData.add("TOTAL CH: ;" + DateConverter.amountFormat(ch) + "; F CFA;;1;1;G");
        }
        int cb = _totalCB.intValue();
        if (cb != 0) {
            lstData.add("TOTAL CB: ; " + DateConverter.amountFormat(cb) + "; F CFA;;1;1;G");
        }
        int vr = _totalVirement.intValue();
        if (vr != 0) {
            lstData.add("TOTAL VIR: ; " + DateConverter.amountFormat(vr) + "; F CFA;;1;1;G");
        }

        return lstData;
    }

    @Override
    public JSONObject ticketZ(Params params) throws JSONException {
        JSONObject json = new JSONObject();
        try {

            List<String> datas = generateTicketZForPrintMVT(params);
            if (datas.isEmpty()) {
                return json.put("success", false).put("msg", "Aucune donnée trouvée . Veuillez choisir une autre option");
            }
            ImpressionServiceImpl ODriverPrinter = new ImpressionServiceImpl();
            TEmplacement emplacement = params.getOperateur().getLgEMPLACEMENTID();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            ODriverPrinter.setEmplacement(emplacement);
            ODriverPrinter.setTypeTicket(DateConverter.TICKET_Z);
            ODriverPrinter.setDatas(datas);
            ODriverPrinter.setOperation(new Date());
            ODriverPrinter.setOperationLocalTime(LocalDateTime.now());
            ODriverPrinter.setSubtotal(new ArrayList<>());
            ODriverPrinter.setInfoTiersPayants(new ArrayList<>());
            String dtTitle = LocalDate.parse(params.getDtStart()).format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
            String title = "TICKET Z DU " + dtTitle + "  A  " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            ODriverPrinter.setTitle(title);
            ODriverPrinter.setInfoSellers(new ArrayList<>());
            ODriverPrinter.setCommentaires(new ArrayList<>());
            ODriverPrinter.setShowCodeBar(true);
            ODriverPrinter.setOTImprimante(imprimante);
            ODriverPrinter.setOfficine(officine);
            ODriverPrinter.setService(printService);
            ODriverPrinter.setCodeBar(this.buildLineBarecode(DateConverter.getShortId(10)));
            ODriverPrinter.printTicketVente(1);
            return json.put("success", true).put("msg", "Opération effectuée ");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return json.put("success", false).put("msg", "Erreur du serveur");

        }
    }

    @Override
    public JSONObject ticketReglementDiffere(String idDossier) throws JSONException {
        JSONObject json = new JSONObject();
        List<String> infotiersPayants;
        try {
            TDossierReglement dossierReglement = getEntityManager().find(TDossierReglement.class, idDossier);
            List<TDossierReglementDetail> lstTDossierReglementDetail = getListeDossierReglementDetail(idDossier);
            int numbretiket = nombreExemplaireTicket();
            MvtTransaction mvtTransaction = findByPkey(idDossier);
            TEmplacement emplacement = mvtTransaction.getMagasin();
            String num = DateConverter.getShortId(10);
            String fileBarecode = buildLineBarecode(num);
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            List<String> datas = generateData(lstTDossierReglementDetail, dossierReglement);
            List<String> infoSellers = generateDataOperateur(mvtTransaction.getUser());
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setEmplacement(emplacement);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTypeTicket(commonparameter.str_TICKET_REGLEMENT);
            imp.setShowCodeBar(true);
            imp.setOperation(dossierReglement.getDtCREATED());
            imp.setIntBegin(0);
            infotiersPayants = generateDataClient(findClientById(mvtTransaction.getOrganisme()));
            imp.setTitle("");
            imp.buildTicket(datas, infoSellers, infotiersPayants, generateDataSummary(dossierReglement, mvtTransaction), Collections.emptyList(), fileBarecode);
            for (int i = 0; i < numbretiket; i++) {
                imp.printTicketVente(1);
            }
            json.put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }
        return json;
    }

    private int nombreExemplaireTicket() {
        try {
            TParameters KEY_TICKET_COUNT = getEntityManager().find(TParameters.class, "KEY_TICKET_COUNT");

            if (KEY_TICKET_COUNT != null) {
                return Integer.valueOf(KEY_TICKET_COUNT.getStrVALUE().trim());
            }
            return 1;
        } catch (Exception e) {
            return 1;
        }

    }

    public List<TDossierReglementDetail> getListeDossierReglementDetail(String lg_DOSSIER_REGLEMENT_ID) {
        try {
            TypedQuery<TDossierReglementDetail> q = getEntityManager().createQuery("SELECT t FROM TDossierReglementDetail t WHERE t.lgDOSSIERREGLEMENTID.lgDOSSIERREGLEMENTID =?1", TDossierReglementDetail.class);
            q.setParameter(1, lg_DOSSIER_REGLEMENT_ID);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }

    }

    private List<String> generateData(List<TDossierReglementDetail> lstTDossierReglementDetail, TDossierReglement dossierReglement) {
        List<String> datas = new ArrayList<>();

        double int_AMOUNT = 0, int_AMOUNT_RESTE;

        int_AMOUNT = dossierReglement.getDblAMOUNT();
        int_AMOUNT_RESTE = dossierReglement.getDblMONTANTATTENDU() - int_AMOUNT;
        datas.add("NBRE DOSSIER(S);VERSE;RESTANT");
        datas.add(lstTDossierReglementDetail.size() + ";" + DateConverter.amountFormat((int) int_AMOUNT, ' ') + "F CFA;" + DateConverter.amountFormat((int) int_AMOUNT_RESTE, ' ') + "F CFA");
        datas.add(" ; ; ");

        return datas;
    }

    public List<String> generateDataClient(TClient client) {
        List<String> datas = new ArrayList<>();
        datas.add("Client :: " + client.getStrFIRSTNAME() + " " + client.getStrLASTNAME());
        return datas;
    }

    public List<String> generateDataOperateur(TUser u) {
        List<String> datas = new ArrayList<>();
        datas.add("Opérateur:: " + DataStringManager.subStringData(u.getStrFIRSTNAME(), 0, 1) + "." + u.getStrLASTNAME());
        return datas;
    }

    public List<String> generateDataSummary(TDossierReglement dossierReglement, MvtTransaction mvtTransaction) {
        List<String> datas = new ArrayList<>();
        datas.add("Net à payer: ;     " + DateConverter.amountFormat(DateConverter.arrondiModuloOfNumber(dossierReglement.getDblMONTANTATTENDU().intValue(), 5)) + "; F CFA;1");
        datas.add("Règlement: ;     " + mvtTransaction.getReglement().getStrNAME() + "; ;0");
        datas.add("Montant Versé: ;     " + DateConverter.amountFormat(DateConverter.arrondiModuloOfNumber(mvtTransaction.getMontantVerse(), 5)) + "; F CFA;0");
        final Integer change = Math.abs(mvtTransaction.getMontantVerse()) - Math.abs(mvtTransaction.getMontantPaye());

        datas.add("Monnaie: ;     " + DateConverter.amountFormat((change >= 0 ? change : 0)) + "; F CFA;0");
        return datas;
    }

    public void lunchPrinterForTicketVo(TPreenregistrement oTPreenregistrement) {

        int counter = 40, k = 0, page, pageCurrent = 0, diff, counter_constante = 40;
        String title;
        List<String> lstDataFinal = new ArrayList<>();
        List<String> infotiersPayants;

        try {

            MvtTransaction mvtTransaction = findByPkey(oTPreenregistrement.getLgPREENREGISTREMENTID());
            String fileBarecode = buildLineBarecode(oTPreenregistrement.getStrREFTICKET());
            TEmplacement te = oTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
            boolean voirNumTicket = voirNumeroTicket();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            if (voirNumTicket) {
                title = oTPreenregistrement.getStrREF();
            } else {
                title = oTPreenregistrement.getStrREFTICKET();
            }
            List<String> datas = generateData(oTPreenregistrement);
            List<TPreenregistrementCompteClientTiersPayent> listeVenteTiersPayants = listeVenteTiersPayantsByIdVente(oTPreenregistrement.getLgPREENREGISTREMENTID());
            List<String> infoSellers = generateDataSeller(oTPreenregistrement);
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTypeTicket(commonparameter.str_ACTION_VENTE);
            imp.setShowCodeBar(true);
            imp.setEmplacement(te);
            imp.setOperation(oTPreenregistrement.getDtUPDATED());
            imp.setIntBegin(0);
            infotiersPayants = this.generateDataTiersPayant(oTPreenregistrement, listeVenteTiersPayants);
            title = title + " | Bon N°:: " + oTPreenregistrement.getStrREFBON();
            imp.setTitle("Ticket N° " + title);

            if (datas.size() <= counter) {
                imp.buildTicket(datas, infoSellers, infotiersPayants, generateDataSummaryVo(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                imp.printTicketVente(1);
            } else {
                page = datas.size() / counter;
                while (page != pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infotiersPayants, Collections.emptyList(), Collections.emptyList(), fileBarecode);
                    imp.printTicketVente(1);

                    k = counter;
                    diff = datas.size() - counter;
                    if (diff > counter_constante) {
                        counter = counter + counter_constante;
                    } else {
                        counter = counter + diff;
                    }

                    pageCurrent++;
                    lstDataFinal.clear();
                }
                if (page == pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, infotiersPayants, generateDataSummaryVno(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                }
                imp.printTicketVente(1);

            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);

        }

    }
    Comparator<TicketDTO> comparator = Comparator.comparing(TicketDTO::getMontantVente);

    @Override
    public JSONObject generateticket10(String venteId) {
        JSONObject json = new JSONObject();

        try {
            TPreenregistrement oPreenregistrement = getEntityManager().find(TPreenregistrement.class, venteId);
            TicketDTO o = salesStatsService.getVenteById(oPreenregistrement.getLgPARENTID());
            List<TicketDTO> dTOs = new ArrayList<>();
            dTOs.add(o);
            List<TicketDTO> os = venteLiees(oPreenregistrement.getLgPARENTID()).stream().map(x -> salesStatsService.getVenteById(x)).sorted(comparator).collect(Collectors.toList());
            dTOs.addAll(os);
            printTicketModificationVenteVo(dTOs);
            return json.put("success", true);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            LOG.log(Level.SEVERE, null, e);
            return json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }

    }

    @Override
    public void printReceintWithJasper(String venteId) {
//        TOfficine officine = findOfficine();
//        TPreenregistrement oTPreenregistrement = getEntityManager().find(TPreenregistrement.class, venteId);
//        MvtTransaction mvtTransaction = findByPkey(oTPreenregistrement.getLgPREENREGISTREMENTID());
//        printTicketVNO(oTPreenregistrement, mvtTransaction, officine);
        generateticket10(venteId);
    }

    private void printTicketVNO(TPreenregistrement p, MvtTransaction mvtTransaction, TOfficine officine) {
        try {
            Map<String, Object> parameters = reportUtil.ticketParamsCommons(officine);
            parameters = reportUtil.barecodeDataParams(parameters, p.getStrREFTICKET());
            parameters = reportUtil.numTicketParams(parameters, p.getStrREFTICKET());
            parameters = reportUtil.ticketParams(parameters, mvtTransaction.getReglement().getStrNAME(), p.getIntPRICE());
            parameters = reportUtil.setSignature(parameters, "Logiciel DICI");
            parameters = reportUtil.ticketParamsMontantVerse(parameters, mvtTransaction.getMontantVerse(), (mvtTransaction.getMontantVerse() - mvtTransaction.getMontantPaye() > 0 ? mvtTransaction.getMontantVerse() - mvtTransaction.getMontantPaye() : 0));
            parameters = reportUtil.ticketParams(parameters, p.getStrREFTICKET(), p.getDtUPDATED(), "Caissier:: " + p.getLgUSERCAISSIERID().getStrFIRSTNAME().substring(0, 1).toUpperCase() + " " + p.getLgUSERCAISSIERID().getStrLASTNAME() + " Vendeur:: " + p.getLgUSERVENDEURID().getStrFIRSTNAME().substring(0, 1).toUpperCase() + " " + p.getLgUSERVENDEURID().getStrLASTNAME());
            reportUtil.printTicket(parameters, "ticket_annuler", jdom.scr_report_file, findPrintService(), listeVenteByIdVente(p.getLgPREENREGISTREMENTID()).stream().map(VenteDetailsDTO::new).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }

    private void printTicketModificationVenteVo(List<TicketDTO> os) {
        try {
            TicketDTO o = os.get(os.size() - 1);
            String file = "ticketsubreport.jasper";
            if (o.getLgTYPEVENTEID().equals(DateConverter.VENTE_CARNET_ID) || o.getMontantClient() == 0) {
                file = "ticketsubreportcarnet.jasper";
            }
            Map<String, Object> parameters = reportUtil.ticketParamsCommons(findOfficine());
            parameters.put("sub_reportUrl", jdom.scr_report_file + file);
            parameters.put("matricule", o.getMatricule());
            parameters.put("clientFullName", o.getClientFullName());
            parameters = reportUtil.setSignature(parameters, "Logiciel DICI");
            parameters = reportUtil.barecodeDataParams(parameters, o.getStrREFTICKET());
            reportUtil.printTicket(parameters, "ticket_copyventevo", jdom.scr_report_file, findPrintService(), os);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }

    private List<String> infoDepot(TPreenregistrement OTPreenregistrement) throws Exception {
        List<String> datas = new ArrayList<>();
        datas.add("Caissier(e):: " + DataStringManager.subStringData(OTPreenregistrement.getLgUSERCAISSIERID().getStrFIRSTNAME(), 0, 1) + "." + OTPreenregistrement.getLgUSERCAISSIERID().getStrLASTNAME() + "   |   " + "Vendeur:: " + DataStringManager.subStringData(OTPreenregistrement.getLgUSERVENDEURID().getStrFIRSTNAME(), 0, 1) + "." + OTPreenregistrement.getLgUSERVENDEURID().getStrLASTNAME());
        if (OTPreenregistrement.getBISCANCEL() || OTPreenregistrement.getIntPRICE() < 0) {
            datas.add("Annulée par :: " + DataStringManager.subStringData(OTPreenregistrement.getLgUSERID().getStrFIRSTNAME(), 0, 1) + "." + OTPreenregistrement.getLgUSERID().getStrLASTNAME());
        }
        TEmplacement OTEmplacement = getEntityManager().find(TEmplacement.class, OTPreenregistrement.getPkBrand());
        datas.add(OTEmplacement != null ? "Dépôt: " + OTEmplacement.getStrDESCRIPTION() : " ");
        datas.add("Client(e):: " + (OTEmplacement != null ? OTEmplacement.getStrFIRSTNAME() + " " + OTEmplacement.getStrLASTNAME() : OTPreenregistrement.getStrFIRSTNAMECUSTOMER() + " " + OTPreenregistrement.getStrLASTNAMECUSTOMER()));

        return datas;
    }

    @Override
    public JSONObject lunchPrinterForTicketDepot(String id) throws JSONException {
        JSONObject json = new JSONObject();
        int counter = 40, k = 0, page, pageCurrent = 0, diff, counter_constante = 40;
        String title;
        List<String> lstDataFinal = new ArrayList<>();
        try {
            TPreenregistrement oTPreenregistrement = getEntityManager().find(TPreenregistrement.class, id);
            String _id = id;
            MvtTransaction mvtTransaction = findByPkey(_id);
            String fileBarecode = buildLineBarecode(oTPreenregistrement.getStrREFTICKET());
            TEmplacement te = oTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
            boolean voirNumTicket = voirNumeroTicket();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            if (voirNumTicket) {
                title = oTPreenregistrement.getStrREF();
            } else {
                title = oTPreenregistrement.getStrREFTICKET();
            }
            List<String> datas = generateData(oTPreenregistrement);
            List<String> infoSellers = infoDepot(oTPreenregistrement);
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTypeTicket(commonparameter.str_ACTION_VENTE);
            imp.setShowCodeBar(true);
            imp.setEmplacement(te);
            imp.setOperation(oTPreenregistrement.getDtUPDATED());
            imp.setIntBegin(0);
            imp.setTitle("Ticket N° " + title);
            if (datas.size() <= counter) {
                imp.buildTicket(datas, infoSellers, Collections.emptyList(), generateDataSummaryDepot(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                if (oTPreenregistrement.getIntPRICE() < 0) {
                    imp.printTicketVente(1);
                } else {

                    imp.printTicketVente(1);
                }
            } else {
                page = datas.size() / counter;
                while (page != pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), fileBarecode);
                    if (oTPreenregistrement.getIntPRICE() < 0) {
                        imp.printTicketVente(1);
                    } else {

                        imp.printTicketVente(1);
                    }

                    k = counter;
                    diff = datas.size() - counter;
                    if (diff > counter_constante) {
                        counter = counter + counter_constante;
                    } else {
                        counter = counter + diff;
                    }

                    pageCurrent++;
                    lstDataFinal.clear();
                }
                if (page == pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, Collections.emptyList(), generateDataSummaryDepot(oTPreenregistrement, mvtTransaction), generateCommentaire(oTPreenregistrement, mvtTransaction), fileBarecode);
                }
                if (oTPreenregistrement.getIntPRICE() < 0) {
                    imp.printTicketVente(1);
                } else {

                    imp.printTicketVente(1);
                }

            }
            json.put("success", true);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }
        return json;
    }

    public List<String> generateDataSummaryDepot(TPreenregistrement OTPreenregistrement, MvtTransaction clotureVenteParams) {
        List<String> datas = new ArrayList<>();
        int remise = clotureVenteParams.getMontantRemise();
        remise = Math.abs(remise);
        if (remise > 0) {
            datas.add("* ;(-) " + DateConverter.amountFormat(remise) + "; F CFA;1");
        }
        datas.add("Vente à terme: ;    " + DateConverter.amountFormat(clotureVenteParams.getMontantNet()) + "; F CFA;1");

        return datas;
    }

    public List<String> generateDataSummaryDepot(TPreenregistrement OTPreenregistrement) {
        List<String> datas = new ArrayList<>();

        if (OTPreenregistrement.getIntPRICEREMISE() > 0) {
            datas.add("* ;(-) " + DateConverter.amountFormat(OTPreenregistrement.getIntPRICEREMISE()) + "; F CFA;1");
        }
        datas.add("Vente à terme: ;    " + DateConverter.amountFormat(OTPreenregistrement.getIntPRICE() - OTPreenregistrement.getIntPRICEREMISE()) + "; F CFA;1");

        return datas;
    }

    @Override
    public JSONObject lunchPrinterForTicketDepot(ClotureVenteParams clotureVenteParams) throws JSONException {
        JSONObject json = new JSONObject();
        int counter = 40, k = 0, page, pageCurrent = 0, diff, counter_constante = 40;
        String title;
        List<String> lstDataFinal = new ArrayList<>();
        try {
            TPreenregistrement oTPreenregistrement = getEntityManager().find(TPreenregistrement.class, clotureVenteParams.getVenteId());
            String _id = clotureVenteParams.getVenteId();
            MvtTransaction mvtTransaction = findByPkey(_id);
            String fileBarecode = buildLineBarecode(oTPreenregistrement.getStrREFTICKET());
            TEmplacement te = oTPreenregistrement.getLgUSERID().getLgEMPLACEMENTID();
            boolean voirNumTicket = voirNumeroTicket();
            PrintService printService = findPrintService();
            TImprimante imprimante = findImprimanteByName();
            TOfficine officine = findOfficine();
            if (voirNumTicket) {
                title = oTPreenregistrement.getStrREF();
            } else {
                title = oTPreenregistrement.getStrREFTICKET();
            }
            List<String> datas = generateData(oTPreenregistrement);
            List<String> infoSellers = infoDepot(oTPreenregistrement);
            ImpressionServiceImpl imp = new ImpressionServiceImpl();
            imp.setOTImprimante(imprimante);
            imp.setOfficine(officine);
            imp.setService(printService);
            imp.setTypeTicket(commonparameter.str_ACTION_VENTE);
            imp.setShowCodeBar(true);
            imp.setEmplacement(te);
            imp.setOperation(oTPreenregistrement.getDtUPDATED());
            imp.setIntBegin(0);
            imp.setTitle("Ticket N° " + title);
            List<String> generateDataSummarys = generateDataSummaryDepot(oTPreenregistrement);
            List<String> commentaires = generateCommentaire(oTPreenregistrement, mvtTransaction);
            if (datas.size() <= counter) {
                imp.buildTicket(datas, infoSellers, Collections.emptyList(), generateDataSummarys, commentaires, fileBarecode);
                if (oTPreenregistrement.getIntPRICE() < 0) {
                    imp.printTicketVente(1);
                } else {

                    imp.printTicketVente(1);
                }

            } else {
                page = datas.size() / counter;
                while (page != pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), fileBarecode);
                    if (oTPreenregistrement.getIntPRICE() < 0) {
                        imp.printTicketVente(1);
                    } else {

                        imp.printTicketVente(1);
                    }

                    k = counter;
                    diff = datas.size() - counter;
                    if (diff > counter_constante) {
                        counter = counter + counter_constante;
                    } else {
                        counter = counter + diff;
                    }

                    pageCurrent++;
                    lstDataFinal.clear();
                }
                if (page == pageCurrent) {
                    for (int i = k; i < counter; i++) {
                        lstDataFinal.add(datas.get(i));
                    }
                    imp.buildTicket(lstDataFinal, infoSellers, Collections.emptyList(), generateDataSummarys, commentaires, fileBarecode);
                }
                if (oTPreenregistrement.getIntPRICE() < 0) {
                    imp.printTicketVente(1);
                } else {

                    imp.printTicketVente(1);
                }

            }
            json.put("success", true);
            afficheurWellComeMessage();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            json.put("success", false).put("msg", "Impression n'a pas aboutie");
        }
        return json;

    }

    void afficheurWellComeMessage() {
        if (afficheurActif()) {
            try {
                Afficheur afficheur = Afficheur.getInstance();
                afficheur.affichage(DataStringManager.subStringData(findOfficine().getStrNOMABREGE(), 0, 20));
                afficheur.affichage(DataStringManager.subStringData("  BIENVENUE A VOUS", 0, 20));
            } catch (Exception e) {
            }
        }

    }

    public boolean voirNumeroTicket() {
        try {

            TParameters tp = getEntityManager().find(TParameters.class, Parameter.KEY_SHOW_NUMERO_TICKET);
            return (Integer.valueOf(tp.getStrVALUE()) == 1);

        } catch (Exception e) {

            return false;
        }
    }

    public boolean afficheurActif() {

        try {
            TParameters tp = getEntityManager().find(TParameters.class, "KEY_ACTIVATE_DISPLAYER");
            return (tp != null && tp.getStrVALUE().trim().equals("1"));
        } catch (Exception e) {
            return false;
        }
    }

    public TOfficine findOfficine() {

        return getEntityManager().find(TOfficine.class, "1");

    }

    public PrintService findPrintService() {

        return PrintServiceLookup.lookupDefaultPrintService();

    }

    public TImprimante findImprimanteByName() {

        try {

            Query qry = getEntityManager().createQuery("SELECT t FROM TImprimante t WHERE t.strNAME = ?1 ")
                    .setParameter(1, findPrintService().getName());
            qry.setMaxResults(1);
            return (TImprimante) qry.getSingleResult();

        } catch (Exception e) {
//            LOG.log(Level.SEVERE, null, e);
            return null;
        }

    }

    @Override
    public JSONObject generateTicketOnFly(String venteId) throws JSONException {
        JSONObject json = new JSONObject();
        int counter = 40;

        try {

//            TImprimante imprimante = findImprimanteByName();
            TPreenregistrement op = getEntityManager().find(TPreenregistrement.class, venteId);
            MvtTransaction mt = findByPkey(op.getLgPREENREGISTREMENTID());
            List<TPreenregistrementDetail> items = listeVenteByIdVente(op.getLgPREENREGISTREMENTID());
            List<TPreenregistrementCompteClientTiersPayent> listeVenteTiersPayants = new ArrayList<>();
//            String imgSrc = buildLineBarecode(op.getStrREFTICKET());
            boolean voirNumTicket = voirNumeroTicket();
            File barcode = copyBarcodeToFile(op.getStrREFTICKET());
            TUser caisse = op.getLgUSERCAISSIERID();
            TUser vendeur = op.getLgUSERVENDEURID();
            TUser operateur = op.getLgUSERID();
            TEmplacement e = caisse.getLgEMPLACEMENTID();
            TOfficine officine = findOfficine();
            boolean canceled = (op.getIntPRICE() < 0);
            boolean isDiffere = (op.getStrSTATUTVENTE().equalsIgnoreCase(commonparameter.statut_differe) && op.getIntPRICE() > 0);
            boolean isVo = (op.getStrTYPEVENTE().equalsIgnoreCase(DateConverter.VENTE_ASSURANCE));
            boolean printUniqueTicket = true;
            int avoir = 0, acompte = 0, montantRestant = 0, total = mt.getMontantNet();
            int montantNet = 0, montantClient = op.getIntCUSTPART();
            if (isVo) {
                listeVenteTiersPayants = listeVenteTiersPayantsByIdVente(op.getLgPREENREGISTREMENTID());
                total = mt.getMontantCredit();
                montantNet = getMontantNet(isVo, op, mt.getMontantNet());
                if (montantClient < 0) {
                    montantClient = (-1) * DateConverter.arrondiModuloOfNumber(Math.abs(montantClient), 5);
                } else {
                    montantClient = DateConverter.arrondiModuloOfNumber(montantClient, 5);
                }
                printUniqueTicket = printUniqueTicket();
            }

            if (isDiffere) {
                montantRestant = mt.getMontantRestant();
            }
            String ticketNum;
            if (voirNumTicket) {
                ticketNum = op.getStrREF();
            } else {
                ticketNum = op.getStrREFTICKET();
            }
            if (!canceled && op.getBISAVOIR()) {
                for (TPreenregistrementDetail item : items) {
                    avoir += item.getIntAVOIR();
                    acompte += (item.getIntAVOIR() * item.getIntPRICEUNITAIR());
                }
            }
            StringBuilder sb = TicketTemplate.buildStyle();
            sb.append(TicketTemplate.buildInfosOficine(officine, ticketNum, false, e, op.getStrREFBON(), op.getClient()));
            sb.append(TicketTemplate.buildInfoCaisse(caisse, vendeur, canceled, operateur, false, e));
            if (isVo) {
                sb.append(TicketTemplate.buildInfoClientVo(op.getAyantDroit(), op.getClient(), montantClient, listeVenteTiersPayants));
            }
            int k = items.size();
            System.out.println(k);
            if (k <= counter) {
                sb.append(TicketTemplate.buildItemsContent(op, items, (int) items.size()));
                int monnaie = (Math.abs(mt.getMontantVerse()) - montantNet > 0 ? Math.abs(mt.getMontantVerse()) - Math.abs(mt.getMontantPaye()) : 0);
                sb.append(TicketTemplate.buildContentReglement(isVo, op, mt, avoir, acompte, montantRestant, total, montantNet, mt.getMontantRemise(), mt.getMontantVerse(), monnaie));
                sb.append(TicketTemplate.buildBottomContent(barcode.getPath(), op));
                System.out.println(sb.toString());
//                print(receipTmpFile(sb), listeVenteTiersPayants.size(), printUniqueTicket);
            } else {

                int page = (int) Math.ceil(Double.valueOf(k)) / counter;
                for (int i = 0, j = counter; i < page; i++, j += counter) {
                    List<TPreenregistrementDetail> details = items.subList(i, (j > k) ? k : j);
                    if (j < k) {
                        sb.append(TicketTemplate.buildItemsContentPortion(op, details));
//                        print(receipTmpFile(sb), listeVenteTiersPayants.size(), printUniqueTicket);
                    } else {
                        int monnaie = (Math.abs(mt.getMontantVerse()) - montantNet > 0 ? Math.abs(mt.getMontantVerse()) - Math.abs(mt.getMontantPaye()) : 0);
                        sb.append(TicketTemplate.buildContentReglement(isVo, op, mt, avoir, acompte, montantRestant, total, montantNet, mt.getMontantRemise(), mt.getMontantVerse(), monnaie));
                        sb.append(TicketTemplate.buildBottomContent(barcode.getPath(), op));
//                        print(receipTmpFile(sb), listeVenteTiersPayants.size(), printUniqueTicket);
                    }

                }

            }
            json.put("success", true);
            try {
                barcode.deleteOnExit();
            } catch (Exception xe) {
            }
            afficheurWellComeMessage();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            json.put("success", false);
        }
        return json;
    }

    @Override
    public JSONObject generateVoTicketOnFly(ClotureVenteParams clotureVenteParams) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject generateVoTicketOnFly(String venteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject generateDepotTicketOnFly(String venteId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int getMontantNetVo(TPreenregistrement OTPreenregistrement) {
        Integer vente_net = OTPreenregistrement.getIntCUSTPART();
        if (vente_net > 0) {
            vente_net = DateConverter.arrondiModuloOfNumber(OTPreenregistrement.getIntCUSTPART(), 5);
        } else {
            vente_net = (-1) * DateConverter.arrondiModuloOfNumber(Math.abs(OTPreenregistrement.getIntCUSTPART()), 5);
        }
        return Maths.arrondiModuloOfNumber((vente_net - OTPreenregistrement.getIntPRICEREMISE()), 5);

    }

    private int getMontantNetVno(Integer montantNet) {
        if (montantNet > 0) {
            montantNet = DateConverter.arrondiModuloOfNumber(montantNet, 5);
        } else {
            montantNet = (-1) * DateConverter.arrondiModuloOfNumber(((-1) * montantNet), 5);
        }
        return montantNet;
    }

    private int getMontantNet(boolean isVo, TPreenregistrement OTPreenregistrement, Integer montantNet) {
        if (isVo) {
            return getMontantNetVo(OTPreenregistrement);
        }
        return getMontantNetVno(montantNet);

    }


    private File receipTmpFile(StringBuilder sb) {
        DataOutputStream dataOutputStream = null;
        File tmpFile = null;
        try {
            tmpFile = new File("receipt.xhtml");
            if (!tmpFile.exists()) {
                tmpFile.createNewFile();
            }
            dataOutputStream = new DataOutputStream(new FileOutputStream(tmpFile));
            dataOutputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            dataOutputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateTicketServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateTicketServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dataOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(GenerateTicketServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tmpFile;
    }

    private File copyBarcodeToFile(String data) {
        File f = null;
        try {

            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(data);
            barcode128.setBaseline(10);
            barcode128.setBarHeight(50);
            barcode128.setCodeType(Barcode128.CODE128);
            java.awt.Image img = barcode128.createAwtImage(Color.BLACK, Color.WHITE);
            BufferedImage bi = new BufferedImage(100, 70, BufferedImage.BITMASK);
            Graphics2D gd = bi.createGraphics();
            gd.drawImage(img, 4, 2, null);
            gd.setColor(Color.BLACK);
            gd.drawString(data, 10, 65);
            gd.dispose();
            f = new File(data + ".png");
            ImageIO.write(bi, "png", f);
//            file = f.getAbsolutePath();
        } catch (IOException ex) {
//            LOG.log(Level.SEVERE, null, ex);
        }
        return f;

    }

    @Override
    public JSONObject generateTicketOnFly(ClotureVenteParams clotureVenteParams) throws JSONException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
