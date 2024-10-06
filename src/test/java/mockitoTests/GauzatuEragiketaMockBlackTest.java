package mockitoTests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
import domain.User;
import testOperations.TestDataAccess;

public class GauzatuEragiketaMockBlackTest {
    
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
    protected TypedQuery<User> query;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        persistenceMock = Mockito.mockStatic(Persistence.class);
        persistenceMock.when(() -> Persistence.createEntityManagerFactory(Mockito.any()))
            .thenReturn(entityManagerFactory);
        
        Mockito.doReturn(db).when(entityManagerFactory).createEntityManager();
        Mockito.doReturn(et).when(db).getTransaction();
        Mockito.doReturn(query).when(db).createQuery(anyString(), eq(User.class));
        sut = new DataAccess(db);
        testDataAccess = new TestDataAccess();
        testDataAccess.setEntityManager(db); // Asegurarse de que TestDataAccess use el EntityManager mockeado
    }

    @After
    public void tearDown() {
        persistenceMock.close();
    }

    @Test
    // sut.gauzatuEragiketa: Deposit money successfully.
    public void testDepositSuccess() {
        String username = "testUser";
        double amount = 50;
        boolean deposit = true;

        User mockedUser = new User(username, "password", "mota");
        Mockito.when(db.find(User.class, username)).thenReturn(mockedUser);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(mockedUser);
        testDataAccess.updateUserMoney(username, 100.0);

        boolean result = false;
        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, deposit);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }

        // Assert
        assertTrue(result);
        assertEquals(150.0, mockedUser.getMoney(), 0.01);
    }

    @Test
    // sut.gauzatuEragiketa: Withdraw money successfully with sufficient balance.
    public void testWithdrawSuccess() {
        // Arrange
        String username = "testUser";
        double amount = 30;
        boolean deposit = false;

        User mockedUser = new User(username, "password", "mota");
        Mockito.when(db.find(User.class, username)).thenReturn(mockedUser);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(mockedUser);
        testDataAccess.updateUserMoney(username, 100.0);

        // Act
        boolean result = false;
        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, deposit);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }

        // Assert
        assertTrue(result);
        assertEquals(70.0, mockedUser.getMoney(), 0.01);
    }

    @Test
    // sut.gauzatuEragiketa: Withdraw money with insufficient balance.
    public void testWithdrawMoreThanBalance() {
        // Arrange
        String username = "testUser";
        double amount = 50;
        boolean deposit = false;

        User mockedUser = new User(username, "password", "mota");
        Mockito.when(db.find(User.class, username)).thenReturn(mockedUser);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(mockedUser);
        testDataAccess.updateUserMoney(username, 20.0);

        // Act
        boolean result = false;
        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, deposit);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }

        // Assert
        assertTrue(result); 
        assertEquals(0.0, mockedUser.getMoney(), 0.01);
    }

    @Test
    // sut.gauzatuEragiketa: The user does not exist in the DB. The test must return false.
    public void testUserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        double amount = 50;
        boolean deposit = true;

        Mockito.when(db.find(User.class, username)).thenReturn(null);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenThrow(new RuntimeException());

        // Act
        boolean result = false;
        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, deposit);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        } finally {
            sut.close();
        }

        // Assert
        assertFalse(result);
    }

    @Test
    // sut.gauzatuEragiketa: Exception during transaction.
    public void testTransactionException() {
        // Arrange
        String username = "testUser";
        double amount = 50;
        boolean deposit = true;

        User mockedUser = new User(username, "password", "mota");
        Mockito.when(db.find(User.class, username)).thenReturn(mockedUser);
        Mockito.when(query.setParameter(anyString(), any())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(mockedUser);
        testDataAccess.updateUserMoney(username, 100.0);
        doThrow(new RuntimeException()).when(db).merge(mockedUser);

        // Act
        boolean result = false;
        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, deposit);
        } catch (Exception e) {
            // Handle exception
        } finally {
            sut.close();
        }

        // Assert
        assertFalse(result);
    }

    @Test
    // sut.gauzatuEragiketa: Exception during query.
    public void testExceptionDuringQuery() {
        // Arrange
        String username = "testUser";
        double amount = 50;
        boolean deposit = true;

        Mockito.when(db.find(User.class, username)).thenThrow(new RuntimeException());

        // Act
        boolean result = false;
        try {
            sut.open();
            result = sut.gauzatuEragiketa(username, amount, deposit);
        } catch (Exception e) {
            // Handle exception
        } finally {
            sut.close();
        }

        // Assert
        assertFalse(result);
    }
}