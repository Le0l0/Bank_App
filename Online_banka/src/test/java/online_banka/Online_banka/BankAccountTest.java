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
		BankAccount.updateUserIBANList(user.username);
	}
	
	@Test
	@Order(01)
	public void test_initializeLastNumber() throws IOException, NumberFormatException {
		BankAccount.initializeIBAN();
		assertNotEquals(null, BankAccount.getNewIBAN());
	}
	
	@Test
	@Order(02)
	public void test_getAccount() {
		user = new User(testUsername, testPassword);
		user.makePayment(100.);
		
        bankAcc = BankAccount.getAccount(user);

        assertAll(
    		"getAccount",
    		() -> assertFalse(bankAcc.IBAN.equals("HR" + String.format("%032d", 0))),
    		() -> assertNotNull(bankAcc.value)
    	);
	}
	
	@Test
	@Order(03)
	public void test_updateAccount() throws IOException {
		String prevIBAN = bankAcc.IBAN;
		String prevValue = bankAcc.value;
		bankAcc.IBAN = BankAccount.getNewIBAN();
		
		bankAcc.updateAccount(user);
		bankAcc = BankAccount.getAccount(user);
		
		assertAll(
	    	"updateAccount",
	    	() -> assertFalse(prevIBAN.equals(bankAcc.IBAN)),
	    	() -> assertTrue(prevValue.equals(bankAcc.value))
	    );
		
		bankAcc.IBAN = prevIBAN;
		bankAcc.updateAccount(user);
	}
	
	@Test
	@Order(04)
	public void test_updateUserIBANList() throws IOException {
		User user1 = new User("testUser1", "321");
		
		user1.deleteFiles();
		BankAccount.updateUserIBANList("testUser1");
		
		boolean usernamePresent = false;
		String tmpS = null;
		BufferedReader reader = new BufferedReader(new FileReader("userIBANlist.txt"));
		tmpS = reader.readLine();
		while (tmpS != null && tmpS != "") {
			if (tmpS.equals("testUser1") == true) {
				usernamePresent = true;
				break;
			}
			tmpS = reader.readLine();
		}
		reader.close();
		
		assertFalse(usernamePresent);
	}
	
	@Test
	@Order(100)
	public void test_writeLastIBAN() throws IOException {
		String lastIBANBefore = null;
		String lastIBANAfter = null;

		
		// procitaj lastNumber iz datoteke prije nego sta zapisemo novi
		BufferedReader reader = new BufferedReader(new FileReader("lastIBAN.txt"));
		lastIBANBefore = reader.readLine();
		reader.close();

		// zapisi novi lastNumber u datoteku
		BankAccount.writeLastIBAN();
		
		// procitaj lastNumber iz datoteke nakon sta smo zapisali novi
		reader = new BufferedReader(new FileReader("lastIBAN.txt"));
		lastIBANAfter = reader.readLine();
		reader.close();
		
		assertTrue(lastIBANBefore.equals(lastIBANAfter) == false && lastIBANBefore != null && lastIBANAfter != null);
	}

}
