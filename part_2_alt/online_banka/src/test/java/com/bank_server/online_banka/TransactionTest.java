package com.bank_server.online_banka;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
//ostalo
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionTest
{
	private static String username1 = "testuser1";
	private static String password1 = "testpassword1";
	private static String username2 = "testuser2";
	private static String password2 = "testpassword2";
	private static char encryption = 's';
	private static String iban1 = null;
	private static String iban2 = null;
	
	
	
	@BeforeAll
	// test se ne moze odraditi nez ove pripreme za testiranje, ali ona ovisi o metodama klase 'User', 
	// tako da ako ne rade testovi za tu klasu, nece ni za ovu
	public static void TEST_initialize() {
		// stvori korisnike
		User.registration(username1, password1, encryption);
		User.registration(username2, password2, encryption);
		// uplati neki iznos na racune
		User.makePayment(username1, 100.0);
		User.makePayment(username2, 100.0);
		// napravi par transakcija
		User.makeTransaction(username1, "recipient", 20.0);
		User.makeTransaction(username1, username2, 10.0);
		User.makeTransaction(username2, username1, 30.0);
		// ucitaj IBAN-ove
		try {
			iban1 = BankAccount.ownerToIban(username1);
			iban2 = BankAccount.ownerToIban(username2);
		} catch (Exception e) {}
	}
	
	@AfterAll
	public static void TEST_clean_up() {
		// obrisi korisnike koristene za testiranje
		User.deleteUser(username1);
		User.deleteUser(username2);
		// obrisi transakcije
		Session session = BankDB.getSessionFactory().openSession();
		List<TransactionDB> transList1 = session.createQuery("FROM TransactionDB WHERE payer = :payer", TransactionDB.class).setParameter("payer", iban1).list();
		List<TransactionDB> transList2 = session.createQuery("FROM TransactionDB WHERE recipient = :recipient", TransactionDB.class).setParameter("recipient", iban1).list();
		transList2.forEach(TransactionDB -> transList1.add(TransactionDB));
		session.beginTransaction();
		transList1.forEach(TransactionDB -> session.remove(TransactionDB));
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	@Test
	@Order(01)
	public void test_getTransactionList() {
		// dohvati listu transakcija
		ArrayList<TransactionM> list = Transaction.getTransactionList(username1);
		
		assertAll(
				// prva transakcija
				() -> assertEquals(iban1, list.get(0).payer()),
				() -> assertEquals("recipient", list.get(0).recipient()),
				() -> assertEquals(20, list.get(0).amount()),
				() -> assertEquals(LocalDate.now(), list.get(0).date()),
				// druga transakcija
				() -> assertEquals(iban1, list.get(1).payer()),
				() -> assertEquals(iban2, list.get(1).recipient()),
				() -> assertEquals(10, list.get(1).amount()),
				() -> assertEquals(LocalDate.now(), list.get(1).date()),
				// treca transakcija
				() -> assertEquals(iban2, list.get(2).payer()),
				() -> assertEquals(iban1, list.get(2).recipient()),
				() -> assertEquals(30, list.get(2).amount()),
				() -> assertEquals(LocalDate.now(), list.get(2).date())
		);
	}

}
