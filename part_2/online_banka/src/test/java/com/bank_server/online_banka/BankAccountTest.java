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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankAccountTest
{
	private static String usernameT = "testuser";
	private static String passwordT = "testpassword";
	private static char encryptionT = 's';
	private static BankAccount bankAcc = null;
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// stvori korisnikove datoteke
		new User(usernameT, passwordT, encryptionT);
		User.makePayment(usernameT, 100.0);
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// niista, korisnikove datoteke se izbrisu u zadnjem testu
	}
	
	
	
	@Test
	@Order(00)
	// konstruktori
	public void test_BankAccount() {
		BankAccount bankAccE = new BankAccount();
		BankAccount bankAccF = new BankAccount("HR123", 1.0, "KN");
		
		assertAll(
				() -> assertNull(bankAccE.IBAN),
    			() -> assertEquals(0.0, bankAccE.balance),
				() -> assertNull(bankAccE.value),
				() -> assertTrue(bankAccF.IBAN.equals("HR123")),
    			() -> assertEquals(1.0, bankAccF.balance),
				() -> assertTrue(bankAccF.value.equals("KN"))
    	);
	}
	
	
	
	@Test
	@Order(01)
	public void test_getNewIBAN() {
		String newIBAN = BankAccount.getNewIBAN();
		
		assertAll(
				() -> assertNotNull(newIBAN),
    			() -> assertFalse(newIBAN.equals("HR" + String.format("%032d", 0)))
    	);
	}
	
	
	
	@Test
	@Order(02)
	public void test_getAccount() {
		try {
			bankAcc = BankAccount.getAccount(usernameT);
		} catch (IOException e) {
			fail("Nemoguce dohvatiti racun: " + e.getMessage());
		}

        assertAll(
        		() -> assertNotNull(bankAcc.IBAN),
        		() -> assertFalse(bankAcc.IBAN.equals("HR" + String.format("%032d", 0))),
        		() -> assertEquals(bankAcc.balance, 100.0),
        		() -> assertNotNull(bankAcc.value)
    	);
	}
	
	
	
	@Test
	@Order(03)
	public void test_updateAccount() {
		
		
		String prevIBAN = bankAcc.IBAN;
		double prevBalance = bankAcc.balance;
		String prevValue = bankAcc.value;

		bankAcc.IBAN = BankAccount.getNewIBAN();
		// treba azururati 2 puta posebno za testirati dvije metode jer je druga staticka i ne vidi promjenu IBAN-a(linija iznad)
		try {
			bankAcc.updateAccount(usernameT);
			BankAccount.updateAccountS(usernameT, 10);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			bankAcc = BankAccount.getAccount(usernameT);
		} catch (IOException e) {
			fail("Nemoguce dohvatiti racun: " + e.getMessage());
		}		
		
		String newIBAN = bankAcc.IBAN;
		double newBalance = bankAcc.balance;
		String newValue = bankAcc.value;
		
		bankAcc.IBAN = prevIBAN;
		bankAcc.balance = prevBalance;
		try {
			bankAcc.updateAccount(usernameT);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		assertAll(
	    	"updateAccount",
	    	() -> assertFalse(prevIBAN.equals(newIBAN)),
	    	() -> assertEquals(prevBalance, newBalance - 10),
	    	() -> assertTrue(prevValue.equals(newValue))
	    );
	}
	
	
	
	@Test
	@Order(04)
	public void test_updateUserIBANList() {		
		// metoda 'deleteFiles' poziva 'BankAccount.updateuserIBANList(username)'
		User.deleteFiles(usernameT);
		
		// provjeri nalazi li se jos uvijek korisnicko ime u datoteci
		boolean usernamePresent = false;
		String tmpS = null;
		try (BufferedReader reader = new BufferedReader(new FileReader("userIBANlist.txt"))) {
			tmpS = reader.readLine();
			while (tmpS != null && tmpS != "") {
				if (tmpS.equals(usernameT) == true) {
					usernamePresent = true;
					break;
				}
				tmpS = reader.readLine();
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		assertFalse(usernamePresent);
	}

}
