/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import toolkits.utils.jdom;

/**
 *
 * @author kkoffi
 */
public class Afficheur {

    private static final Logger LOG = Logger.getLogger(Afficheur.class.getName());
    private OutputStream outStream;
    private SerialPort serialPort; // le port série
    private static Afficheur INSTANCE = null;

    public static Afficheur getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Afficheur();
        }
        return INSTANCE;
    }

    private Afficheur() {

        try {
            serialPort = SerialPort.getCommPort(jdom.com_port_displayer);
            outStream = serialPort.getOutputStream();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
        }

        /*
         * try { outStream = sPort.getOutputStream(); bufRead = new BufferedReader(new
         * InputStreamReader(sPort.getInputStream())); } catch (IOException e) {
         *
         * } try { // sPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
         * SerialPort.PARITY_NONE); } catch (UnsupportedCommOperationException e) {
         *
         * }
         */
    }

    public void communique(char envoie) {
        open();
        try {
            // affiche un caractere a l'ecran
            outStream.write(envoie);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
        }
    }

    /**
     * Méthode de fermeture des flux et port.
     */
    public void close() {
        try {
            serialPort.closePort();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
        }

    }

    public void open() {
        try {
            if (!serialPort.isOpen()) {
                serialPort.openPort();
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());

        }
        // règle les paramètres de la connexion

        // sPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }

    public void affichage(String data, String position) {
        open();
        String remain = "";
        try {
            for (int i = 0; i < (20 - data.length()); i++) {
                remain += " ";
            }
            if (position.equals("begin")) {
                data = remain + data;
            } else {
                data = data + remain;
            }

            String[] tab = data.split("");

            for (String tab1 : tab) {
                communique(tab1.charAt(0));
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
        }
    }

    public void affichage(String data) {
        open();
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
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
        }
    }
}
