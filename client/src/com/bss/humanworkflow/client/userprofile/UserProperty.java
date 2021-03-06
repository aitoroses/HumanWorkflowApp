package com.bss.humanworkflow.client.userprofile;

import java.io.Serializable;

public class UserProperty  implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String label;
    private String value;

    public UserProperty() {
        super();
    }
    
    public UserProperty(String label, String value) {
        super();
        this.label = label;
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
