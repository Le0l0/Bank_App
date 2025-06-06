package bank;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppTest
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
	
	// TODO: treba testirati metode za unos getInput, getCharStrict i getChar
	
	@Test
	@Order(100)
	public void test_App() {
		App.main(null);
		assertTrue(true);
	}

}
