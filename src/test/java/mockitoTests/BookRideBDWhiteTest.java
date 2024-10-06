package mockitoTests;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Booking;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import testOperations.TestDataAccess;

public class BookRideBDWhiteTest {
    
	private static DataAccess sut;
    private static TestDataAccess testDA;
    private static EntityManagerFactory emf;
    private static EntityManager em;

    @Before
    public void setUp() {
        try {
            emf = Persistence.createEntityManagerFactory("objectdb:$objectdb/db/rides.odb");
            em = emf.createEntityManager();
            sut = new DataAccess(em);
            testDA = new TestDataAccess();
            testDA.setEntityManager(em);
            
            // Iniciar transacción para preparar el estado inicial
            em.getTransaction().begin();
            
            // Intentar limpiar datos existentes de manera segura
            try {
                em.createQuery("DELETE FROM Booking b").executeUpdate();
            } catch (Exception e) {
                // Si la entidad no existe, ignorar el error
            }
            
            try {
                em.createQuery("DELETE FROM Ride r").executeUpdate();
            } catch (Exception e) {
                // Si la entidad no existe, ignorar el error
            }
            
            try {
                em.createQuery("DELETE FROM Traveler t").executeUpdate();
            } catch (Exception e) {
                // Si la entidad no existe, ignorar el error
            }
            
            try {
                em.createQuery("DELETE FROM Driver d").executeUpdate();
            } catch (Exception e) {
                // Si la entidad no existe, ignorar el error
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }
    
    @After
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    public void test1() {
        try {
            // Configuración
            String username = "testUser1";
            Driver driver = new Driver("driverUser1", "passwd");
            Traveler traveler = new Traveler(username, "password");
            traveler.setMoney(100.0);
            Ride ride = new Ride("Origin", "Destination", new Date(), 5, 20.0, driver);
            
            em.getTransaction().begin();
            em.persist(driver);
            em.persist(traveler);
            em.persist(ride);
            em.getTransaction().commit();
            
            // Test
            boolean result = sut.bookRide(username, ride, 2, 5.0);
            
            // Verificación
            assertTrue(result);
            
            em.refresh(ride);
            em.refresh(traveler);
            
            assertEquals(3, ride.getnPlaces());
            assertEquals(70.0, traveler.getMoney(), 0.01);
            assertEquals(30.0, traveler.getIzoztatutakoDirua(), 0.01);
            
            // Verificar la reserva
            Booking booking = em.createQuery("SELECT b FROM Booking b WHERE b.traveler.username = :username", 
                                            Booking.class)
                               .setParameter("username", username)
                               .getSingleResult();
            assertNotNull(booking);
            assertEquals(2, booking.getSeats());
            assertEquals(5.0, booking.getDeskontua(), 0.01);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void test2() {
        try {
            // Configuración
            String username = "nonExistentUser";
            Driver driver = new Driver("driverUser2", "passwd");
            Ride ride = new Ride("Origin", "Destination", new Date(), 5, 20.0, driver);
            
            em.getTransaction().begin();
            em.persist(driver);
            em.persist(ride);
            em.getTransaction().commit();
            
            // Test
            boolean result = sut.bookRide(username, ride, 2, 5.0);
            
            // Verificación
            assertFalse(result);
            
            // Verificar que no hay reservas
            Long bookingCount = em.createQuery("SELECT COUNT(b) FROM Booking b", Long.class)
                                 .getSingleResult();
            assertEquals(Long.valueOf(0), bookingCount);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void test3() {
        try {
            // Configuración
            String username = "testUser2";
            Driver driver = new Driver("driverUser3", "passwd");
            Traveler traveler = new Traveler(username, "password");
            traveler.setMoney(100.0);
            Ride ride = new Ride("Origin", "Destination", new Date(), 1, 20.0, driver);
            
            em.getTransaction().begin();
            em.persist(driver);
            em.persist(traveler);
            em.persist(ride);
            em.getTransaction().commit();
            
            // Test
            boolean result = sut.bookRide(username, ride, 2, 5.0);
            
            // Verificación
            assertFalse(result);
            
            em.refresh(ride);
            em.refresh(traveler);
            
            assertEquals(1, ride.getnPlaces());
            assertEquals(100.0, traveler.getMoney(), 0.01);
            assertEquals(0.0, traveler.getIzoztatutakoDirua(), 0.01);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void test4() {
        try {
            // Configuración
            String username = "testUser3";
            Driver driver = new Driver("driverUser4", "passwd");
            Traveler traveler = new Traveler(username, "password");
            traveler.setMoney(10.0);
            Ride ride = new Ride("Origin", "Destination", new Date(), 5, 20.0, driver);
            
            em.getTransaction().begin();
            em.persist(driver);
            em.persist(traveler);
            em.persist(ride);
            em.getTransaction().commit();
            
            // Test
            boolean result = sut.bookRide(username, ride, 2, 5.0);
            
            // Verificación
            assertFalse(result);
            
            em.refresh(ride);
            em.refresh(traveler);
            
            assertEquals(5, ride.getnPlaces());
            assertEquals(10.0, traveler.getMoney(), 0.01);
            assertEquals(0.0, traveler.getIzoztatutakoDirua(), 0.01);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
}