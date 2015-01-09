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

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

@Path("/TaskService")
public class TaskService extends AbstractService {
  
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateTask")
  public void updateTask(@QueryParam("token") String token, Task task) {
    getWorkflow().updateTask(token, task);
  }
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateOutcome/{taskId}/{outcome}")
  public void updateOutcome(@QueryParam("token") String token, @PathParam("outcome") String outcome, @PathParam("taskId") String taskId) {
    getWorkflow().updateOutcome(token, outcome, taskId);
  }
  
  
  
}
