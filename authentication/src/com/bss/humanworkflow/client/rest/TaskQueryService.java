package com.bss.humanworkflow.client.rest;


import com.bss.humanworkflow.client.config.AuthConfig;
import com.bss.humanworkflow.client.rest.types.AuthenticateInput;
import com.bss.security.JWTokens;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import oracle.bpel.services.bpm.common.IBPMContext;
import oracle.bpel.services.workflow.WorkflowException;
import oracle.bpel.services.workflow.client.IWorkflowServiceClient;
import oracle.bpel.services.workflow.client.IWorkflowServiceClientConstants;
import oracle.bpel.services.workflow.client.WorkflowServiceClientFactory;

import oracle.bpel.services.workflow.query.ITaskQueryService;
import oracle.bpel.services.workflow.verification.IWorkflowContext;

import oracle.bpm.client.BPMServiceClientFactory;
import oracle.bpm.services.authentication.IBPMUserAuthenticationService;

import oracle.bpm.services.organization.common.BPMContext;

import weblogic.security.URLCallbackHandler;
import weblogic.security.services.Authentication;


@Path("/")
public class TaskQueryService  {
    
    private static IBPMUserAuthenticationService iuas = null;
    
    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(AuthenticateInput input, @Context HttpServletResponse res, @Context HttpServletRequest req, 
                                @Context ServletContext context)  {
        
        try {                                    
            
            String userId = "";  
            
            if(input.getLogin() != null  && !input.getLogin().equals(""))
                userId = input.getLogin().toLowerCase();
            else
                return WorkflowError.respond(401, "Verify the userId.");
    

            IBPMContext ibpmCntx  = null;
            if(input.getPassword() != null && !input.getPassword().equals("")) {
                ibpmCntx = getIBPMContext(input.getLogin(), input.getPassword());
           
                // ADF Authentication
                adfAuthenticate(userId, input.getPassword(), req);
            } else {
                
                String username = "username=" + input.getLogin();
                
                Subject subject = Authentication.assertIdentity("FakeToken", username.getBytes(), null);
                weblogic.servlet.security.ServletAuthentication.runAs(subject, req);
                
                IWorkflowContext iwfContx = initBPMContext(req);
                
                ibpmCntx = (IBPMContext)iwfContx;
            }

            String lang = "";
            
            try {
            // Accept-Language:en,es;q=0.8,gl;q=0.6,de;q=0.4 or en-US,en;q=0.8,es;q=0.6
            
                lang = req.getHeader("Accept-Language");
                
                if(lang != null && !lang.equals("")){
                    lang = lang.substring(0,2);
                    //lang = req.getHeader("Accept-Language").split(";")[0].split(",")[0].substring(2);
                } else
                    lang = ibpmCntx.getLocale().getLanguage().substring(0, 2);
            
            } catch(Exception e) {
                System.out.println("Error getting locale on authentication: Fallback to user profile's one");
                lang = ibpmCntx.getLocale().getLanguage().substring(0, 2);            
            }
                        
            HashMap claims = new HashMap<String, Object>();
          
            claims.put("workflowContext", ibpmCntx.getToken());
            claims.put("locale", lang);
            claims.put("AccessLevel", 1);


            String token = JWTokens.getToken(userId, claims);
            
            //System.out.println("REMOTE USER: " + req.getRemoteUser() + "  EN REMOTE USER");
          
            // Setup the cookies
            res.addCookie(Utils.createCookie("eappu", userId));
            res.addCookie(Utils.createCookie("eapplg", lang));
          
            return Response.ok().entity(ibpmCntx).header("Authorization", "Bearer " + token).build();
        }
        catch(Exception e) {
            e.printStackTrace(); 
            
            if( e.getMessage()!= null &&  e.getMessage().contains(input.getLogin() + " denied")) {
                return WorkflowError.respond(401, "Verify that the user credentials are correct.");
            }
            else {
                return WorkflowError.respond(400, "Bad request.");        
            }
        }
        
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
  

    
    public  IBPMContext getIBPMContext(String userId, String password) throws Exception {
        
        IBPMContext retContext = null;
        
        String uri = AuthConfig.getAuthDomain();
        System.out.println("URI: " + uri);
        
        iuas = getBPMServiceClientFactory(userId, password, uri).getBPMUserAuthenticationService();
       
        retContext = iuas.authenticate(userId, password.toCharArray(), null);
        
        
        
        return retContext;
    }
    
    private  BPMServiceClientFactory getBPMServiceClientFactory(String userId, String password, String uri) throws Exception{
        
        Map<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, String> properties = new HashMap<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, String>();
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.CLIENT_TYPE,WorkflowServiceClientFactory.REMOTE_CLIENT);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_SECURITY_PRINCIPAL, userId);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_SECURITY_CREDENTIALS, password);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_PROVIDER_URL,uri);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
         
        return BPMServiceClientFactory.getInstance(properties,null, null);
    }
    
    
    private IWorkflowContext initBPMContext(HttpServletRequest request) throws WorkflowException {
        IWorkflowServiceClient wfSvcClient = WorkflowServiceClientFactory.getWorkflowServiceClient(WorkflowServiceClientFactory.REMOTE_CLIENT);
        
        ITaskQueryService querySvc = wfSvcClient.getTaskQueryService();
        
        IWorkflowContext ctx = querySvc.createContext(request);
        
        return ctx;
    }
    
/*
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
  */
}
