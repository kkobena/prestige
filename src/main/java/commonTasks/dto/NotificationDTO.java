/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.Notification;
import dal.TUser;
import dal.enumeration.Statut;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author koben
 */
public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;

    private String message;

    private String statut;

    private String canal;

    private String typeNotification;
    private String modfiedAt;

    private String user;

    private String userTo;
    private List<NotificationClientDTO> clients = new ArrayList<>();

    public NotificationDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(String typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getModfiedAt() {
        return modfiedAt;
    }

    public void setModfiedAt(String modfiedAt) {
        this.modfiedAt = modfiedAt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public List<NotificationClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<NotificationClientDTO> clients) {
        this.clients = clients;
    }

    public NotificationDTO addClients(List<NotificationClientDTO> clients) {
        this.clients = clients;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NotificationDTO other = (NotificationDTO) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public NotificationDTO(Notification n, List<NotificationClientDTO> clients) {
        this.id = n.getId();
        this.message = n.getMessage();
        if (n.getStatut() == Statut.SENT) {
            this.statut = "Envoyé";
        } else {
            this.statut = "Non envoyé";
        }

        this.canal = n.getCanal().name();
        this.typeNotification = n.getTypeNotification().getValue();
        this.modfiedAt = n.getModfiedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        TUser user = n.getUser();
        if (user != null) {
            this.user = user.getStrFIRSTNAME() + " " + user.getStrLASTNAME();
        }
        TUser userTo = n.getUserTo();
        if (userTo != null) {
            this.userTo = userTo.getStrFIRSTNAME() + " " + userTo.getStrLASTNAME();
        }
        this.clients = clients;
    }

}
