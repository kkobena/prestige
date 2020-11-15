/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonTasks.dto;

import dal.NotificationClient;
import dal.TClient;
import java.io.Serializable;

/**
 *
 * @author koben
 */
public class NotificationClientDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fullName;
    private String clientPhone;
    private String clientEmail;
    private String firstName;
    private String LastName;
    private String email;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public NotificationClientDTO() {
    }

    public NotificationClientDTO(NotificationClient notificationClient) {
        TClient client = notificationClient.getClient();
        this.LastName = client.getStrFIRSTNAME() + " " + client.getStrLASTNAME();
        this.email = client.getEmail();
        this.clientPhone = client.getStrADRESSE();
        this.LastName = client.getStrLASTNAME();
        this.firstName = client.getStrFIRSTNAME();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
