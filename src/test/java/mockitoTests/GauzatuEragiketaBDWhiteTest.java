package mockitoTests;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.User;
import testOperations.TestDataAccess;

public class GauzatuEragiketaBDWhiteTest {
    private DataAccess sut;
    private TestDataAccess testDA;

    @Before
    public void init() {
        testDA = new TestDataAccess();
        testDA.open();
        testDA.removeAllUsers();
        sut = new DataAccess(testDA.getEntityManager());
    }

    @After
    public void close() {
        testDA.close();
    }
    @Test
    public void testDepositSuccess() {
        // Arrange
        String username = "testUser1";
        testDA.createUser(username, "password", "mota");
        testDA.updateUserMoney(username, 100.0);
        // Act
        boolean result = sut.gauzatuEragiketa(username, 50, true);

        // Assert
        assertTrue(result);
        User updatedUser = testDA.findUser(username);
        assertEquals(150.0, updatedUser.getMoney(), 0.01);
    }

    @Test
    public void testWithdrawSuccess() {
        // Arrange
        String username = "testUser2";
        testDA.createUser(username, "password", "mota");
        testDA.updateUserMoney(username, 100.0);
        // Act
        boolean result = sut.gauzatuEragiketa(username, 30, false);

        // Assert
        assertTrue(result);
        User updatedUser = testDA.findUser(username);
        assertEquals(70.0, updatedUser.getMoney(), 0.01);
    }

    @Test
    public void testWithdrawInsufficientFunds() {
        // Arrange
        String username = "testUser3";
        testDA.createUser(username, "password", "mota");
        testDA.updateUserMoney(username, 10.0);
        // Act
        boolean result = sut.gauzatuEragiketa(username, 30, false);

        // Assert
        assertTrue(result);
        User updatedUser = testDA.findUser(username);
        assertEquals(0.0, updatedUser.getMoney(), 0.01);//
    }

    @Test
    public void testUserNotFound() {
        try {
        	boolean result = sut.gauzatuEragiketa("nonExistentUser", 50, true);

        	// Assert
        	assertFalse(result);
        } catch (javax.persistence.NoResultException e) {
            assertFalse(false);
        }
    }
}