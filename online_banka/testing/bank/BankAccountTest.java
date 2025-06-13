package bank;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankAccountTest
{
	private static BankAccount bankAcc = null;
	public static String testUsername = "testUser";
	public static String testPassword = "123";
	private static User user = null;
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// nista, usera treba kreirati tek nakon sto ne ucita "lastNumber", za sto imamo test
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		user.deleteFiles();
	}
	
	@Test
	@Order(01)
	public void test_initializeLastNumber() {
		try {
			BankAccount.initializeLastNumber();
		} catch (IOException | NumberFormatException e) {
			System.out.println(e);
		}
		
		assertNotEquals(-1, BankAccount.lastNumber);
	}
	
	@Test
	@Order(02)
	public void test_getAccount() {
		user = new User(testUsername, testPassword);
		user.makePayment(100.);
		
        bankAcc = BankAccount.getAccount(user);

        assertAll(
    		"getAccount",
    		() -> assertNotEquals(-1, bankAcc.accNumber),
    		() -> assertNotNull(bankAcc.value)
    	);
	}
	
	@Test
	@Order(03)
	public void test_updateAccount() {
		int prevAccNum = bankAcc.accNumber;
		String prevAccValue = bankAcc.value;
		bankAcc.accNumber = ++BankAccount.lastNumber;
		
		try {
			bankAcc.updateAccount(user);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		bankAcc = BankAccount.getAccount(user);
		assertAll(
	    	"updateAccount",
	    	() -> assertNotEquals(prevAccNum, bankAcc.accNumber),
	    	() -> assertEquals(prevAccValue, bankAcc.value)
	    );
		
		bankAcc.accNumber = prevAccNum;
		try {
			bankAcc.updateAccount(user);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	@Order(100)
	public void test_writelastNumber() {
		int lastNumBefore = -1;
		int lastNumAfter = -1;
		
		// procitaj lastNumber iz datoteke prije nego sta zapisemo novi
		try (BufferedReader reader = new BufferedReader(new FileReader("lastNumber.txt"))) {
			lastNumBefore = Integer.parseInt(reader.readLine());
		} catch (IOException e) {
			System.out.println(e);
		}
		
		// zapisi novi lastNumber u datoteku
		try {
			BankAccount.writeLastNumber();
		} catch (IOException e) {
			System.out.println(e);
		}
		
		// procitaj lastNumber iz datoteke nakon sta smo zapisali novi
		try (BufferedReader reader = new BufferedReader(new FileReader("lastNumber.txt"))) {
			lastNumAfter = Integer.parseInt(reader.readLine());
		} catch (IOException e) {
			System.out.println(e);
		}
		
		assertTrue(lastNumBefore < lastNumAfter && lastNumBefore != -1 && lastNumAfter != -1);
	}

}
