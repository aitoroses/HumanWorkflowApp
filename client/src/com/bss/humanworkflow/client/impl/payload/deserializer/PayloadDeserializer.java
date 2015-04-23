package com.bss.humanworkflow.client.impl.payload.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;

import javax.xml.bind.Unmarshaller;


/**
 * The target of this class is to custom deserialize the body of the payload using PayloadElement class
 * In order to get a ElementNSImpl object we have to wrap the existing tag into <payloadElement>
 * Then JAXB will take care of creating the deserialized ElementNSImpl
 */
public class PayloadDeserializer extends JsonDeserializer<ElementNSImpl> {
  public ElementNSImpl deserialize(JsonParser parser, DeserializationContext context) {
    ElementNSImpl payload = null;
    try {
      String value = parser.getText();
      
      value = value.substring(0, 39) + "<payloadElement>" + value.substring(39, value.length()) + "</payloadElement>";
      
      JAXBContext jaxbContext = JAXBContext.newInstance(PayloadElement.class);
       
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      PayloadElement el = (PayloadElement) jaxbUnmarshaller.unmarshal(new StringReader(value));
      
      payload = (ElementNSImpl)el.getPayload();
            
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return payload;
  }
}
