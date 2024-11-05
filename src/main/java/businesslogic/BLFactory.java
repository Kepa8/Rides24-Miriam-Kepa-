package businesslogic;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML;
import dataAccess.DataAccess;

public class BLFactory {
    private ConfigXML config;
    private BLFacade businessLogicLocal;
    public BLFactory(ConfigXML config) {
        this.config = config;
    }

    public BLFacade createBLFacade() {
        if (config.isBusinessLogicLocal()) {
            DataAccess da = new DataAccess();
            return new BLFacadeImplementation(da);
        } else {
            String serviceName = "http://" + config.getBusinessLogicNode() + ":" + config.getBusinessLogicPort() + "/ws/" + config.getBusinessLogicName() + "?wsdl";
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

    public BLFacade getBusinessLogicFactory(boolean isLocal) {
        if (isLocal) {
            // Crear BLFacade como local
            if (businessLogicLocal == null) {
                businessLogicLocal = createBLFacade();
            }
            return businessLogicLocal;
        } else {
            // Crear BLFacade como remoto (si se requiere)
            return createBLFacade();
        }
    }
}