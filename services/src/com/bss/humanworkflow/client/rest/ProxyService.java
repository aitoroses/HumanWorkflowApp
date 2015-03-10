package com.bss.humanworkflow.client.rest;

import com.bss.humanworkflow.client.rest.security.NotAuthenticated;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/")
public class ProxyService {
  
  @GET
  @Path("/proxy.html")
  @Produces(MediaType.TEXT_HTML)
  @NotAuthenticated
  public Response proxy() {
    
    String html =  "<!DOCTYPE HTML>\n" +
    "<html><head>" +
    "<script src=\"//cdn.rawgit.com/jpillora/xdomain/0.6.17/dist/xdomain.min.js\" master=\"*\"></script>" +
    "</head>" +
    "</html>";
    
    return Response.ok().entity(html).build();
  }
}
