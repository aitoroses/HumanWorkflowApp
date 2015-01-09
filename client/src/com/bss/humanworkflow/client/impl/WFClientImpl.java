package com.bss.humanworkflow.client.impl;

import com.bpmsoasolutions.xmlns.eapproval.forms.FormComponentType;

import com.bss.humanworkflow.client.model.IWFClient;
import com.bss.humanworkflow.client.TaskQueryService.TaskQueryService;
import com.bss.humanworkflow.client.TaskQueryService.WorkflowErrorMessage;
import com.bss.humanworkflow.client.TaskService.StaleObjectFaultMessage;
import com.bss.humanworkflow.client.logging.JAXBLogger;
import com.bss.humanworkflow.client.model.WFClientAbstract;

import com.oracle.xmlns.bpel.workflow.taskservice.TaskServiceContextTaskBaseType;

import com.oracle.xmlns.bpel.workflow.taskservice.UpdateTaskOutcomeType;

import java.io.StringWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import oracle.bpel.services.workflow.common.model.CredentialType;
import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.query.model.AssignmentFilterEnum;
import oracle.bpel.services.workflow.query.model.ClauseType;
import oracle.bpel.services.workflow.query.model.DisplayColumnType;
import oracle.bpel.services.workflow.query.model.ObjectFactory;
import oracle.bpel.services.workflow.query.model.PredicateClauseType;
import oracle.bpel.services.workflow.query.model.PredicateType;
import oracle.bpel.services.workflow.query.model.TaskDetailsByIdRequestType;
import oracle.bpel.services.workflow.query.model.TaskListRequestType;
import oracle.bpel.services.workflow.query.model.TaskPredicateQueryType;
import oracle.bpel.services.workflow.query.model.TaskPredicateType;
import oracle.bpel.services.workflow.task.model.Task;

public class WFClientImpl extends WFClientAbstract implements IWFClient {
  
  /**
   * Authentication method
   * 
   * @param login
   * @param password
   */
  public WorkflowContextType authenticate(String login, String password) {
    CredentialType payload = new CredentialType();
    payload.setLogin(login);
    payload.setPassword(password);
    WorkflowContextType wfctx = null;;
    try {
      wfctx = getTaskQueryService().authenticate(payload);
    } catch (WorkflowErrorMessage e) {
      e.printStackTrace();
    }
    return wfctx;
  }
  
  /**
   * GetTaskDetailsById
   *
   * @param token
   * @param taskId
   */
  public Task getTaskDetailsById(String token, String taskId) {
    TaskDetailsByIdRequestType payload = new TaskDetailsByIdRequestType();
    WorkflowContextType wc = getContext(token);
    payload.setWorkflowContext(wc);
    payload.setTaskId(taskId);
    Task a = null;
    try {
      a = getTaskQueryService().getTaskDetailsById(payload);
    } catch (WorkflowErrorMessage e) {
      e.printStackTrace();
    }
    
    return a;
  }
  
  /**
   * UpdateTask
   *
   * @param Task
   */
  public void updateTask(String token, Task task) {
    TaskServiceContextTaskBaseType input = new TaskServiceContextTaskBaseType();
    input.setTask(task);
    WorkflowContextType wfcx = getContext(token);
    input.setWorkflowContext(wfcx);
    try {
      JAXBLogger.log(input);
      //getTaskService().updateTask(input);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * UpdateOutcome
   *
   * @param Task
   */
  public void updateOutcome(String token, String outcome, String taskId) {
    UpdateTaskOutcomeType input = new UpdateTaskOutcomeType();
    // context
    WorkflowContextType wfcx = getContext(token);
    input.setWorkflowContext(wfcx);
    input.setOutcome(outcome);
    input.setTaskId(taskId);
    Task task;
    try {
      JAXBLogger.log(input);
      task = getTaskService().updateTaskOutcome(input);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * This function creates a basic payload for the task querying
   * Returns all My+Group instances 
   * @param token
   * @return
   */
  private TaskListRequestType createBasicTaskListRequest(String token) {
    TaskListRequestType payload = new TaskListRequestType();
    payload.setWorkflowContext(getContext(token));
    
    // Create a predicatequery
    TaskPredicateQueryType predicateQuery = new TaskPredicateQueryType();
    
    // Create the displayColumnList
    DisplayColumnType columnList = new DisplayColumnType();
    columnList.setDisplayColumn(new ArrayList<String>());
    predicateQuery.setDisplayColumnList(columnList);
    
    // Predicate
    TaskPredicateType taskPredicate = new TaskPredicateType();
    taskPredicate.setClause(new ArrayList<PredicateClauseType>());
    // AssignmentFilter
    taskPredicate.setAssignmentFilter(AssignmentFilterEnum.MY_GROUP);
    // Finish the predicate
    predicateQuery.setPredicate(taskPredicate);
    payload.setTaskPredicateQuery(predicateQuery);
    
    // Return
    return payload;
    
  }

  public List<Task> queryTasks(String token) {
    try {
      return getTaskQueryService().queryTasks(createBasicTaskListRequest(token)).getTask();
    } catch (WorkflowErrorMessage e) {
      e.printStackTrace();
      return null;
    }
  }
  
  private WorkflowContextType getContext(String token) {
    WorkflowContextType wfcx = new WorkflowContextType();
    wfcx.setToken(token);
    return wfcx;
  }
}
