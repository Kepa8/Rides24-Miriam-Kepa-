package domain;

import businesslogic.BLFacade;
import businesslogic.BLFactory;
import configuration.ConfigXML;

public class Main {
    public static void main(String[] args) {
        ConfigXML co = ConfigXML.getInstance();
        boolean isLocal = true;
        BLFacade blFacade = new BLFactory(co).getBusinessLogicFactory(isLocal);
        Driver driver = blFacade.getDriver("Urtzi");
        DriverTable driverTable = new DriverTable(driver);
        driverTable.setVisible(true);
    }
}