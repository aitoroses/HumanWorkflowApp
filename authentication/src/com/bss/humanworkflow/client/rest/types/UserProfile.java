package com.bss.humanworkflow.client.rest.types;

import java.io.Serializable;

public class UserProfile implements Serializable  {
    private static final long serialVersionUID = 1L;

    public UserProfile() {
        super();
    }
    
    private String displayName;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
