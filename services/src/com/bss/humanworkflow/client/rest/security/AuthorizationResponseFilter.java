package com.bss.humanworkflow.client.rest.security;

import com.bss.security.JWTokens;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
 
public class AuthorizationResponseFilter implements ContainerResponseFilter {

  public void filter(ContainerRequestContext requestContext,
                     ContainerResponseContext responseContext) {
    
    // Refresh the expire date of the authorizationToken
    String token = (String) requestContext.getProperty("AuthorizationToken");
    
    if (token == null) {
      System.out.println("AuthorizationResponseFilter: AuthorizationToken it's null.");
      return;
    }
    
    MultivaluedMap<String, Object> headers = responseContext.getHeaders();
    
    String newToken = JWTokens.refreshToken(token);
    
    headers.add("Authorization", "Bearer " + newToken);
  }
}

