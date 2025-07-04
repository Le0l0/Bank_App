//package com.bank_server.online_banka;
//
////za JUnit testiranje
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.TestMethodOrder;
////ostalo
//import java.util.ArrayList;
//import java.time.LocalDate;
//
//
//
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class TransactionTest
//{
//	private static String usernameT = "testuser";
//	private static String passwordT = "testpassword";
//	private static char encryptionT = 's';
//	
//	
//	
//	@BeforeAll
//	// test se ne moze odraditi nez ove pripreme za testiranje, ali ona ovisi o metodama klase 'User', 
//	// tako da ako ne rade testovi za tu klasu, nece ni za ovu
//	public static void TEST_initialize() {
//		// stvori datoteke
//		new User(usernameT, passwordT, encryptionT);
//		// uplati neki iznos na racun
//		User.makePayment(usernameT, 100.0);
//		// napravi par transakcija
//		User.makeTransaction(usernameT, "r1", 20.0);
//		User.makeTransaction(usernameT, "r2", 10.0);
//		User.makeTransaction(usernameT, "r3", 30.0);
//	}
//	
//	@AfterAll
//	public static void TEST_clean_up() {
//		// obrisi datoteke testnog korisnika
//		User.deleteFiles(usernameT);
//	}
//	
//	
//	
//	@Test
//	@Order(01)
//	// ne provjerava se je li dobro zapisan IBAN platitelja zato sto to ovisi o metodi 'getAccount()' iz klase 'BankAccount'
//	public void test_getTransactionList() {
//		ArrayList<TransactionM> list = Transaction.getTransactionList(usernameT);
//		
//		assertAll(
//				// prva transakcija
//				() -> assertTrue(list.get(0).recipient().equals("r1")),
//				() -> assertTrue(list.get(0).amount() == 20.0),
//				() -> assertTrue(list.get(0).date().equals(LocalDate.now())),
//				// druga transakcija
//				() -> assertTrue(list.get(1).recipient().equals("r2")),
//				() -> assertTrue(list.get(1).amount() == 10.0),
//				() -> assertTrue(list.get(1).date().equals(LocalDate.now())),
//				// treca transakcija
//				() -> assertTrue(list.get(2).recipient().equals("r3")),
//				() -> assertTrue(list.get(2).amount() == 30.0),
//				() -> assertTrue(list.get(2).date().equals(LocalDate.now()))
//		);
//	}
//
//}
