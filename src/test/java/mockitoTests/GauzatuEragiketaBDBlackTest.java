package mockitoTests;

import static org.mockito.Mockito.*;
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

    @Test
    public void testDepositSuccess() {
        // Arrange
        User mockedUser = mock(User.class);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockedUser);
        when(mockedUser.getMoney()).thenReturn(100.0);

        // Act
        boolean result = sut.gauzatuEragiketa("testUser", 50, true);

        // Assert
        verify(et).begin();
        verify(mockedUser).setMoney(150.0);
        verify(db).merge(mockedUser);
        verify(et).commit();

        assertTrue(result);
    }

    @Test
    public void testWithdrawSuccess() {
        // Arrange
        User mockedUser = mock(User.class);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockedUser);
        when(mockedUser.getMoney()).thenReturn(100.0);

        // Act
        boolean result = sut.gauzatuEragiketa("testUser", 30, false);

        // Assert
        verify(et).begin();
        verify(mockedUser).setMoney(70.0);
        verify(db).merge(mockedUser);
        verify(et).commit();

        assertTrue(result);
    }

    @Test
    public void testWithdrawMoreThanBalance() {
        // Arrange
        User mockedUser = mock(User.class);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(mockedUser);
        when(mockedUser.getMoney()).thenReturn(20.0);

        // Act
        boolean result = sut.gauzatuEragiketa("testUser", 50, false);

        // Assert
        verify(et).begin();
        verify(mockedUser).setMoney(0.0);
        verify(db).merge(mockedUser);
        verify(et).commit();

        assertTrue(result);
    }

    @Test
    public void testUserNotFound() {
        // Arrange
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        // Act
        boolean result = sut.gauzatuEragiketa("nonExistentUser", 50, true);

        // Assert
        verify(et).begin();
        verify(et).commit();
        verify(db, never()).merge(any(User.class));

        assertFalse(result);
    }
}