package com.bss.humanworkflow.client.rest.types;

import java.io.Serializable;

public class UserProfile implements Serializable  {
    private static final long serialVersionUID = 1L;

    public UserProfile() {
        super();
    }
    
    private String userDisplayName;

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }
}
