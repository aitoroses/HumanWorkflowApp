package com.bss.humanworkflow.client.rest.security;

import com.bss.security.JWTokens;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;

import java.util.Date;

import javax.ws.rs.container.ContainerRequestContext;

public class TokenValidator {
    
  public static boolean isValid(String token, ContainerRequestContext request) {
    
    try {
      
      // Split the token bearer extraction from authentication header
      String[] tokensplit = token.split(" ");
      if (tokensplit[0].equals("Bearer")) {
        token = tokensplit[1];
      } else {
        token = tokensplit[0];
      }
      
      // Put the token as a property into the request
      request.setProperty("AuthorizationToken", token);

      ReadOnlyJWTClaimsSet jw = JWTokens.parseToken(token);
      
      // Get the subject
      String subject = jw.getSubject();
      // Get the accessLevel
      Long accessLevel = (Long) jw.getClaim("AccessLevel");
      // Get the locale
      String locale = (String) jw.getClaim("locale");
      // Get Workflow Context
      String wf = (String) jw.getClaim("workflowContext");
      // Expiricy date
      Date expire = (Date) jw.getExpirationTime();
      
      // Expired?
      boolean expired = (new Date()).after(expire);
      
      // Store the props in the request to then require it with "@Context Request request"
      request.setProperty("user", subject);
      request.setProperty("accessLevel", accessLevel);
      request.setProperty("locale", locale);
      request.setProperty("workflowContext", wf);


      // Print
      System.out.println("=======================================");
      System.out.println("User         ========> " + subject);
      System.out.println("Access Level ========> " + accessLevel);
      System.out.println("Locale       ========> " + locale);
      System.out.println("Token        ========> " + wf);
      System.out.println("Now          ========> " + new Date());
      System.out.println("Expires      ========> " + expire);
      System.out.println("Expired      ========> " + expired);
      System.out.println("=======================================");

      
      // If user has access level higher than 0, it's allowed
      return ((accessLevel != null) && accessLevel > 0) && !expired;
      
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
