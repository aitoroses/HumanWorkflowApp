package com.bss.humanworkflow.client.rest;

import javax.ws.rs.ApplicationPath;

import jersey.repackaged.com.google.common.collect.Sets;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {
    public Application() {  
      super(Sets.<Class<?>>newHashSet(
          
        // resources, other features and providers would also go here
        ResourceFilterBindingFeature.class
      ));
      
      // Specify packages  
      packages("com.bss.humanworkflow.client.rest")
        .register(CustomObjectMapperProvider.class)
        .register(JacksonFeature.class);
    }
}
