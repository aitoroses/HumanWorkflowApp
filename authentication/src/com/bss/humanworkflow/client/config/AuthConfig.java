package com.bss.humanworkflow.client.config;

import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthConfig {
    
    
    private static boolean fileLoaded = false;

    private static String AUTH_DOMAIN = "AUTH_DOMAIN";
    private static String CONFIG_FILE_PATH = "CONFIG_FILE_PATH";
    private static String TEST_MODE = "TEST_MODE";
    private static String CONFIG_FILE_NAME = "hwconfig.properties";


    private static String authDomain = "";
    
    public AuthConfig() {
        super();
    }

    private static String authDomainTest = "t3://soa-server:7003";


    private static Logger logger = Logger.getLogger("com.bss.humanworkflow.client.config.AuthConfig");

    public static String getAuthDomain() {
        //System.out.println("getAuthDomain() >> init");
        String ret = "";
        if (fileLoaded) {
            //System.out.println("getAuthDomain()  >> "+authDomain);
            ret = authDomain;
        } else {
            //System.out.println("getAuthDomain() >> loadConfProperties");
            loadConfProperties();
            //System.out.println("authDomain() >> "+ authDomain);
            logger.info("authDomain >> " + authDomain);
            ret = authDomain;
        }

        return ret;
    }

    private static void loadConfProperties() {

        //
        // Load first Config file
        InputStream configStreamFile = null;
        FileInputStream extConfigFile = null;
        if (!fileLoaded) {
            logger.log(Level.ALL, "Loading configuration properties file....");
            try {
                configStreamFile = AuthConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
                
                if (configStreamFile != null) {
                    Properties propertiesPath = new Properties();
                    propertiesPath.load(configStreamFile);
                    //System.out.println("loadConfProperties() >> loaded internal conf file....");
                    String externalPropertiesFilePath = propertiesPath.getProperty(CONFIG_FILE_PATH);
                    //System.out.println("loadConfProperties() >> CONFIG_FILE_PATH="+CONFIG_FILE_PATH);

                    boolean testMode = propertiesPath.getProperty(TEST_MODE).equals("true");
                    //System.out.println("loadConfProperties() >> TEST_MODE="+TEST_MODE);

                    if (testMode) {

                        authDomain = authDomainTest;
                        
                        logger.info("Configuration for TEST loaded.");
                        logger.info("authDomain: " + authDomain);

                        //System.out.println("Configuration for TEST loaded.");
                        //System.out.println("authDomain: " + authDomain);
                        
                        fileLoaded = true;
                    } else if (externalPropertiesFilePath != null) {
                        //
                        // Load external Config File
                        extConfigFile = new FileInputStream(externalPropertiesFilePath);

                        if (extConfigFile != null) {
                            Properties extPropertiesFile = new Properties();
                            extPropertiesFile.load(extConfigFile);

                            System.out.println("loadConfProperties() >> loaded external conf file....");


                            authDomain = extPropertiesFile.getProperty(AUTH_DOMAIN);


                            logger.info("Configuration loaded.");
                            logger.info("authDomain: " + authDomain);


                            //System.out.println("Configuration loaded.");
                            //System.out.println("authDomain: "+ authDomain);

                            fileLoaded = true;
                        }
                    }
                } else
                    System.out.println("Exception loading property authDomain");
            } catch (Exception ex) {
                //System.out.println("Exception loading property authDomain");
                logger.log(Level.ALL, "Exception loading property authDomain", ex);
            } finally {
                try {
                    if (configStreamFile != null) {
                        configStreamFile.close();
                    }
                    if (extConfigFile != null) {
                        extConfigFile.close();
                    }
                } catch (Exception e) {
                    logger.log(Level.ALL, "Exception Closing configuration files", e);
                }
            }
        }

    }
}
