
package com.oracle.xmlns.bpel.workflow.taskservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import oracle.bpel.services.workflow.task.model.Task;


/**
 * <p>Java class for taskUpdatedMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="taskUpdatedMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="action" type="{http://xmlns.oracle.com/bpel/workflow/taskService}actionType"/>
 *         &lt;element name="updatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://xmlns.oracle.com/bpel/workflow/task}task"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "taskUpdatedMessageType", propOrder = {
    "action",
    "updatedBy",
    "task"
})
public class TaskUpdatedMessageType {

    @XmlElement(required = true)
    protected ActionType action;
    @XmlElement(required = true)
    protected String updatedBy;
    @XmlElement(namespace = "http://xmlns.oracle.com/bpel/workflow/task", required = true)
    protected Task task;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link ActionType }
     *     
     */
    public ActionType getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionType }
     *     
     */
    public void setAction(ActionType value) {
        this.action = value;
    }

    /**
     * Gets the value of the updatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the updatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link Task }
     *     
     */
    public Task getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link Task }
     *     
     */
    public void setTask(Task value) {
        this.task = value;
    }

}
