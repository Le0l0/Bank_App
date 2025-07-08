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
import java.time.LocalDate;
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
	
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// stvori korisnika
		user = new UserDB(username, password);
		// stvori racun
		acc  = new BankAccDB(username, iban, balance, "EUR");
		// stvori transakcije
		trans1 = new TransactionDB(iban, "recipient1", 10, LocalDate.now());
		trans2 = new TransactionDB(iban, "recipient2", 10, LocalDate.now());
		
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
		BankDB.saveUser(user);
		
		Session session = BankDB.getSessionFactory().openSession();
		UserDB saved = session.createQuery("FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username).uniqueResult();
		session.close();
		
		assertAll(
				() -> assertTrue(user.getUsername().equals(saved.getUsername())),
				() -> assertTrue(user.getEPassword().equals(saved.getEPassword()))
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
				() -> assertTrue(user.getUsername().equals(found.getUsername())),
				() -> assertTrue(user.getEPassword().equals(found.getEPassword()))
				);
	}
	
	
	
	@Test
	@Order(04)
	public void test_saveAcc() {
		BankDB.saveAcc(acc);
		
		Session session = BankDB.getSessionFactory().openSession();
		BankAccDB saved = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username).uniqueResult();
		session.close();
		
		assertAll(
				() -> assertTrue(acc.getOwner().equals(saved.getOwner())),
				() -> assertTrue(acc.getIban().equals(saved.getIban())),
				() -> assertTrue(acc.getBalance() == saved.getBalance()),
				() -> assertTrue(acc.getValue().equals(saved.getValue()))
				);
	}
	
	
	
	@Test
	@Order(05)
	public void test_findAccByOwner() {
		BankAccDB found = BankDB.findAccByOwner(username);
		
		assertAll(
				() -> assertTrue(acc.getOwner().equals(found.getOwner())),
				() -> assertTrue(acc.getIban().equals(found.getIban())),
				() -> assertTrue(acc.getBalance() == found.getBalance()),
				() -> assertTrue(acc.getValue().equals(found.getValue()))
				);
	}
	
	
	
	@Test
	@Order(06)
	public void test_updateAcc() {
		BankAccDB old_acc = new BankAccDB(acc.getOwner(), acc.getIban(), acc.getBalance(), acc.getValue());
		
		acc.setBalance(200);
		BankDB.updateAcc(acc);
		
		Session session = BankDB.getSessionFactory().openSession();
		acc = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username).uniqueResult();
		session.close();
		
		assertAll(
				() -> assertTrue(acc.getOwner().equals(old_acc.getOwner())),
				() -> assertTrue(acc.getIban().equals(old_acc.getIban())),
				() -> assertTrue(acc.getBalance() == 200),
				() -> assertTrue(old_acc.getBalance() == 100),
				() -> assertTrue(acc.getValue().equals(old_acc.getValue()))
				);
	}
	
	
	
	@Test
	@Order(07)
	public void test_saveTransaction() {
		BankDB.saveTransaction(trans1);
		
		Session session = BankDB.getSessionFactory().openSession();
		TransactionDB saved = session.createQuery("FROM TransactionDB WHERE recipient = :recipient", TransactionDB.class).setParameter("recipient", "recipient1").uniqueResult();
		session.close();
		
		assertAll(
				() -> assertTrue(trans1.getPayer().equals(saved.getPayer())),
				() -> assertTrue(trans1.getRecipient().equals(saved.getRecipient())),
				() -> assertTrue(trans1.getAmount() == saved.getAmount()),
				() -> assertTrue(trans1.getDate().equals(saved.getDate()))
				);
	}
	
	
	
	@Test
	@Order( 8)
	public void test_findTransByPayer() {
		List<TransactionDB> foundList = BankDB.findTransByPayer(iban);
		
		TransactionDB saved1 = foundList.get(1);
		TransactionDB saved2 = foundList.get(0);
		
//		System.out.println("\n\n\n");
//		System.out.println(trans1.getPayer() + " " + trans1.getRecipient() + " " + trans1.getAmount() + " " + trans1.getDate());
//		System.out.println(saved1.getPayer() + " " + saved1.getRecipient() + " " + saved1.getAmount() + " " + saved1.getDate());
//		System.out.println("\n\n\n");
		
		assertAll(
				// transakcija 1
				() -> assertTrue(trans1.getPayer().equals(saved1.getPayer())),
				() -> assertTrue(trans1.getRecipient().equals(saved1.getRecipient())),
				() -> assertTrue(trans1.getAmount() == saved1.getAmount()),
				() -> assertTrue(trans1.getDate().toString().equals(saved1.getDate().toString())),
				// trasakcija 2
				() -> assertTrue(trans2.getPayer().equals(saved2.getPayer())),
				() -> assertTrue(trans2.getRecipient().equals(saved2.getRecipient())),
				() -> assertTrue(trans2.getAmount() == saved2.getAmount()),
				() -> assertTrue(trans2.getDate().toString().equals(saved2.getDate().toString()))
				);
	}
	
	
	
	@Test
	@Order( 9)
	public void test_findTransByRecipient() {
		List<TransactionDB> foundList = BankDB.findTransByRecipient("recipient2");
		
		TransactionDB saved = foundList.get(0);
		
		assertAll(
				() -> assertTrue(trans2.getPayer().equals(saved.getPayer())),
				() -> assertTrue(trans2.getRecipient().equals(saved.getRecipient())),
				() -> assertTrue(trans2.getAmount() == saved.getAmount()),
				() -> assertTrue(trans2.getDate().equals(saved.getDate()))
				);
	}
	
	
	
	@Test
	@Order(10)
	public void test_deleteUser() {
		BankDB.deleteUserByUsername(username);
		
		Session session = BankDB.getSessionFactory().openSession();
		UserDB result = session.createQuery("FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username).uniqueResult();
		session.close();
		
		assertNull(result);
	}
	
	
	
	@Test
	@Order(11)
	public void test_deleteAcc() {
		BankDB.deleteAcc(BankDB.findAccByOwner(username));
		
		Session session = BankDB.getSessionFactory().openSession();
		BankAccDB result = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", username).uniqueResult();
		session.close();
		
		assertNull(result);
	}

}
