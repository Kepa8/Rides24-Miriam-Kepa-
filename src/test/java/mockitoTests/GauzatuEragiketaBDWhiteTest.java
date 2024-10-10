package mockitoTests;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.User;
import testOperations.TestDataAccess;

public class GauzatuEragiketaBDWhiteTest {
	/*
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
    public void test1() {
        // Arrange
        String username = "testUser1";
        testDA.createUser(username, "password", "mota");
        testDA.updateUserMoney(username, 100.0);
        try {
        	boolean result = sut.gauzatuEragiketa(username, 50, true);
        	assertTrue(result);
        	User updatedUser = testDA.findUser(username);
        	assertEquals(150.0, updatedUser.getMoney(), 0.01);
        }catch(Exception e) {
        	fail();
        }
    }

    @Test
    public void test2() {
        // Arrange
        String username = "testUser2";
        testDA.createUser(username, "password", "mota");
        testDA.updateUserMoney(username, 100.0);
        try {
        	boolean result = sut.gauzatuEragiketa(username, 30, false);
        	assertTrue(result);
        	User updatedUser = testDA.findUser(username);
        	assertEquals(70.0, updatedUser.getMoney(), 0.01);
        }catch(Exception e) {
        	fail();
        }
    }

    @Test
    public void test3() {
        // Arrange
        String username = "testUser3";
        testDA.createUser(username, "password", "mota");
        testDA.updateUserMoney(username, 10.0);
        try {
        	boolean result = sut.gauzatuEragiketa(username, 30, false);
        	assertTrue(result);
        	User updatedUser = testDA.findUser(username);
        	assertEquals(0.0, updatedUser.getMoney(), 0.01);
    	}catch(Exception e) {
    		fail();
    	}
    }

    @Test
    public void test4() {
        boolean result = sut.gauzatuEragiketa("nonExistentUser", 50, true);
        assertFalse(result);
    } 
    */
}