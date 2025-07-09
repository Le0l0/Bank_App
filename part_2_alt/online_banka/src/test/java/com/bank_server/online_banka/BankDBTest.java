package com.bank_server.online_banka;

// za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// ostalo
import java.time.LocalDateTime;
import org.hibernate.Session;
import java.util.List;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankDBTest
{
	private static String username = "testuser";
	private static String password = "testpassword";
	private static String iban = "HR123";
	private static double balance = 100;
	private static UserDB user = null;
	private static BankAccDB acc = null;
	private static TransactionDB trans1 = null;
	private static TransactionDB trans2 = null;
	
	// zapisano vrijeme (localDateTime) se ne provjerava je li dobro pohranjeno u bazu podataka zato sto 
	// je LocalDateTime "precizniji" od tipa podatka za vrijeme od 'postgreSQL'-a pa se "zaokruzuje"
	
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// stvori korisnika
		user = new UserDB(username, password);
		// stvori racun
		acc  = new BankAccDB(username, iban, balance, "EUR");
		// stvori transakcije
		trans1 = new TransactionDB(iban, "recipient1", 10, LocalDateTime.now());
		trans2 = new TransactionDB(iban, "recipient2", 10, LocalDateTime.now());
		// spremi 'trans2' u bazu podataka ('trans1' se sprema u testu)
		BankDB.saveTransaction(trans2);
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// obrisi transakcije
		Session session = BankDB.getSessionFactory().openSession();
		List<TransactionDB> transList = session.createQuery("FROM TransactionDB WHERE payer = :payer", TransactionDB.class).setParameter("payer", iban).list();
		session.beginTransaction();
		transList.forEach(TransactionDB -> session.remove(TransactionDB));
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	@Test
	@Order(01)
	public void test_saveUser() {
		// spremi korisnika u bazu podataka
		BankDB.saveUser(user);
		
		// dohvati spremljenog korisnika iz baze podataka
		Session session = BankDB.getSessionFactory().openSession();
		UserDB saved = session.createQuery("FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username).uniqueResult();
		session.close();
		
		assertAll(
				() -> assertEquals(user.getUsername(), saved.getUsername()),
				() -> assertEquals(user.getEPassword(), saved.getEPassword())
				);
	}
	
	
	
	@Test
	@Order(02)
	public void test_existsUserByUsername() {
		assertTrue(BankDB.existsUserByUsername(username));
	}
	
	
	
	@Test
	@Order(03)
	public void test_findUserByUsername() {
		UserDB found = BankDB.findUserByUsername(username);
		
		assertAll(
				() -> assertEquals(user.getUsername(), found.getUsername()),
				() -> assertEquals(user.getEPassword(), found.getEPassword())
				);
	}
	
	
	
	@Test
	@Order(04)
	public void test_saveAcc() {
		// spremi racun u bazu podataka
		BankDB.saveAcc(acc);
		
		// dohvati spremljeni racun iz baze podataka
		Session session = BankDB.getSessionFactory().openSession();
		BankAccDB saved = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username).uniqueResult();
		session.close();
		
		assertAll(
				() -> assertEquals(acc.getOwner(), saved.getOwner()),
				() -> assertEquals(acc.getIban(), saved.getIban()),
				() -> assertEquals(acc.getBalance(), saved.getBalance()),
				() -> assertEquals(acc.getValue(), saved.getValue())
				);
	}
	
	
	
	@Test
	@Order(05)
	public void test_findAccByOwner() {
		BankAccDB found = BankDB.findAccByOwner(username);
		
		assertAll(
				() -> assertEquals(acc.getOwner(), found.getOwner()),
				() -> assertEquals(acc.getIban(), found.getIban()),
				() -> assertEquals(acc.getBalance(), found.getBalance()),
				() -> assertEquals(acc.getValue(), found.getValue())
				);
	}
	
	
	
	@Test
	@Order(06)
	public void test_updateAcc() {
		// spremi racun prije azuriranja
		BankAccDB old_acc = new BankAccDB(acc.getOwner(), acc.getIban(), acc.getBalance(), acc.getValue());
		
		// postavi novo stanje i azuriraj
		acc.setBalance(200);
		BankDB.updateAcc(acc);
		
		// dohvati spremljeni racun
		Session session = BankDB.getSessionFactory().openSession();
		acc = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username).uniqueResult();
		session.close();
		
		assertAll(
				() -> assertEquals(old_acc.getOwner(), acc.getOwner()),
				() -> assertEquals(old_acc.getIban(), acc.getIban()),
				() -> assertEquals(200, acc.getBalance()),
				() -> assertEquals(100, old_acc.getBalance()),
				() -> assertEquals(old_acc.getValue(), acc.getValue())
				);
	}
	
	
	
	@Test
	@Order(07)
	public void test_saveTransaction() {
		// spremi transakciju u bazu podataka
		BankDB.saveTransaction(trans1);
		
		// dohvati spremljenu transakciju iz baze podataka
		Session session = BankDB.getSessionFactory().openSession();
		TransactionDB saved = session.createQuery("FROM TransactionDB WHERE recipient = :recipient", TransactionDB.class).setParameter("recipient", "recipient1").uniqueResult();
		session.close();
		
		assertAll(
				() -> assertEquals(trans1.getPayer(), saved.getPayer()),
				() -> assertEquals(trans1.getRecipient(), saved.getRecipient()),
				() -> assertEquals(trans1.getAmount(), saved.getAmount())
//				() -> assertEquals(trans1.getDateTime(), saved.getDateTime())
				);
	}
	
	
	
	@Test
	@Order( 8)
	public void test_findTransByPayer() {
		// dohvati listu transakcija iz baze podataka
		List<TransactionDB> foundList = BankDB.findTransByPayer(iban);
		
		TransactionDB saved1 = foundList.get(1);
		TransactionDB saved2 = foundList.get(0);
		assertAll(
				// transakcija 1
				() -> assertEquals(trans1.getPayer(), saved1.getPayer()),
				() -> assertEquals(trans1.getRecipient(), saved1.getRecipient()),
				() -> assertEquals(trans1.getAmount(), saved1.getAmount()),
//				() -> assertEquals(trans1.getDateTime(), saved1.getDateTime()),
				// trasakcija 2
				() -> assertEquals(trans2.getPayer(), saved2.getPayer()),
				() -> assertEquals(trans2.getRecipient(), saved2.getRecipient()),
				() -> assertEquals(trans2.getAmount(), saved2.getAmount())
//				() -> assertEquals(trans2.getDateTime(), saved2.getDateTime())
				);
	}
	
	
	
	@Test
	@Order( 9)
	public void test_findTransByRecipient() {
		// dohvati listu transakcija iz baze podataka
		List<TransactionDB> foundList = BankDB.findTransByRecipient("recipient2");
		
		TransactionDB saved = foundList.get(0);
		assertAll(
				() -> assertEquals(trans2.getPayer(), saved.getPayer()),
				() -> assertEquals(trans2.getRecipient(), saved.getRecipient()),
				() -> assertEquals(trans2.getAmount(), saved.getAmount())
//				() -> assertEquals(trans2.getDateTime(), saved.getDateTime())
				);
	}
	
	
	
	@Test
	@Order(10)
	public void test_deleteUser() {
		// obrisi korisnika iz baze podataka
		BankDB.deleteUserByUsername(username);
		
		// probaj dohvatiti korisnika iz baze podataka
		Session session = BankDB.getSessionFactory().openSession();
		UserDB result = session.createQuery("FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username).uniqueResult();
		session.close();
		
		assertNull(result);
	}
	
	
	
	@Test
	@Order(11)
	public void test_deleteAcc() {
		// obrisi racun iz baze podataka
		BankDB.deleteAcc(BankDB.findAccByOwner(username));
		
		// probaj dohvatiti racun iz baze podataka
		Session session = BankDB.getSessionFactory().openSession();
		BankAccDB result = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username).uniqueResult();
		session.close();
		
		assertNull(result);
	}

}
