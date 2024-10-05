package mockitoTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

public class BookRideMockBlackTest {

    @InjectMocks
    private DataAccess sut;  // System under test (DataAccess)

    @Mock
    private EntityManager db;  // Mock del EntityManager

    @Mock
    private EntityTransaction et;  // Mock de EntityTransaction

    @Mock
    private TypedQuery<Traveler> query;  // Mock de una query para encontrar el viajero

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        // Configura el EntityManager y el EntityTransaction
        when(db.getTransaction()).thenReturn(et);
        
        // Mockea la consulta TypedQuery
        when(db.createNamedQuery("User.getTravelerByUsername", Traveler.class)).thenReturn(query);
        
        // Configura el comportamiento de setParameter y otros métodos
        when(query.setParameter(anyString(), any())).thenReturn(query); // Simulamos que devuelve el propio query para que se puedan encadenar llamadas.
    }

    @Test
    public void testBookRide_TravelerNotExists() {
        // Simula que el viajero no existe
        when(sut.getTraveler("usuarioInexistente")).thenReturn(null);

        Ride mockRide = new Ride("origen", "destino", new Date(), 1, 100.0, mock(Driver.class));

        // Ejecuta la reserva con un número de asientos y precio
        boolean result = sut.bookRide("usuarioInexistente", mockRide, 1, 100.0);

        // Verifica que el resultado sea false
        assertFalse("La reserva debería fallar porque el viajero no existe", result);
    }

    @Test
    public void testBookRide_NotEnoughSeats() {
        // Simula el viajero
        Traveler traveler = new Traveler("usuario3", "password"); // Contraseña ficticia
        when(sut.getTraveler("usuario3")).thenReturn(traveler);

        Ride mockRide = new Ride("origen", "destino", new Date(), 10, 100.0, mock(Driver.class)); // Más asientos de los disponibles

        // Ejecuta la reserva con un número de asientos y precio
        boolean result = sut.bookRide("usuario3", mockRide, 10, 100.0);

        // Verifica que el resultado sea false
        assertFalse("La reserva debería fallar por falta de asientos disponibles", result);
    }

    @Test
    public void testBookRide_NotEnoughMoney() {
        // Simula el viajero con poco dinero
        Traveler traveler = new Traveler("usuario2", "password"); // Contraseña ficticia
        when(sut.getTraveler("usuario2")).thenReturn(traveler);

        Ride mockRide = new Ride("origen", "destino", new Date(), 1, 100.0, mock(Driver.class));

        // Ejecuta la reserva con un número de asientos y precio
        boolean result = sut.bookRide("usuario2", mockRide, 1, 100.0);

        // Verifica que el resultado sea false
        assertFalse("La reserva debería fallar por falta de fondos", result);
    }

    @Test
    public void testBookRide_SuccessfulBooking() {
        // Simula el viajero
        Traveler traveler = new Traveler("usuario1", "password"); // Contraseña ficticia
        when(sut.getTraveler("usuario1")).thenReturn(traveler);

        Ride mockRide = new Ride("origen", "destino", new Date(), 1, 100.0, mock(Driver.class));

        // Ejecuta la reserva con un número de asientos y precio
        boolean result = sut.bookRide("usuario1", mockRide, 1, 100.0);

        // Verifica que el resultado sea true
        assertTrue("La reserva debería ser exitosa", result);
    }
}
