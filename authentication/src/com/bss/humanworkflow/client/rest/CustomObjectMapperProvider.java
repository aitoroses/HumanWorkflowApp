package com.bss.humanworkflow.client.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


@Provider
public class CustomObjectMapperProvider implements ContextResolver<ObjectMapper> {
 
    final ObjectMapper defaultObjectMapper;
 
    public CustomObjectMapperProvider() {
        defaultObjectMapper = createDefaultMapper();
    }
    
    @Override
      public ObjectMapper getContext(final Class<?> type) {
          return defaultObjectMapper;
      }
    
 
    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper result = new ObjectMapper();
        result.enable(SerializationFeature.INDENT_OUTPUT);

        return result;
    }
}
