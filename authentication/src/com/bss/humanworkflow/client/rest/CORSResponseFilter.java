package com.bss.humanworkflow.client.rest;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;


public class CORSResponseFilter implements ContainerResponseFilter {
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
      
      MultivaluedMap<String, Object> headers = response.getHeaders();
       
      headers.add("Access-Control-Allow-Origin", "*");
      headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");          
      headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization");
                  
      headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate"); // HTTP 1.1
      headers.add(HttpHeaders.EXPIRES, -1);
    }
}
