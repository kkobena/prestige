/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import dal.SmsToken;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import rest.service.SmsService;
import util.DateConverter;
import util.SmsParameters;

/**
 *
 * @author koben
 */
@Stateless
public class SmsImpl implements SmsService{
 @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;
     @Override
     public JSONObject findAccessToken() {
        try {
            Client client = ClientBuilder.newClient();
            SmsParameters sp = SmsParameters.getInstance();
            MultivaluedMap<String, String> formdata = new MultivaluedHashMap<>();
//            String auth = "Basic ".concat(new String(Base64.encodeBase64(sp.clientId.concat(":").concat(sp.clientSecret).getBytes())));
            formdata.add("grant_type", DateConverter.GRANT_TYPE);
            WebTarget myResource = client.target(sp.pathsmsapitokenendpoint);
            Response response = myResource.request(MediaType.APPLICATION_JSON).header("Authorization",StringUtils.isNotEmpty(getBasicHeader())?getBasicHeader(): sp.header)
                    .post(Entity.entity(formdata, MediaType.APPLICATION_FORM_URLENCODED), Response.class);
            if (response.getStatus() == 200) {
                return new JSONObject().put("success", true).put("data", new JSONObject(response.readEntity(String.class)));
            }

            return new JSONObject().put("success", false).put("msg", "Le token n'a pad pu être géneré ");
        } catch (JSONException e) {
            e.printStackTrace(System.err);
            return new JSONObject().put("success", false).put("msg", "Le token n'a pad pu être géneré ");
        }
    }

  
     @Override
    public String getAccessToken() {
        SmsParameters sp = SmsParameters.getInstance();
        return sp.accesstoken;
    }

    private String getBasicHeader(){
        try {
           return  em.find(SmsToken.class, "sms").getHeader();
            
        } catch (Exception e) {
            return  "";
        }
    }
    
}
