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

public class HWFConfig {


    private static boolean fileLoaded = false;

    private static String WSDL_TASKQUERYSERVICE = "WSDL_TASKQUERYSERVICE";
    private static String WSDL_TASKSERVICE = "WSDL_TASKSERVICE";
    private static String CONFIG_FILE_PATH = "CONFIG_FILE_PATH";
    private static String TEST_MODE = "TEST_MODE";
    private static String CONFIG_FILE_NAME = "hwconfig.properties";
    private static String LOAD_FROM_FILE = "LOAD_FROM_FILE";  
    
    private static String HTTP = "http";


    private static String taskQueryServiceWSDL = "";
    private static String taskServiceWSDL = "";


    private static String taskQueryServiceWSDLTest = "http://soa-server:7003/integration/services/TaskQueryService/TaskQueryService?WSDL";
    private static String taskServiceWSDLTest = "http://soa-server:7003/integration/services/TaskService/TaskServicePort?WSDL";

    private static Logger logger = Logger.getLogger("com.bss.humanworkflow.client.config.HWFConfig");

    public static String getTaskQueryServiceWSDL() {
        
        String ret = "";
        if (fileLoaded) {
            //System.out.println("getTaskQueryServiceWSDL() >> "+taskQueryServiceWSDL);
            ret = taskQueryServiceWSDL;
        } else {
            //System.out.println("getTaskQueryServiceWSDL() >> loadConfProperties");
            loadConfProperties();
            //System.out.println("getTaskQueryServiceWSDL() >> "+taskQueryServiceWSDL);
            logger.info("getTaskQueryServiceWSDL() >> " +taskQueryServiceWSDL);
            ret = taskQueryServiceWSDL;
        }

        return ret;

    }

    public static String getTaskServiceWSDL() {
        String ret = "";
        if (fileLoaded) {
            ret = taskServiceWSDL;
        } else {
            loadConfProperties();
            logger.info("getTaskServiceWSDL() >> " + taskServiceWSDL);
            
            ret = taskServiceWSDL;
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
            //System.out.println("loadConfProperties() >> Loading configuration properties file....");
            try {
                configStreamFile = HWFConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
                if (configStreamFile != null) {
                    Properties propertiesPath = new Properties();
                    propertiesPath.load(configStreamFile);
                    
                    boolean loadFromFile = Boolean.parseBoolean(propertiesPath.getProperty(LOAD_FROM_FILE));
                    
                    if(loadFromFile){
                    
                        System.out.println("Loading from file... ");
                    
                        //System.out.println("loadConfProperties() >> loaded internal conf file....");
                        String externalPropertiesFilePath = propertiesPath.getProperty(CONFIG_FILE_PATH);
    
                        boolean testMode = Boolean.parseBoolean(propertiesPath.getProperty(TEST_MODE));
    
                        if (testMode) {
    
                            taskQueryServiceWSDL = taskQueryServiceWSDLTest;
                            taskServiceWSDL = taskServiceWSDLTest;
                            logger.info("Configuration for TEST loaded.");
                            logger.info("taskQueryServiceWSDL: " +
                                        taskQueryServiceWSDL);
                            logger.info("taskServiceWSDL: " + taskServiceWSDL);
    
                            //System.out.println("Configuration for TEST loaded.");
                            //System.out.println("taskQueryServiceWSDL: "+taskQueryServiceWSDL);
                            //System.out.println("taskServiceWSDL: "+taskServiceWSDL);
                            fileLoaded = true;
                        } else if (externalPropertiesFilePath != null) {
                            //
                            // Load external Config File
                            extConfigFile =
                                    new FileInputStream(externalPropertiesFilePath);
    
                            if (extConfigFile != null) {
                                Properties extPropertiesFile = new Properties();
                                extPropertiesFile.load(extConfigFile);
    
                                System.out.println("loadConfProperties() >> loaded external conf file....");
    
    
                                taskQueryServiceWSDL = extPropertiesFile.getProperty(WSDL_TASKQUERYSERVICE);
                                taskServiceWSDL = extPropertiesFile.getProperty(WSDL_TASKSERVICE);
    
                                logger.info("Configuration loaded.");
                                logger.info("taskQueryServiceWSDL: " + taskQueryServiceWSDL);
                                logger.info("taskServiceWSDL: " + taskServiceWSDL);
    
                                //System.out.println("Configuration loaded.");
                                //System.out.println("taskQueryServiceWSDL: "+taskQueryServiceWSDL);
                                //System.out.println("taskServiceWSDL: "+taskServiceWSDL);
    
                                fileLoaded = true;
                            }
                        }
                    } else {
                        System.out.println("Loading from getDomain(): ");
                        fileLoaded = true;
                        
                        String domain = getDomain(true, HTTP);
                        
                        taskQueryServiceWSDL = domain + "/integration/services/TaskQueryService/TaskQueryService?WSDL";
                        taskServiceWSDL = domain + "/integration/services/TaskService/TaskServicePort?WSDL";
                        
                        System.out.println("PROCESS_MAIN_WSDL EAPPServiceConfig: " + taskQueryServiceWSDL);
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.ALL,
                           "Exception loading endpoints for  taskQueryServiceWSDL and taskerviceWSDL", ex);


            } finally {
                try {
                    if (configStreamFile != null) {
                        configStreamFile.close();
                    }
                    if (extConfigFile != null) {
                        extConfigFile.close();
                    }
                } catch (Exception e) {
                    logger.log(Level.ALL,
                               "Exception Closing configuration files", e);
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

    public HWFConfig() {
        super();
    }
}
