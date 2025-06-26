package com.bank_server.online_banka;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
//ostalo
// nista




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OnlineBankAppServerTest
{
	// varijable i objekti
	
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// nista
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// nista
	}
	
	
	
	@Test
	@Order(01)
	public void test_serverState() {
		fail("Kako testirati? ");
	}

}
