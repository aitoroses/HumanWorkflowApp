package com.bss.humanworkflow.client.rest.security;

import com.bss.security.JWTokens;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
 
public class AuthorizationRequestFilter implements ContainerRequestFilter {
 
    @Override
    public void filter(ContainerRequestContext requestContext)
                    throws IOException {
      
        // Validate the token
        String token = (String) requestContext.getHeaders().getFirst("Authorization");
        
        if (!TokenValidator.isValid(token, requestContext)) {
          requestContext.abortWith(Response
              .status(Response.Status.UNAUTHORIZED)
              .header("Content-Type", "text/plain")
              .entity("User cannot access the resource.")
              .build());
        } else {
          
        };
    }
}
