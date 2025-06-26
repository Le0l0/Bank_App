package online_banka_client.bank_client;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// ostalo
import java.util.Scanner;
import org.springframework.web.client.RestClientException;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest
{
	// varijable i objekti
	

	
	@BeforeAll
	public static void TEST_initialize() {
		// nista
	}
	
	@AfterAll
	public static void TEST_clean_up() {
		// nista
	}
	
	

	@Test
	@Order(00)
	// konstruktori
	public void test_User() {
		User userE = new User();
		User userF = new User("covjek");
		
		assertAll(
        		() -> assertNull(userE.username),
        		() -> assertNull(userE.transactionList),
        		() -> assertTrue(userF.username.equals("covjek")),
        		() -> assertNotNull(userF.transactionList)
		);
	}
	
	
	
	@Test
	@Order(01)
	public void test_registration() {
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("aaa\naaa\n");
        
        // testiranje
        User userFail = User.registration();
        User userSuccess = User.registration();
        
        assertAll(
        		() -> assertNull(userFail),
        		() -> assertNull(userSuccess)
		);
	}
	
	

	@Test
	@Order(02)
	public void test_login() {
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("nepostojeci_korisnik\na\ne\naaa\naaa\n");
        
        // testiranje
        User userFail = User.login();
        User userSuccess = User.login();
        
        assertAll(
        		() -> assertNull(userFail),
        		() -> assertNull(userSuccess)
		);
	}
	
	
	
	@Test
	@Order(03)
	public void test_fetchTransactionList() {
		User user = new User("test");
		try {
			user.fetchTransactionList();
		} catch (RestClientException e) {
			assertTrue(true);
			return;
		}
		
		fail("Server radi! ");
	}

}
