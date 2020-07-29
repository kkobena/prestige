/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import rest.service.SuggestionService;

/**
 *
 * @author Kobena
 */
@JMSDestinationDefinition(
        name = "java:global/queue/suggestionAuto",
        interfaceName = "javax.jms.Queue",
        destinationName = "suggestionAuto"
)
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
            propertyValue = "java:global/queue/suggestionAuto"),
//    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
      @ActivationConfigProperty(propertyName = "acknowledgeMode",propertyValue = "Dups-ok-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class SuggestionMSB implements MessageListener {

    @EJB
    SuggestionService suggestionService;

    @Override
    public void onMessage(Message message) {

        try {
            String id = ((TextMessage) message).getText();
            suggestionService.makeSuggestionAuto(id);

        } catch (JMSException e) {
            e.printStackTrace(System.err);
        }
    }

}
