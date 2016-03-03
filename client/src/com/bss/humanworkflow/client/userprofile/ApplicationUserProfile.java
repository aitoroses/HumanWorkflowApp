package com.bss.humanworkflow.client.userprofile;

import java.io.Serializable;

import java.util.List;

public class ApplicationUserProfile  implements Serializable  {

    private static final long serialVersionUID = 1L;
    
    private List<String> ou;
    
    private List<String> businessRoles;
    
    private List<UserProperty> properties;
    
    public ApplicationUserProfile() {
        super();
    }    
    
    public ApplicationUserProfile(List<String> ou, List<String> businessRole, List<UserProperty> properties) {
        super();

        this.ou = ou;
        this.businessRoles = businessRole;
        this.properties = properties;
    }


    public void setOu(List<String> ou) {
        this.ou = ou;
    }

    public List<String> getOu() {
        return ou;
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
