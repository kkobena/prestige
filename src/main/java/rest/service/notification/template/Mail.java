package rest.service.notification.template;

import commonTasks.dto.NotificationDTO;
import dal.Notification;
import dal.TUser;
import java.util.List;
import java.util.Properties;
import org.apache.commons.collections4.CollectionUtils;
import rest.service.v2.dto.NotificationUtilsDTO;
import util.SmsParameters;

/**
 *
 * @author koben
 */
public class Mail {

    public static Properties getEmail() {
        SmsParameters sp = SmsParameters.getInstance();
        Properties props = new Properties();
        props.put("mail.smtp.host", sp.smtpHost);
        props.put("mail.transport.protocol", sp.protocol);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return props;
    }

    public static StringBuilder beginTag() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        return sb;
    }

    public static void endTag(StringBuilder sb) {

        sb.append("</body></html>");

    }

    public static String buildMvtCaisse(List<Notification> mvtCaisses) {
        if (CollectionUtils.isEmpty(mvtCaisses)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>").append("Mouvements de caisse")
                .append("</caption>").append("<tr><th>Opérateur</th><th>Date</th><th>Montant</th></tr>");

        mvtCaisses.forEach(t -> {
            TUser tu = t.getUser();
            NotificationDTO notification = new NotificationDTO(t);
            NotificationUtilsDTO item = notification.getNotificationDetail();
            sb.append("<tr><td>").append(tu.getStrFIRSTNAME()).append(" ").append(tu.getStrLASTNAME())
                    .append("</td><td>").append(item.getDateMvt()).append("</td><td  style='text-align: right;'>")
                    .append(item.getMontant()).append("</td></tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildBonLivraison(List<Notification> mvtCaisses) {
        if (CollectionUtils.isEmpty(mvtCaisses)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>").append("Bons de livraisons")
                .append("</caption>")
                .append("<tr><th>Opérateur</th><th>Numéro Bon</th><th>Date Bon</th><th>Date de saisie</th><th>Montant Tva</th><th>Montant Ttc</th></tr>");

        mvtCaisses.forEach(t -> {
            TUser tu = t.getUser();
            NotificationDTO notification = new NotificationDTO(t);
            NotificationUtilsDTO item = notification.getNotificationDetail();
            sb.append("<tr><td>").append(tu.getStrFIRSTNAME()).append(" ").append(tu.getStrLASTNAME())
                    .append("</td><td>").append(item.getNumBon()).append("</td><td>").append(item.getDateBon())
                    .append("</td><td>").append(item.getDateMvt()).append("</td><td  style='text-align: right;'>")
                    .append(item.getMontantTva()).append("</td><td style='text-align: right;'>")
                    .append(item.getMontantTtc()).append("</td></tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildRetourFournisseur(List<Notification> mvtCaisses) {
        if (CollectionUtils.isEmpty(mvtCaisses)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>").append("Retour fournisseurs")
                .append("</caption>")
                .append("<tr><th>Opérateur</th><th>Numéro Bon</th><th>Date Bon</th><th>Date de saisie</th><th>Montant Tva</th><th>Montant Ttc</th></tr>");

        mvtCaisses.forEach(t -> {
            TUser tu = t.getUser();
            NotificationDTO notification = new NotificationDTO(t);
            NotificationUtilsDTO item = notification.getNotificationDetail();
            sb.append("<tr><td>").append(tu.getStrFIRSTNAME()).append(" ").append(tu.getStrLASTNAME())
                    .append("</td><td>").append(item.getNumBon()).append("</td><td>").append(item.getDateBon())
                    .append("</td><td>").append(item.getDateMvt()).append("</td><td  style='text-align: right;'>")
                    .append(item.getMontantTva()).append("</td><td style='text-align: right;'>")
                    .append(item.getMontantTtc()).append("</td></tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildPerimes(List<Notification> mvtCaisses) {
        if (CollectionUtils.isEmpty(mvtCaisses)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>")
                .append("Liste de saisie de produits périmés").append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th></tr>");

        mvtCaisses.forEach(t -> {
            TUser tu = t.getUser();
            NotificationDTO notification = new NotificationDTO(t);
            NotificationUtilsDTO item = notification.getNotificationDetail();
            sb.append("<tr><td>").append(tu.getStrFIRSTNAME()).append(" ").append(tu.getStrLASTNAME())
                    .append("</td><td>").append(item.getCode()).append("</td><td>").append(item.getDescription())
                    .append("</td><td>").append(item.getDateMvt()).append("</td></tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildCreationProduit(List<Notification> mvtCaisses) {
        if (CollectionUtils.isEmpty(mvtCaisses)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>")
                .append("Liste de saisie de produits périmés").append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th></tr>");
        mvtCaisses.stream().map(NotificationDTO::new).map(NotificationDTO::getNotificationDetail).forEach(t -> {

            sb.append("<tr><td>").append(t.getUser()).append("</td><td>").append(t.getCode()).append("</td><td>")
                    .append(t.getDescription()).append("</td><td>").append(t.getDateMvt()).append("</td></tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }
}
