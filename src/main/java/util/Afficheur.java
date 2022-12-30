/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.sun.comm.Win32Driver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;
import toolkits.utils.jdom;

/**
 *
 * @author kkoffi
 */
public class Afficheur {

    private static final Logger LOG = Logger.getLogger(Afficheur.class.getName());

    public BufferedReader bufRead; //flux de lecture du port
    public OutputStream outStream; //flux d'écriture du port
    public CommPortIdentifier portId; //identifiant du port
    public SerialPort sPort; //le port série
    private static Afficheur INSTANCE = null;
    public Boolean isOpen;
    public CommPort cp;

    public  static Afficheur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Afficheur();
        }
        return INSTANCE;
    }

    private Afficheur() {
        try {
            Win32Driver w32Driver = new Win32Driver();
            w32Driver.initialize();
//            sPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (Exception e) {
            
        }
        try {
            portId = CommPortIdentifier.getPortIdentifier(jdom.com_port_displayer);
        } catch (NoSuchPortException e) {
            LOG.log(Level.INFO, "--------------->>>> NoSuchPortException {0} --->>> error msg {1} ", new Object[]{jdom.com_port_displayer,e.getLocalizedMessage()});
        }
        try {
            sPort = (SerialPort) portId.open(jdom.APP_NAME, 3000);
        } catch (PortInUseException e) {
              
        }

        try {
            outStream = sPort.getOutputStream();
            bufRead
                    = new BufferedReader(
                            new InputStreamReader(sPort.getInputStream()));
        } catch (IOException e) {
           
        }
        try {
            sPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
              
        }

    }

    public void communique(char envoie) {
        try {
            //affiche un caractere a l'ecran
            outStream.write( envoie);
        } catch (IOException e) {
          
        }
    }

    /**
     * Méthode de fermeture des flux et port.
     */
    public void close() {
        try {
//            bufRead.close();
            outStream.close();
            sPort.close();
        } catch (IOException e) {
           
        }

    }

    public void open() {
        try {

            sPort = (SerialPort) portId.open(jdom.APP_NAME, 1000);

            isOpen = true;
        } catch (PortInUseException e) {
            isOpen = false;
         
        }
        //règle les paramètres de la connexion
        try {
            sPort.setSerialPortParams(
                    9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {

         
        }
        //récupération du flux de lecture et écriture du port
        try {
            outStream = sPort.getOutputStream();
            bufRead
                    = new BufferedReader(
                            new InputStreamReader(sPort.getInputStream()));
        } catch (IOException e) {
       
        }

    }

    public void affichage(String data, String position) {
        String remain = "";
        try {
            for (int i = 0; i < (20 - data.length()); i++) {
                remain += " ";
            }
            if (position.equalsIgnoreCase("begin")) {
                data = remain + data;
            } else {
                data = data + remain;
            }

            String[] tab = data.split("");

            for (String tab1 : tab) {
                communique(tab1.charAt(0));
            }
        } catch (Exception e) {
           
        }
    }

    public void affichage(String data) {
        String remain = "";
        try {
            for (int i = 0; i < (20 - data.length()); i++) {
                remain += " ";
            }
            data = data + remain;
            String[] tab = data.split("");

            for (String tab1 : tab) {
                communique(tab1.charAt(0));
            }
        } catch (Exception e) {
          
        }
    }
}
