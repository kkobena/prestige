package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketParameters {

    private static TicketParameters instance;
    public int pageWidth;
    public int height;
    public int marginTop;
    public int marginLeft;
    public int fontSize;
    public int itemBodyWidth;
    public int titleFontSize;
    public String applicationId = "MT7XwvGX6qGAPdfQ", clientId = "fJaOlJgwU2ggcRWmIlbU9s7jY8tbsRy8",
            clientSecret = "UgTURjiCK1rZtsnA",
            header = "Basic ZkphT2xKZ3dVMmdnY1JXbUlsYlU5czdqWTh0YnNSeTg6VWdUVVJqaUNLMXJadHNuQQ==";
    public String email = "dici.servtech@gmail.com", password = "Dici@2020",
            pathsmsapitokenendpoint = "https://api.orange.com/oauth/v2/token",
            pathsmsapisendmessageurl = "https://api.orange.com/smsmessaging/v1/outbound/tel%3A%2B225000000/requests",
            senderAddress = "tel:+225000000";

    public String pathWindow = System.getProperty("user.home") + File.separator + "Documents" + File.separator
            + "config.properties";
    public String pathUnix = System.getProperty("user.home") + File.separator + "Home" + File.separator
            + "config.properties";
    String Os = System.getProperty("os.name");
    Pattern pattern = Pattern.compile("Windows");

    Matcher m = pattern.matcher(Os);
    String path = (m.find() ? pathWindow : pathUnix);

    private TicketParameters() {
        Properties prop = new Properties();

        try (InputStream in = Files.newInputStream(FileSystems.getDefault().getPath(path))) {
            prop.load(in);
            in.close();
            // pageWidth = Integer.valueOf(prop.getProperty("pagewidth"));
            // height = Integer.valueOf(prop.getProperty("height"));
            // marginTop = Integer.valueOf(prop.getProperty("margintop"));
            // fontSize = Integer.valueOf(prop.getProperty("fontsize"));
            // marginLeft = Integer.valueOf(prop.getProperty("marginleft"));
            // itemBodyWidth = Integer.valueOf(prop.getProperty("itembodywidth"));
            // titleFontSize = Integer.valueOf(prop.getProperty("titlefontsize"));

        } catch (IOException e) {
            createFile(prop, path);

        }

        /*
         * InputStream inputStream = null; try {
         *
         * String propFileName = System.getProperty("user.home") + File.separator + "config.properties"; inputStream =
         * getClass().getClassLoader().getResourceAsStream(propFileName); if (inputStream != null) {
         *
         * prop.load(inputStream); pageWidth = Integer.valueOf(prop.getProperty("pagewidth")); height =
         * Integer.valueOf(prop.getProperty("height")); marginTop = Integer.valueOf(prop.getProperty("margintop"));
         * fontSize = Integer.valueOf(prop.getProperty("fontsize")); marginLeft =
         * Integer.valueOf(prop.getProperty("marginleft")); itemBodyWidth =
         * Integer.valueOf(prop.getProperty("itembodywidth")); titleFontSize =
         * Integer.valueOf(prop.getProperty("titlefontsize")); } else { createFile(prop, propFileName); }
         *
         * } catch (Exception ex) { ex.printStackTrace(System.err); } finally { try { inputStream.close(); } catch
         * (Exception e) {
         *
         * }
         *
         * }
         */
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
            prop.setProperty("applicationId", applicationId);
            prop.setProperty("clientId", clientId);
            prop.setProperty("clientSecret", clientSecret);
            prop.setProperty("header", header);
            prop.setProperty("email", email);
            prop.setProperty("pathsmsapitokenendpoint", pathsmsapitokenendpoint);
            prop.setProperty("pathsmsapisendmessageurl", pathsmsapisendmessageurl);

            prop.store(new FileOutputStream(propFileName), "Fichier de configuartion imprimante ticket");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
