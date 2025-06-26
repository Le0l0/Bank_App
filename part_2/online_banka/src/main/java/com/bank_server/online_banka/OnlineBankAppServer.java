package com.bank_server.online_banka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//------------------------------------------------------------//
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
////////////////////////////////////////////////////////////////
import java.util.ArrayList;
import java.time.LocalDate;


// tipovi poruka - objekti za komunikaciju (DTOs)
record UserM(String username, String password, char encryption) {}
record BankAccM(String IBAN, double balance, String value) {}
record TransactionReq(String recipient, double amount) {}
record TransactionM(String payer, String recipient, double amount, LocalDate date) {}




@SpringBootApplication
@RestController
public class OnlineBankAppServer
{
	// stanje servera - nigdje se ne mijenja
	private static String state = "hello";
	
	
	
	// main - pokreni server
	public static void main(String[] args) {
		SpringApplication.run(OnlineBankAppServer.class, args);
	}
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ostale metode su odgovori na pojedine HTTP requestove od klijentske aplikacije
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	// vrati stanje servera - za sada se koristi samo za provjeru je li server pokrenut
	@GetMapping("/")
	public String serverState() {
		return state;
	}
	
	
	
	// vraca odgovor postoji li korisnik s unesenim imenom
	@GetMapping("/users/{username}/exists")
	public boolean checkUserExists(@PathVariable String username) {
		return User.userExists(username);
	}
	
	
	
	// vraca objekt tipa BankAccM koji sadrzi informacije o korisnikovom racunu
	@GetMapping("/users/{username}/account-details")
	public BankAccM getAccDetails(@PathVariable String username) {
		BankAccount acc = BankAccount.getAccount(username);
		return new BankAccM(acc.IBAN, acc.balance, acc.value);
	}
	
	// vraca (nesortiranu) listu transakcija
	@GetMapping("/users/{username}/history")
	public ResponseEntity<ArrayList<TransactionM>> getAccHistory(@PathVariable String username) {
		return new ResponseEntity<ArrayList<TransactionM>>(Transaction.getTransactionList(username), HttpStatus.OK);
	}
	
	
	
	// registracija korisnika
	@PostMapping("/users/registration")
	public boolean registerUser(@RequestBody UserM userM) {
		return User.registration(userM.username(), userM.password(), userM.encryption());
	}
	
	// prijava korisnika
	@PostMapping("/users/login")
	public boolean loginUser(@RequestBody UserM userM) {
		return User.login(userM.username(), userM.password());
	}
	
	
	
	// provjeri je li lozinka valjana
	@PostMapping("/users/{username}/check-password")
	public boolean checkPassword(@PathVariable String username, @RequestBody String passwordAttempt) {
		return Encryption.testPassword(passwordAttempt, User.getEPassword(username));
	}
	
	
	
	// uplata/isplata sa racuna
	@PostMapping("/users/{username}/make-payment")
	public Integer payment(@PathVariable String username, @RequestBody double payment) {
		return User.makePayment(username, payment);
	}
	
	// obavljanje transakcije
	@PostMapping("/users/{username}/make-transaction")
	public Integer transaction(@PathVariable String username, @RequestBody TransactionReq transactionReq) {
		return User.makeTransaction(username, transactionReq.recipient(), transactionReq.amount());
	}
	
	
	
	// brisanje korisnikoovih datoteka
	@DeleteMapping("/users/{username}")
	public void deleteUser(@PathVariable String username) {
		if (User.deleteFiles(username) == false)
			System.out.println(" !!! DATOTEKE KORISNIKA " + username + " NISU USPJESNO OBRISANE !!! ");
	}	
}
