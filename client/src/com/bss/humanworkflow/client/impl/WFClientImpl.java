package com.bss.humanworkflow.client.impl;


import com.bss.humanworkflow.client.TaskQueryService.WorkflowErrorMessage;
import com.bss.humanworkflow.client.impl.view.Criteria;
import com.bss.humanworkflow.client.impl.view.CriteriaInput;
import com.bss.humanworkflow.client.logging.JAXBLogger;
import com.bss.humanworkflow.client.model.IWFClient;
import com.bss.humanworkflow.client.model.WFClientAbstract;

import com.oracle.xmlns.bpel.workflow.taskservice.TaskServiceContextTaskBaseType;
import com.oracle.xmlns.bpel.workflow.taskservice.UpdateTaskOutcomeType;

import java.util.ArrayList;
import java.util.List;

import oracle.bpel.services.workflow.common.model.CredentialType;
import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.query.model.AssignmentFilterEnum;
import oracle.bpel.services.workflow.query.model.CountTasksRequestType;
import oracle.bpel.services.workflow.query.model.DisplayColumnType;
import oracle.bpel.services.workflow.query.model.PredicateClauseType;
import oracle.bpel.services.workflow.query.model.TaskDetailsByIdRequestType;
import oracle.bpel.services.workflow.query.model.TaskListRequestType;
import oracle.bpel.services.workflow.query.model.TaskPredicateQueryType;
import oracle.bpel.services.workflow.query.model.TaskPredicateType;
import oracle.bpel.services.workflow.query.model.WorkflowContextRequestType;
import oracle.bpel.services.workflow.task.model.Task;


public class WFClientImpl extends WFClientAbstract implements IWFClient {
  
  /**
   * Authentication method
   * 
   * @param login
   * @param password
   */
    public WorkflowContextType authenticate(String login, String password) throws WorkflowErrorMessage {
        CredentialType payload = new CredentialType();
        payload.setLogin(login);
        payload.setPassword(password);
        WorkflowContextType wfctx = null;
        try {
            wfctx = getTaskQueryService().authenticate(payload);
        } catch (WorkflowErrorMessage e) {
            throw e;  
            //e.printStackTrace();
        }
        return wfctx;
    }

  /**
   * Authentication on behalf method
   *
   * @param login
   * @param password
   */
    public WorkflowContextType authenticate(String login, String password, String onBehalf) throws WorkflowErrorMessage {
        CredentialType payload = new CredentialType();
        payload.setLogin(login);
        payload.setPassword(password);
        payload.setOnBehalfOfUser(onBehalf);
        WorkflowContextType wfctx = null;
        try {
            wfctx = getTaskQueryService().authenticate(payload);
        } catch (WorkflowErrorMessage e) {
            throw e;  
            //e.printStackTrace();
        }
        return wfctx;
    }
  
  /**
   * Get WorkflowContext
   * 
   * @param token
   */
  public WorkflowContextType getWorkflowContext(String token) throws WorkflowErrorMessage {
    WorkflowContextRequestType payload = new WorkflowContextRequestType();
    payload.setToken(token);
    WorkflowContextType wfctx = null;;
    try {
      wfctx = getTaskQueryService().getWorkflowContext(payload);
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
  public void updateTask(String token, Task task) throws Exception {
    TaskServiceContextTaskBaseType input = new TaskServiceContextTaskBaseType();
    input.setTask(task);
    WorkflowContextType wfcx = getContext(token);
    input.setWorkflowContext(wfcx);
    
    //JAXBLogger.log(task);
    try {
      getTaskService().updateTask(input);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  /**
   * UpdateOutcome
   *
   * @param Task
   */
  public void updateOutcome(String token, String outcome, String taskId) throws Exception {
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
      throw e;
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

    public List<Task> queryTasks(String token, CriteriaInput input,
                                 long startRow, long endRow) throws Exception {
        List<Task> tasks = null;
        try {
            TaskListRequestType criteria =
                Criteria.getQuery(token, input, startRow, endRow);


            //JAXBLogger.log(criteria);
            tasks = getTaskQueryService().queryTasks(criteria).getTask();

            System.out.println("NUMBER in queryTasks:" + tasks.size());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return tasks;
    }

    public int queryCountTasks(String token,
                               CriteriaInput input) throws Exception {
        int numTasks = 0;
        try {

            CountTasksRequestType criteria = Criteria.getQueryCount(token, input);

            //JAXBLogger.log(criteria);
            numTasks = getTaskQueryService().countTasks(criteria);

            System.out.println("NUMBER OF TASKS:" + numTasks);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return numTasks;
    }

    private WorkflowContextType getContext(String token) {
        WorkflowContextType wfcx = new WorkflowContextType();
        wfcx.setToken(token);
        return wfcx;
    }
}
