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
import java.util.Scanner;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankAccountTest
{
	private static BankAccount bankAcc = null;
	public static String testUsername = "testUser";
	public static String testPassword = "123";
	private static User user = null;
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// nista, usera treba kreirati tek nakon sto ne ucita 'lastIBAN', za sto imamo test
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// izbrisi korisnika
		user.deleteFiles();
	}
	
	@Test
	@Order(01)
	public void test_getNewIBAN() throws IOException, NumberFormatException {
		String newIBAN = BankAccount.getNewIBAN();
		
		assertAll(
    		"getNewIBAN",
    		() -> assertNotEquals(null, newIBAN),
    		() -> assertFalse(newIBAN.equals("HR" + String.format("%032d", 0)))
    	);
	}
	
	@Test
	@Order(02)
	public void test_getAccount() {
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("100.\n");
        
        // testiranje
		user = new User(testUsername, testPassword);
		user.makePayment();
		
        bankAcc = BankAccount.getAccount(user.username);

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
		double prevBalance = bankAcc.balance;
		String prevValue = bankAcc.value;

		bankAcc.IBAN = BankAccount.getNewIBAN();
		// treba azururati 2 puta posebno za testirati dvije metode jer je druga staticka i ne vidi promjenu IBAN-a(linija iznad)
		bankAcc.updateAccount(user);
		BankAccount.updateAccount(user.username, 10);
		bankAcc = BankAccount.getAccount(user.username);
		
		
		String newIBAN = bankAcc.IBAN;
		double newBalance = bankAcc.balance;
		String newValue = bankAcc.value;
		
		bankAcc.IBAN = prevIBAN;
		bankAcc.balance = prevBalance;
		bankAcc.updateAccount(user);
		
		assertAll(
	    	"updateAccount",
	    	() -> assertFalse(prevIBAN.equals(newIBAN)),
	    	() -> assertEquals(prevBalance, newBalance - 10),
	    	() -> assertTrue(prevValue.equals(newValue))
	    );
	}
	
	@Test
	@Order(04)
	public void test_updateUserIBANList() throws IOException {
		User user1 = new User("testUser1", "321");
		
		// metoda 'deleteFiles' poziva 'BankAccount.updateuserIBANList(username)'
		user1.deleteFiles();
		
		// provjeri nalazi li se jos uvijek korisnicko ime u datoteci
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

}
