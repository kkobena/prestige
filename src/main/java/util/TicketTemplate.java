package util;

import dal.MvtTransaction;
import dal.TAyantDroit;
import dal.TClient;
import dal.TEmplacement;
import dal.TOfficine;
import dal.TPreenregistrement;
import dal.TPreenregistrementCompteClientTiersPayent;
import dal.TPreenregistrementDetail;
import dal.TUser;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TicketTemplate {

    public static StringBuilder buildStyle() {
        TicketParameters pm = TicketParameters.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<meta charset='UTF-8'></meta>");
        sb.append("<head>");
        sb.append("<style type='text/css'>");
        sb.append("@page {  margin-top:");
        sb.append(pm.marginTop);
        sb.append("px;  margin-left:");
        sb.append(pm.marginLeft);
        sb.append("px;  width:");
        sb.append(pm.pageWidth);
        sb.append("mm;  height:");
        sb.append(pm.height);
        sb.append("mm;  }");

        sb.append("h1 {font-size:");
        sb.append(pm.titleFontSize);
        sb.append("px;margin-bottom: 1px;}");
        sb.append("p{margin: 0 0 5px 0;} p,h3,td,th {font-size:");
        sb.append(pm.fontSize);
        sb.append("px;} body {font-size:");
        sb.append(pm.fontSize);
        sb.append("px;} table {border-collapse: collapse;width:");
        sb.append(pm.itemBodyWidth); 
        sb.append("mm;margin: 3px 0 3px 0;}");
        sb.append(".tableItems thead tr th {border-bottom: 1px solid #D4D4D4;} .tableItems thead tr th:nth-child(3), .tableItems thead tr th:last-child{text-align: right;}");
        sb.append(" .tableItems tfoot tr th {border-top: 1px solid #D4D4D4;border-bottom: 1px solid #D4D4D4;} table tbody tr td:first-child {text-align: left;}");
        sb.append(
                "table tbody tr td:last-child {text-align: right;} .tableItems tbody tr td:nth-child(3),.tableItems tbody tr td:last-child {text-align: right;margin-left: 5px;}");
        sb.append(
                ".infosreglment tbody tr:first-child,.montantnet{font-weight: bold;} .tableItems tfoot tr th:last-child {text-align: right;}");
        sb.append(".tableItems tfoot tr th:first-child {text-align: left;}");

        sb.append("</style></head>");

        return sb;
    }

    public static StringBuilder buildInfosOficine(TOfficine officine, String ticketNum, boolean isDepot, TEmplacement te, String numBon, TClient c) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>");
        sb.append(officine.getStrNOMCOMPLET());
        sb.append("</h1>");
        if (isDepot) {
            sb.append("<p>");
            sb.append(te.getLgTYPEDEPOTID().getStrDESCRIPTION());
            sb.append(": ");
            sb.append(te.getStrDESCRIPTION());
            sb.append("</p>");
        }
        sb.append("<p>");
        sb.append(officine.getStrFIRSTNAME());
        sb.append(" ");
        sb.append(officine.getStrLASTNAME());
        sb.append("</p>");
        sb.append("<p>");
        sb.append(officine.getStrPHONE());
        sb.append("|");
        sb.append(officine.getStrADRESSSEPOSTALE());
        sb.append("</p>");
        if (!StringUtils.isEmpty(officine.getStrENTETE())) {
            sb.append("<p>");
            sb.append(officine.getStrENTETE());
            sb.append("</p>");
        }
        if (!StringUtils.isEmpty(ticketNum) || !StringUtils.isEmpty(numBon)) {
            sb.append("<p>");
            if (!StringUtils.isEmpty(ticketNum)) {
                sb.append("Ticket: ");
                sb.append(ticketNum);
            }
            if (!StringUtils.isEmpty(numBon)) {
                sb.append("| ");
                sb.append("Bon: ");
                sb.append(numBon);
            }

            sb.append("</p>");
            if (c != null) {
                sb.append("<p>");
                sb.append("Client: ");
                sb.append(c.getStrFIRSTNAME());
                sb.append(" ");
                sb.append(c.getStrLASTNAME());
                sb.append("</p>");
            }
        }

        return sb;
    }

    public static StringBuilder buildInfoCaisse(TUser caisse, TUser vendeur, boolean isCanceled, TUser canceledBy, boolean isDepot, TEmplacement e) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        sb.append("Caissier: ");
        sb.append(caisse.getStrFIRSTNAME().substring(0, 1).toUpperCase());
        sb.append(".");
        sb.append(caisse.getStrLASTNAME());
        sb.append("|");
        sb.append("Vendeur: ");
        sb.append(vendeur.getStrFIRSTNAME().substring(0, 1).toUpperCase());
        sb.append(vendeur.getStrLASTNAME());
        sb.append("</p>");
        if (isCanceled) {
            sb.append("<p>");
            sb.append("Annulée par: ");
            sb.append(canceledBy.getStrFIRSTNAME().substring(0, 1).toUpperCase());
            sb.append(canceledBy.getStrLASTNAME());
            sb.append("</p>");
        }
        if (isDepot) {
            sb.append("<p>");
            sb.append("Dépôt: ");
            sb.append(e.getStrDESCRIPTION());
            sb.append("</p>");
            sb.append("<p>");
            sb.append("Client: ");
            sb.append(e.getStrFIRSTNAME());
            sb.append(" ");
            sb.append(e.getStrLASTNAME());
            sb.append("</p>");
        }
        return sb;
    }

    public static StringBuilder buildInfoClientVo(TAyantDroit ayantDroit, TClient c, int montantClient, List<TPreenregistrementCompteClientTiersPayent> lstT) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        sb.append("Matricule assuré: ");
        sb.append(c.getStrNUMEROSECURITESOCIAL());
        sb.append("</p>");

        sb.append("<p>");
        sb.append("Bénéficiaire: ");
        if (ayantDroit != null) {
            sb.append(ayantDroit.getStrFIRSTNAME());
            sb.append(" ");
            sb.append(ayantDroit.getStrLASTNAME());

        } else {
            sb.append(c.getStrFIRSTNAME());
            sb.append(" ");
            sb.append(c.getStrLASTNAME());
        }

        sb.append("</p>");

        sb.append("<p>");
        sb.append("Part assuré: ");
        sb.append(" ");
        sb.append(DateConverter.amountFormat(montantClient));
        sb.append("</p>");
        lstT.forEach(e -> {
            sb.append("<p>");
            sb.append(e.getLgCOMPTECLIENTTIERSPAYANTID().getLgTIERSPAYANTID().getStrNAME());
            sb.append(" ");
            sb.append(e.getIntPERCENT());
            sb.append("%");
            sb.append(": ");
            sb.append(DateConverter.amountFormat(e.getIntPRICE()));
            sb.append("</p>");

        });

        return sb;
    }

    public static StringBuilder buildItemsContent(TPreenregistrement p, List<TPreenregistrementDetail> items, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='tableItems'><thead><tr><th>Qté</th><th>Désignation</th><th>P.u</th><th>Prix</th></tr></thead><tbody>");

        for (TPreenregistrementDetail item : items) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(Math.abs(item.getIntQUANTITY()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(item.getLgFAMILLEID().getStrNAME());
            sb.append("</td>");

            sb.append("<td>");
            sb.append(DateConverter.amountFormat(Math.abs(item.getIntPRICEUNITAIR())));
            sb.append("</td>");

            sb.append("<td>");
            sb.append(DateConverter.amountFormat(Math.abs(item.getIntPRICE())));
            sb.append("</td>");
            sb.append("</tr>");

        }

        sb.append("</tbody>");

        sb.append("<tfoot>");
        sb.append("<tr>");

        sb.append("<th>");
        sb.append(DateConverter.amountFormat(count));
        sb.append("</th>");

        sb.append("<th>");
        sb.append("produit(s)");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("</th>");
        sb.append("<th>");
        sb.append(DateConverter.amountFormat(Math.abs(p.getIntPRICE())));
        sb.append("</th>");
        sb.append("</tr>");
        sb.append("</tfoot>");

        sb.append("</table>");

        return sb;
    }

    public static StringBuilder buildContentReglement(boolean isVo, TPreenregistrement p, MvtTransaction m, int avoir, int acompte, int montantRestant, int totalRecap, int montantNet, int montantRemise, int montantVerse, int monnaie) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='infosreglment'><tbody>");
        if (avoir > 0) {
            sb.append("<tr class='montantnet'>");
            sb.append("<td>");
            sb.append("Qté avoir:");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(avoir);
            sb.append("</td>");

            sb.append("</tr>");
        }
        if (montantRemise > 0) {
            sb.append("<tr class='montantnet'>");
            sb.append("<td>");
            sb.append("*");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(DateConverter.amountFormat(montantRemise));
            sb.append("</td>");
            sb.append("</tr>");
        }
        if ((p.getIntCUSTPART() == 0) && isVo) {
            sb.append("<tr class='montantnet'>");
            sb.append("<td>");
            sb.append("Vente à terme:");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(DateConverter.amountFormat(totalRecap));
            sb.append("</td>");
            sb.append("</tr>");
        } else {
            sb.append("<tr class='montantnet'>");
            sb.append("<td>");
            sb.append("Net à payer:");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(DateConverter.amountFormat(montantNet));
            sb.append("</td>");
            sb.append("</tr>");
            try {
                sb.append("<tr>");
                sb.append("<td>");
                sb.append("Règlement:");
                sb.append("</td>");
                sb.append("<td>");
                sb.append(m.getReglement().getStrNAME());
                sb.append("</td>");
                sb.append("</tr>");
            } catch (Exception e) {
            }
            if (p.getIntPRICE() > 0) {
                sb.append("<tr>");
                sb.append("<td>");
                sb.append("Montant versé:");
                sb.append("</td>");
                sb.append("<td>");
                sb.append(DateConverter.amountFormat(montantVerse));
                sb.append("</td>");
                sb.append("</tr>");
                sb.append("<tr>");
                sb.append("<td>");
                sb.append("Monnaie:");
                sb.append("</td>");
                sb.append("<td>");
                sb.append(DateConverter.amountFormat(monnaie));
                sb.append("</td>");
                sb.append("</tr>");
            }

        }
        if (acompte > 0) {
            sb.append("<tr class='montantnet'>");
            sb.append("<td>");
            sb.append("Acompte:");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(DateConverter.amountFormat(acompte));
            sb.append("</td>");
            sb.append("</tr>");
        }
        if (montantRestant > 0) {
            sb.append("<tr class='montantnet'>");
            sb.append("<td>");
            sb.append("Montant restant:");
            sb.append("</td>");
            sb.append("<td>");
            sb.append(DateConverter.amountFormat(montantRestant));
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");
        return sb;
    }

    public static StringBuilder buildBottomContent(String srcImg, TPreenregistrement p) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table><tbody><tr>  ");
        sb.append("<td><img src=\"");
        sb.append(srcImg);  
        sb.append("\" alt='barcode' width='100px' height='100px'   /></td> "); 
        sb.append("<td>");
        sb.append(DateConverter.formatDateToEEEE_dd_MM_yyyy_HH_mm(p.getDtUPDATED()));
        sb.append("</td></tr></tbody></table></html>");

        return sb;
    }

    public static StringBuilder buildItemsContentPortion(TPreenregistrement p, List<TPreenregistrementDetail> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='tableItems'><thead><tr><th>Qté</th><th>Désignation</th><th>P.U</th><th>Prix</th></tr></thead><tbody>");

        for (TPreenregistrementDetail item : items) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(Math.abs(item.getIntQUANTITY()));
            sb.append("</td>");
            sb.append("<td>");
            sb.append(item.getLgFAMILLEID().getStrNAME());
            sb.append("</td>");

            sb.append("<td>");
            sb.append(DateConverter.amountFormat(Math.abs(item.getIntPRICEUNITAIR())));
            sb.append("</td>");

            sb.append("<td>");
            sb.append(DateConverter.amountFormat(Math.abs(item.getIntPRICE())));
            sb.append("</td>");
            sb.append("</tr>");

        }
        sb.append("</tbody>");
        sb.append("</table></html>");

        return sb;
    }

}
