package rest.service.notification.template;

import commonTasks.dto.NotificationDTO;
import dal.Notification;
import dal.TUser;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.collections4.CollectionUtils;
import rest.service.v2.dto.NotificationUtilsDTO;
import util.DateUtil;
import util.SmsParameters;

/**
 *
 * @author koben
 */
public final class Mail {

    public static SmsParameters sp = SmsParameters.getInstance();

    private Mail() {
    }

    public static Properties getEmailProperties() {
        if (Objects.isNull(sp)) {
            sp = SmsParameters.getInstance();

        }

        Properties props = new Properties();
        props.put("mail.smtp.host", sp.smtpHost);
        props.put("mail.transport.protocol", sp.protocol);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return props;
    }

    public static String beginTag() {

        return "<html><body>";

    }

    public static String endTag() {

        return "</body></html>";

    }

    public static String buildClotureCaisse(List<Notification> mvtCaisses) {
        if (CollectionUtils.isEmpty(mvtCaisses)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        mvtCaisses.forEach(n -> {
            NotificationDTO notification = new NotificationDTO(n);
            TUser tu = n.getUser();
            NotificationUtilsDTO item = notification.getNotificationDetail();
            sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                    .append("<caption  style='font-weight: bold;text-align: center;'>")
                    .append("Clôture de la caisse de").append(tu.getStrFIRSTNAME()).append(" ")
                    .append(tu.getStrLASTNAME()).append(" du ").append(DateUtil.convertDate(n.getCreatedAt()))
                    .append("</caption>").append("<tr><th>Type</th><th>Mode règlement</th><th>Montant</th></tr>");

            item.getDetail().forEach(t -> {

                sb.append("<tr><td>").append(t.getCode()).append("</td><td>").append(t.getDescription())
                        .append("</td><td  style='text-align: right;'>").append(item.getMontant()).append("</td></tr>");

            });
            sb.append("<tr><td colspan='2'   style='text-align: right;font-weight: bold;'>").append(item.getMontant())
                    .append("</td></tr>");
            sb.append("</table>");
        });

        return sb.toString();
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

    public static String buildRetourFournisseur(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>").append("Retour fournisseurs")
                .append("</caption>")
                .append("<tr><th>Opérateur</th><th>Numéro Bon</th><th>Date Bon</th><th>Date de saisie</th><th>Montant Tva</th><th>Montant Ttc</th></tr>");

        notifications.forEach(t -> {
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

    public static String buildPerimes(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>")
                .append("Liste de saisie de produits périmés").append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th><th>Date</th></tr>");

        notifications.forEach(t -> {
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

    public static String buildCreationProduit(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>").append("Création de produits")
                .append("</caption>").append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th><th>Date</th></tr>");
        notifications.stream().map(NotificationDTO::new).forEach(t -> {
            NotificationUtilsDTO item = t.getNotificationDetail();
            sb.append("<tr><td>").append(t.getUser()).append("</td><td>").append(item.getCode()).append("</td><td>")
                    .append(item.getDescription()).append("</td><td>").append(item.getDateMvt()).append("</td></tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildVente(List<Notification> mvtCaisses) {
        if (CollectionUtils.isEmpty(mvtCaisses)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>").append("Liste des ventes modifiées")
                .append("</caption>")
                .append("<tr><th>Opérateur</th><th>Type modification</th><th>Référence</th><th>Date</th></tr>");

        mvtCaisses.forEach(t -> {
            TUser tu = t.getUser();
            NotificationDTO notification = new NotificationDTO(t);
            NotificationUtilsDTO item = notification.getNotificationDetail();
            sb.append("<tr><td>").append(tu.getStrFIRSTNAME()).append(" ").append(tu.getStrLASTNAME())
                    .append("</td><td>").append(item.getType()).append("</td><td>").append(item.getCode())
                    .append("</td><td>").append(item.getDateMvt()).append("</td></tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildEntreeUg(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>")
                .append("Liste des entrée unités gratuites").append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th><th>Date</th><th>Quantité</th></tr>");
        notifications.stream().map(NotificationDTO::new).forEach(t -> {
            NotificationUtilsDTO notificationDTO = t.getNotificationDetail();
            notificationDTO.getDetail().forEach(e -> {

                sb.append("<tr><td>").append(notificationDTO.getUser()).append("</td><td>").append(e.getCode())
                        .append("</td><td>").append(e.getDescription()).append("</td><td>")
                        .append(notificationDTO.getDateMvt()).append("</td><td>").append(e.getQuantite())
                        .append("</td></tr>");

            });

        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildAjustement(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>").append("Liste des produits ajustés")
                .append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th><th>Date</th><th>Quantité initiale</th><th>Quantité ajusté</th><th>Stock final</th></tr>");
        notifications.stream().map(NotificationDTO::new).forEach(t -> {
            NotificationUtilsDTO notificationUtilsDTO = t.getNotificationDetail();

            notificationUtilsDTO.getDetail().forEach(e -> {

                sb.append("<tr><td>").append(notificationUtilsDTO.getUser()).append("</td><td>").append(e.getCode())
                        .append("</td><td>").append(e.getDescription()).append("</td><td>").append(e.getDateMvt())
                        .append("</td><td>").append(e.getQuantiteInit()).append("</td><td>").append(e.getQuantite())
                        .append("</td><td>").append(e.getQuantiteFinale()).append("</td></tr>");
            });

        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildDeconditionnement(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>")
                .append("Liste des produits déconditionnés").append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th><th>Date</th><th>Quantité initiale</th><th>Quantité </th><th>Stock final</th></tr>");
        notifications.stream().map(NotificationDTO::new).forEach(t -> {
            NotificationUtilsDTO notificationUtilsDTO = t.getNotificationDetail();

            notificationUtilsDTO.getDetail().forEach(e -> {
                List<NotificationUtilsDTO> produitDetails = e.getDetail();
                sb.append("<tr><td>").append(notificationUtilsDTO.getUser()).append("</td><td>").append(e.getCode())
                        .append("</td><td>").append(e.getDescription()).append("</td><td>").append(e.getDateMvt())
                        .append("</td><td>").append(e.getQuantiteInit()).append("</td><td>").append(e.getQuantite())
                        .append("</td><td>").append(e.getQuantiteFinale()).append("</td></tr>");

                if (!CollectionUtils.isEmpty(produitDetails)) {
                    NotificationUtilsDTO de = produitDetails.get(0);
                    sb.append("<tr><td>").append(notificationUtilsDTO.getUser()).append("</td><td>")
                            .append(de.getCode()).append("</td><td>").append(de.getDescription()).append("</td><td>")
                            .append(e.getDateMvt()).append("</td><td>").append(de.getQuantiteInit()).append("</td><td>")
                            .append(de.getQuantite()).append("</td><td>").append(de.getQuantiteFinale())
                            .append("</td></tr>");

                }
            });

        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildModificationProduitCommande(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>")
                .append("Liste des produits modifiés à la commande").append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th><th>Date</th><th>P.Achat.Init</th><th>Nouveau prix.Achat</th><th>Prix.U.Init</th><th>Nouveau.Prix.U</th></tr>");
        notifications.stream().map(NotificationDTO::new).forEach(t -> {
            NotificationUtilsDTO notificationUtilsDTO = t.getNotificationDetail();
            notificationUtilsDTO.getDetail().forEach(e -> {

                sb.append("<tr><td>").append(notificationUtilsDTO.getUser()).append("</td><td>").append(e.getCode())
                        .append("</td><td>").append(e.getDescription()).append("</td><td>").append(e.getDateMvt())
                        .append("</td><td>").append(e.getPrixAchatUni()).append("</td><td>")
                        .append(e.getPrixAchatFinal()).append("</td><td>").append(e.getPrixUni()).append("</td><td>")
                        .append(e.getPrixFinal()).append("</td></tr>");
            });

        });

        sb.append("</table>");
        return sb.toString();
    }

    public static String buildModificationProduitPu(List<Notification> notifications) {
        if (CollectionUtils.isEmpty(notifications)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border: 1px solid black;border-collapse: collapse;'").append(">")
                .append("<caption  style='font-weight: bold;text-align: center;'>")
                .append("Liste des produits dont le prix de vente à été modifié à la vente").append("</caption>")
                .append("<tr><th>Opérateur</th><th>Code</th><th>Libellé</th><th>Date</th><th>Prix.U.Init</th><th>Nouveau.Prix.U</th></tr>");
        notifications.stream().map(NotificationDTO::new).forEach(t -> {
            NotificationUtilsDTO notificationUtilsDTO = t.getNotificationDetail();
            notificationUtilsDTO.getDetail().forEach(e -> {

                sb.append("<tr><td>").append(notificationUtilsDTO.getUser()).append("</td><td>").append(e.getCode())
                        .append("</td><td>").append(e.getDescription()).append("</td><td>").append(e.getDateMvt())
                        .append("</td><td>").append(e.getPrixUni()).append("</td><td>").append(e.getPrixFinal())
                        .append("</td></tr>");
            });

        });

        sb.append("</table>");
        return sb.toString();
    }
}
