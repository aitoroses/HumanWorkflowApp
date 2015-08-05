package com.bss.humanworkflow.client.model;

import com.bss.humanworkflow.client.TaskQueryService.WorkflowErrorMessage;

import com.bss.humanworkflow.client.impl.view.CriteriaInput;

import java.util.List;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.task.model.Task;

public interface IWFClient {

    public WorkflowContextType authenticate(String login, String password);

    public WorkflowContextType getWorkflowContext(String token);

    public List<Task> queryTasks(String token, CriteriaInput input,
                                 long startRow, long endRow) throws Exception;

    public int queryCountTasks(String token,
                               CriteriaInput input) throws Exception;

    public Task getTaskDetailsById(String token, String taskId);

    public void updateTask(String token, Task task) throws Exception;

    public void updateOutcome(String token, String outcome,
                              String taskId) throws Exception;
}
