package com.bss.humanworkflow.client.impl.view;

import java.util.ArrayList;

import java.util.List;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.query.model.AssignmentFilterEnum;
import oracle.bpel.services.workflow.query.model.DisplayColumnType;
import oracle.bpel.services.workflow.query.model.PredicateClauseType;
import oracle.bpel.services.workflow.query.model.PredicateJoinOperatorEnum;
import oracle.bpel.services.workflow.query.model.PredicateOperationEnum;
import oracle.bpel.services.workflow.query.model.TaskListRequestType;
import oracle.bpel.services.workflow.query.model.TaskPredicateQueryType;
import oracle.bpel.services.workflow.query.model.TaskPredicateType;

public class Criteria {
  
  public static TaskListRequestType getQuery(String token, CriteriaInput input) {
    
    WorkflowContextType wf = new WorkflowContextType();
    wf.setToken(token);
    
    TaskListRequestType payload = new TaskListRequestType();
    payload.setWorkflowContext(wf);
    
    // Create a predicatequery
    TaskPredicateQueryType predicateQuery = new TaskPredicateQueryType();
    
    // Create the displayColumnList
    DisplayColumnType columnList = new DisplayColumnType();
    ArrayList<String> columnArray;
    if (input.getColumns() != null) {
      columnArray = (ArrayList<String>) input.getColumns();
    } else {
      columnArray  = new ArrayList<String>();
    }
    columnList.setDisplayColumn(columnArray);
    predicateQuery.setDisplayColumnList(columnList);
    
    // Predicate holder
    TaskPredicateType taskPredicate = new TaskPredicateType();
    
    // Predicate (clauses)
    List<PredicateClauseType> predicateClauses = new ArrayList<PredicateClauseType>();
    if (input.getClauses() != null) {
      for (Clause c: input.getClauses()) { // Fill the clauses list from input
        PredicateClauseType clause = new PredicateClauseType();
        clause.setColumn(c.getColumn());
        clause.setJoinOperator(c.getJoin());
        clause.setValue(c.getValue());
        clause.setOperator(c.getOperator());
        predicateClauses.add(clause);
      }
    }
    taskPredicate.setClause(predicateClauses);
    
    // Predicate AssignmentFilter
    taskPredicate.setAssignmentFilter(input.getAssignmentFilter());
    
    // Finish the predicate
    predicateQuery.setPredicate(taskPredicate);
    
    payload.setTaskPredicateQuery(predicateQuery);
    
    // Return
    return payload;
  }
  
  public static TaskListRequestType getBasicQuery(String token) {
    return getQuery(token, getBaseCriteria());
  }
  
  public static CriteriaInput getMyAssignedTasks() {
    CriteriaInput criteria = getBaseCriteria();
    
    // Creator clause
    Clause c = new Clause();
    c.setColumn("state");
    c.setOperator(PredicateOperationEnum.EQ);
    c.setValue("ASSIGNED");
    criteria.getClauses().add(c);
    
    return criteria;
  }
  
  public static CriteriaInput getMyInitiatedTasks(String userId) {
    CriteriaInput criteria = getCreatorTasksCriteria(userId);
    Clause c = new Clause();
    c.setJoin(PredicateJoinOperatorEnum.AND);
    c.setColumn("state");
    c.setOperator(PredicateOperationEnum.EQ);
    c.setValue("ASSIGNED");
    criteria.getClauses().add(c);
    
    return criteria;
  }
  
  public static CriteriaInput getCreatorTasksCriteria(String creator) {
    CriteriaInput criteria = getBaseCriteria();
    
    // Creator clause
    Clause c = new Clause();
    c.setColumn("creator");
    c.setOperator(PredicateOperationEnum.EQ);
    c.setValue(creator);
    criteria.getClauses().add(c);
    
    return criteria;
  }
  
  public static CriteriaInput getBaseCriteria() {
    CriteriaInput input = new CriteriaInput();
    input.setColumns(new ArrayList<String>());
    input.setClauses(new ArrayList<Clause>());
    input.setAssignmentFilter(AssignmentFilterEnum.MY_GROUP);
    return input;
  }
}
