package com.bss.humanworkflow.client.userprofile;

import java.io.Serializable;

import java.util.List;
import java.util.Map;


public class UserProfileHW  implements Serializable  {

    private static final long serialVersionUID = 1L;
    private String userId;
    private String displayName;
    private String firstName;
    private String lastName;
    private String mail;
    
    private List<String> ous;
    
    private List<String> businessRoles;
    
    private List<PropertyHW> properties;
    
    public UserProfileHW() {
        super();
    }    
    
    public UserProfileHW(String userId, String displayName, String firstName, String lastName, String mail, 
                         List<String> ous, List<String> businessRole, List<PropertyHW> properties) {
        super();
        this.userId = userId;
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.ous = ous;
        this.businessRoles = businessRole;
        this.properties = properties;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
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

    public void setProperties(List<PropertyHW> properties) {
        this.properties = properties;
    }

    public List<PropertyHW> getProperties() {
        return properties;
    }
}
