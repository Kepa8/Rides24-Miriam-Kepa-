package mockitoTests;
import org.mockito.Mockito;
import businesslogic.BLFacade;
import businesslogic.BLFacadeImplementation;
import businesslogic.BLFactory;
import configuration.ConfigXML;
import dataAccess.DataAccess;
import org.junit.Test;


public class BLFacadeFactoryTest {

	@Test
	public void testCreateBLFacadeLocal() {
	    // Arrange
	    ConfigXML configXML = Mockito.mock(ConfigXML.class);
	    Mockito.when(configXML.isBusinessLogicLocal()).thenReturn(true);
	    BLFactory factory = new BLFactory(configXML);

	    // Act
	    BLFacade blFacade = factory.createBLFacade();

	    // Assert
	    if (!(blFacade instanceof BLFacadeImplementation)) {
	        throw new AssertionError("Expected a BLFacadeImplementation instance");
	    }
	}

	@Test
	public void testCreateBLFacadeRemote() {
	    // Arrange
	    ConfigXML configXML = Mockito.mock(ConfigXML.class);
	    Mockito.when(configXML.isBusinessLogicLocal()).thenReturn(false);
	    Mockito.when(configXML.getBusinessLogicNode()).thenReturn("remote-server");
	    Mockito.when(configXML.getBusinessLogicPort()).thenReturn("8080");
	    Mockito.when(configXML.getBusinessLogicName()).thenReturn("BLFacadeImplementationService");
	    BLFactory factory = new BLFactory(configXML);

	    // Act & Assert
	    try {
	        factory.createBLFacade();
	        throw new AssertionError("Expected a RuntimeException to be thrown");
	    } catch (RuntimeException e) {
	        // Expected behavior
	    }
	}
}