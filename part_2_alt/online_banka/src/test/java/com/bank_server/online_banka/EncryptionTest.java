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
class EncryptionTest
{
	private static String password1 = "lopoc123";
	private static String password2 = "copol321";

	
	
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
	public void test_AESEncryption() {
		String ePassword = Encryption.encryptAES(password1);
		String tmpPassword = Encryption.decryptAES(ePassword);
		
		assertEquals(password1, tmpPassword);
	}
	
	
	
	@Test
	@Order(02)
	public void test_SHAEncryption() { // cudan test
		String ePassword0 = Encryption.encryptSHA(password1);
		String ePassword1 = Encryption.encryptSHA(password1);
		String ePassword2 = Encryption.encryptSHA(password2);
		
		assertAll(
				() -> assertEquals(ePassword0, ePassword1),
				() -> assertNotEquals(ePassword1, ePassword2)
				);
	}
	
}
