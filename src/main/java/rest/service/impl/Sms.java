/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import util.SmsParameters;

/**
 *
 * @author koben
 */
public class Sms implements Runnable {

    private String message, receiverAddres;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiverAddres() {
        return receiverAddres;
    }

    public void setReceiverAddres(String receiverAddres) {
        this.receiverAddres = receiverAddres;
    }

    public void sendSMS() {
        try {
            Client client = ClientBuilder.newClient();
            SmsParameters sp = SmsParameters.getInstance();
            String address = getReceiverAddres();
            if (StringUtils.isEmpty(address)) {
                address = sp.mobile;
            }
            JSONObject jSONObject = new JSONObject();
            JSONObject outboundSMSMessageRequest = new JSONObject();
            outboundSMSMessageRequest.put("address", "tel:+225" + address);
            outboundSMSMessageRequest.put("senderAddress", sp.senderAddress);
            JSONObject outboundSMSTextMessage = new JSONObject();
            outboundSMSTextMessage.put("message", getMessage());
            outboundSMSMessageRequest.put("outboundSMSTextMessage", outboundSMSTextMessage);
            jSONObject.put("outboundSMSMessageRequest", outboundSMSMessageRequest);
            WebTarget myResource = client.target(sp.pathsmsapisendmessageurl);
            Response response = myResource.request().header("Authorization", "Bearer ".concat(sp.accesstoken))
                    .post(Entity.entity(jSONObject.toString(), MediaType.APPLICATION_JSON_TYPE));
            System.out.println("response ---  " + response.getStatus());
            System.out.println("jSONObject ---  " + jSONObject);
            System.out.println("response ---  " + response.readEntity(String.class));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void run() {
        sendSMS();
    }

}
