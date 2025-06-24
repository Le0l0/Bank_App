package com.bank_server.online_banka;

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




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest
{
	private static String usernameT = "testuser";
	private static String passwordT = "testpassword";
	private static String recipientNameT = "testrecipient";
	private static String recipientPassT = "testpasswordrecipient";
	private static char encryptionT = 's';
	
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// stvori korisnike
		new User(usernameT, passwordT, encryptionT);
		new User(recipientNameT, recipientPassT, encryptionT);
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// izbrisi korisnike
		User.deleteFiles(usernameT);
		User.deleteFiles(recipientNameT);
	}
	
	
	
	// konstruktori se testiraju u prvom testu - ?
	
	
	
	@Test
	@Order(01)
	public void test_userExists() {
		// datoteke su se vec stvorile u pripremi za test
		assertAll(
				() -> assertTrue(User.userExists(usernameT)),
				() -> assertTrue(User.userExists(recipientNameT)),
				() -> assertFalse(User.userExists("nepostojeciKorisnik"))
		);
	}
	
	
	
	@Test
	@Order(100)
	public void test_deleteFiles() {
		User.deleteFiles("usertest");
		
		File file = new File("usertest.txt");
		File file_history = new File("usertest_history.txt");
		
		assertAll(
				() -> assertFalse(file.exists()),
				() -> assertFalse(file_history.exists())
		);
	}
	
	
	
	@Test
	@Order(02)
	public void test_getEPassword() {
		String EPassword = User.getEPassword(usernameT);
		assertTrue(EPassword.equals(Encryption.encryptSHA(passwordT)));
	}
	
	
	
	@Test
	@Order(03)
	public void test_makePayment() {
		User.makePayment(usernameT, 101.0);
		double balance0 = BankAccount.getAccount(usernameT).balance;
		User.makePayment(usernameT, -1000.0);
		double balance1 = BankAccount.getAccount(usernameT).balance;		
		User.makePayment(usernameT, -1.0);
		double balance2 = BankAccount.getAccount(usernameT).balance;
		
		assertAll(
				() -> assertEquals(101.0, balance0),
				() -> assertEquals(101.0, balance1),
				() -> assertEquals(100.0, balance2)
		);
	}
	
	
	
	@Test
	@Order(04)
	public void test_makeTransaction() {
        // App.scanner = new Scanner("H R123\ntestRecipient\n1 2 3\n-123\n10\n");
        User.makeTransaction(usernameT, recipientNameT, 10.0);
        
        assertAll(
	    		() -> assertEquals(90.0, BankAccount.getAccount(usernameT).balance),
	    		() -> assertEquals(10.0, BankAccount.getAccount(recipientNameT).balance),
	    		() -> assertEquals(1, Transaction.getTransactionList(usernameT).size()),
	    		() -> assertEquals(1, Transaction.getTransactionList(recipientNameT).size())
    	);
	}

	
	
	@Test
	@Order(05)
	public void test_registration() {
		assertAll(
				// registracija korisnika koji vec postoji
	    		() -> assertFalse(User.registration(usernameT, passwordT, encryptionT)),
	    		// registracija (stvaranje) novog korisnika, cije datoteke cemo izbrisati u testu za metodu 'deleteFiles()'
	    		() -> assertTrue(User.registration("usertest", "-", encryptionT))
	    );
	}
	
	
	
	@Test
	@Order(06)
	public void test_login() {
        assertAll(
				// neuspjeli pokusaj prijave
	    		() -> assertFalse(User.login(usernameT, passwordT + "tekst")),
	    		// uspjesna prijava
	    		() -> assertTrue(User.login(usernameT, passwordT))
	    );
	}

}
