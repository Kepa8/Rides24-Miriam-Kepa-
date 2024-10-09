package mockitoTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataAccess.DataAccess;
import domain.Booking;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import testOperations.TestDataAccess;

public class BookRideMockBlackTest {
    //proba
    static DataAccess sut; 
    static TestDataAccess testDataAccess;
    protected MockedStatic<Persistence> persistenceMock;

    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager db; 
    @Mock
    protected EntityTransaction et;
    @Mock
    protected TypedQuery<Traveler> query;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
            .thenReturn(entityManagerFactory);
        
        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        Mockito.doReturn(query).when(db).createQuery(anyString(), eq(Traveler.class));
        sut = new DataAccess(db);
        testDataAccess = new TestDataAccess();
        testDataAccess.setEntityManager(db); // Asegurarse de que TestDataAccess use el EntityManager mockeado
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }
    
    @Test
    public void test1() {
        String username = "testUser1";
        Driver driver = new Driver("driverUser1", "passwd");
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, driver);
        int seats = 2;
        double desk = 5.0;

        Traveler mockedTraveler = new Traveler(username, "password");
        mockedTraveler.setMoney(100.0);
        
        // Configurar el mock para la consulta
        List<Traveler> travelerList = Collections.singletonList(mockedTraveler);
        Mockito.when(query.getResultList()).thenReturn(travelerList);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);

        // Configurar el mock para EntityManager
        Mockito.when(db.createQuery(anyString(), eq(Traveler.class))).thenReturn(query);
        Mockito.when(db.find(eq(Traveler.class), eq(username))).thenReturn(mockedTraveler);

        boolean result = false;
        try {
            sut.open();
            result = sut.bookRide(username, ride, seats, desk);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }

        // Assert
        assertTrue("bookRide should return true", result);
        assertEquals("Ride should have 3 places left", 3, ride.getnPlaces());
        assertEquals("Traveler should have 60.0 money left", 70.0, mockedTraveler.getMoney(), 0.01);
        assertEquals("Traveler should have 40.0 frozen money", 30.0, mockedTraveler.getIzoztatutakoDirua(), 0.01);

        // Verify interactions
        verify(db).persist(any(Booking.class));
        verify(db).merge(ride);
        verify(db).merge(mockedTraveler);
        verify(et).begin();
        verify(et).commit();
    }

    @Test
    // sut.bookRide: Usuario no encontrado.
    public void test2() {
        String username = "nonExistentUser";
        Driver driver = new Driver("driverUser2", "passwd");
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, driver);
        int seats = 2;
        double desk = 5.0;

        Mockito.when(db.find(Traveler.class, username)).thenReturn(null);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenThrow(new RuntimeException());

        boolean result = false;
        try {
            sut.open();
            result = sut.bookRide(username, ride, seats, desk);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }
        assertFalse(result);
    }

    @Test
    // sut.bookRide: No hay suficientes asientos.
    public void test3() {
        String username = "testUser2";
        Driver driver = new Driver("driverUser3", "passwd");
        Ride ride = new Ride("from", "to", new Date(), 1, 20.0, driver);
        int seats = 2;
        double desk = 5.0;

        Traveler mockedTraveler = new Traveler(username, "password");
        mockedTraveler.setMoney(100.0);
        Mockito.when(db.find(Traveler.class, username)).thenReturn(mockedTraveler);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(mockedTraveler);

        boolean result = false;
        try {
            sut.open();
            result = sut.bookRide(username, ride, seats, desk);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }

        // Assert
        assertFalse(result);
    }
    
    @Test
    public void test4() {
        String username = "nonExistentUser";
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, new Driver("driver", "pass"));
        
        when(query.getResultList()).thenReturn(Collections.emptyList());

        boolean result = sut.bookRide(username, ride, 2, 5.0);

        assertFalse(result);
        verify(et, never()).commit();
    }
    @Test
    // sut.bookRide: Saldo insuficiente.
    public void test5() {
        String username = "testUser4";
        Driver driver = new Driver("driverUser4", "passwd");
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, driver);
        int seats = 2;
        double desk = 5.0;

        Traveler mockedTraveler = new Traveler(username, "password");
        mockedTraveler.setMoney(10.0);
        Mockito.when(db.find(Traveler.class, username)).thenReturn(mockedTraveler);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(mockedTraveler);

        boolean result = false;
        try {
            sut.open();
            result = sut.bookRide(username, ride, seats, desk);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }

        // Assert
        assertFalse(result);
    }
}