/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import rest.service.ClientService;

/**
 *
 * @author Kobena
 */
@JMSDestinationDefinition(
        name = "java:global/queue/clientjms",
        interfaceName = "javax.jms.Queue",
        destinationName = "clientjms"
)
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
            propertyValue = "java:global/queue/clientjms"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class ClientJms implements MessageListener {

    @EJB
    ClientService clientService;

    @Override
    public void onMessage(Message message) {

        try {
            String id = ((TextMessage) message).getText();
            clientService.updateCompteClientTiersPayantEncourAndPlafond(id);
        } catch (JMSException ex) {
            Logger.getLogger(ClientJms.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
