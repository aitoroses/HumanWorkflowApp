package com.bss.humanworkflow.client.rest;

import javax.ws.rs.core.Response;

public class WorkflowError {
  
  public static Response respond(Integer status, String message) {
    String m = "{\"status\": "+ status +", \"error\": \"" + message + "\"}";
    return Response.status(status).entity(m).build();
  }
}
