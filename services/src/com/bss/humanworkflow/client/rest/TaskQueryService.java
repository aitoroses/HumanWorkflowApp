package com.bss.humanworkflow.client.rest;

import com.bss.humanworkflow.client.impl.view.Criteria;
import com.bss.humanworkflow.client.impl.view.CriteriaInput;
import com.bss.humanworkflow.client.rest.security.NotAuthenticated;
import com.bss.humanworkflow.client.rest.security.Utils;
import com.bss.humanworkflow.client.rest.types.AuthenticateInput;

import com.bss.security.JWTokens;

import com.novartis.bpm.um.UMMClientProxy;

import com.novartis.bpm.um.client.InvokeContext;

import java.util.HashMap;
import java.util.List;

import javax.security.auth.Subject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;


import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

import weblogic.security.URLCallbackHandler;
import weblogic.security.services.Authentication;

@Path("/TaskQueryService")
public class TaskQueryService extends AbstractService {
    
  @POST
  @Path("/authenticate")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @NotAuthenticated
  public Response authenticate(
    AuthenticateInput input, 
    @Context HttpServletResponse res, 
    @Context HttpServletRequest req, 
    @Context ServletContext context,
    @QueryParam("ummContext") String ummContext ) {
    
    try {
      WorkflowContextType wf = getWorkflow().authenticate(input.getLogin(), input.getPassword());
      
      String lang = wf.getLocale().split("#")[0].split("_")[0];
      
      HashMap claims = new HashMap<String, Object>();
      
      claims.put("workflowContext", wf.getToken());
      claims.put("locale", lang);
      claims.put("AccessLevel", 1);

      String token = JWTokens.getToken(input.getLogin(), claims);
      
      // ADF Authentication
      adfAuthenticate(input.getLogin(), input.getPassword(), req);
      
      // UMM ensure user existance (optional, only if appId specified)
      if (ummContext != null && !ummContext.equals("")) {
        ensureUMMUser(input.getLogin(), ummContext);
      }
      
      // Setup the cookies
      res.addCookie(Utils.createCookie("eappu", input.getLogin()));
      res.addCookie(Utils.createCookie("eapplg", lang));
      
      return Response.ok().entity(wf).header("Authorization", "Bearer " + token).build();
    } catch(Exception e) {
      e.printStackTrace();
      return WorkflowError.respond(400, "Bad request.");
    }    
    
  }

  @POST
  @Path("/authenticateOnBehalf")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @NotAuthenticated
  public Response authenticateOnBehalf(
          AuthenticateInput input,
          @Context HttpServletResponse res,
          @Context HttpServletRequest req,
          @Context ServletContext context,
          @QueryParam("onBehalf") String onBehalf,
          @QueryParam("ummContext") String ummContext ) {

    try {

      if (onBehalf == null || onBehalf.equals("")) {
        return authenticate(input, res, req, context, ummContext);
      }

      WorkflowContextType wf = getWorkflow().authenticate(input.getLogin(), input.getPassword(), onBehalf);

      String lang = wf.getLocale().split("#")[0].split("_")[0];

      HashMap claims = new HashMap<String, Object>();

      claims.put("workflowContext", wf.getToken());
      claims.put("locale", lang);
      claims.put("AccessLevel", 1);
      claims.put("onBehalf", onBehalf);

      String token = JWTokens.getToken(input.getLogin(), claims);

      // ADF Authentication
      adfAuthenticate(input.getLogin(), input.getPassword(), req);

      // UMM ensure user existance (optional, only if appId specified)
      if (ummContext != null && !ummContext.equals("")) {
        ensureUMMUser(input.getLogin(), ummContext);
      }

      // Setup the cookies
      res.addCookie(Utils.createCookie("eappu", input.getLogin()));
      res.addCookie(Utils.createCookie("eapplg", lang));

      return Response.ok().entity(wf).header("Authorization", "Bearer " + token).build();
    } catch(Exception e) {
      e.printStackTrace();
      return WorkflowError.respond(400, "Bad request.");
    }
  }
  
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getWorkflowContext")
  public Response getWorkflowContext(@Context HttpServletRequest request) {
    String token = (String) request.getAttribute("workflowContext");
    if (token == null) {
      return Response.status(404).entity("{\"message\":\"Token not present.\"}").build();
    }
    WorkflowContextType a = getWorkflow().getWorkflowContext(token);
    return Response.ok(a).build();
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/queryTasks")
  public Response queryTasks(@Context HttpServletRequest request, @QueryParam("initiated") Boolean initiated) {
    if (initiated == null) {
      initiated = false;
    }
    String userId = (String) request.getAttribute("user");
    String token = (String) request.getAttribute("workflowContext");
    List<Task> tasks = null;
    try {
      CriteriaInput criteria;
      if (initiated) {
          System.out.println("Initiated");
        criteria = Criteria.getMyAssignedTasks();
      } else {
          System.out.println(" NOT Initiated");
        criteria = Criteria.getMyInitiatedTasks(userId);
      } 
      tasks = getWorkflow().queryTasks(token, criteria,0,0);

    } catch (Exception e) {
      return WorkflowError.respond(500, e.getMessage());
    }
    return Response.ok().entity(tasks).build();

  }
  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/queryCountTasks")
    public Response queryCountTasks(@Context HttpServletRequest request, @QueryParam("initiated") Boolean initiated) {
      if (initiated == null) {
        initiated = false;
      }
      String userId = (String) request.getAttribute("user");
      String token = (String) request.getAttribute("workflowContext");
      int numTasks = 0;
      try {
        CriteriaInput criteria;
        if (initiated) {
          System.out.println("Initiated");
          criteria = Criteria.getMyAssignedTasks();
        } else {
          System.out.println("Not initiated");
          criteria = Criteria.getMyInitiatedTasks(userId);
        } 
        numTasks = getWorkflow().queryCountTasks(token, criteria);

      } catch (Exception e) {
        return WorkflowError.respond(500, e.getMessage());
      }
      return Response.ok().entity(numTasks).build();

    }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/queryCountTasks")
  public Response queryTasksCountCustom(@Context HttpServletRequest request, CriteriaInput criteria) {
    String token = (String) request.getAttribute("workflowContext");
    System.out.println("testing count");
    int tasks = 0;
    try {
   
      tasks = getWorkflow().queryCountTasks(token, criteria);
    } catch (Exception e) {
      return WorkflowError.respond(500, e.getMessage());
    }
    return Response.ok().entity(tasks).build();

  }
  
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/queryTasks/{startRow}/{endRow}")
    public Response queryTasksCustom(@Context HttpServletRequest request, CriteriaInput criteria, @PathParam("startRow") long startRow, @PathParam("endRow") long endRow) {
      String token = (String) request.getAttribute("workflowContext");
      List<Task> tasks = null;
      try {
          
        tasks = getWorkflow().queryTasks(token, criteria, startRow, endRow);
      } catch (Exception e) {
        return WorkflowError.respond(500, e.getMessage());
      }
      return Response.ok().entity(tasks).build();

    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/queryTasks")
    public Response queryTasksCustom(@Context HttpServletRequest request, CriteriaInput criteria) {
      String token = (String) request.getAttribute("workflowContext");
      List<Task> tasks = null;
      try {
          
        tasks = getWorkflow().queryTasks(token, criteria, 0,0);
      } catch (Exception e) {
        return WorkflowError.respond(500, e.getMessage());
      }
      return Response.ok().entity(tasks).build();

    }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getTaskDetailsById/{taskId}")
  public Task getTaskDetailsById(@PathParam("taskId") String taskId, @Context HttpServletRequest request, @QueryParam("token") String tokenParam) {
    
    String token = (String) request.getAttribute("workflowContext");
    
    // If there is no presence of token in the request attributes we will use the querystring token param;
    if (token == null) {
      token = tokenParam;
    }
    
    Task a = getWorkflow().getTaskDetailsById(token, taskId);
    return a;
  }
  
  private void adfAuthenticate(String _username, String _password , HttpServletRequest req){
      
      try {
        Subject subject = null;
  
        byte[] pw = _password.getBytes();
        subject = Authentication.login(new URLCallbackHandler(_username, pw));
        weblogic.servlet.security.ServletAuthentication.runAs(subject, req);
        
          
      } catch (Exception e) {
        e.printStackTrace();
      }
      
  }
  
  private boolean ensureUMMUser(String userId, String applicationId) {
    InvokeContext ctx = new InvokeContext();
    ctx.setRequester(userId);
    ctx.setApplicationId(applicationId);
    UMMClientProxy prox = new UMMClientProxy();
    try {
      return prox.ummCheckUser(ctx, userId);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
}
