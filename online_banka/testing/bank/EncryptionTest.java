package bank;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EncryptionTest
{
	private static String password = "lopoc123";

	
	
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
		String ePassword = Encryption.encryptAES(password);
		String tmpPassword = Encryption.decryptAES(ePassword);
		
		assertTrue(tmpPassword.equals(password));
	}
	
	@Test
	@Order(02)
	public void test_SHAEncryption() {
		// TODO: kako testirati?		
		assertTrue(true);
	}
	
}
