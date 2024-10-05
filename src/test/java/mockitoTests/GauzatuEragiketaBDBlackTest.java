package mockitoTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dataAccess.DataAccess;
import domain.Driver;
import testOperations.TestDataAccess;

public class GauzatuEragiketaBDBlackTest {

    // sut: system under test
    static DataAccess sut = new DataAccess();
    static TestDataAccess testDA=new TestDataAccess();
    @Before
    public void setUp() {
       
        sut.open();
        sut.initializeDB(); // Inicializar los datos de prueba
        sut.close();
    }

    @After
    public void tearDown() {
        // Limpiamos la base de datos después de cada test
        sut.open();
        testDA.removeAll(); // Si tienes un método para eliminar los datos, llámalo aquí
        sut.close();
    }

    @Test
    public void testExceptionHandling() {
        // Configurar: intentar hacer un depósito pero forzar una excepción en la base de datos.
        
        sut.open();
        Driver driver = sut.getDriver("Urtzi"); // Obtén el conductor de la base de datos

        // Aquí puedes configurar el estado del conductor para que provoque un error,
        // por ejemplo, eliminando el conductor de la base de datos para forzar la excepción.
        testDA.removeDriver("Urtzi"); // Esto provoca que se lance una excepción en la operación
        sut.close();
        
        // Ejecutar el test
        boolean result = sut.gauzatuEragiketa("Urtzi", 50, true);

        // Verificaciones: Se espera que se maneje la excepción y la transacción se deshaga (rollback).
        assertFalse(result); // La operación debe fallar
    }

    @Test
    public void testDepositSuccess() {
        // Configuración inicial: driver con dinero inicial
        sut.open();
        Driver driver = sut.getDriver("Urtzi");
        assertTrue(driver.getMoney() == 15); // Verificar dinero inicial
        sut.close();

        // Ejecutar: hacer un depósito exitoso
        boolean result = sut.gauzatuEragiketa("Urtzi", 50, true);

        // Verificación: el depósito debe ser exitoso
        assertTrue(result);

        // Verificar que el dinero del usuario ha aumentado en 50
        sut.open();
        driver = sut.getDriver("Urtzi"); // Obtener el estado actualizado del conductor
        assertTrue(driver.getMoney() == 65); // 15 + 50 = 65
        sut.close();
    }

    @Test
    public void testWithdrawSuccess() {
        // Configuración inicial: driver con dinero inicial
        sut.open();
        Driver driver = sut.getDriver("Urtzi");
        assertTrue(driver.getMoney() == 15); // Verificar dinero inicial
        sut.close();

        // Ejecutar: hacer un retiro exitoso
        boolean result = sut.gauzatuEragiketa("Urtzi", 10, false);

        // Verificación: el retiro debe ser exitoso
        assertTrue(result);

        // Verificar que el dinero del usuario ha disminuido en 10
        sut.open();
        driver = sut.getDriver("Urtzi"); // Obtener el estado actualizado del conductor
        assertTrue(driver.getMoney() == 5); // 15 - 10 = 5
        sut.close();
    }

    @Test
    public void testWithdrawInsufficientFunds() {
        // Configuración inicial: driver con dinero inicial
        sut.open();
        Driver driver = sut.getDriver("Urtzi");
        assertTrue(driver.getMoney() == 15); // Verificar dinero inicial
        sut.close();

        // Ejecutar: intentar retirar más dinero del que tiene
        boolean result = sut.gauzatuEragiketa("Urtzi", 50, false);

        // Verificación: el retiro debe fallar y el saldo debe quedar en 0
        assertTrue(result);

        // Verificar que el dinero del usuario ha quedado en 0
        sut.open();
        driver = sut.getDriver("Urtzi");
        assertTrue(driver.getMoney() == 0); // Retiró todo lo posible y quedó en 0
        sut.close();
    }

    @Test
    public void testUserNotFound() {
        // Ejecutar: intentar realizar una operación con un usuario que no existe
        boolean result = sut.gauzatuEragiketa("nonExistentUser", 50, true);

        // Verificación: la operación debe fallar ya que el usuario no existe
        assertFalse(result);
    }
}