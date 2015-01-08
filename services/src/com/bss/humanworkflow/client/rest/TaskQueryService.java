package com.bss.humanworkflow.client.rest;

import com.bss.humanworkflow.client.rest.types.AuthenticateInput;

import com.bss.humanworkflow.client.rest.types.TokenInput;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

@Path("/TaskQueryService")
public class TaskQueryService extends AbstractService {
  
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/authenticate")
  public WorkflowContextType authenticate(AuthenticateInput input) {
    WorkflowContextType a = getWorkflow().authenticate(input.getLogin(), input.getPassword());
    return a;
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/queryTasks")
  public List<Task> queryTasks(@QueryParam("token") String token) {
    return getWorkflow().queryTasks(token);
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getTaskDetailsById/{taskId}")
  public Task getTaskDetailsById(@PathParam("taskId") String taskId, @QueryParam("token") String token) {
    Task a = getWorkflow().getTaskDetailsById(token, taskId);
    return a;
  }
  
  
}
