package com.bss.humanworkflow.client.rest;

import com.bss.humanworkflow.client.TaskQueryService.WorkflowErrorMessage;
import com.bss.humanworkflow.client.impl.view.Criteria;
import com.bss.humanworkflow.client.impl.view.CriteriaInput;
import com.bss.humanworkflow.client.rest.security.NotAuthenticated;
import com.bss.humanworkflow.client.rest.security.Utils;
import com.bss.humanworkflow.client.rest.types.AuthenticateInput;

import com.bss.humanworkflow.client.userprofile.PropertyHW;
import com.bss.humanworkflow.client.userprofile.UserProfileHW;
import com.bss.security.JWTokens;

import com.novartis.bpm.um.UMMClientProxy;

import com.novartis.bpm.um.client.InvokeContext;

import com.novartis.bpm.um.client.UMMClient;
import com.novartis.bpm.um.model.Property;
import com.novartis.bpm.um.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

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
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotAuthenticated
    public Response authenticate(AuthenticateInput input, @Context HttpServletResponse res, @Context HttpServletRequest req, 
                                @Context ServletContext context, @QueryParam("ummContext") String ummContext )  {
        
        try {                                    
            String userId = "";  
            
            if(input.getLogin() != null  && !input.getLogin().equals(""))
                userId = input.getLogin().toLowerCase();
            else
                return WorkflowError.respond(401, "Verify the userId.");
    
            WorkflowContextType wf = null;
            
             
            wf = getWorkflow().authenticate(userId, input.getPassword());

            
            String lang = "";
            
            try {
            // Accept-Language:en,es;q=0.8,gl;q=0.6,de;q=0.4 or en-US,en;q=0.8,es;q=0.6
            
                lang = req.getHeader("Accept-Language");
                
                if(lang != null && !lang.equals("")){
                    lang = lang.substring(0,2);
                    //lang = req.getHeader("Accept-Language").split(";")[0].split(",")[0].substring(2);
                }//else
                  //  lang = wf.getLocale().split("#")[0].split("_")[0];
            
            } catch(Exception e) {
                System.out.println("Error getting locale on authentication: Fallback to user profile's one");
                //lang = wf.getLocale().split("#")[0].split("_")[0];
            }
                        
            HashMap claims = new HashMap<String, Object>();
          
            claims.put("workflowContext", wf.getToken());
            claims.put("locale", lang);
            claims.put("AccessLevel", 1);

            /*
            if (ummContext != null && !ummContext.equals("")) {
                claims.put("BusinessRole", getUserProlile(userId, ummContext));
            }
            */
            String token = JWTokens.getToken(userId, claims);
          
            // ADF Authentication
            adfAuthenticate(userId, input.getPassword(), req);
          
            /*** UMM ensure user existance (optional, only if appId specified)***/
            //if (ummContext != null && !ummContext.equals("")) {
                //ensureUMMUser(userId, ummContext);
            //}
            
            //System.out.println("REMOTE USER: " + req.getRemoteUser() + "  EN REMOTE USER");
          
            // Setup the cookies
            res.addCookie(Utils.createCookie("eappu", userId));
            res.addCookie(Utils.createCookie("eapplg", lang));
          
            return Response.ok().entity(wf).header("Authorization", "Bearer " + token).build();
        } catch(WorkflowErrorMessage ewf) {
        
            if(ewf.getFaultInfo()!= null && ewf.getFaultInfo().getMessage().contains("ORA-30501")) {
                return WorkflowError.respond(401, "Verify that the user credentials are correct.");
            }
            else {
                return WorkflowError.respond(400, "Bad request.");        
            }
        }
        catch(Exception e) {
            e.printStackTrace();     
            return WorkflowError.respond(400, "Bad request.");
        }
        
    }

    @POST
    @Path("/authOnBehalf")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NotAuthenticated
    public Response authenticateOnBehalf(AuthenticateInput input, @Context HttpServletResponse res, @Context HttpServletRequest req,
                                        @Context ServletContext context, @QueryParam("onBehalf") String onBehalf, @QueryParam("ummContext") String ummContext ) {
    
        try {
        
            if (onBehalf == null || onBehalf.equals("")) {
                return authenticate(input, res, req, context, ummContext);
            }
            
            String userId = "";  
            if(input.getLogin() != null  && !input.getLogin().equals(""))
              userId = input.getLogin().toLowerCase();
            
            WorkflowContextType wf = getWorkflow().authenticate(userId, input.getPassword(), onBehalf);
            
            String lang = wf.getLocale().split("#")[0].split("_")[0];
            
            HashMap claims = new HashMap<String, Object>();
            
            claims.put("workflowContext", wf.getToken());
            claims.put("locale", lang);
            claims.put("onBehalf", onBehalf);
            
            if (ummContext != null && !ummContext.equals("")) {
                claims.put("BusinessRole", getUserProlile(userId, ummContext));
            }
            
            String token = JWTokens.getToken(userId, claims);
            
            // ADF Authentication
            adfAuthenticate(userId, input.getPassword(), req);
            
            /*** UMM ensure user existance (optional, only if appId specified)***/
            //if (ummContext != null && !ummContext.equals("")) {
                //ensureUMMUser(userId, ummContext);
            //}
            
            // Setup the cookies
            res.addCookie(Utils.createCookie("eappu", userId));
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
        
        WorkflowContextType wfContext = null;
        
        try {
            wfContext = getWorkflow().getWorkflowContext(token);
        } catch (WorkflowErrorMessage e) {
            return WorkflowError.respond(400, "Bad request.");
        }
        return Response.ok(wfContext).build();
    }
  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/queryTasks")
    @Deprecated
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
                criteria = Criteria.getMyAssignedTasks();
            } else {
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
    @Deprecated
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
                criteria = Criteria.getMyAssignedTasks();
            } else {
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
    public Response getTaskDetailsById(@PathParam("taskId") String taskId, @Context HttpServletRequest request, @QueryParam("token") String tokenParam) {
    
        String token = (String) request.getAttribute("workflowContext");
    
        // If there is no presence of token in the request attributes we will use the querystring token param;
        if (token == null) {
            token = tokenParam;
        }
    
        Task a = null;
        
        try {
            a = getWorkflow().getTaskDetailsById(token, taskId);
        } catch (WorkflowErrorMessage e) {
            return WorkflowError.respond(500, e.getMessage());            
        }
        
        return Response.ok().entity(a).build();  
    }
  
    private void adfAuthenticate(String _username, String _password , HttpServletRequest req) throws Exception {      
        
        try {
            Subject subject = null;
    
            byte[] pw = _password.getBytes();
            subject = Authentication.login(new URLCallbackHandler(_username, pw));
            weblogic.servlet.security.ServletAuthentication.runAs(subject, req);                  
        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }      
    }
  
    private boolean ensureUMMUser(String userId, String applicationId) throws Exception {
        InvokeContext ctx = new InvokeContext(userId, applicationId);
        
        UMMClientProxy prox = new UMMClientProxy();
        try {
            return prox.ummCheckUser(ctx, userId);
        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }
    }
  
    private UserProfileHW getUserProlile(String userId, String applicationId) throws Exception {
        
        InvokeContext ctx = new InvokeContext(userId, applicationId);

        UMMClientProxy prox = new UMMClientProxy();
        
        UserProfileHW userProfileHW = null;
        
        try {
            
            UserProfile userP = prox.getUserProfile(ctx, userId);
            
            userProfileHW = new UserProfileHW();
            
            if(userP != null ){
                //data user
                if(userP.getUserData()!= null){ 
                    userProfileHW.setUserId(userP.getUserData().getUid());
                    userProfileHW.setDisplayName(userP.getUserData().getDisplayName());
                    userProfileHW.setFirstName(userP.getUserData().getFirstName());
                    userProfileHW.setLastName(userP.getUserData().getLastName());
                    userProfileHW.setMail(userP.getUserData().getMail());
                }
                //ous
                userProfileHW.setOus(userP.getOus());
                
                //properties
                if(userP.getProperties() != null){
                    
                    List<PropertyHW> listPropHW = new ArrayList<PropertyHW>();
                    PropertyHW propHW = null;
                    
                    
                    for(Property prop : userP.getProperties()){                        
                        propHW = new PropertyHW();
                        propHW.setLabel(prop.getLabel());
                        propHW.setValue(prop.getValue());
                        
                        listPropHW.add(propHW);
                    }                    
                    userProfileHW.setProperties(listPropHW);                    
                }
                
                //business roles
                List<Map<String, String>> businessRole = prox.getBusinessRolesForUser(ctx, userId);
                
                List<String> brListStr = new ArrayList<String>();
                
                if(businessRole != null){
                    for(int i = 0; i < businessRole.size(); i++){
                        
                        Map<String, String> bRole = businessRole.get(i);
                        
                        brListStr.add(bRole.get("businessRole"));
                    }
                    userProfileHW.setBusinessRoles(brListStr);
                }
            }

        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }
        
        return userProfileHW;
    }
  
}
