package online_banka_client.bank_client;

//za komunikaciju sa serverom
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
// ostalo
import java.util.Scanner;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;


// tipovi poruka - objekti za komunikaciju (DTOs)
record UserM(String username, String password, char encryption) {}
record BankAccM(String IBAN, double balance, String value) {}
record TransactionReq(String recipient, double amount) {}
record TransactionM(String payer, String recipient, double amount, LocalDateTime dateTime) {}




public class App
{
	static Scanner scanner = new Scanner(System.in);
	// za komunikaciju sa serverom
	static RestTemplate rest = new RestTemplate();
	static final String serverAddr = "http://localhost:8080";
	
	
	
	// funkcije za unos
	static String getInput(String text, int maxLength, boolean noSpace) {
		String input = null;
		
		while (true) {
			System.out.println(text);
			input = scanner.nextLine();
			if (input.length() > 0 && input.length() <= maxLength && (!noSpace || !input.contains(" "))) return input;
			else if (input.length() > 0) {
				System.out.print("Unos nesmije biti dulji od " + maxLength);
				if (noSpace) System.out.println(" i ne smije sadrzavati razmake. ");
				else System.out.println(". ");
			}
		}
	}
	
	static char getChar(String text) {
		String input = null;
		
		while (true) {
			System.out.println(text);
			input = scanner.nextLine();
			if (input.length() == 1) {
				return input.toLowerCase().charAt(0);
			}
		}
	}
	
	static char getCharStrict(String text, String allowedInputs) {
		String input = null;
		
		while (true) {
			System.out.println(text);
			input = scanner.nextLine();
			if (input.length() == 1) {
				char tmp = input.toLowerCase().charAt(0);
				for (int i = 0; i < allowedInputs.length(); i++) {
					if (tmp == allowedInputs.charAt(i)) return tmp;
				}
			}
		}
	}
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// main program aplikacije
	public static void main(String[] args)
	{
		// prije svega provjeri radi li server
		try {
			String response = rest.getForObject(serverAddr + "/", String.class);
			if (response.equals("hello") == true) {} // server vraca hello kada je pokrenut
		} catch (RestClientException e) {
			// server se ne moze dohvatiti
			System.out.println("Nemoguce dohvatiti server! ");
			cleanUp();
			return;
		}
		
		
		// korisnikov unos kod menu-a
		char choice = '-';
		// objekt u kojem su zapisani podatci o prijavljenom korisniku
		User user = null;
		
		// naslov
		System.out.println();
		System.out.println("################################################################");
		System.out.println("                       Online Bankarstvo                        ");
		System.out.println("################################################################");
		System.out.println();
		
		
		// prijava ili registracija u sustav
		choice = getCharStrict("r - registracija, p - prijava, e - izlaz", "rpe");
		if (choice == 'r') user = User.registration();
		else if (choice == 'p') user = User.login();
		
		if (user == null) {
			// korisnik je odabrao izlaz iz aplikacije
			cleanUp();
			return;
		}
		
		
		System.out.println("\nDobrodosli, " + user.username + "! \n");
		

		// glavna infinite petlja - menu aplikacije
		while (true) {
			
			// odabir opcije
			choice = getCharStrict("\ne - odjava, s - stanje racuna, u - uplati/isplati novac, p - placanje, t - popis transakcija, l - prikaz tecajne liste, b - brisanje racuna\n", "esuptlb");
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// odjava
			if (choice == 'e') {
				// pitaj korisnika je li siguran
				choice = getCharStrict("Jeste li sigurni da se zelite odjaviti od aplikacije? d/n", "dn");
				if (choice == 'd') break;
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// stanje racuna
			else if (choice == 's') {
				// dohvati racun prijavljenog korisnika i ispisi podatke
				//
				try {
					BankAccM response = rest.getForObject(serverAddr + "/users/" + user.username + "/account-details", BankAccM.class);
					if (response == null) throw new RestClientException("Server je vratio null objekt! ");
					System.out.printf("IBAN racuna: %s\nStanje na racunu: %.2f %s\n\n", response.IBAN(), response.balance(), response.value());
				} catch (RestClientException e) {
					System.out.println("Nemoguce dohvatiti podatke o racunu: " + e.getMessage());
				}
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// uplata
			else if (choice == 'u') {
				// pitaj korisnika da unese iznos koji zeli uplatiti/isplatiti, te pokusaj izvrsiti uplatu/isplatu
				//
				double payment = 0;
				// unos kolicine
				while (true) {
					String tmp = App.getInput("Unesite iznos uplate: ", 32, true);
					try {
						payment = Double.parseDouble(tmp);
						break;
					} catch (NumberFormatException e) {
						System.out.println("Neispravan unos, pokusajte ponovo. ");
					}
				}
				
				// pokusaj izvrsiti uplatu/isplatu
				user.makePayment(payment);
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// placanje
			else if (choice == 'p') {
				// obavi transakciju i zapisi je u tablicu transakcija
				//
				String recipient = null;
				double amount = 0;
				
				// unos IBAN-a
				recipient = App.getInput("IBAN ili korisnicko ime primatelja: ", 34, true);
				
				// unos iznosa placanja
				while (true) {
					String tmp = App.getInput("Iznos: ", 32, true);
					try {
						amount = Double.parseDouble(tmp);
						if (amount < 0)
							throw new NumberFormatException();
						else break;
					} catch (NumberFormatException e) {
						System.out.println("Neispravan unos, pokusajte ponovo. ");
					}
				}
				
				// ako je korisnik unesao 0 vrati ga u main program
				if (amount == 0) {
					System.out.println("Nemoguce izvrsiti transakciju iznosa 0. ");
					return;
				}
				
				// pokusaj izvrsiti transakciju
				user.makeTransaction(recipient, amount);
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// popis transakcija
			else if (choice == 't') {
				// ispisi sortiranu povijest transakcija korisnika
				//
				// dohvati povijest transakcija
				try {user.fetchTransactionList();}
				catch (RestClientException e) {
					System.out.println("Nemoguce dohvatiti povijest transakcija: " + e.getMessage());
					continue;
				}
				// ako u povijesti nema transakcija ispisi obavijest i idi dalje
				if (user.transactionList.isEmpty()) {
					System.out.println("U povijesti nema zapisanih transakcija. \n");
					continue;
				}
				
				// pitaj korisnika kako zeli sortirati
				char[] sort = new char[2];
				sort[0] = getCharStrict("Zelite li ih sortirane prema datumu ili iznosu? \nd - po datumu, i - po iznosu", "di");
				if (sort[0] == 'd') sort[1] = getCharStrict("Od najnovijih prema najstarijima ili obrnuto? \nn - od najnovijih, o - obrnuto", "no");
				if (sort[0] == 'i') sort[1] = getCharStrict("Zelite li ih sortirane od najmanjih prema najvecima ili obrnuto? \nn - od najvecih, o - obrnuto", "no");
				
				// sortiraj korisnikovu povijest transakcija
				user.sortHistory(sort);
				
				// ispisi sortiranu povijest transakcija
				user.transactionList.forEach(Transaction -> Transaction.printTransaction());
				System.out.println();
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// tecajna lista
			else if (choice == 'l') {
				// za ovo se ne koristi server zato sto nije potrebno
				// ucitaj tecajnu listu u staticki objekt klase ExchangeRate
				try {
					ExchangeRate.loadExchangeRateList();
				} catch (URISyntaxException e) {
					System.out.println("NEVALJANI LINK");
				} catch (IOException e) {
					System.out.println("Trenutno nije moguce dohvatiti tecajnu listu. ");
				} 
				// ispisi tecajnu listu
				for (ExchangeRate rate : ExchangeRate.eRateList) {
					rate.printExchangeRate();
				}
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// brisanje racuna
			else if (choice == 'b') {
				// pitaj korisnika je li siguran
				choice = App.getCharStrict("Jeste li sigurni da zelite izbrisati vas racun i sve podatke o stanju i transakcijama? d/n", "dn");
				if (choice == 'n') continue;
				
				String passwordAttempt = App.getInput("Molimo vas potvrdite odluku svojom lozinkom: ", 32, false);
				
				// pokusaj obrisati racun
				int retval = -1;
				try {
					retval = user.deleteUser(passwordAttempt);
				} catch (RestClientException e) {
					System.out.println("Greska: " + e.getMessage());
					continue;
				}
				if (retval == 0) {
					System.out.println("Racun uspjesno obrisan! ");
					break;
				} else if (retval == 1) {
					System.out.println("Neispravna lozinka! ");
				}
			}
			
		}
		
		
		// kraj aplikacije
		cleanUp();
		return;
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	// pospremanje
	private static void cleanUp() {
		scanner.close();
		System.out.println("\n\n\nBye! ");
	}
	
}
