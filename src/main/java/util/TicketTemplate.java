package util;

import dal.TEmplacement;
import dal.TOfficine;
import dal.TUser;
import org.apache.commons.lang3.StringUtils;

public class TicketTemplate {

    public static StringBuilder buildStyle() {
        TicketParameters pm = TicketParameters.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<meta charset='UTF-8'></meta>");
        sb.append("<head><style language='text/css'>");
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
//        sb.append("@page {  margin-top:" + pm.marginTop + "px;  margin-left:" + pm.marginLeft + "px;  width:" + pm.pageWidth
//                + "mm;  height:" + pm.height + "mm;  }");
        sb.append("h1 {font-size:");
        sb.append(pm.titleFontSize);
        sb.append("px;margin-bottom: 1px;}");
//        sb.append("h1 {font-size:" + pm.titleFontSize + "px;margin-bottom: 1px;}");
//        sb.append("p{margin: 0 0 3px 0;}  h3 {font-size: " + pm.fontSize + "px;} body {font-size:" + pm.fontSize
//                + "px;} table {border-collapse: collapse;width:" + pm.itemBodyWidth + "mm;margin: 3px 0 3px 0;}");
        sb.append("p{margin: 0 0 3px 0;}  h3 {font-size:");
        sb.append(pm.fontSize);
        sb.append("px;} body {font-size:");
        sb.append(pm.fontSize);
        sb.append("px;} table {border-collapse: collapse;width:");
        sb.append(pm.itemBodyWidth);
        sb.append("mm;margin: 3px 0 3px 0;}");
        sb.append(
                "tableItems thead tr th {border-bottom: 1px solid #D4D4D4;} .tableItems thead tr th:nth-child(3), .tableItems thead tr th:last-child{text-align: right;}");
        sb.append(
                " .tableItems tfoot tr th {border-top: 1px solid #D4D4D4;border-bottom: 1px solid #D4D4D4;} table tbody tr td:first-child {text-align: left;}");
        sb.append(
                "table tbody tr td:last-child {text-align: right;} .tableItems tbody tr td:nth-child(3) {text-align: right;margin-left: 5px;}");
        sb.append(
                ".infosreglment tbody tr:first-child{font-weight: bold;} .tableItems tfoot tr th:last-child {text-align: right;}");
        sb.append(".tableItems tfoot tr th:first-child {text-align: left;}");

        sb.append("</style></head>");

        return sb;
    }

    public static StringBuilder buildInfosOficine(TOfficine officine, String ticketNum, boolean ticketIsVisible, boolean isDepot, TEmplacement te) {
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
        if (ticketIsVisible) {
            sb.append("Ticket: ");
            sb.append(ticketNum);
            sb.append(": ");
            sb.append("</p>");
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
            sb.append("Dépôt:");
            sb.append(e.getStrDESCRIPTION());
            sb.append("</p>");
            sb.append("<p>");
            sb.append("Client:");
            sb.append(e.getStrFIRSTNAME());
            sb.append(" ");
            sb.append(e.getStrLASTNAME());
            sb.append("</p>");
        }
        return sb;
    }

    public static StringBuilder buildInfoClientVo(TOfficine officine, String ticketNum, boolean ticketIsVisible, boolean isDepot, TEmplacement te) {
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
        if (ticketIsVisible) {
            sb.append("Ticket: ");
            sb.append(ticketNum);
            sb.append(": ");
            sb.append("</p>");
        }

        return sb;
    }

}
