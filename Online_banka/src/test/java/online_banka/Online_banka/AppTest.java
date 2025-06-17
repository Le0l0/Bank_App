package online_banka.Online_banka;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Scanner;




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
	
	@Test
	@Order(01)
	public void test_getInput() {
		App.scanner.close();
		App.scanner = new Scanner("ab cd\n\nabcd\nabc\na b\n");
		
		String inputNoSpace = App.getInput("test", 3, true);
		String inputSpace = App.getInput("test", 3, false);
		
		assertAll(
			"getInput",
			() -> assertTrue(inputNoSpace.equals("abc")),
			() -> assertTrue(inputSpace.equals("a b"))
		);
	}
	
	@Test
	@Order(02)
	public void test_getChar() {
		App.scanner.close();
		App.scanner = new Scanner("ab\n\nB\n");
		
		char input = App.getChar("test");
		
		assertEquals('b', input);
	}
	
	@Test
	@Order(02)
	public void test_getCharStrict() {
		App.scanner.close();
		App.scanner = new Scanner("bC\n\nb\nA\n");
		
		char input = App.getCharStrict("test", "a");
		
		assertEquals('a', input);
	}
	
	@Test
	@Order(100)
	public void test_App() {
		//App.main(null);
		assertTrue(true);
	}

}
