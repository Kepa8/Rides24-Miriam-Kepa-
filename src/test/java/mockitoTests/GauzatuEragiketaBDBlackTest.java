package mockitoTests;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import dataAccess.DataAccess;
import domain.User;

@RunWith(MockitoJUnitRunner.class)
public class GauzatuEragiketaBDBlackTest {
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

    @After
    public void tearDown() {
        // Clean up resources if needed
    }
/*
    @Test
    public void test1() {
        // Usar una instancia real de User en lugar de mock
        User realUser = new User("testUser1", "password", "type");  // Crear un objeto real de User
        realUser.setMoney(100.0);  // Establecer dinero inicial

        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(realUser);

        boolean result = sut.gauzatuEragiketa("testUser1", 50, true);

        verify(et).begin();
        assertEquals(150.0, realUser.getMoney(), 0.001);  // Verificar el nuevo saldo
        verify(db).merge(realUser);
        verify(et).commit();
        assertTrue(result);
    }
*/
    @Test
    public void test2() {
        User realUser = new User("testUser2", "password", "type");  // Crear un objeto real de User
        realUser.setMoney(100.0);  // Establecer dinero inicial

        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(realUser);

        boolean result = sut.gauzatuEragiketa("testUser2", 30, false);

        verify(et).begin();
        assertEquals(70.0, realUser.getMoney(), 0.001);  // Verificar el nuevo saldo
        verify(db).merge(realUser);
        verify(et).commit();
        assertTrue(result);
    }

    @Test
    public void test3() {
        User realUser = new User("testUser3", "password", "type");  // Crear un objeto real de User
        realUser.setMoney(20.0);  // Establecer dinero inicial

        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(realUser);

        boolean result = sut.gauzatuEragiketa("testUser3", 50, false);

        verify(et).begin();
        assertEquals(0.0, realUser.getMoney(), 0.001);  // Verificar el saldo reducido a 0
        verify(db).merge(realUser);
        verify(et).commit();
        assertTrue(result);
    }

    @Test(expected = NoResultException.class)
    public void test4() {
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        sut.gauzatuEragiketa("nonExistentUser", 50, true);

        // Estas verificaciones solo se ejecutarán si no se lanza una excepción
        verify(et).begin();
        verify(et).rollback();
        verify(db, never()).merge(any(User.class));
    }

    @Test(expected = RuntimeException.class)
    public void test5() {
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Database error"));

        sut.gauzatuEragiketa("testUser", 50, true);

        // Estas verificaciones solo se ejecutarán si no se lanza una excepción
        verify(et).begin();
        verify(et).rollback();
        verify(db, never()).merge(any(User.class));
    }
}