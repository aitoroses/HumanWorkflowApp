package com.bss.humanworkflow.client.test;

import com.bss.humanworkflow.client.model.IWFClient;
import com.bss.humanworkflow.client.impl.WFClientImpl;

import com.bss.humanworkflow.client.impl.payload.Payload;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import java.util.List;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

public class ClientTest {
  public static void main(String[] main) {
    
    // Instantiate the client
    IWFClient wf = new WFClientImpl();

    // Authenticate the user
    WorkflowContextType wfcx = wf.authenticate("buhead15", "welcome1");
    
    // Return a Task
    Task task = wf.getTaskDetailsById(wfcx.getToken(), "d3eec5c2-3840-4b5b-91ce-1fc43278c966");

    Payload a = task.getPayload();
    
    String requestId = a.getAttribute("requestId");

    List<Task> tasks = wf.queryTasks(wfcx.getToken());
    
    // Get the first taskId
    String taskId = tasks.get(0).getSystemAttributes().getTaskId();
    
    task = wf.getTaskDetailsById(wfcx.getToken(), taskId);

    System.out.println();
  }
}
