package com.bss.humanworkflow.client.impl.payload.deserializer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is just a holder used by the PayloadDeserializer
 * for extracting a ElementNSImpl object
 */

@XmlRootElement(name="payloadElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayloadElement {
  
  @XmlAnyElement
  protected Object payload;

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  public Object getPayload() {
    return payload;
  }
}
