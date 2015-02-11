package com.bss.humanworkflow.client.rest;

import com.bss.humanworkflow.client.rest.types.AuthenticateInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

@Path("/TaskService")
public class TaskService extends AbstractService {
  
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateTask")
  public Response updateTask(@QueryParam("token") String token, Task task) {

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
  public Response updateOutcome(@QueryParam("token") String token, @PathParam("outcome") String outcome, @PathParam("taskId") String taskId) {
    try {
      getWorkflow().updateOutcome(token, outcome, taskId);
    } catch (Exception e) {
      return WorkflowError.respond(500, "Error updating the outcome.");
    }
    return Response.ok().build();
  }
  
  
  
}
