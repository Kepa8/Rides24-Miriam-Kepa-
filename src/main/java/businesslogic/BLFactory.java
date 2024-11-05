package businesslogic;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML;
import dataAccess.DataAccess;

public class BLFactory {
    public BLFacade createBLFacade() {
        ConfigXML c = ConfigXML.getInstance();
        if (c.isBusinessLogicLocal()) {
            DataAccess da = new DataAccess();
            return new BLFacadeImplementation(da);
        } else {
            // Implementación remota
            String serviceName = "http://" + c.getBusinessLogicNode() + ":" + c.getBusinessLogicPort() + "/ws/" + c.getBusinessLogicName() + "?wsdl";
            try {
                URL url = new URL(serviceName);
                QName qname = new QName("http://businesslogic/", "BLFacadeImplementationService");
                Service service = Service.create(url, qname);
                return service.getPort(BLFacade.class);
            } catch (Exception e) {
                throw new RuntimeException("Error creating remote BLFacade: " + e.getMessage(), e);
            }
        }
    }
    
    
    
    
    
    
}