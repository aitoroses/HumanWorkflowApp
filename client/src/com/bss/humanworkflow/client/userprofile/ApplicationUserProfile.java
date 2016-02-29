package com.bss.humanworkflow.client.userprofile;

import java.io.Serializable;

import java.util.List;

public class ApplicationUserProfile  implements Serializable  {

    private static final long serialVersionUID = 1L;
    
    private List<String> ous;
    
    private List<String> businessRoles;
    
    private List<UserProperty> properties;
    
    public ApplicationUserProfile() {
        super();
    }    
    
    public ApplicationUserProfile(List<String> ous, List<String> businessRole, List<UserProperty> properties) {
        super();

        this.ous = ous;
        this.businessRoles = businessRole;
        this.properties = properties;
    }


    public void setOus(List<String> ous) {
        this.ous = ous;
    }

    public List<String> getOus() {
        return ous;
    }

    public void setBusinessRoles(List<String> businessRoles) {
        this.businessRoles = businessRoles;
    }

    public List<String> getBusinessRoles() {
        return businessRoles;
    }

    public void setProperties(List<UserProperty> properties) {
        this.properties = properties;
    }

    public List<UserProperty> getProperties() {
        return properties;
    }
}
