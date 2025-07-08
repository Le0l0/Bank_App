package com.bank_server.online_banka;

// za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// ostalo
//import org.hibernate.Session;
import java.util.List;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest
{
	private static String username1 = "testuser";
	private static String password1 = "testpassword";
	private static String username2 = "testuser2";
	private static String password2 = "testpassword2";
	private static char encryption = 's';
	private static String iban2 = "HR123";
	
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// spremi samo korisnika 2 u bazu podataka - korisnik 1 se sprema u testu za registraciju
		BankDB.saveUser(new UserDB(username2, Encryption.encryptSHA(password2)));
		BankDB.saveAcc(new BankAccDB(username2, iban2, 0, "EUR"));
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// obrisi 'user2' iz baze podataka
		BankDB.deleteUserByUsername(username2);
		BankDB.deleteAcc(BankDB.findAccByIban(iban2));
		// obrisi transakcije
		Session session = BankDB.getSessionFactory().openSession();
		List<TransactionDB> transList = session.createQuery("FROM TransactionDB WHERE recipient = :recipient", TransactionDB.class).setParameter("recipient", iban2).list();
		session.beginTransaction();
		transList.forEach(TransactionDB -> session.remove(TransactionDB));
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	@Test
	@Order(01)
	public void test_registration() {
		boolean result1 = User.registration(username1, password1, encryption);
		boolean result2 = User.registration(username1, password1, encryption);
		
		Session session = BankDB.getSessionFactory().openSession();
		boolean userSaved =	session.createQuery("FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username1).uniqueResult() != null;
		boolean accSaved =	session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username1).uniqueResult() != null;
		session.close();
		
		assertAll(
				() -> assertTrue(result1),
				() -> assertFalse(result2),
				() -> assertTrue(userSaved),
				() -> assertTrue(accSaved)
				);
	}
	
	
	
	@Test
	@Order(02)
	public void test_login() {
		boolean response1 = User.login(username1, password1);
		boolean response2 = User.login(username1, "krivi password");
		boolean response3 = User.login("", password1);
		
        assertAll(
	    		() -> assertTrue(response1),
	    		() -> assertFalse(response2),
	    		() -> assertFalse(response3)
	    );
	}
	
	
	
	@Test
	@Order(03)
	public void test_userExists() {
		boolean response1 = User.userExists(username1);
		boolean response2 = User.userExists("nepostojeci korisnik");
		
		assertAll(
	    		() -> assertTrue(response1),
	    		() -> assertFalse(response2)
	    );
	}
	
	
	
	@Test
	@Order(04)
	public void test_getEPassword() {
		String EPassword = User.getUserEPassword(username1);
		assertTrue(EPassword.equals(Encryption.encryptSHA(password1)));
	}
	
	
	
	@Test
	@Order(05)
	public void test_makePayment() {
		double balance0 = -1, balance1 = -1, balance2 = -1;
		
		int response0 = (int) User.makePayment(username1, 101.0);
		try {balance0 = BankAccount.getAccountByOwner(username1).balance;} catch (Exception e) {}
		int response1 = (int) User.makePayment(username1, -1000.0);
		try {balance1 = BankAccount.getAccountByOwner(username1).balance;} catch (Exception e) {}
		int response2 = (int) User.makePayment(username1, -1.0);
		try {balance2 = BankAccount.getAccountByOwner(username1).balance;} catch (Exception e) {}
		
		int response3 = (int) User.makePayment("nepostojeci korisnik", 10.0);
		
		final double balanceF0 = balance0, balanceF1 = balance1, balanceF2 = balance2;
		assertAll(
				// provjera stanja na racunu
				() -> assertEquals(101.0, balanceF0),
				() -> assertEquals(101.0, balanceF1),
				() -> assertEquals(100.0, balanceF2),
				// provjera vraca li metoda dobar rezultat
				() -> assertEquals(0, response0),
				() -> assertEquals(1, response1),
				() -> assertEquals(0, response2),
				() -> assertEquals(2, response3)
		);
	}
	
	
	
	@Test
	@Order(06)
	public void test_makeTransaction() {
		int response0 = (int) User.makeTransaction(username1, username2, 10.0);
		int response1a = (int) User.makeTransaction(username1, username2, 0.0);
		int response1b = (int) User.makeTransaction(username1, username2, 200.0);
		int response2 = (int) User.makeTransaction("nepostojeci korisnik", username2, 1.0);
		// error kodove 3 i 4 ne ocekujemo, treba se desiti nesto cudo da bi dolo do njih
		
		Session session = BankDB.getSessionFactory().openSession();
		List<TransactionDB> list = session.createQuery("FROM TransactionDB WHERE recipient = :recipient", TransactionDB.class).setParameter("recipient", iban2).list();
		session.close();
        
        assertAll(
	    		() -> assertEquals(0, response0),
	    		() -> assertEquals(1, response1a),
	    		() -> assertEquals(1, response1b),
	    		() -> assertEquals(2, response2),
	    		() -> assertFalse(list.isEmpty())
    	);
	}
	
	
	
	@Test
	@Order(07)
	public void test_deleteUser() {
		User.deleteUser(username1);
		
		Session session = BankDB.getSessionFactory().openSession();
		UserDB userDB = session.createQuery("FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username1).uniqueResult();
		BankAccDB bankAccDB = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username1).uniqueResult();
		session.close();
		
		assertAll(
	    		() -> assertNull(userDB),
	    		() -> assertNull(bankAccDB)
    	);
	}

}
