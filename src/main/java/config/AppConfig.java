package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;


/**
 *
 * @author koben
 */
@Singleton
@Startup
public class AppConfig {

    private static final Logger LOG = Logger.getLogger(AppConfig.class.getName());

    private final Properties properties = new Properties();

    private String applicationMode;
    private String modReappro;
   /* private String clientId;
    private String clientSecret;
    private String header;
    private String email;
    private String password;
    private String pathsmsapitokenendpoint;
    private String pathsmsapisendmessageurl;
    private String senderAddress;
    private String accesstoken;
    private String expiresin;
    private String mobile;
    private String smtpHost;
    private String protocol;
    private String mailOfficine;
    private String fneUrl;
    private String fnePkey;
    private String fnepointOfSale;
    private String pharmaMlDir;*/

    @PostConstruct
    public void init() {

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator
                + "dicisms.properties";

        try (InputStream in = new FileInputStream(path)) {

            properties.load(in);

            applicationMode = properties.getProperty("mode");//server/client
            modReappro = properties.getProperty("mode.reappro");//server/client
          /*  clientId = properties.getProperty("clientId");
            clientSecret = properties.getProperty("clientSecret");
            header = properties.getProperty("header");
            email = properties.getProperty("email");
            password = properties.getProperty("password");
            pathsmsapitokenendpoint = properties.getProperty("pathsmsapitokenendpoint");
            pathsmsapisendmessageurl = properties.getProperty("pathsmsapisendmessageurl");
            senderAddress = properties.getProperty("senderAddress");
            accesstoken = properties.getProperty("accesstoken");
            expiresin = properties.getProperty("expiresin");
            mobile = properties.getProperty("mobile");
            smtpHost = properties.getProperty("smtphost", "smtp.gmail.com");
            protocol = properties.getProperty("protocol", "smtps");
            mailOfficine = properties.getProperty("usermail");
            fneUrl = properties.getProperty("fneUrl");
            fnePkey = properties.getProperty("fnePkey");
            fnepointOfSale = properties.getProperty("fnepointOfSale");
            pharmaMlDir = properties.getProperty("pharmaMlDir");*/

            LOG.info("Notification configuration loaded from " + path);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to load configuration file", e);
        }
    }
    
   
/*
    public String getApplicationId() {
        return applicationId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getHeader() {
        return header;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPathsmsapitokenendpoint() {
        return pathsmsapitokenendpoint;
    }

    public String getPathsmsapisendmessageurl() {
        return pathsmsapisendmessageurl;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public String getExpiresin() {
        return expiresin;
    }

    public String getMobile() {
        return mobile;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMailOfficine() {
        return mailOfficine;
    }

    public String getFneUrl() {
        return fneUrl;
    }

    public String getFnePkey() {
        return fnePkey;
    }

    public String getFnepointOfSale() {
        return fnepointOfSale;
    }

    public String getPharmaMlDir() {
        return pharmaMlDir;
    }*/

    public String getModReappro() {
        return modReappro;
    }

    public String getApplicationMode() {
        return applicationMode;
    }

   
}
