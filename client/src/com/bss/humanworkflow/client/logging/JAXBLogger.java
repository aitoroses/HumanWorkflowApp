package com.bss.humanworkflow.client.logging;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

public class JAXBLogger {
  public static void log(Object o) throws JAXBException {
    final Marshaller m = JAXBContext.newInstance(o.getClass())
                                    .createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    //final StringWriter w = new StringWriter();
    
    // JAXBElement
    JAXBElement jax =
      new JAXBElement (new QName("uri","local"), o.getClass(), o);
    m.marshal(jax, System.out);

    //m.marshal(o, System.out);
    //return w.toString();
  }
}
