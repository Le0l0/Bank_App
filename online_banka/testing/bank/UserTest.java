package bank;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

import java.util.Scanner;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest
{
	private static String testUsername = "testUser";
	private static String testPassword = "123";
	private static User user = null;

	
	
	@BeforeAll
	public static void TEST_initialize() {
		user = new User(testUsername, testPassword);
	}
	
	@AfterAll
	public static void TEST_clean_up() {
		user.deleteFiles();
	}
	
	@Test
	@Order(01)
	public void test_createFiles() {
		// datoteke su se vec stvorile kada smo stvorili user objekt
		File file = new File(testUsername + ".txt");
		File file_history = new File(testUsername + "_history.txt");
		assertTrue(file.exists() && file_history.exists());
	}
	
	@Test
	@Order(100)
	public void test_deleteFiles() {
		User user = new User(testUsername, testPassword);
		File file = new File(user.username + ".txt");
		File file_history = new File(user.username + "_history.txt");
		user.deleteFiles();
		assertFalse(file.exists() && file_history.exists());
	}
	
	@Test
	@Order(02)
	public void test_getEPassword() {
		String EPassword = user.getEPassword();
		String EPasswordStatic = User.getEPassword(testUsername);
		
		assertAll(
			"getEPassword",
			() -> assertTrue(EPassword.equals(Encryption.encryptSHA(testPassword))),
			() -> assertTrue(EPasswordStatic.equals(Encryption.encryptSHA(testPassword)))
		);
	}
	
	@Test
	@Order(03)
	public void test_makePayment() {
		user.makePayment(101.);
		double balance0 = BankAccount.getAccount(user).balance;
		
		user.makePayment(-1000.);
		double balance1 = BankAccount.getAccount(user).balance;		
		
		user.makePayment(-1.);
		double balance2 = BankAccount.getAccount(user).balance;
		
		assertAll(
			"makePayment",
			() -> assertEquals(101., balance0),
			() -> assertEquals(101., balance1),
			() -> assertEquals(100., balance2)
		);
	}
	
	@Test
	@Order(04)
	public void test_makeTransaction() {
		// postavljanje unosa
//        App.scanner.close();
//        App.scanner = new Scanner("H R123\nHR123\n1 2 3\n-123\n9999\nHR123\n10\n");
        // TODO: dovrsi
	}
	
	@Test
	@Order(05)
	// ovisi o test_makeTransaction
	public void test_loadTransactionList() {
		user.loadTransactionList();
		assertTrue(user.transactionList.size() > 0);
	}

	@Test
	@Order(11)
	public void test_registration() {
		new User("aaa").deleteFiles();
		
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("aaa\naa\na\ne\naaa\naa\na\nd\naaa\naaa\naaa\n");
        
        // testiranje
        User userFail = User.registration();
        User userSuccess = User.registration();
        
        assertAll(
			"registration",
			() -> assertNull(userFail),
			() -> assertNotNull(userSuccess)
		);
        
	}
	
	@Test
	@Order(12)
	public void test_login() {
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("nepostojeci_korisnik\na\ne\naaa\naaa\n");
        
        // testiranje
        User userFail = User.login();
        User userSuccess = User.login();
        userSuccess.deleteFiles();
        assertAll(
    			"login",
    			() -> assertNull(userFail),
    			() -> assertNotNull(userSuccess)
    		);
	}

}
