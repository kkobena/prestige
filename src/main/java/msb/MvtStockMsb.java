/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import rest.service.MouvementProduitService;

/**
 *
 * @author DICI
 */
@JMSDestinationDefinition(
        name = "java:global/queue/stocktransac",
        interfaceName = "javax.jms.Queue",
        destinationName = "stocktransac"
)
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
            propertyValue = "java:global/queue/stocktransac"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MvtStockMsb implements MessageListener {

    @EJB
    MouvementProduitService mvtProduitService;
    @Inject
    JMSContext ctx;
    @Resource(lookup = "java:global/queue/suggestionAuto")
    Queue queue;

    public void callSuggestionMSB(String msg) {
        ctx.createProducer().send(queue, msg);
    }

    @Override
    public void onMessage(Message message) {

        try {
            String id = ((TextMessage) message).getText();
            mvtProduitService.updateVenteStock(id);
            callSuggestionMSB(id);
        } catch (JMSException ex) {
            Logger.getLogger(MouvementStockMSB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
