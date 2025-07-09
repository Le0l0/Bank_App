package com.bank_server.online_banka;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
//ostalo
// nista




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OnlineBankAppServerTest
{
	private static OnlineBankServer server = null;
	private static String usernameT = "testuser";
	private static String passwordT = "testpassword";
	private static char encryptionT = 's';
	
	
	
	@BeforeAll
	public static void TEST_initialize() {
		server = new OnlineBankServer();
		// user se stvara u testu za registraciju
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// obrisi transakcije
		Session session = BankDB.getSessionFactory().openSession();
		List<TransactionDB> transList = session.createQuery("FROM TransactionDB WHERE recipient = :recipient", TransactionDB.class).setParameter("recipient", "HR123").list();
		session.beginTransaction();
		transList.forEach(TransactionDB -> session.remove(TransactionDB));
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	@Test
	@Order(01)
	public void test_serverState() {
		// testira je li server pokrenut
		assertEquals("hello", server.serverState());
	}
	
	
	
	@Test
	@Order(02)
	public void test_registerUser() {
		UserM tmpUserM = new UserM(usernameT, passwordT, encryptionT);
		assertAll(
				() -> assertTrue(server.registerUser(tmpUserM)),
				// druga registracija treba vratiti 'false' jer korisnik vec postoji nakon prve
				() -> assertFalse(server.registerUser(tmpUserM))
				);
	}
	
	
	
	@Test
	@Order(03)
	public void test_checkUserExists() {
		assertAll(
				() -> assertTrue(server.checkUserExists(usernameT)),
				() -> assertFalse(server.checkUserExists("nepostojeci_korisnik"))
				);
	}
	
	
	
	@Test
	@Order(04)
	public void test_loginUser() {
		UserM tmpUserM = new UserM(usernameT, passwordT, encryptionT);
		assertAll(
				() -> assertTrue(server.loginUser(tmpUserM)),
				() -> assertFalse(server.loginUser(new UserM("nepostojeci_korisnik", "abc123", 's')))
				);
	}
	
	
	
	@Test
	@Order(05)
	public void test_checkPassword() {
		assertAll(
				() -> assertTrue(server.checkPassword(usernameT, passwordT)),
				() -> assertFalse(server.checkPassword(usernameT, "krivi_password")),
				() -> assertFalse(server.checkPassword("nepostojeci_korisnik", passwordT))
				);
	}
	
	
	
	@Test
	@Order(06)
	public void test_payment() {
		int response0a = server.payment(usernameT, 101.0);				// uspjesna uplata
		int response0b = server.payment(usernameT, -1.0);				// uspjesna isplata
		int response1 = server.payment(usernameT, -1000.0);				// nedovoljno novaca za isplatiti
		int response2 = server.payment("nepostojeci_korisnik", 1.0);	// uplata na korisnicko ime koje nije registrirano u banci
		
		assertAll(
				() -> assertEquals(0, response0a),
				() -> assertEquals(0, response0b),
				() -> assertEquals(1, response1),
				() -> assertEquals(2, response2)
				);
	}
	
	
	
	@Test
	@Order(07)
	public void test_transaction() {
		int response0 = server.transaction(usernameT, new TransactionReq("HR123", 10.0));	// uspjesna transakcija
		int response1 = server.transaction(usernameT, new TransactionReq("HR123", 1000.0));	// neodvoljno novaca na racunu
		// druge errore ne mozemo lako testirati
		
		assertAll(
				() -> assertEquals(0, response0),
				() -> assertEquals(1, response1)
				);
	}
	
	
	
	@Test
	@Order( 8)	// iz nekog razloga baca error ako upisem 08 ili 09
	public void test_getAccDetails() {
		BankAccM response0 = server.getAccDetails(usernameT);
		BankAccM response1 = server.getAccDetails("nepostojeci_korinsik");
		
		assertAll(
				() -> assertEquals(90.0, response0.balance()),
				() -> assertNull(response1)
				);
	}
	
	
	
	@Test
	@Order( 9)	// iz nekog razloga baca error ako upisem 08 ili 09
	public void test_getAccHistory() {
		assertEquals(1, server.getAccHistory(usernameT).getBody().size());
	}
	
	
	
	@Test
	@Order(10)
	public void test_deleteUser() {
		server.deleteUser(usernameT);
		assertFalse(User.userExists(usernameT));
	}
	
	
	
	@Test
	@Order(11)
	public void test_shutServer() {
		// TODO: kako testirati?
	}

}
