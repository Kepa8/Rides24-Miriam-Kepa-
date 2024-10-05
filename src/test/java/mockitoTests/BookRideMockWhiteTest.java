package mockitoTests;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Date;
import dataAccess.DataAccess;
import domain.Driver;
import domain.Ride;
import domain.Traveler;
import domain.User;

public class BookRideMockWhiteTest {

    @InjectMocks
    private DataAccess sut;  // System under test (DataAccess)

    @Mock
    private EntityManager db;  // Mock del EntityManager

    @Mock
    private EntityTransaction et;  // Mock de EntityTransaction

    @Mock
    private TypedQuery<User> query;  // Mock de una query para encontrar el viajero

    @SuppressWarnings("deprecation")
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        // Configura el EntityManager y el EntityTransaction
        when(db.getTransaction()).thenReturn(et);
        
        // Mockea la consulta TypedQuery
        when(db.createNamedQuery("User.getTravelerByUsername", User.class)).thenReturn(query);
        
        // Configura el comportamiento de setParameter y otros métodos
        when(query.setParameter(anyString(), any())).thenReturn(query); // Simulamos que devuelve el propio query para que se puedan encadenar llamadas.
        when(query.getSingleResult()).thenReturn(mock(User.class)); // Simula un usuario como resultado
    }

    @Test
    public void testBookRide_TravelerNotExists() {
        // Simulamos que el viajero no existe en la base de datos
        when(db.find(Traveler.class, "nonexistentUser")).thenReturn(null);

        // Creamos un Driver para el Ride (puede ser un mock)
        Driver driver = mock(Driver.class);

        // Creamos una Ride usando el constructor que requiere parámetros
        Ride ride = new Ride("Madrid", "Barcelona", new Date(), 5, 20, driver);

        // Ejecutamos el método y verificamos el resultado
        boolean result = sut.bookRide("nonexistentUser", ride, 1, 0);
        assertFalse(result); //ESTA MAL

        // Verificamos que no se haya iniciado una transacción
        verify(et, never()).begin();
    }

    @Test
    public void testBookRide_NotEnoughSeats() {
        // Creamos un viajero existente usando el constructor que requiere username y passwd
        Traveler traveler = new Traveler("existentUser", "password123");

        // Creamos un Driver para el Ride (puede ser un mock)
        Driver driver = mock(Driver.class);

        // Creamos una Ride usando el constructor que requiere parámetros
        Ride ride = new Ride("Madrid", "Barcelona", new Date(), 1, 10, driver);  // Solo 1 asiento disponible

        when(db.find(Traveler.class, "existentUser")).thenReturn(traveler);

        // Ejecutamos el método y verificamos el resultado
        boolean result = sut.bookRide("existentUser", ride, 5, 0);  // Pedimos más asientos de los disponibles
        assertFalse(result);

        // Verificamos que no se haya persistido ni hecho merge en la base de datos
        verify(db, never()).persist(any());
        verify(db, never()).merge(any());
    }

    @Test
    public void testBookRide_NotEnoughMoney() {
        // Creamos un viajero con dinero insuficiente
        Traveler traveler = new Traveler("existentUser", "password123");
        traveler.setMoney(10);  // Dinero insuficiente

        // Creamos un Driver para el Ride (puede ser un mock)
        Driver driver = mock(Driver.class);

        // Creamos una Ride con suficientes asientos y precio alto
        Ride ride = new Ride("Madrid", "Barcelona", new Date(), 5, 100, driver);  // Precio elevado

        when(db.find(Traveler.class, "existentUser")).thenReturn(traveler);

        // Ejecutamos el método y verificamos el resultado
        boolean result = sut.bookRide("existentUser", ride, 2, 0);  // No tiene suficiente dinero
        assertFalse(result);

        // Verificamos que no se haya persistido ni hecho merge en la base de datos
        verify(db, never()).persist(any());
        verify(db, never()).merge(any());
    }

    @Test
    public void testBookRide_SuccessfulBooking() {
        // Creamos un viajero con suficiente dinero
        Traveler traveler = new Traveler("existentUser", "password123");
        traveler.setMoney(500);  // Dinero suficiente

        // Creamos un Driver para el Ride (puede ser un mock)
        Driver driver = mock(Driver.class);

        // Creamos una Ride con suficientes asientos y precio razonable
        Ride ride = new Ride("Madrid", "Barcelona", new Date(), 5, 50, driver);  // Precio razonable

        when(db.find(Traveler.class, "existentUser")).thenReturn(traveler);

        // Ejecutamos el método y verificamos el resultado
        boolean result = sut.bookRide("existentUser", ride, 2, 10);
        assertTrue(result);

        // Verificamos que los métodos de persistencia se llaman correctamente
        verify(db).persist(any());  // Debe persistir la reserva
        verify(db).merge(any(Ride.class));  // Debe hacer merge de la ride
        verify(db).merge(any(Traveler.class));  // Debe hacer merge del traveler
        verify(et).commit();  // La transacción debe ser committeada
    }
}
