package online_banka_client.bank_client;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// mockito
import static org.mockito.Mockito.*;
// testiranje 'stdout-a'
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
// ostalo
import java.util.Scanner;
import java.time.LocalDate;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest
{
//	private static ArrayList<Transaction> tranList = null;
	// za mijenjanje izlaza, odnosno gdje se ispisuje
	private static final PrintStream stdout = System.out;
	

	
	@BeforeEach
	public void TEST_set_up_each() {
		App.rest = mock(RestTemplate.class);
		System.setOut(stdout);
	}
		
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
	public void test_registration() {
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("aaa\naaa\nbbbb\ne\naaa\naaa\naaa\ns\n");
        
        // mock
		when(App.rest.getForObject(App.serverAddr + "/users/aaa/exists", boolean.class))
		.thenReturn(false)	// korisnicko ime nije zauzeto
		.thenReturn(false);	// korisnicko ime nije zauzeto
		when(App.rest.postForObject(App.serverAddr + "/users/registration", new UserM("aaa", "aaa", 's'), boolean.class))
		.thenReturn(true);	// uspjesna registracija od strane servera
        
        // testiranje
        User userFail = User.registration();
        User userSuccess = User.registration();
        
        assertAll(
        		() -> assertNull(userFail),
        		() -> assertNotNull(userSuccess),
        		() -> assertTrue(userSuccess.username.equals("aaa"))
		);
	}
	
	

	@Test
	@Order(02)
	public void test_login() {
		// postavljanje unosa
        App.scanner.close();
        App.scanner = new Scanner("nepostojeci_korisnik\na\ne\naaa\naaa\n");
        
        // mock
		when(App.rest.postForObject(App.serverAddr + "/users/login", new UserM("nepostojeci_korisnik", "a", '-'), boolean.class))
		.thenReturn(false);	// neuspjesna prijava
		when(App.rest.postForObject(App.serverAddr + "/users/login", new UserM("aaa", "aaa", '-'), boolean.class))
		.thenReturn(true);	// uspjesna prijava
        
        // testiranje
        User userFail = User.login();
        User userSuccess = User.login();
        
        assertAll(
        		() -> assertNull(userFail),
        		() -> assertNotNull(userSuccess)
		);
	}
	
	
	
	@Test
	@Order(03)
	public void test_makePayment() {
		// preusmjeri standardni izlaz u objekt 'out'
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // stvori korisnika preko kojeg cemo pozivati metodu koju testiramo
        User user = new User("test");
        
        // mock
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-payment", (double) 0, Integer.class))
        .thenReturn(0);	// uplate/isplata uspjesna
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-payment", (double) 1, Integer.class))
        .thenReturn(1);	// nedovoljno novaca
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-payment", (double) 2, Integer.class))
        .thenReturn(2);	// nemoguce izvrsiti
        
        // testiranje
        user.makePayment(0);
        user.makePayment(1);
        user.makePayment(2);
        
        // spremi linije iz 'out' u polje string-ova
        String[] outputLines = out.toString().split("\n\n");
        
        assertAll(
        		() -> assertTrue(outputLines[0].contains("uspjesna")),
        		() -> assertTrue(outputLines[1].contains("novca")),
        		() -> assertTrue(outputLines[2].contains("moguce"))
        		);
	}
	
	
	
	@Test
	@Order(04)
	public void test_makeTransaction() {
		// preusmjeri standardni izlaz u objekt 'out'
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        // stvori korisnika preko kojeg cemo pozivati metodu koju testiramo
        User user = new User("test");
        
        // mock
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-transaction", new TransactionReq("a", (double) 0), Integer.class))
        .thenReturn(0);	// transakcija uspjesna
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-transaction", new TransactionReq("a", (double) 1), Integer.class))
        .thenReturn(1);	// nedovoljno novca
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-transaction", new TransactionReq("a", (double) 2), Integer.class))
        .thenReturn(2);	// nije moguce dohvatiti korisnikov racun
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-transaction", new TransactionReq("a", (double) 3), Integer.class))
        .thenReturn(3);	// neuspjesno azuriranje racuna primatelja
        when(App.rest.postForObject(App.serverAddr + "/users/test/make-transaction", new TransactionReq("a", (double) 4), Integer.class))
        .thenReturn(4);	// greska
        
        
        // testiranje
        user.makeTransaction("a", 0);
        user.makeTransaction("a", 1);
        user.makeTransaction("a", 2);
        user.makeTransaction("a", 3);
        user.makeTransaction("a", 4);
        
        // spremi linije iz 'out' u polje string-ova
        String[] outputLines = out.toString().split("\n\n");
        
        assertAll(
        		() -> assertTrue(outputLines[0].contains("uspjesna")),
        		() -> assertTrue(outputLines[1].contains("novca")),
        		() -> assertTrue(outputLines[2].contains("moguce")),
        		() -> assertTrue(outputLines[3].contains("primatelj")),
        		() -> assertTrue(outputLines[4].contains("gresk"))
        		);
	}
	
	
	
	@Test
	@Order(05)
	public void test_fetchTransactionList() {
        // stvori korisnika preko kojeg cemo pozivati metodu koju testiramo
		User user = new User("test");
		// popis transakcija
		ArrayList<TransactionM> list = new ArrayList<TransactionM>();
		list.add(new TransactionM("a", "b", 1, LocalDate.now()));
		list.add(new TransactionM("b", "a", 2, LocalDate.now()));
		list.add(new TransactionM("a", "c", 3, LocalDate.now()));
		
		// mock
		when(App.rest.exchange(
				App.serverAddr + "/users/test/history",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<ArrayList<TransactionM>>(){}
		))
		.thenReturn(new ResponseEntity<ArrayList<TransactionM>>(list, HttpStatus.OK));
		
		// testiranje
		try {
			user.fetchTransactionList();
		} catch (RestClientException e) {
			return;
		}
		
		// spremi popis transakcija za druge testove
//		tranList = user.transactionList;
		
		assertAll(
				// prva transakcija
        		() -> assertTrue(user.transactionList.get(0).payer.equals("a")),
        		() -> assertTrue(user.transactionList.get(0).recipient.equals("b")),
        		() -> assertTrue(user.transactionList.get(0).amount == 1),
        		// druga transakcija?
        		() -> assertTrue(user.transactionList.get(2).payer.equals("a")),
        		() -> assertTrue(user.transactionList.get(2).recipient.equals("c")),
        		() -> assertTrue(user.transactionList.get(2).amount == 3),
        		// treca transakcija?
        		() -> assertTrue(user.transactionList.get(1).payer.equals("b")),
        		() -> assertTrue(user.transactionList.get(1).recipient.equals("a")),
        		() -> assertTrue(user.transactionList.get(1).amount == 2)
        		);
	}
	
	
	
	@Test
	@Order(06)
	public void test_sortHistory() {
		// TODO: kako testirati?
		assertTrue(true);
	}
	
	
	
	@Test
	@Order(07)
	public void test_deleteUser() {	// TODO: glupi test?
		// stvori korisnika preko kojeg cemo pozivati metodu koju testiramo
		User user = new User("test");
		
		// mock
		when(App.rest.postForObject(App.serverAddr + "/users/test/check-password", "krivi_password", boolean.class))
		.thenReturn(false);	// krivi password
		when(App.rest.postForObject(App.serverAddr + "/users/test/check-password", "tocan_password", boolean.class))
		.thenReturn(true);	// ispravan password
		// nemoj slati serveru zahtjev za brisanje korisnika 'test'
		doNothing().when(App.rest).delete(App.serverAddr + "/users/test");
		
		// testiranje
		int result1 = user.deleteUser("krivi_password");
		int result0 = user.deleteUser("tocan_password");
		
		assertAll(
        		() -> assertTrue(result1 == 1),
        		() -> assertTrue(result0 == 0)
        		);
	}

}
