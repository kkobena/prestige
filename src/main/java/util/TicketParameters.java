package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TicketParameters {

    private static TicketParameters instance;
    public int pageWidth = 80;
    public int height = 80;
    public int marginTop = 2;
    public int marginLeft = 2;
    public int fontSize = 10;
    public int itemBodyWidth = 76;
    public int titleFontSize = 12;

    private TicketParameters() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {

                prop.load(inputStream);
                pageWidth = Integer.valueOf(prop.getProperty("pagewidth"));
                height = Integer.valueOf(prop.getProperty("height"));
                marginTop = Integer.valueOf(prop.getProperty("margintop"));
                fontSize = Integer.valueOf(prop.getProperty("fontsize"));
                marginLeft = Integer.valueOf(prop.getProperty("marginleft"));
                itemBodyWidth = Integer.valueOf(prop.getProperty("itembodywidth"));
                titleFontSize = Integer.valueOf(prop.getProperty("titlefontsize"));
            } else {
                createFile(prop, propFileName);
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {

            }

        }
    }

    ;

	public static TicketParameters getInstance() {
        if (instance == null) {
            instance = new TicketParameters();

        }
        return instance;
    }

    private void createFile(Properties prop, String propFileName) {
        try {
            prop.setProperty("pagewidth", "80");
            prop.setProperty("height", "80");
            prop.setProperty("margintop", "2");
            prop.setProperty("marginleft", "2");
            prop.setProperty("fontsize", "10");
            prop.setProperty("itembodywidth", "76");
            prop.setProperty("titlefontsize", "12");
            prop.store(new FileOutputStream(propFileName), "Fichier de configuartion imprimante ticket");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
