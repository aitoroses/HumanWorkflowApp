package com.bss.humanworkflow.client.impl.payload;

import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Payload {
  
  public ElementNSImpl el;
  
  public Payload(Object el) {
    this.el = (ElementNSImpl) el;
  }
  
  public String getAttribute(String attr) {
    NodeList nodes = el.getChildNodes();
    for(int i=0; i<nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeName().equals(attr)) {
        // The first child it's the value
        if (node.getFirstChild() != null) {
          node = node.getFirstChild();
        }
        return node.getNodeValue();
      }
    }
    return null;
  }
}
