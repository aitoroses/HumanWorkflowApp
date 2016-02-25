package com.bss.humanworkflow.client.rest;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
 
@Provider
public class ResourceFilterBindingFeature implements DynamicFeature {
 
  @Override
  public void configure(ResourceInfo resourceInfo, FeatureContext context) {
   
    // Apply CORS always
    context.register(CORSResponseFilter.class);
    
  }
}