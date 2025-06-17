package online_banka.Online_banka;

import java.util.Scanner;
import java.io.*;
import java.util.Collections;					// za sortiranje ArrayList
import java.net.URISyntaxException;




public class App
{
	static Scanner scanner = new Scanner(System.in);
	private static String secretKey = "9087e911d037b1c52b9417877c406784";
	private static String salt = "5105c7ca13276c99f8a79dc21ffca120";
	
	
	
	// getteri za secretKey i salt (za AES)
	static String getSecretKey() {return secretKey;}
	static String getSalt() {return salt;}
	
	
	
	// funkcije za unos
	public static String getInput(String text, int maxLength, boolean noSpace) {
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
	
	public static char getChar(String text) {
		String input = null;
		
		while (true) {
			System.out.println(text);
			input = scanner.nextLine();
			if (input.length() == 1) {
				return input.toLowerCase().charAt(0);
			}
		}
	}
	
	public static char getCharStrict(String text, String allowedInputs) {
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

	
	
	// izbornik za prijavu u aplikaciju
	public static User welcomeSignIn() {
		char choice = '0';
		User user = null;
		
		// prijava ili registracija u sustav
		while (true) {
			
			choice = getCharStrict("r - registracija, p - prijava, e - izlaz", "rpe");
			
			// registracija
			if (choice == 'r') {
				user = User.registration();
				break;
			}
			
			// prijava
			else if (choice == 'p') {
				user = User.login();
				break;
			}
			
			// izlaz
			else if (choice == 'e') {
				break;
			}
		}
		
		return user;
	}
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// main program aplikacije
	public static void main(String[] args)
	{
		// postavi lastNumber(zadnji koristeni broj racuna) na odgovarajucu vrijednost kad se pokrene aplikacija
		try {
			BankAccount.initializeIBAN();
		} catch (IOException e) {
			System.out.println(e + "\nNe mogu ocitati zadnji broj! ");
			return;
		}
		
		
		// naslov
		System.out.println();
		System.out.println("################################################################");
		System.out.println("                       Online Bankarstvo                        ");
		System.out.println("################################################################");
		System.out.println();
		
		
		// korisnik se prijavljuje
		User user = welcomeSignIn();
		// korisnik je odabrao izlaz iz programa u nekoj od metoda za prijavu
		if (user == null) {
			cleanUp();
			return;
		}
		// uspjesna prijava
		else {
			System.out.println("\nDobrodosli, " + user.username + "! \n");
		}
		

		// glavna infinite petlja - menu aplikacije
		char choice = '0';
		while (true) {
			
			// odabir opcije
			choice = getCharStrict("\ne - odjava, s - stanje racuna, u - uplati/isplati novac, p - placanje, t - popis transakcija, l - prikaz tecajne liste, b - brisanje racuna\n", "esuptlb");
			
			
			// odjava
			if (choice == 'e') {
				// pitaj korisnika je li siguran
				choice = getCharStrict("Jeste li sigurni da se zelite odjaviti od aplikacije? d/n", "dn");
				if (choice == 'd') {
					break;
				}
			}
			
			
			// stanje racuna
			else if (choice == 's') {
				// dohvati racun prijavljenog korisnika i ispisi podatke
				BankAccount account = BankAccount.getAccount(user);
				System.out.printf("IBAN racuna: %s\nStanje na racunu: %.2f %s\n\n", account.IBAN, account.balance, account.value);
			}
			
			
			// uplata
			else if (choice == 'u') {
				// pitaj korisnika da unese iznos koji zeli uplatiti/isplatiti, te ako je moguce azuriraj stanje racuna
				// kod obavljanja uplate/isplate je odvojen u klasu "User" jer je metoda duga (i jer se odnosi na User-a)
				user.makePayment();
			}
			
			
			// placanje
			else if (choice == 'p') {
				// obavi transakciju i zapisi je u povijest transakcija korisnika (ako je dovoljno novca na racunu)
				// odvojeno u klasu "User" jer je metoda duga (i jer se odnosi na User-a)
				user.makeTransaction();
			}
			
			
			// popis transakcija
			else if (choice == 't') {
				// spremi sve transakcije u jednu listu za lakse sortiranje
				user.loadTransactionList();
				if (user.transactionList.isEmpty()) {
					System.out.println("Nema zapisanih transakcija u povijesti. \n");
					continue;
				}
				
				// pitaj korisnika kako zeli sortirati
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
				else if (choice == 'i') {
					// sortiraj po iznosu
					user.transactionList.sort(new TransactionComparator());
					
					choice = getCharStrict("Zelite li ih sortirane od najvecih prema najmanjima ili obrnuto? \nn - od najvacih, o - obrnuto", "no");
					// okreni poredak ako treba
					if (choice == 'o') {
						Collections.reverse(user.transactionList);
					}
				}
				
				// ispisi transakcije
				String userIBAN = BankAccount.getAccount(user).IBAN;
				for (Transaction transaction : user.transactionList) {
					transaction.printTransaction(userIBAN, user.username);
				}
				System.out.println();
			}
			
			// tecajna lista
			else if (choice == 'l') {
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
			
			
			// tecajna lista
			else if (choice == 'b') {
				// pitaj korisnika je li siguran
				choice = App.getCharStrict("Jeste li sigurni da zelite izbrisati vas racun i sve podatke o stanju i transakcijama? d/n", "dn");
				if (choice == 'n') continue;
				
				String EPassword = user.getEPassword();
				String passwordAttempt = App.getInput("Molimo vas potvrdite odluku svojom lozinkom: ", 32, false);
				
				// krivi password - vrati korisnika nazad na izbornik
				if (Encryption.testPassword(passwordAttempt, EPassword) == false) {
					System.out.println("Neispravna lozinka!\n");
					continue;
				}
				
				// tocan password - izbrisi korisnikove datoteke i izadi iz aplikacije
				if (user.deleteFiles() == true) {
					BankAccount.updateUserIBANList(user.username);
					System.out.println("Podatci uspjesno izbrisani. ");
				} else {
					System.out.println("Doslo je do pogreske. ");
				}
				break;
			}
			
		}
		
		
		// kraj aplikacije
		cleanUp();
		return;
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	// pospremanje
	public static void cleanUp() {
		scanner.close();
		try {
			BankAccount.writeLastIBAN();
		} catch (IOException e) {
			System.out.println(e + "\nNe mogu zapisati zadnji iskoristeni IBAN! ");
			return;
		}
		System.out.println("\n\n\nBye! ");
	}
	
}
