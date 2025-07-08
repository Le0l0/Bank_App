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
	ArrayList<Transaction> transactionList;

	

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
				if (response == true) {
					System.out.println("Ovo korisnicko ime je zauzeto, uneste neko drugo. ");
					continue;
				}
			} catch (RestClientException e) {
				System.out.println("Greska: " + e.getMessage());
				return null;
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
			response = App.rest.postForObject(App.serverAddr + "/users/registration", new UserM(username, password, choice), boolean.class);
			if (response == false) throw new RestClientException("Servrer nije uspio kreirati korisnika! ");
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
	
	
	
	void makePayment(double payment) {
		try {
			int response = (int) App.rest.postForObject(App.serverAddr + "/users/" + this.username + "/make-payment", payment, Integer.class);
			switch (response) {
			case 0:  System.out.println("Uplata/isplata uspjesna. \n"); break;
			case 1:  System.out.println("Isplata neuspjesna - nedovoljno novca na racunu. \n"); break;
			default: System.out.println("Nije moguce izvrsiti uplatu/isplatu! \n");
			}
		} catch (RestClientException e) {
			System.out.println("Nemoguce izvrsiti uplatu/isplatu: " + e.getMessage());
		}
	}
	
	
	
	void makeTransaction(String recipient, double amount) {
		try {
			int response = (int) App.rest.postForObject(App.serverAddr + "/users/" + this.username + "/make-transaction", new TransactionReq(recipient, amount), Integer.class);
			switch (response) {
			case 0:  System.out.printf("Transakcija uspjesna. Uplaceno %.2f EUR na racun %s. \n\n", amount, recipient); break;
			case 1:  System.out.println("Nedovoljno novca na racunu. Transakcija neuspjesna. \n"); break;
			case 2:  System.out.println("Nije moguce dohvatiti korisnikov racun! \n"); break;
			case 3:  System.out.println("Azuriranje racuna primatelja neuspjesno! \n"); break;
			default: System.out.println("Doslo je do greske! \n");
			}
		} catch (RestClientException e) {
			System.out.println("Nemoguce izvrsiti transakciju: " + e.getMessage());
		}
	}
	
	
	
	// dohvati povijest transakcija sa servera i ucitaj u objekt 'transactionList'
	void fetchTransactionList() throws RestClientException {
		ResponseEntity<ArrayList<TransactionM>> response = App.rest.exchange(
				App.serverAddr + "/users/" + this.username + "/history",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<ArrayList<TransactionM>>(){}
		);
		this.transactionList = Transaction.convertMessage(response.getBody());
	}
	
	
	
	void sortHistory(char[] sort) {
		// sortiranje po datumu - od najstarijeg prema najnovijem
		if (sort[0] == 'd')
			this.transactionList.sort(new TransactionDateComparator());
		// sortiranje po iznosu - od najmanjeg prema najvecem
		else if (sort[0] == 'i')
			this.transactionList.sort(new TransactionAmountComparator());
		
		// okreni poredak ako je korisnik tako odabrao
		if (sort[1] == 'o')
			for (int i = this.transactionList.size() - 2; i >= 0; i--) {
				this.transactionList.add(this.transactionList.remove(i));
			}
	}
	
	
	
	int deleteUser(String passAtt) throws RestClientException {
		// provjeri je li unesena lozinka tocna
		boolean response = App.rest.postForObject(App.serverAddr + "/users/" + this.username + "/check-password", passAtt, boolean.class);
		if (response == false) return 1;
		
		// ako je tocna lozinka, obrisi sve podatke o korisnikovom racunu
		App.rest.delete(App.serverAddr + "/users/" + this.username);
		return 0;
	}

}
