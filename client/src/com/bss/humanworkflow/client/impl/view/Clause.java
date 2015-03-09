package com.bss.humanworkflow.client.impl.view;

import java.util.HashMap;
import java.util.Map;

import oracle.bpel.services.workflow.evidence.model.PredicateOperatorEnumType;
import oracle.bpel.services.workflow.query.model.PredicateJoinOperatorEnum;
import oracle.bpel.services.workflow.query.model.PredicateOperationEnum;

public class Clause {

  private PredicateJoinOperatorEnum join;
  private String column;
  private PredicateOperationEnum operator;
  private String value;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
  
  /**
  * 
  * @return
  * The join
  */
  public PredicateJoinOperatorEnum getJoin() {
  return join;
  }
  
  /**
  * 
  * @param join
  * The join
  */
  public void setJoin(PredicateJoinOperatorEnum join) {
  this.join = join;
  }
  
  /**
  * 
  * @return
  * The column
  */
  public String getColumn() {
  return column;
  }
  
  /**
  * 
  * @param column
  * The column
  */
  public void setColumn(String column) {
  this.column = column;
  }
  
  /**
  * 
  * @return
  * The operator
  */
  public PredicateOperationEnum getOperator() {
  return operator;
  }
  
  /**
  * 
  * @param operator
  * The operator
  */
  public void setOperator(PredicateOperationEnum operator) {
  this.operator = operator;
  }
  
  /**
  * 
  * @return
  * The value
  */
  public String getValue() {
  return value;
  }
  
  /**
  * 
  * @param value
  * The value
  */
  public void setValue(String value) {
  this.value = value;
  }
  
  public Map<String, Object> getAdditionalProperties() {
  return this.additionalProperties;
  }
  
  public void setAdditionalProperty(String name, Object value) {
  this.additionalProperties.put(name, value);
  }

}
