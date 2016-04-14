package com.bss.humanworkflow.client.config;

import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import weblogic.rjvm.JVMID;

public class AuthConfig {
    
    
    private static boolean fileLoaded = false;

    private static String AUTH_DOMAIN = "AUTH_DOMAIN";
    private static String CONFIG_FILE_PATH = "CONFIG_FILE_PATH";
    private static String TEST_MODE = "TEST_MODE";
    private static String CONFIG_FILE_NAME = "hwconfig.properties";
    private static String LOAD_FROM_FILE = "LOAD_FROM_FILE";  
    
    private static String T = "t3";


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
                    
                    boolean loadFromFile = Boolean.parseBoolean(propertiesPath.getProperty(LOAD_FROM_FILE));
                    
                    if(loadFromFile){
                    
                        logger.info("Loading from file... ");
                        //System.out.println("loadConfProperties() >> loaded internal conf file....");
                        String externalPropertiesFilePath = propertiesPath.getProperty(CONFIG_FILE_PATH);
                        //System.out.println("loadConfProperties() >> CONFIG_FILE_PATH="+CONFIG_FILE_PATH);
    
                        boolean testMode = Boolean.parseBoolean(propertiesPath.getProperty(TEST_MODE));
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
    
                                fileLoaded = true;
                            }
                        }
                    } else {
                        logger.info("Loading from getDomain(): ");
                        fileLoaded = true;
                        
                        String domain = getDomain(true, T);
                        
                        authDomain = domain;
                        
                        logger.info("PROCESS_MAIN_WSDL EAPPServiceConfig: " + authDomain);
                        
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
    
    /**
     * Author: MAV
     * Date: 07/04/2016
     *
     * @return
     */
    private static String getDomain(boolean checkSafePort, String protocol)  {
    
        JVMID jvmid = JVMID.localID();
        String serverName = jvmid.getServerName();
        String serverAddress = jvmid.getAddress();
        
        String frontEndAddress = "";
        
        InitialContext ctx;
        boolean https = false;
        int port = 7003;
        
        try {
            ctx = new InitialContext();

            // weblogic.management.configuration.ServerMBean
            MBeanServer server = (MBeanServer) ctx.lookup("java:comp/env/jmx/runtime");
    
            ObjectName objName = new ObjectName("com.bea:Name=" + serverName + ",Type=Server"); 

            port = (Integer) server.getAttribute(objName, "ListenPort");
            
            
            ObjectName objWebServerName = new ObjectName("com.bea:Name=" + serverName + ",Type=WebServer,Server=soa_server1"); 
            
            frontEndAddress = (String) server.getAttribute(objWebServerName, "FrontendHost");
            
            // weblogic.management.configuration.SSLMBean
            ObjectName sslObjName = new ObjectName("com.bea:Name=" + serverName + ",Type=SSL,Server=soa_server1"); 
            
            if(checkSafePort)
                https = (Boolean) server.getAttribute(sslObjName, "Enabled");
            else
                https = false;
            
            if(https && frontEndAddress!= null && !frontEndAddress.equals("")){
                port = (Integer) server.getAttribute(sslObjName, "ListenPort");
            }
            else{
                https = false;
                frontEndAddress = serverAddress;
            }
            
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        }
        
        String domainAddress = protocol + (https ? "s" : "") + "://" + frontEndAddress + ":" + port;
        /******
          *System.out.println("\n**************************************");
          *System.out.println("Domain Address EAPPServiceConfig: " + domainAddress);
          *System.out.println("**************************************\n");
        ******/
        
        return domainAddress;
    }    
}
