package com.bss.humanworkflow.client.rest;

import com.bss.humanworkflow.client.model.IWFClient;
import com.bss.humanworkflow.client.impl.WFClientImpl;

public abstract class AbstractService {
  
  protected IWFClient wf;
  
  //protected HumanTaskClient htc;
  
  public AbstractService() {
    super();
    wf = new WFClientImpl();
    //htc = new HumanTaskClientImpl();
  }

  public IWFClient getWorkflow() {
    return wf;
  }
  /*
  public HumanTaskClient getEjbWorkflow() {
      return htc;
  }
*/
}
