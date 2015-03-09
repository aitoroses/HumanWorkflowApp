package com.bss.humanworkflow.client.rest;

import com.bss.humanworkflow.client.model.IWFClient;
import com.bss.humanworkflow.client.impl.WFClientImpl;

public abstract class AbstractService {
  
  protected IWFClient wf;
  
  public AbstractService() {
    super();
    wf = new WFClientImpl();
  }

  public IWFClient getWorkflow() {
    return wf;
  }
}
