package bank;

import java.util.Scanner;
import java.io.*;
import java.util.Collections;




public class App
{
	static Scanner scanner = new Scanner(System.in);
	
	
	
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
			BankAccount.initializeLastNumber();
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
		//String input = null;
		while (true) {
			
			// odabir opcije
			choice = getCharStrict("\ne - odjava, s - stanje racuna, u - uplati/isplati novac, p - placanje, t - popis transakcija, l - prikaz tecajne liste\n", "esuptl");
			
			
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
				System.out.printf("Broj racuna: %d\nStanje na racunu: %.2f %s", account.accNumber, account.balance, account.value);
			}
			
			
			// uplata
			else if (choice == 'u') {
				double paymentA = 0;
				// unos
				while (true) {
					String tmp = getInput("Unesite iznos uplate: ", 32, true);
					try {
						paymentA = Double.parseDouble(tmp);
						break;
					} catch (NumberFormatException e) {
						System.out.println("Neispravan unos, pokusajte ponovo. ");
					}
				}
				// pokusaj uplate/isplate
				user.makePayment(paymentA);
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
				for (Transaction transaction : user.transactionList) {
					System.out.println("\nIBAN primatelja:\t" + transaction.IBAN);
					System.out.println("Iznos:\t\t\t" + transaction.amount);
					System.out.println("Datum:\t\t\t" + transaction.date);
				}
			}
			
			// tecajna lista
			else if (choice == 'l') {
				// ucitaj tecajnu listu u staticki objekt klase ExchangeRate
				try {
					ExchangeRate.setExchangerateList();
				} catch (IOException e) {
					System.out.println("Trenutno nije moguce dohvatiti tecajnu listu. ");
				}
				// ispisi tecajnu listu
				for (ExchangeRate rate : ExchangeRate.eRateList) {
					rate.printExchangeRate();
				}
			}
			
		}
		
		
		// kraj aplikacije
		cleanUp();
		System.out.println("Bye! ");
		return;
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	// pospremanje
	public static void cleanUp() {
		scanner.close();
		try {
			BankAccount.writeLastNumber();
		} catch (IOException e) {
			System.out.println(e + "\nNe mogu zapisati zadnji broj! ");
			return;
		}
		System.out.println("Bye! ");
	}
	
}
