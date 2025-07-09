package com.bank_server.online_banka;


/*
 * 
 * Kako bi ova aplikacija, zajedno sa klijentskim dijelom, funkcionirala kako treba, na uredaju na kojem 
 * se pokrece serverski dio treba napraviti sljedece:
 * 		- instalirati postgreSQL i pokrenuti ga
 * 		- stvoriti bazu podataka naziva 'bankdb', i korisnika zvanog 'bank_admin' sa lozinkom 'pass'
 * 
 */


// za sprig boot server
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
// ostalo
import java.util.ArrayList;
import java.time.LocalDateTime;


// tipovi poruka - objekti za komunikaciju (DTOs)
record UserM(String username, String password, char encryption) {}
record BankAccM(String IBAN, double balance, String value) {}
record TransactionReq(String recipient, double amount) {}
record TransactionM(String payer, String recipient, double amount, LocalDateTime dateTime) {}




@SpringBootApplication
@RestController
public class OnlineBankServer
{
	// treba za gasenje servera
	@Autowired private ApplicationContext appContext;
	// admin password - treba za usporediti ako se pokusa ugasiti server
	//	server se moze ugasiti izvrsavanjem ove komande u CLI-u:
	// 	curl -X DELETE http://localhost:8080/shutdown?passAtt=pass
	// 	ovaj nacin gasenja servera nije siguran, ali je za sada dovoljan
	private String adminPass = "pass";
	// stanje servera - nigdje se ne mijenja (jos)
	// hello znaci da je server pokrenut
	private String state = "hello";
	
	
	
	// main - pokreni server
	public static void main(String[] args) {
		SpringApplication.run(OnlineBankServer.class, args);
	}

	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// metode ispod su odgovori na pojedine HTTP requestove od klijentske aplikacije											 ///
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
////////////////////////////////////////////////////////////////
	/// upravljanje serverom
	
	// vraca stanje servera - za sada se koristi samo za provjeru je li server pokrenut
	@GetMapping("/")
	String serverState() {
		return state;
	}
	
	// gasenje servera
	// nije dovoljno sigurno za upotrebu u pravoj aplikaciji, kao i neke druge stvari u ovom projektu
	@DeleteMapping("/shutdown")
	void shutServer(@RequestParam(value = "passAtt", defaultValue = "-") String passAtt) {
		if (passAtt.equals(adminPass)) {
			BankDB.shutdown();
			SpringApplication.exit(appContext, () -> 0);
		    System.exit(0);
		}
	}
	
	
	
////////////////////////////////////////////////////////////////
	/// get mapping
	
	
	
	// vraca odgovor postoji li korisnik s unesenim imenom
	@GetMapping("/users/{username}/exists")
	boolean checkUserExists(@PathVariable String username) {
		return User.userExists(username);
	}
	
	
	
	// vraca objekt tipa BankAccM koji sadrzi informacije o korisnikovom racunu
	@GetMapping("/users/{username}/account-details")
	BankAccM getAccDetails(@PathVariable String username) {
		try {
			return BankAccount.getAccountByOwner(username).toMessage();
		} catch (Exception e) {
			System.out.println("Greska" + e.getMessage());
			return null;
		}
	}
	
	// vraca (nesortiranu) listu transakcija
	@GetMapping("/users/{username}/history")
	ResponseEntity<ArrayList<TransactionM>> getAccHistory(@PathVariable String username) {
		return new ResponseEntity<ArrayList<TransactionM>>(Transaction.getTransactionList(username), HttpStatus.OK);
	}
	
	
	
////////////////////////////////////////////////////////////////
	/// post mapping
	
	
	
	// registracija korisnika
	@PostMapping("/users/registration")
	boolean registerUser(@RequestBody UserM userM) {
		return User.registration(userM.username(), userM.password(), userM.encryption());
	}
	
	// prijava korisnika
	@PostMapping("/users/login")
	boolean loginUser(@RequestBody UserM userM) {
		return User.login(userM.username(), userM.password());
	}
	
	
	
	// provjeri je li lozinka valjana
	@PostMapping("/users/{username}/check-password")
	boolean checkPassword(@PathVariable String username, @RequestBody String passwordAttempt) {
		return Encryption.testPassword(passwordAttempt, User.getUserEPassword(username));
	}
	
	
	
	// uplata/isplata sa racuna
	@PostMapping("/users/{username}/make-payment")
	Integer payment(@PathVariable String username, @RequestBody double payment) {
		return User.makePayment(username, payment);
	}
	
	// obavljanje transakcije
	@PostMapping("/users/{username}/make-transaction")
	Integer transaction(@PathVariable String username, @RequestBody TransactionReq transactionReq) {
		return User.makeTransaction(username, transactionReq.recipient(), transactionReq.amount());
	}
	
	
	
////////////////////////////////////////////////////////////////
	/// delete mapping
	
	
	
	// brisanje korisnikovih datoteka
	@DeleteMapping("/users/{username}")
	void deleteUser(@PathVariable String username) {
		User.deleteUser(username);
	}

}
