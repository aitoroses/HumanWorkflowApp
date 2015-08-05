package com.bss.humanworkflow.client.impl.view;

import java.math.BigInteger;

import java.util.ArrayList;

import java.util.List;

import oracle.bpel.services.workflow.common.model.WorkflowContextType;
import oracle.bpel.services.workflow.query.model.AssignmentFilterEnum;
import oracle.bpel.services.workflow.query.model.ClauseType;
import oracle.bpel.services.workflow.query.model.ColumnType;
import oracle.bpel.services.workflow.query.model.CountTasksRequestType;
import oracle.bpel.services.workflow.query.model.DisplayColumnType;
import oracle.bpel.services.workflow.query.model.OrderingClauseType;
import oracle.bpel.services.workflow.query.model.PredicateClauseType;
import oracle.bpel.services.workflow.query.model.PredicateJoinOperatorEnum;
import oracle.bpel.services.workflow.query.model.PredicateOperationEnum;
import oracle.bpel.services.workflow.query.model.PredicateType;
import oracle.bpel.services.workflow.query.model.SortOrderEnum;
import oracle.bpel.services.workflow.query.model.TaskListRequestType;
import oracle.bpel.services.workflow.query.model.TaskOrderingType;
import oracle.bpel.services.workflow.query.model.TaskPredicateQueryType;
import oracle.bpel.services.workflow.query.model.TaskPredicateType;
//import oracle.bpel.services.workflow.repos.Table;


public class Criteria {

    public static final String WFTASK_TABLE = "WFTask";
    public static final String ASSIGNEDDATE_COLUMN = "assignedDate";

    public static TaskListRequestType getQuery(String token,
                                               CriteriaInput input,
                                               long startRow, long endRow) {

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
            columnArray = (ArrayList<String>)input.getColumns();
        } else {
            columnArray = new ArrayList<String>();
        }
        columnList.setDisplayColumn(columnArray);
        predicateQuery.setDisplayColumnList(columnList);

        // Predicate holder
        TaskPredicateType taskPredicate = new TaskPredicateType();

        // Predicate (clauses)
        List<PredicateClauseType> predicateClauses =
            new ArrayList<PredicateClauseType>();
        if (input.getClauses() != null) {
            for (Clause c :
                 input.getClauses()) { // Fill the clauses list from input
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
        
        if(input.getKeyword() != null && !input.getKeyword().equals(""))
            taskPredicate.setKeywords(input.getKeyword());
        

        // Finish the predicate
        predicateQuery.setPredicate(taskPredicate);

        predicateQuery.setStartRow(BigInteger.valueOf(startRow));

        predicateQuery.setEndRow(BigInteger.valueOf(endRow));
        
        
        //Ordering by assignation date in Descending order
        TaskOrderingType order = new TaskOrderingType();
        
        OrderingClauseType orderClause = new OrderingClauseType();
        orderClause.setColumn(ASSIGNEDDATE_COLUMN);
        orderClause.setTable(WFTASK_TABLE);
        orderClause.setSortOrder(SortOrderEnum.DESCENDING);
        
        order.getClause().add(orderClause);
        
        predicateQuery.setOrdering(order);

        payload.setTaskPredicateQuery(predicateQuery);

        // Return
        return payload;
    }

    public static CountTasksRequestType getQueryCount(String token,
                                                      CriteriaInput input) {

        WorkflowContextType wf = new WorkflowContextType();
        wf.setToken(token);

        CountTasksRequestType payload = new CountTasksRequestType();
        payload.setWorkflowContext(wf);

        //Predicate holder
        TaskPredicateType taskPredicate = new TaskPredicateType();

        PredicateType predicate = new PredicateType();

        // Predicate (clauses)
        //List<PredicateClauseType> predicateClauses = new ArrayList<PredicateClauseType>();
        if (input.getClauses() != null) {
            for (Clause c :
                 input.getClauses()) { // Fill the clauses list from input
                ClauseType clause = new ClauseType();

                ColumnType column = new ColumnType();
                column.setColumnName(c.getColumn());
                column.setTableName(WFTASK_TABLE);

                clause.setColumn(column);
                //clause.setColumnValue(column);

                clause.setJoinOperator(c.getJoin());
                clause.setValue(c.getValue());
                clause.setOperator(c.getOperator());

                predicate.getClause().add(clause);

                //System.out.println("clasue: column: " + c.getColumn() + "   JOIN: " + c.getJoin() +  " value " + c.getValue() + "  operator " + c.getOperator());
            }

            //predicate.setLogicalOperator(PredicateJoinOperatorEnum.AND);
            taskPredicate.setPredicate(predicate);
        }

        // Set Predicate AssignmentFilter
        taskPredicate.setAssignmentFilter(input.getAssignmentFilter());
        
        if(input.getKeyword() != null && !input.getKeyword().equals(""))
            taskPredicate.setKeywords(input.getKeyword());

        payload.setPredicate(taskPredicate);

        //System.out.println("Testing first value: " + payload.getPredicate().getClause().get(0).getValue());

        // Return
        return payload;
    }

    public static TaskListRequestType getBasicQuery(String token) {
        return getQuery(token, getBaseCriteria(), 0, 0);
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
