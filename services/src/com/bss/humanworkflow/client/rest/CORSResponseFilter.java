package com.bss.humanworkflow.client.rest;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

public class CORSResponseFilter implements ContainerResponseFilter {
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
      MultivaluedMap<String, Object> headers = response.getHttpHeaders();
      headers.remove(HttpHeaders.CONTENT_TYPE);

      headers.add("Access-Control-Allow-Origin", "*");
      headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");      
      headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");

      headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
      
      headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate"); // HTTP 1.1
      headers.add(HttpHeaders.EXPIRES, -1);
      
      return response;
    }
}
