
package com.oracle.xmlns.bpel.workflow.taskservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reassignTaskType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reassignTaskType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://xmlns.oracle.com/bpel/workflow/taskService}taskServiceContextTaskTaskIdBaseType">
 *       &lt;sequence>
 *         &lt;element name="taskAssignees" type="{http://xmlns.oracle.com/bpel/workflow/taskService}taskAssigneesType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reassignTaskType", propOrder = {
    "taskAssignees"
})
public class ReassignTaskType
    extends TaskServiceContextTaskTaskIdBaseType
{

    @XmlElement(required = true)
    protected TaskAssigneesType taskAssignees;

    /**
     * Gets the value of the taskAssignees property.
     * 
     * @return
     *     possible object is
     *     {@link TaskAssigneesType }
     *     
     */
    public TaskAssigneesType getTaskAssignees() {
        return taskAssignees;
    }

    /**
     * Sets the value of the taskAssignees property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskAssigneesType }
     *     
     */
    public void setTaskAssignees(TaskAssigneesType value) {
        this.taskAssignees = value;
    }

}
