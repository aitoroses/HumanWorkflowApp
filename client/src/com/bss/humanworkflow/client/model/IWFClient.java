package com.bss.humanworkflow.client.model;

import java.util.List;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

public interface IWFClient {
  
  public WorkflowContextType authenticate(String login, String password);
  public WorkflowContextType getWorkflowContext(String token);
  public List<Task> queryTasks(String token);
  public Task getTaskDetailsById(String token, String taskId);
  public void updateTask(String token, Task task);
  public void updateOutcome(String token, String outcome, String taskId);
}
