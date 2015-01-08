package com.bss.humanworkflow.client.model;

import com.bss.humanworkflow.client.TaskQueryService.TaskQueryService;
import com.bss.humanworkflow.client.TaskQueryService.TaskQueryService_Service;
import com.bss.humanworkflow.client.TaskService.TaskService;
import com.bss.humanworkflow.client.TaskService.TaskService_Service;

import javax.xml.ws.WebServiceRef;

public abstract class WFClientAbstract {
  
  // Services
  private TaskQueryService taskQueryService;
  private TaskService taskService;
  
  public WFClientAbstract() {

    // TaskQueryService
    TaskQueryService_Service taskQueryService_Service = new TaskQueryService_Service();
    taskQueryService = taskQueryService_Service.getTaskQueryServicePort();
    
    // TaskService
    TaskService_Service taskService_Service = new TaskService_Service();
    taskService = taskService_Service.getTaskServicePort();
    
  }
  
  public TaskQueryService getTaskQueryService() {
    return taskQueryService;
  }

  public TaskService getTaskService() {
    return taskService;
  }
}
