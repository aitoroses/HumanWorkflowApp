package com.bss.humanworkflow.client.model;


import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import java.io.StringReader;

import java.sql.Timestamp;

import java.text.ParsePosition;

import java.util.Date;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;


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
