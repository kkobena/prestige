/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dal.enumeration.Canal;
import dal.enumeration.Statut;
import dal.enumeration.TypeNotification;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author koben
 */
@Entity
@Table(name = "notification", indexes = { @Index(name = "notificationIdex1", columnList = "canal"),
        @Index(name = "notificationStatut", columnList = "statut") })
@NamedQueries({
        @NamedQuery(name = "Notification.findAllByCreatedAtAndStatus", query = "SELECT o FROM Notification o WHERE o.createdAt >= :createdAt AND o.statut=:statut "),
        @NamedQuery(name = "Notification.findAllByStatus", query = "SELECT o FROM Notification o LEFT JOIN FETCH o.notificationClients WHERE  o.statut=:statut "),
        @NamedQuery(name = "Notification.findAllByCreatedAtAndStatusAndCanal", query = "SELECT o FROM Notification o WHERE o.createdAt >= :createdAt AND  o.statut=:statut AND o.canal IN :canaux")

})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private String id = UUID.randomUUID().toString();
    @NotBlank
    @NotNull
    @Column(name = "message", length = 3000, nullable = false)
    private String message;
    @NotNull
    @Column(name = "statut")
    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.NOT_SEND;
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "canal", nullable = false)
    private Canal canal;
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type_notification", nullable = false)
    private TypeNotification typeNotification;
    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @NotNull
    @Column(name = "modfied_at", nullable = false)
    private LocalDateTime modfiedAt = LocalDateTime.now();
    @JoinColumn(name = "user_id", referencedColumnName = "lg_USER_ID")
    @ManyToOne
    private TUser user;
    @JoinColumn(name = "user_to", referencedColumnName = "lg_USER_ID")
    @ManyToOne
    private TUser userTo;
    @OneToMany(cascade = { CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "notification")
    private Collection<NotificationClient> notificationClients = new ArrayList<>();
    @Column(name = "number_attempt", nullable = false)
    private int numberAttempt = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public Notification message(String message) {
        this.message = message;
        return this;
    }

    public Notification statut(Statut statut) {
        this.statut = statut;
        return this;
    }

    public Notification canal(Canal canal) {
        this.canal = canal;
        return this;
    }

    public Notification typeNotification(TypeNotification typeNotification) {
        this.typeNotification = typeNotification;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Canal getCanal() {
        return canal;
    }

    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public TypeNotification getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(TypeNotification typeNotification) {
        this.typeNotification = typeNotification;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModfiedAt() {
        return modfiedAt;
    }

    public void setModfiedAt(LocalDateTime modfiedAt) {
        this.modfiedAt = modfiedAt;
    }

    public TUser getUser() {
        return user;
    }

    public Notification addUser(TUser user) {
        this.user = user;
        return this;
    }

    public void setUser(TUser user) {
        this.user = user;
    }

    public TUser getUserTo() {
        return userTo;
    }

    public Notification addUserTo(TUser userTo) {
        this.userTo = userTo;
        return this;
    }

    public void setUserTo(TUser userTo) {
        this.userTo = userTo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.id);
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
        final Notification other = (Notification) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Notification() {
    }

    public Collection<NotificationClient> getNotificationClients() {
        return notificationClients;
    }

    public void setNotificationClients(Collection<NotificationClient> notificationClients) {
        this.notificationClients = notificationClients;
    }

    public Notification addNotificationClients(NotificationClient notificationClient) {
        notificationClient.setNotification(this);
        this.getNotificationClients().add(notificationClient);
        return this;

    }

    public int getNumberAttempt() {
        return numberAttempt;
    }

    public void setNumberAttempt(int numberAttempt) {
        this.numberAttempt = numberAttempt;
    }

    public Notification numberAttempt(int numberAttempt) {
        this.numberAttempt = numberAttempt;
        return this;
    }
}
