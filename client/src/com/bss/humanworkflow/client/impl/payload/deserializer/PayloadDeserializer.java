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
        
      //String value = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><payload xmlns=\"http://xmlns.oracle.com/bpel/workflow/task\"><approvers>at_auditor,sk_auditor</approvers><requestId>20524</requestId><expirationDays>P1D</expirationDays><currentStepId>-1</currentStepId><textAttribute1>Market research</textAttribute1><textAttribute2>CC</textAttribute2><textAttribute3>dsfasf</textAttribute3><textAttribute4/><textAttribute5/><numberAttribute1>0</numberAttribute1><numberAttribute2>0</numberAttribute2><numberAttribute3>0</numberAttribute3><userComment/><userAction/><businessRole>_Medical Advisor</businessRole><attributes xmlns=\"http://process.session.eappconfig.np5services.bss.com/types\"><attributes xmlns=\"http://xmlns.oracle.com/bpel/workflow/task\"><label>OU</label><value>AUSTRIA_PH</value></attributes><attributes xmlns=\"\"><label>Therapeutical Area</label><value>CC</value></attributes></attributes></payload>";
      
      value = value.substring(0, 39) + "<payloadElement>" + value.substring(39, value.length()) + "</payloadElement>";
      
      JAXBContext jaxbContext = JAXBContext.newInstance(PayloadElement.class);
       
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      PayloadElement el = (PayloadElement) jaxbUnmarshaller.unmarshal(new StringReader(value));
         
      
      payload = (ElementNSImpl)el.getPayload();
        
          
       /*
        NodeList nodes = payload.getChildNodes();
        
        for(int i = 0; i<nodes.getLength(); i++){
            //System.out.println("Node Name: " + nodes.item(i).getNodeName());
            //System.out.println("Node Value: " + nodes.item(i).getTextContent());
            
            
            if(nodes.item(i).getNodeName().equals("attributes")){
                NodeList childs = nodes.item(i).getChildNodes();
                
                nodes.item(i).setPrefix("ns0");
                
                System.out.println("Attributes:");
                
                for(int j = 0; j<childs.getLength(); j++){
                    System.out.println("Child Name: " + childs.item(j).getNodeName());
                    System.out.println("Child Value: " + childs.item(j).getTextContent());
                    
                    NodeList subchilds = childs.item(j).getChildNodes();
                    
                    for(int z = 0; z<subchilds.getLength(); z++){
                        System.out.println("SubChild Name: " + subchilds.item(z).getNodeName());
                        System.out.println("SubChild Value: " + subchilds.item(z).getTextContent());
                    }
                }
                
                System.out.println("\n\n");                    
            }
        }   
        */
        
        //JAXBLogger.log(el);
        
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return payload;
  }
    
    
    public static void main(String[] args){
        //JsonParser parser = new JsonParser();
        
        PayloadDeserializer desre = new PayloadDeserializer();
        
        desre.deserialize(null, null);
    }
    
}
