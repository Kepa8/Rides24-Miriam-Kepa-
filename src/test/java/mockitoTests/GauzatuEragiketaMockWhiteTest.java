package mockitoTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import dataAccess.DataAccess;
import domain.User;

public class GauzatuEragiketaMockWhiteTest {
	
    // sut: system under test
    private DataAccess sut;

    @Mock
    private EntityManager db;

    @Mock
    private EntityTransaction et;

    @Mock
    private TypedQuery<User> query;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    // sut.gauzatuEragiketa: The user does not exist in the DB. The test must return false.
    public void test1() {
        MockedStatic<Persistence> persistenceMock = Mockito.mockStatic(Persistence.class);
        try {
            persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any())).thenReturn(entityManagerFactory);
            Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
            Mockito.doReturn(et).when(db).getTransaction();
            sut = new DataAccess(db);

            String username = "nonExistentUser";
            double amount = 50;
            boolean deposit = true;

            when(db.createQuery(anyString(), eq(User.class))).thenReturn(query);
            when(query.setParameter(eq("username"), anyString())).thenReturn(query);
            when(query.getSingleResult()).thenReturn(null);

            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertFalse(result);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
            persistenceMock.close();
        }
    }

    @Test
    // sut.gauzatuEragiketa: Deposit money successfully.
    public void test2() {
        MockedStatic<Persistence> persistenceMock = Mockito.mockStatic(Persistence.class);
        try {
            persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

            Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
            Mockito.doReturn(et).when(db).getTransaction();
            sut = new DataAccess(db);

            String username = "testUser";
            double amount = 50;
            boolean deposit = true;

            User mockedUser = mock(User.class);
            when(db.createQuery(anyString(), eq(User.class))).thenReturn(query);
            when(query.setParameter(eq("username"), anyString())).thenReturn(query);
            when(query.getSingleResult()).thenReturn(mockedUser);
            when(mockedUser.getMoney()).thenReturn(100.0);

            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertTrue(result);

            verify(et).begin();
            verify(mockedUser).setMoney(150.0);
            verify(db).merge(mockedUser);
            verify(et).commit();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
            persistenceMock.close();
        }
    }

    @Test
    // sut.gauzatuEragiketa: Withdraw money successfully with sufficient balance.
    public void test3() {
        MockedStatic<Persistence> persistenceMock = Mockito.mockStatic(Persistence.class);
        try {
            persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

            Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
            Mockito.doReturn(et).when(db).getTransaction();
            sut = new DataAccess(db);

            String username = "testUser";
            double amount = 30;
            boolean deposit = false;

            User mockedUser = mock(User.class);
            when(db.createQuery(anyString(), eq(User.class))).thenReturn(query);
            when(query.setParameter(eq("username"), anyString())).thenReturn(query);
            when(query.getSingleResult()).thenReturn(mockedUser);
            when(mockedUser.getMoney()).thenReturn(100.0);

            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertTrue(result);

            verify(et).begin();
            verify(mockedUser).setMoney(70.0);
            verify(db).merge(mockedUser);
            verify(et).commit();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
            persistenceMock.close();
        }
    }

    @Test
    // sut.gauzatuEragiketa: Withdraw money with insufficient balance.
    public void test4() {
        MockedStatic<Persistence> persistenceMock = Mockito.mockStatic(Persistence.class);
        try {
            persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
                .thenReturn(entityManagerFactory);

            Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
            Mockito.doReturn(et).when(db).getTransaction();
            sut = new DataAccess(db);

            String username = "testUser";
            double amount = 50;
            boolean deposit = false;

            User mockedUser = mock(User.class);
            when(db.createQuery(anyString(), eq(User.class))).thenReturn(query);
            when(query.setParameter(eq("username"), anyString())).thenReturn(query);
            when(query.getSingleResult()).thenReturn(mockedUser);
            when(mockedUser.getMoney()).thenReturn(20.0);

            sut.open();
            boolean result = sut.gauzatuEragiketa(username, amount, deposit);
            assertTrue(result);

            verify(et).begin();
            verify(mockedUser).setMoney(0.0);
            verify(db).merge(mockedUser);
            verify(et).commit();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
            persistenceMock.close();
        }
    }
}