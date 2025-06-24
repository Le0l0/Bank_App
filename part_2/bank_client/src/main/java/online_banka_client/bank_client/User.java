package online_banka_client.bank_client;

import java.util.ArrayList;
////////////////////////////////////////////////////////////////
import org.springframework.web.client.RestClientException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;




class User
{
	String username;
	ArrayList<Transaction> transactionList = null;

	

	// prazan konstruktor
	public User() {
		this.username = null;
		this.transactionList = null;
	}
	
	// normalan konstruktor - koristen kada se vecpostojeci korisnik ulogira u aplikaciju
	public User(String username) {
		this.username = username;
		this.transactionList = new ArrayList<Transaction>();
	}
	
	
	
	// registracija korisnika
	static User registration() {
		String username = null;
		String password = null;
		char choice = '0';
		boolean response = false;
		
		while (true) {
			// unos korisnickog imena
			username = App.getInput("Username: ", 32, true);
			
			// provjeri postoji li vec korisnik s unesenim imenom
			try {
				response = App.rest.getForObject(App.serverAddr + "/users/" + username + "/exists", boolean.class);
			} catch (RestClientException e) {
				System.out.println("Greska: " + e.getMessage());
				continue;
			}
			if (response == true) {
				System.out.println("Ovo korisnicko ime je zauzeto, uneste neko drugo. ");
				continue;
			}
			
			// unos lozinke
			password = App.getInput("Password: ", 32, false);
			
			// potvrdi password i provjeri jesu li isti:
			// registracija uspjesna
			if (password.equals(App.getInput("Confirm Password: ", 32, false)) == true) {
				break;
			}
			// registracija neuspjesna
			else {
				choice = App.getChar("Registracija neuspjesna, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ");
				if (choice == 'e') {													// izlaz
					return null;
				}
			}
		}
		
		choice = App.getCharStrict("Odaberite koji tip enkripcije cete koristiti za lozinku. a - AES, s - SHA", "as");
		
		// posalji serveru da registrira korisnika
		try {
			App.rest.postForObject(App.serverAddr + "/users/registration", new UserM(username, password, choice), boolean.class);
		} catch (RestClientException e) {
			System.out.println("Nemoguce stvoriti korisnika: " + e.getMessage());
			return null;
		}
		
		return new User(username);
	}
	
	
	
	// prijava korisnika
	static User login() {
		String username = null;
		String passwordAttempt = null;
		boolean response = false;
		
		while (true) {
			// unos korisnickog imena i lozinke
			username = App.getInput("Username: ", 32, true);
			passwordAttempt = App.getInput("Password: ", 32, false);
			
			// pokusamo se prijaviti
			try {
				response = App.rest.postForObject(App.serverAddr + "/users/login", new UserM(username, passwordAttempt, '-'), boolean.class);
				// prijava uspjesna
				if (response == true) break;
				// ako prijava nije uspjesna pokusaj ponovo ili izadi iz aplikacije
				else {
					char choice = App.getChar("Korisnicko ime ili lozinka su pogresni, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ");
					// izlaz
					if (choice == 'e')
						return null;
				}
			} catch (RestClientException e) {
				System.out.println("Nemoguce prijaviti se: " + e.getMessage());
				return null;
			}
		}
		
		return new User(username);
	}
	
	
	
	// dohvati povijest transakcija sa servera i ucitaj u objekt 'transactionList'
	void fetchTransactionList() throws RestClientException {
		ResponseEntity<ArrayList<TransactionM>> response = App.rest.exchange(
				App.serverAddr + "/users/" + this.username + "/history",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<ArrayList<TransactionM>>(){}
		);
		this.transactionList = Transaction.convertM(response.getBody());
	}

}
