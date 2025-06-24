package online_banka_client.bank_client;

import java.util.Scanner;
import java.util.Collections;					// za sortiranje ArrayList
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
////////////////////////////////////////////////////////////////
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;


// klase koje se koriste kao poruke za komunikaciju sa serverom
record UserM(String username, String password, char encryption) {}
record BankAccM(String IBAN, double balance, String value) {}
record TransactionReq(String recipient, double amount) {}
record TransactionM(String payer, String recipient, double amount, LocalDate date) {}




public class App
{
	static Scanner scanner = new Scanner(System.in);
	
	static final RestTemplate rest = new RestTemplate();
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
			if (response.equals("hello") == true) {}
		} catch (RestClientException e) {
			System.out.println("Nemoguce dohvatiti server! ");
			cleanUp();
			return;
		}
		
		
		char choice = '0';
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
			// korisnik je odabrao izlaz
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
				if (choice == 'd') {
					break;
				}
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// stanje racuna
			else if (choice == 's') {
				// dohvati racun prijavljenog korisnika i ispisi podatke
				try {
					BankAccM response = rest.getForObject(serverAddr + "/users/" + user.username + "/account-details", BankAccM.class);
					System.out.printf("IBAN racuna: %s\nStanje na racunu: %.2f %s\n\n", response.IBAN(), response.balance(), response.value());
				} catch (RestClientException e) {
					System.out.println("Nemoguce dohvatiti podatke o racunu: " + e.getMessage());
				}
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// uplata
			else if (choice == 'u') {
				// pitaj korisnika da unese iznos koji zeli uplatiti/isplatiti, te pokusaj izvrsiti uplatu/isplatu
				// unos kolicine
				double payment = 0;
				while (true) {
					String tmp = App.getInput("Unesite iznos uplate: ", 32, true);
					try {
						payment = Double.parseDouble(tmp);
						break;
					} catch (NumberFormatException e) {
						System.out.println("Neispravan unos, pokusajte ponovo. ");
					}
				}
				
				// pokusaj izvrsiti
				try {
					int response = (int) rest.postForObject(serverAddr + "/users/" + user.username + "/make-payment", payment, Integer.class);
					switch (response) {
					case 0: System.out.println("Uplata/isplata uspjesna. \n"); break;
					case 1: System.out.println("Isplata neuspjesna - nedovoljno novca na racunu. \n"); break;
					default: System.out.println("Nije moguce izvrsiti uplatu/isplatu! ");
					}
				} catch (RestClientException e) {
					System.out.println("Nemoguce izvrsiti uplatu/isplatu: " + e.getMessage());
				}
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// placanje
			else if (choice == 'p') {
				// obavi transakciju i zapisi je u povijest transakcija korisnika (ako je dovoljno novca na racunu)
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
				
				// probaj izvrsiti transakciju
				try {
					int response = (int) rest.postForObject(serverAddr + "/users/" + user.username + "/make-transaction", new TransactionReq(recipient, amount), Integer.class);
					switch (response) {
					case 0: System.out.printf("Transakcija uspjesna. Uplaceno %.2f EUR na racun %s. \n", amount, recipient); break;
					case 1: System.out.println("Nedovoljno novca na racunu. Transakcija neuspjesna. \n"); break;
					case 2: System.out.println("Nije moguce citati iz datoteke 'userIBANlist.txt'! \n"); break;
					case 3: System.out.println("Azuriranje racuna primatelja neuspjesno! \n"); break;
					default: System.out.println("Doslo je do greske! \n");
					}
				} catch (RestClientException e) {
					System.out.println("Nemoguce izvrsiti transakciju: " + e.getMessage());
				}
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// popis transakcija
			else if (choice == 't') {
//				// dohvati povijest transakcija
				try {
					user.fetchTransactionList();
				} catch (RestClientException e) {
					System.out.println("Nemoguce dohvatiti povijest transakcija: " + e.getMessage());
					continue;
				}
				
				// ako u povijesti nema transakcija ispisi obavijest i idi dalje
				if (user.transactionList.isEmpty()) {
					System.out.println("U povijesti nema zapisanih transakcija. \n");
					continue;
				}
				
				// pitaj korisnika kako zeli sortirati - u datoteci su transakcije vec zapisane od najstarijih prema
				// najnovijima, tako da ih sortiramo samo ako korisnik odabere da su sortirane po velicini
				choice = getCharStrict("Zelite li ih sortirane prema datumu ili iznosu? \nd - po datumu, i - po iznosu", "di");
				// po datumu
				if (choice == 'd') {
					choice = getCharStrict("Zelite li ih sortirane od najstrarijih prema najnovijim ili obrnuto? \nn - od najstrarijih, o - obrnuto", "no");
					// okreni poredak ako treba
					if (choice == 'o') {
						Collections.reverse(user.transactionList);
					}
				}
				// po iznosu
				else {
					// sortiraj po iznosu
					user.transactionList.sort(new TransactionComparator());
					choice = getCharStrict("Zelite li ih sortirane od najvecih prema najmanjima ili obrnuto? \nn - od najvecih, o - obrnuto", "no");
					// okreni poredak ako treba
					if (choice == 'o') {
						Collections.reverse(user.transactionList);
					}
				}
				
				// dohvati IBAN korisnika - treba za lijapsi ispis
				String userIBAN = null;
				try {
					userIBAN = rest.getForObject(serverAddr + "/users/" + user.username + "/account-details", BankAccM.class).IBAN();
				} catch (RestClientException e) {
					System.out.println("Nemoguce dohvatiti podatke o racunu: " + e.getMessage());
				}
				
				// ispisi transakcije
				for (Transaction transaction : user.transactionList) {
					transaction.printTransaction(userIBAN, user.username);
				}
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
				
				// provjeri je li unesena lozinka tocna
				try {
					boolean response = rest.postForObject(serverAddr + "/users/" + user.username + "/check-password", passwordAttempt, boolean.class);
					if (response == false) {
						System.out.println("Neispravna lozinka! \n");
						continue;
					}
				} catch (RestClientException e) {
					System.out.println("Greska: " + e.getMessage());
				}
				
				// ako je tocna lozinka, obrisi sve podatke o korisnikovom racunu
				try {
					rest.delete(serverAddr + "/users/" + user.username);
					System.out.println("Racun uspjesno obrisan! ");
					user = null;
					break;
				} catch (RestClientException e) {
					System.out.println("Greska: " + e.getMessage());
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
