package online_banka_client.bank_client;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// mockito
import static org.mockito.Mockito.*;
// ostalo
import java.util.Scanner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest
{
	// varijable i objekti
	
	

	@BeforeEach
	public void TEST_set_up_each() {
		App.rest = mock(RestTemplate.class);
	}
	
	@BeforeAll
	public static void TEST_initialize() {
		// nista
	}
	
	@AfterAll
	public static void TEST_clean_up() {
		// nista
	}
	
	
	
	@Test
	@Order(01)
	public void test_getInput() {
		// postavljanje unosa
		App.scanner.close();
		App.scanner = new Scanner("ab cd\n\nabcd\nabc\na b\n");
		
		// testiranje
		String inputNoSpace = App.getInput("test", 3, true);
		String inputSpace = App.getInput("test", 3, false);
		
		assertAll(
				() -> assertEquals("abc", inputNoSpace),
				() -> assertEquals("a b", inputSpace)
		);
	}
	
	
	
	@Test
	@Order(02)
	public void test_getChar() {
		// postavljanje unosa
		App.scanner.close();
		App.scanner = new Scanner("ab\n\nB\n");
		
		// testiranje
		char input = App.getChar("test");
		
		assertEquals('b', input);
	}
	
	
	
	@Test
	@Order(03)
	public void test_getCharStrict() {
		// postavljanje unosa
		App.scanner.close();
		App.scanner = new Scanner("bC\n\nb\nA\n");
		
		// testiranje
		char input = App.getCharStrict("test", "a");
		
		assertEquals('a', input);
	}
	
	
	
	@Test
	@Order(04)
	public void test_server() {
		// mock
		when(App.rest.getForObject(App.serverAddr + "/", String.class))
		.thenReturn("hello");	// server je pokrenut
		
		// testiranje
		String response = null;
		try {
			response = App.rest.getForObject(App.serverAddr + "/", String.class);
		} catch (RestClientException e) {
			fail("Mock ne radi! ");
		}
		
		assertEquals("hello", response);
	}
	
}
