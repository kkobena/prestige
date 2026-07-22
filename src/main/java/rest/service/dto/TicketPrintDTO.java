/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.dto;

import dal.SupportTicket;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

/**
 * Ligne du dictionnaire de suivi des pannes (impression des tickets support). Tous les champs sont des chaines deja
 * formatees pour le rapport Jasper.
 *
 * @author koben
 */
public class TicketPrintDTO {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String numero;
    private final String dateCreation;
    private final String module;
    private final String type;
    private final String priorite;
    private final String sujet;
    private final String statut;
    private final String affecteA;
    private final String utilisateur;

    public TicketPrintDTO(SupportTicket ticket) {
        this.numero = StringUtils.defaultString(ticket.getNumero());
        this.dateCreation = ticket.getCreatedAt() != null ? ticket.getCreatedAt().format(DATE_FORMAT) : "";
        this.module = StringUtils.defaultString(ticket.getModule());
        this.type = StringUtils.defaultString(ticket.getType());
        this.priorite = StringUtils.defaultString(ticket.getPriorite());
        this.sujet = StringUtils.defaultString(ticket.getSujet());
        this.statut = StringUtils.defaultString(ticket.getStatutTicket());
        this.affecteA = StringUtils.defaultString(ticket.getAffecteA());
        this.utilisateur = StringUtils.defaultString(ticket.getUtilisateur());
    }

    public String getNumero() {
        return numero;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public String getModule() {
        return module;
    }

    public String getType() {
        return type;
    }

    public String getPriorite() {
        return priorite;
    }

    public String getSujet() {
        return sujet;
    }

    public String getStatut() {
        return statut;
    }

    public String getAffecteA() {
        return affecteA;
    }

    public String getUtilisateur() {
        return utilisateur;
    }
}
