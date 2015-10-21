package com.bss.humanworkflow.client.impl.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.bpel.services.workflow.query.model.AssignmentFilterEnum;

public class CriteriaInput {
    private AssignmentFilterEnum assignmentFilter;
    private List<String> columns = new ArrayList<String>();
    private List<Clause> clauses = new ArrayList<Clause>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private String keyword;

    /**
     *
     * @return
     * The assignmentFilter
     */
    public AssignmentFilterEnum getAssignmentFilter() {
        return assignmentFilter;
    }

    /**
     *
     * @param assignmentFilter
     * The assignmentFilter
     */
    public void setAssignmentFilter(AssignmentFilterEnum assignmentFilter) {
        this.assignmentFilter = assignmentFilter;
    }

    /**
     *
     * @return
     * The columns
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     *
     * @param columns
     * The columns
     */
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    /**
     *
     * @return
     * The clauses
     */
    public List<Clause> getClauses() {
        return clauses;
    }

    /**
     *
     * @param clauses
     * The clauses
     */
    public void setClauses(List<Clause> clauses) {
        this.clauses = clauses;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
