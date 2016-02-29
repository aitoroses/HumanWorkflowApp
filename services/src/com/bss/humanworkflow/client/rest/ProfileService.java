package com.bss.humanworkflow.client.rest;


import com.bss.humanworkflow.client.userprofile.ApplicationUserProfile;
import com.bss.humanworkflow.client.userprofile.UserProperty;
import com.bss.security.JWTokens;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;

import com.novartis.bpm.um.UMMClientProxy;
import com.novartis.bpm.um.client.InvokeContext;
import com.novartis.bpm.um.model.Property;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.minidev.json.JSONObject;


@Path("/context")
public class ProfileService  {
    public ProfileService() {
        super();
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/profile/{appId}")
    public Response addUserProfileInToken(@PathParam("appId") String appId, @Context HttpServletRequest request) {
        
        String token = getToken(request);       
        
        if(token == null){
            return WorkflowError.respond(404, "Token not present.");
        }
        
        String userId = (String) request.getAttribute("user");

        if(userId != null || !userId.equals("")){
            
            ReadOnlyJWTClaimsSet jw = JWTokens.parseToken(token);
            
            // Apps
            HashMap<String, ApplicationUserProfile> appMaps = (HashMap<String, ApplicationUserProfile>)jw.getClaim("apps");

            
            try {
                if(appMaps != null){
                    appMaps.put(appId, getAppUserProlile(userId, appId));
                }
                else {
                    appMaps = new HashMap<String, ApplicationUserProfile>();
            
                    appMaps.put(appId, getAppUserProlile(userId, appId));
                }
            } catch (Exception e) {
                return WorkflowError.respond(500, "Error invoking UMM");
            }

            token  = udpateToken(jw, request, appMaps);

        }
        else
            return WorkflowError.respond(400, "User id doesn't found");
        
        request.setAttribute("AuthorizationToken", token);
        
        return Response.ok().entity(true).build();
    }

    private String getToken(HttpServletRequest request) {
        
        String token = (String) request.getHeader("Authorization");
        
        if (token == null) {
            return null;
        }
        
        String[] tokensplit = token.split(" ");
        if (tokensplit[0].equalsIgnoreCase("Bearer")) {
          token = tokensplit[1];
        } else {
          token = tokensplit[0];
        }
        return token;
    }


    private String udpateToken(ReadOnlyJWTClaimsSet jw, HttpServletRequest request, HashMap<String, ApplicationUserProfile> appMaps) {
        
        String userId = (String) request.getAttribute("user");
        
        HashMap claims = new HashMap<String, Object>();
        
        //claims.put("user", subject);
        claims.put("workflowContext", jw.getClaim("workflowContext"));
        claims.put("locale", jw.getClaim("locale"));
        claims.put("AccessLevel", jw.getClaim("AccessLevel"));
        claims.put("apps", appMaps);
        claims.put("profile", jw.getClaim("profile"));
        
        return JWTokens.getToken(userId, claims);
    }   
   
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/assing_ou/{appId}")
    public Response addUserToOus(@PathParam("appId") String appId, @QueryParam("ous") List<String> ous, @Context HttpServletRequest request) {
        
        String token = getToken(request);       
        
        if(token == null){
            return WorkflowError.respond(404, "Token not present.");
        }
        
        String userId = (String) request.getAttribute("user");

        if(userId != null || !userId.equals("")){
            
            InvokeContext ctx = new InvokeContext(userId, appId);

            UMMClientProxy ummProx = new UMMClientProxy();
            
            if(ous!= null && ous.size()>0){
                
                try {
                    for(String ouName : ous){
                        ummProx.addUserToOuUM(ctx,userId, ouName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return WorkflowError.respond(500, "Error invoking UMM");
                }
                
                ReadOnlyJWTClaimsSet jw = JWTokens.parseToken(token);
                
                // Apps
                JSONObject appJsonMaps = (JSONObject) jw.getClaim("apps");
                
                ObjectMapper mapper = new ObjectMapper();
                
                HashMap<String,ApplicationUserProfile> appUserProfile = null;
                
                TypeReference<HashMap<String,ApplicationUserProfile>> typeRef = new TypeReference<HashMap<String,ApplicationUserProfile>>() {};

                
                try {
                    
                    if(appJsonMaps != null){
                    
                        try {
                            appUserProfile = (HashMap<String,ApplicationUserProfile>) mapper.readValue(appJsonMaps.toJSONString(), typeRef);
                            
                        } catch (JsonMappingException e) {
                            return WorkflowError.respond(400, "Error extracting application profile from token");
                        } catch (JsonParseException e) {
                            return WorkflowError.respond(400, "Error extracting application profile from token");
                        } catch (IOException e) {
                            return WorkflowError.respond(400, "Error extracting application profile from token");
                        }                                            
                        
                        if(appUserProfile != null && appUserProfile.get(appId) != null){
                            
                            List<String> previousOUs = appUserProfile.get(appId).getOus();
                            
                            if(previousOUs != null){
                               previousOUs.addAll(ous);
                            } else {
                                previousOUs = new ArrayList<String>();
                                previousOUs = ous;                            
                            }
                            
                            appUserProfile.get(appId).setOus(previousOUs);
                        }
                    }
                    else {
                        appUserProfile = new HashMap<String, ApplicationUserProfile>();
                        
                        ApplicationUserProfile appProfile = new ApplicationUserProfile();
                        
                        appProfile.setOus(ous);
                
                        appUserProfile.put(appId, appProfile);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    return WorkflowError.respond(500, "Error adding OUs to application profile");
                }

                token  = udpateToken(jw, request, appUserProfile);
                
                request.setAttribute("AuthorizationToken", token);
                
            } else
                return WorkflowError.respond(500, "OU list empty");            
        }
        else
            return WorkflowError.respond(400, "User id doesn't found");

        
        return Response.ok().entity(true).build();         
    }

    private ApplicationUserProfile getAppUserProlile(String userId, String applicationId) throws Exception {
        
        InvokeContext ctx = new InvokeContext(userId, applicationId);

        UMMClientProxy ummProx = new UMMClientProxy();
        
        ApplicationUserProfile appUserProfile = new ApplicationUserProfile();
        
        try {
            //Setting OU List
            List<String> ous = ummProx.getUserOU(ctx, userId);
            
            appUserProfile.setOus(ous);
            
            
            //Setting user properties
            List<Property> properyUMM = ummProx.getPropertiesForUser(ctx, userId);
            
            
            if(properyUMM != null){
                
                List<UserProperty> listPropHW = new ArrayList<UserProperty>();
                UserProperty propHW = null;
                
                
                for(Property prop : properyUMM){                        
                    propHW = new UserProperty();
                    propHW.setLabel(prop.getLabel());
                    propHW.setValue(prop.getValue());
                    
                    listPropHW.add(propHW);
                }                    
                appUserProfile.setProperties(listPropHW);                    
            }
            
            //business roles
            List<Map<String, String>> businessRole = ummProx.getBusinessRolesForUser(ctx, userId);
            
            List<String> brListStr = new ArrayList<String>();
            
            if(businessRole != null){
                for(int i = 0; i < businessRole.size(); i++){
                    
                    Map<String, String> bRole = businessRole.get(i);
                    
                    brListStr.add(bRole.get("businessRole"));
                }
                appUserProfile.setBusinessRoles(brListStr);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        return appUserProfile;
    }
    
    
    
    /*
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
    */
    
}
