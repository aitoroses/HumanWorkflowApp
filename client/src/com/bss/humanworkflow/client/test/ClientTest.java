package com.bss.humanworkflow.client.test;

import com.bss.humanworkflow.client.model.IWFClient;
import com.bss.humanworkflow.client.impl.WFClientImpl;

import com.bss.humanworkflow.client.impl.payload.Payload;

import com.bss.humanworkflow.client.impl.view.Criteria;
import com.bss.humanworkflow.client.impl.view.CriteriaInput;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import java.util.List;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

public class ClientTest {
  public static void main(String[] main) {
    
    try {
      
      // Instantiate the client
      IWFClient wf = new WFClientImpl();
  
      // Authenticate the user
      WorkflowContextType wfcx = wf.authenticate("at_req_all", "welcome1");
      
      // Query Creator tasks
      List<Task> tasks = wf.queryTasks(wfcx.getToken(), Criteria.getCreatorTasksCriteria("at_req_all"));
      
      tasks = wf.queryTasks(wfcx.getToken(), Criteria.getMyAssignedTasks());

      // Return a Task
      Task task = wf.getTaskDetailsById(wfcx.getToken(), "857d16f9-5c94-44e9-b161-1e12b2e19a46");
  
      //Payload a = task.getPayload();
      
      // Set a user comment
      //a.setAttribute("userComment", "My super comment");
      
      // Set the outcome
      task.getSystemAttributes().setOutcome("SAVE");
      
      // Make the update
      wf.updateTask(wfcx.getToken(), task);
  
      /*List<Task> tasks = wf.queryTasks(wfcx.getToken());
      
      // Get the first taskId
      String taskId = tasks.get(0).getSystemAttributes().getTaskId();
      
      task = wf.getTaskDetailsById(wfcx.getToken(), taskId);*/
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println();
  }
}
