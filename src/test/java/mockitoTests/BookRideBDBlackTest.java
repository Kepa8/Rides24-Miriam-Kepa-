package mockitoTests;

import static org.junit.Assert.*;

import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import dataAccess.DataAccess;
import domain.Ride;
import domain.Traveler;
import testOperations.TestDataAccess;

public class BookRideBDBlackTest {
    private DataAccess sut;
    private TestDataAccess testDA;

    @Before
    public void setUp() {
        try {
            sut = new DataAccess();
            testDA = new TestDataAccess();
            testDA.open();
            testDA.removeAll();
            testDA.close();
        } catch (Exception e) {
            fail("Error in setup: " + e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        testDA.open();
        testDA.removeAll();
        testDA.close();
    }

    @Test
    public void test1() {
        // Arrange
        testDA.open();
        String driverName = "driver1";
        Date date = new Date();
        String origin = "Origin";
        String destination = "Destination";
        int nPlaces = 5;
        float price = 20.0f;

        testDA.addDriverWithRide(driverName, origin, destination, date, nPlaces, price);

        assertTrue(testDA.existRide(driverName, origin, destination, date));

        String username = "testUser1";
        Traveler traveler = new Traveler(username, "password");
        traveler.setMoney(100.0);
        testDA.saveUser(traveler);

        Ride ride = testDA.getRide(driverName, origin, destination, date);
        assertNotNull("Ride should not be null", ride);
        
        testDA.close();

        boolean result = sut.bookRide(username, ride, 2, 5.0);

        assertTrue("Booking should be successful", result);
    }
    
    @Test
    public void test2() {
        // Arrange
        testDA.open();    
        String driverIdentifier = "driver2";
        Date date = new Date();
        String origin = "Origin";
        String destination = "Destination";
        int nPlaces = 1;
        float price = 20.0f;
        
        testDA.addDriverWithRide(driverIdentifier, origin, destination, date, nPlaces, price);
        
        String username = "testUser2";
        Traveler traveler = new Traveler(username, "password");
        traveler.setMoney(100.0);
        testDA.saveUser(traveler);
        
        // Obtener el ride
        Ride ride = testDA.getRide(driverIdentifier, origin, destination, date);
        assertNotNull("Ride should not be null", ride);
        
        testDA.close();
        
        // Act
        boolean result = sut.bookRide(username, ride, 2, 5.0);
        
        // Assert
        assertFalse("Booking should fail due to insufficient seats", result);
    }
    
    @Test
    public void test3() {
        // Arrange
        testDA.open();
        
        String driverName = "driver3";
        Date date = new Date();
        String origin = "Origin";
        String destination = "Destination";
        int nPlaces = 5;
        float price = 20.0f;
        
        testDA.addDriverWithRide(driverName, origin, destination, date, nPlaces, price);
        
        // Obtener el ride
        Ride ride = testDA.getRide(driverName, origin, destination, date);
        assertNotNull("Ride should not be null", ride);
        
        testDA.close();
        
        // Act
        String nonExistentUser = "nonexistentUser";
        boolean result = sut.bookRide(nonExistentUser, ride, 2, 5.0);
        
        // Assert
        assertFalse("Booking should fail due to non-existent user", result);
    }
    
    @Test
    public void test4() {
        // Arrange
        testDA.open();
        
        String driverName = "driver4";
        Date date = new Date();
        String origin = "Origin";
        String destination = "Destination";
        int nPlaces = 5;
        float price = 100.0f;
        
        testDA.addDriverWithRide(driverName, origin, destination, date, nPlaces, price);
        
        String username = "poorUser";
        Traveler traveler = new Traveler(username, "password");
        traveler.setMoney(10.0); // Muy poco dinero
        testDA.saveUser(traveler);
        
        // Obtener el ride
        Ride ride = testDA.getRide(driverName, origin, destination, date);
        assertNotNull("Ride should not be null", ride);
        
        testDA.close();
        
        // Act
        boolean result = sut.bookRide(username, ride, 2, 5.0);
        
        // Assert
        assertFalse("Booking should fail due to insufficient money", result);
    }
}