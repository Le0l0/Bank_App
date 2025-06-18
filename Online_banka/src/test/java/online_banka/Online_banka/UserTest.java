package online_banka.Online_banka;

// za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// ostalo
import java.io.File;
import java.util.Scanner;
import java.io.IOException;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest
{
	private static String testUsername = "testUser";
	private static String testPassword = "123";
	private static User user = null;
	
	private static String testRecipientName = "testRecipient";
	private static String testRecipientPass = "234";
	private static User userRecipient = null;
	

	
	
	@BeforeAll
	public static void TEST_initialize() throws IOException {
		// stvori korisnike
		user = new User(testUsername, testPassword);
		userRecipient = new User(testRecipientName, testRecipientPass);
	}
	
	@AfterAll
	public static void TEST_clean_up() throws IOException {
		// izbrisi korisnika (user je izbrisan u testu za 'deleteFiles')
		userRecipient.deleteFiles();
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
		user.deleteFiles();
		File file = new File(user.username + ".txt");
		File file_history = new File(user.username + "_history.txt");
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
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("101.\n-1000.\n-1.\n");
		
        // testiranje
		user.makePayment();
		double balance0 = BankAccount.getAccount(user.username).balance;
		user.makePayment();
		double balance1 = BankAccount.getAccount(user.username).balance;		
		user.makePayment();
		double balance2 = BankAccount.getAccount(user.username).balance;
		
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
        App.scanner.close();
        App.scanner = new Scanner("H R123\ntestRecipient\n1 2 3\n-123\n10\n");
        
        // testiranje
        user.makeTransaction();
        
        assertAll(
    		"makeTransaction",
    		() -> assertEquals(90., BankAccount.getAccount(user.username).balance),
    		() -> assertEquals(10., BankAccount.getAccount(userRecipient.username).balance)
    	);
	}
	
	@Test
	@Order(05)
	// ovisi o test_makeTransaction
	public void test_loadTransactionList() {
		user.loadTransactionList();
		userRecipient.loadTransactionList();
		
		assertAll(
			"loadTransactionList",
			() -> assertFalse(user.transactionList.isEmpty()),
			() -> assertTrue(userRecipient.transactionList.size() == 1)
		);
	}

	@Test
	@Order(11)
	public void test_registration() {
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("aaa\naa\na\ne\naaa\naa\na\nd\naaa\naaa\naaa\ns\n");
        
        // testiranje
        User userFail = User.registration();
        User userSuccess = User.registration();
        userSuccess.deleteFiles();
        
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
        App.scanner = new Scanner("nepostojeci_korisnik\na\ne\ntestUser\n123\n");
        
        // testiranje
        User userFail = User.login();
        User userSuccess = User.login();
        
        assertAll(
			"login",
			() -> assertNull(userFail),
			() -> assertNotNull(userSuccess)
		);
	}

}
