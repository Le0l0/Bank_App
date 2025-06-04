package bank;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;

import java.io.File;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest
{
	private String testUsername = "testUser";
	private String testPassword = "123";
	private User user = new User(testUsername, testPassword);
	
	
	
	@Test
	@Order(01)
	public void test_createFiles() {
		// datoteke su se vec stvorile kada smo stvorili user objekt
		File file = new File(testUsername + ".txt");
		File file_history = new File(testUsername + "_history.txt");
		assertTrue(file.exists() && file_history.exists());
	}
	
	@Test
	@Order(100)
	public void test_deleteFiles() {
		User user = new User(testUsername, testPassword);
		File file = new File(user.username + ".txt");
		File file_history = new File(user.username + "_history.txt");
		user.deleteFiles();
		assertFalse(file.exists() && file_history.exists());
	}
	
	@Test
	@Order(02)
	public void test_getEPassword() {
		String EPassword = user.getEPassword();
		String EPasswordStatic = User.getEPassword(testUsername);
		assertTrue(EPassword.equals(Encryption.encryptSHA(testPassword)));
		assertTrue(EPasswordStatic.equals(Encryption.encryptSHA(testPassword)));
	}
	
	@Test
	@Order(03)
	public void test_makePayment() {
		// TODO: nastavi
	}
	
//	@Test
//	@Order(11)
//	public void test_registrationFail() {
//		// postavljanje unosa
//		String input = "r\naaa\naa\na\ne\n";
//		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        // testiranje
//        User user = App.welcomeSignIn();
//        assertNull(user);
//	}
																// ???
//	@Test
//	@Order(12)
//	public void test_registrationSuccess() {
//		// postavljanje unosa
//		String input = "r\nbbb\nbb\nb\nd\nbbb\nbbb\nbbb\n\n";
//		System.out.println(input);
//		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        // testiranje
//        User user = App.welcomeSignIn();
//        user.deleteFiles();
//        assertNotNull(user);
//	}

	@Test
	@Order(11)
	public void test_registration() {
		new User("aaa").deleteFiles();
		// postavljanje unosa
		String input = "r\naaa\naa\na\ne\nr\naaa\naa\na\nd\naaa\naaa\naaa\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        // testiranje
        User userFail = App.welcomeSignIn();
        User userSuccess = App.welcomeSignIn();
        
        //userSuccess.deleteFiles();	// -> makni kada se napise test_login()
        
        assertNull(userFail);
        assertNotNull(userSuccess);
	}
	
//	@Test
//	@Order(2)
//	public void test_login() {
//		// postavljanje unosa
//		String input = "l\nnepostojeci_korisnik\na\ne\nl\nbbb\nbbb\n";
//		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        // testiranje
//        User userFail = App.welcomeSignIn();
//        User userSuccess = App.welcomeSignIn();
//        userSuccess.deleteFiles();
//        assertNull(userFail);
//        assertNotNull(userSuccess);
//	}

}
