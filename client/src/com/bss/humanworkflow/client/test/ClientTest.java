package com.bss.humanworkflow.client.test;

import com.bss.humanworkflow.client.model.IWFClient;
import com.bss.humanworkflow.client.impl.WFClientImpl;

import com.bss.humanworkflow.client.impl.payload.Payload;

import com.bss.humanworkflow.client.impl.view.Criteria;
import com.bss.humanworkflow.client.impl.view.CriteriaInput;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import java.io.File;

import java.util.List;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

public class ClientTest {
  
  public static Task getTask() {
    Task task = null;
    
    try {
      // Instantiate the client
      IWFClient wf = new WFClientImpl();
      
      // Authenticate the user
      WorkflowContextType wfcx = wf.authenticate("buhead15", "welcome1");
      
      // Query Creator tasks
      List<Task> tasks = wf.queryTasks(wfcx.getToken(), Criteria.getCreatorTasksCriteria("buhead15"));
      
      tasks = wf.queryTasks(wfcx.getToken(), Criteria.getMyAssignedTasks());
  
      // Return a Task
      task = wf.getTaskDetailsById(wfcx.getToken(), "857d16f9-5c94-44e9-b161-1e12b2e19a46");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return task;
  }
  
  public static void test1() {
    
    try {
      
      Task task = getTask();
      
      // Convert to JSON
      ObjectMapper mapper = new ObjectMapper();
      String asString = mapper.writeValueAsString(task);
      
      System.out.println(asString);
      
      // Read JSON
      Task task1 = mapper.readValue(asString, Task.class);
      
      System.out.println(task1.getCreator());



    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println();
  }
  
  public static void main(String[] main) {
    
    try {
      
      System.out.println(TaskMock.value);
      
      // Read JSON
      ObjectMapper mapper = new ObjectMapper();
      Task task1 = mapper.readValue(TaskMock.value, Task.class);
      
      System.out.println(task1.getCreator());



    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println();
  }
}
