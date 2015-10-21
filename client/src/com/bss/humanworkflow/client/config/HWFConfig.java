package com.bss.humanworkflow.client.config;

import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HWFConfig {


    private static boolean fileLoaded = false;

    private static String WSDL_TASKQUERYSERVICE = "WSDL_TASKQUERYSERVICE";
    private static String WSDL_TASKSERVICE = "WSDL_TASKSERVICE";
    private static String CONFIG_FILE_PATH = "CONFIG_FILE_PATH";
    private static String TEST_MODE = "TEST_MODE";
    private static String CONFIG_FILE_NAME = "hwconfig.properties";


    private static String taskQueryServiceWSDL = "";
    private static String taskServiceWSDL = "";


    private static String taskQueryServiceWSDLTest =
        "http://soa-server:7003/integration/services/TaskQueryService/TaskQueryService?WSDL";
    private static String taskServiceWSDLTest =
        "http://soa-server:7003/integration/services/TaskService/TaskServicePort?WSDL";

    private static Logger logger =
        Logger.getLogger("com.bss.humanworkflow.client.config.HWFConfig");

    public static String getTaskQueryServiceWSDL() {
        //System.out.println("getTaskQueryServiceWSDL() >> init");
        String ret = "";
        if (fileLoaded) {
            //System.out.println("getTaskQueryServiceWSDL() >> "+taskQueryServiceWSDL);
            ret = taskQueryServiceWSDL;
        } else {
            //System.out.println("getTaskQueryServiceWSDL() >> loadConfProperties");
            loadConfProperties();
            //System.out.println("getTaskQueryServiceWSDL() >> "+taskQueryServiceWSDL);
            logger.info("getTaskQueryServiceWSDL() >> " +
                        taskQueryServiceWSDL);
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
                configStreamFile =
                        HWFConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
                if (configStreamFile != null) {
                    Properties propertiesPath = new Properties();
                    propertiesPath.load(configStreamFile);
                    //System.out.println("loadConfProperties() >> loaded internal conf file....");
                    String externalPropertiesFilePath =
                        propertiesPath.getProperty(CONFIG_FILE_PATH);
                    //System.out.println("loadConfProperties() >> CONFIG_FILE_PATH="+CONFIG_FILE_PATH);

                    boolean testMode =
                        propertiesPath.getProperty(TEST_MODE).equals("true");
                    //System.out.println("loadConfProperties() >> TEST_MODE="+TEST_MODE);

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


                            taskQueryServiceWSDL =
                                    extPropertiesFile.getProperty(WSDL_TASKQUERYSERVICE);
                            taskServiceWSDL =
                                    extPropertiesFile.getProperty(WSDL_TASKSERVICE);

                            logger.info("Configuration loaded.");
                            logger.info("taskQueryServiceWSDL: " +
                                        taskQueryServiceWSDL);
                            logger.info("taskServiceWSDL: " + taskServiceWSDL);

                            //System.out.println("Configuration loaded.");
                            //System.out.println("taskQueryServiceWSDL: "+taskQueryServiceWSDL);
                            //System.out.println("taskServiceWSDL: "+taskServiceWSDL);

                            fileLoaded = true;
                        }
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.ALL,
                           "Exception loading endpoints for  taskQueryServiceWSDL and taskerviceWSDL",
                           ex);


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

    public HWFConfig() {
        super();
    }
}
