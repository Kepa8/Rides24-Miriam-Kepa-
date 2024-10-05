package mockitoTests;

import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import dataAccess.DataAccess;
import domain.User;

@RunWith(MockitoJUnitRunner.class)
public class GauzatuEragiketaMockWhiteTest {
    private DataAccess sut;

    @Mock
    private EntityManager db;

    @Mock
    private EntityTransaction et;

    @Mock
    private TypedQuery<User> query;

    @Before
    public void init() {
        when(db.getTransaction()).thenReturn(et);
        when(db.createQuery(anyString(), eq(User.class))).thenReturn(query);
        sut = new DataAccess(db);
    }

    @Test
    public void testExceptionHandling() {
        // Redirigir la salida de error
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        try {
            // Simular una excepción al configurar los parámetros
            when(query.setParameter(anyString(), anyString())).thenThrow(new RuntimeException("Database error"));

            // Ejecutar la operación
            boolean result = sut.gauzatuEragiketa("testUser", 50, true);

            // Verificar que la transacción comenzó y fue revertida
            verify(et).begin();
            verify(et).rollback();

            // Verificar que no se realizó el merge
            verify(db, never()).merge(any());

            // Asegurarse de que la función retornó false
            assertFalse(result);

        } finally {
            // Restaurar el flujo de error a su valor original
            System.setErr(System.err);
        }
    }
    
    @Test
    public void testDepositSuccess() {
        User mockedUser = mock(User.class);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockedUser);
        when(mockedUser.getMoney()).thenReturn(100.0);

        boolean result = sut.gauzatuEragiketa("testUser", 50, true);

        verify(et).begin();
        verify(mockedUser).setMoney(150.0);
        verify(db).merge(mockedUser);
        verify(et).commit();
        assertTrue(result);
    }

    @Test
    public void testWithdrawSuccess() {
        User mockedUser = mock(User.class);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockedUser);
        when(mockedUser.getMoney()).thenReturn(100.0);

        boolean result = sut.gauzatuEragiketa("testUser", 30, false);

        verify(et).begin();
        verify(mockedUser).setMoney(70.0);
        verify(db).merge(mockedUser);
        verify(et).commit();
        assertTrue(result);
    }

    @Test
    public void testWithdrawInsufficientFunds() {
        User mockedUser = mock(User.class);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockedUser);
        when(mockedUser.getMoney()).thenReturn(20.0);

        boolean result = sut.gauzatuEragiketa("testUser", 50, false);

        verify(et).begin();
        verify(mockedUser).setMoney(0.0);
        verify(db).merge(mockedUser);
        verify(et).commit();
        assertTrue(result);
    }

    @Test
    public void testUserNotFound() {
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        boolean result = sut.gauzatuEragiketa("nonExistentUser", 50, true);

        verify(et).begin();
        verify(et).commit();
        verify(db, never()).merge(any(User.class));
        assertFalse(result);
    }
}