package mockitoTests;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import javax.persistence.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import dataAccess.DataAccess;
import domain.*;
import testOperations.TestDataAccess;

public class BookRideMockWhiteTest {

    DataAccess sut; 
    TestDataAccess testDataAccess;
    MockedStatic<Persistence> persistenceMock;
    AutoCloseable closeable;

    @Mock
    EntityManagerFactory entityManagerFactory;
    @Mock
    EntityManager db; 
    @Mock
    EntityTransaction et;
    @Mock
    TypedQuery<Traveler> query;

    @Before
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
        
        persistenceMock = mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(any()))
            .thenReturn(entityManagerFactory);
        
        when(entityManagerFactory.createEntityManager()).thenReturn(db);
        when(db.getTransaction()).thenReturn(et);
        when(db.createQuery(anyString(), eq(Traveler.class))).thenReturn(query);
        
        doNothing().when(et).begin();
        doNothing().when(et).commit();
        doNothing().when(et).rollback();
        
        sut = new DataAccess(db);
        testDataAccess = new TestDataAccess();
        testDataAccess.setEntityManager(db);
    }

    @After
    public void closeAll() throws Exception {
        persistenceMock.close();
        closeable.close();
    }

    @Test
    public void testBookRideSuccess() {
        // Setup
        String username = "testUser1";
        Driver driver = new Driver("driverUser1", "passwd");
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, driver);
        int seats = 2;
        double desk = 5.0;

        Traveler mockedTraveler = new Traveler(username, "password");
        mockedTraveler.setMoney(100.0);
        
        when(query.getResultList()).thenReturn(Collections.singletonList(mockedTraveler));
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(db.find(eq(Traveler.class), eq(username))).thenReturn(mockedTraveler);

        // Execute
        boolean result = sut.bookRide(username, ride, seats, desk);

        // Verify
        assertTrue("bookRide should return true", result);
        assertEquals("Ride should have 3 places left", 3, ride.getnPlaces());
        assertEquals("Traveler should have 70.0 money left", 70.0, mockedTraveler.getMoney(), 0.01);
        assertEquals("Traveler should have 30.0 frozen money", 30.0, mockedTraveler.getIzoztatutakoDirua(), 0.01);

        verify(db).persist(any(Booking.class));
        verify(db).merge(ride);
        verify(db).merge(mockedTraveler);
        verify(et).begin();
        verify(et).commit();
    }

    @Test
    public void testBookRideNoTraveler() {
        String username = "nonExistentUser";
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, new Driver("driver", "pass"));
        
        when(query.getResultList()).thenReturn(Collections.emptyList());

        boolean result = sut.bookRide(username, ride, 2, 5.0);

        assertFalse(result);
        verify(et, never()).commit();
    }

    @Test
    public void testBookRideNotEnoughSeats() {
        String username = "testUser";
        Traveler traveler = new Traveler(username, "pass");
        traveler.setMoney(100.0);
        Ride ride = new Ride("from", "to", new Date(), 1, 20.0, new Driver("driver", "pass"));
        
        when(query.getResultList()).thenReturn(Arrays.asList(traveler));

        boolean result = sut.bookRide(username, ride, 2, 5.0);

        assertFalse(result);
        verify(et, never()).commit();
    }

    @Test
    public void testBookRideInsufficientMoney() {
        String username = "testUser";
        Traveler traveler = new Traveler(username, "pass");
        traveler.setMoney(10.0);
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, new Driver("driver", "pass"));
        
        when(query.getResultList()).thenReturn(Arrays.asList(traveler));

        boolean result = sut.bookRide(username, ride, 2, 5.0);

        assertFalse(result);
        verify(et, never()).commit();
    }

    @Test
    public void testBookRideExceptionHandling() {
        String username = "testUser";
        Traveler traveler = new Traveler(username, "pass");
        traveler.setMoney(100.0);
        Ride ride = new Ride("from", "to", new Date(), 5, 20.0, new Driver("driver", "pass"));
        
        when(query.getResultList()).thenReturn(Arrays.asList(traveler));
        doThrow(new RuntimeException("DB Error")).when(db).persist(any(Booking.class));

        boolean result = sut.bookRide(username, ride, 2, 5.0);

        assertFalse(result);
        verify(et).rollback();
    }
}