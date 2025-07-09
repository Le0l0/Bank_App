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
// nista




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankAccountTest
{
	private static String owner = "testuser";
	private static String iban = "HR123";
	private static double balance = 100;
	private static String value = "HRK";
	private static BankAccDB accDB = null;
	private static BankAccount acc = null;
	
	
	@BeforeAll
	public static void TEST_initialize() {
		// stvori objekt tipa 'BankAccDB' i onda iz njega stvori objekt tipa 'BankAccount'
		accDB = new BankAccDB(owner, iban, balance, value);
		BankDB.saveAcc(accDB);
		// odmah spremi racun u bazu podataka
		acc = new BankAccount(accDB);
	}
	
	@AfterAll
	public static void TEST_cleaning_up() {
		// nista
	}
	
	
	
	@Test
	@Order(01)
	public void test_getNewIBAN() {
		// spremi novi IBAN
		String newIBAN = BankAccount.getNewIBAN();
		
		assertAll(
				() -> assertNotNull(newIBAN),
    			() -> assertNotEquals(("HR" + String.format("%032d", 0)), newIBAN)
				);
	}
	
	
	
	@Test
	@Order(02)
	public void test_getAccountByOwner() {
		// dohvati racun preko imena vlasnika
		BankAccount tmpacc = null;
		try {
			tmpacc = BankAccount.getAccountByOwner(owner);
		} catch (Exception e) {
			fail("BankAccount.getAccountByOwner ne radi");
		}
		
		final BankAccount tmpaccF = tmpacc;
		assertAll(
    			() -> assertTrue(tmpaccF.getIBAN().equals(acc.getIBAN())),
    			() -> assertTrue(tmpaccF.balance == acc.balance),
    			() -> assertTrue(tmpaccF.getValue().equals(acc.getValue()))
				);
	}
	
	
	
	@Test
	@Order(03)
	public void test_getAccountByIban() {
		// dohvati racun preko IBAN-a
		BankAccount tmpacc = null;
		try {
			tmpacc = BankAccount.getAccountByIban(iban);
		} catch (Exception e) {
			fail("BankAccount.getAccountByIban ne radi");
		}
		
		final BankAccount tmpaccF = tmpacc;
		assertAll(
    			() -> assertTrue(tmpaccF.getIBAN().equals(acc.getIBAN())),
    			() -> assertTrue(tmpaccF.balance == acc.balance),
    			() -> assertTrue(tmpaccF.getValue().equals(acc.getValue()))
				);
	}
	
	
	
	@Test
	@Order(04)
	public void test_updateAccount() {
		// dodaj 10 na stanje i azururaj racun
		acc.balance += 10;
		acc.updateAccount(owner);
		
		assertAll(
    			() -> assertTrue(acc.getIBAN().equals(iban)),
    			() -> assertTrue(acc.balance == 110),
    			() -> assertTrue(acc.getValue().equals(value))
				);
	}
	
	
	
	@Test
	@Order(05)
	public void test_ownerToIban() {
		String result1 = null;
		String result2 = null;
		
		// probaj dohvatiti IBAN-ove racuna preko imena vlasnika
		try {
			result1 = BankAccount.ownerToIban(owner);
		} catch (Exception e) {}
		try {
			result2 = BankAccount.ownerToIban("nepostojeci korisnik");
		} catch (Exception e) {}
		
		final String result1F = result1, result2F = result2;
		assertAll(
    			() -> assertEquals(iban, result1F),
    			() -> assertNull(result2F)
				);
	}
	
	
	
	@Test
	@Order(06)
	public void test_deleteAccs() {
		// obrisi racun iz baze podataka
		BankAccount.deleteAccs(owner);
		
		// pokusaj dohvatiti izbrisani racun
		Session session = BankDB.getSessionFactory().openSession();
		BankAccDB tmpdbacc = session.createQuery("FROM BankAccDB WHERE iban = :iban", BankAccDB.class).setParameter("iban", iban).uniqueResult();
		session.close();
		
    	assertNull(tmpdbacc);
	}

}
