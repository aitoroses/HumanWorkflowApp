package com.bss.humanworkflow.client.rest;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;

import oracle.bpel.services.workflow.task.model.Task;

@Path("/TaskService")
public class TaskService extends AbstractService {
  
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateTask")
  public Response updateTask(@Context HttpServletRequest request, Task task, @QueryParam("token") String tokenParam) {
    String token = (String) request.getAttribute("workflowContext");
    
    // If there is no presence of token in the request attributes we will use the querystring token param;
    if (token == null) {
      token = tokenParam;
    }
    
    try {
      getWorkflow().updateTask(token, task);
    } catch (Exception e) {
      return WorkflowError.respond(500, "Error updating the task.");
    }
    return Response.ok().build();
  }
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateOutcome/{taskId}/{outcome}")
  public Response updateOutcome(@Context HttpServletRequest request, @PathParam("outcome") String outcome, @PathParam("taskId") String taskId, @QueryParam("token") String tokenParam) {
    String token = (String) request.getAttribute("workflowContext");
    
    // If there is no presence of token in the request attributes we will use the querystring token param;
    if (token == null) {
      token = tokenParam;
    }
    
    try {
      getWorkflow().updateOutcome(token, outcome, taskId);
    } catch (Exception e) {
      return WorkflowError.respond(500, "Error updating the outcome.");
    }
    return Response.ok().build();
  }
  
  
  
}
