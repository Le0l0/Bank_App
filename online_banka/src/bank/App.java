package bank;

import java.util.Scanner;
import java.io.*;




public class App
{
	static Scanner scanner = new Scanner(System.in);
	
	
	// funkcije za unos
	public static String getInput(int maxLength, boolean noSpace) {
		String input = null;
		
		while (true) {
			input = scanner.nextLine();
			if (input.length() > 0 && input.length() <= maxLength && (!noSpace || !input.contains(" "))) return input;
			else {
				System.out.print("Unos nesmije biti dulji od " + maxLength);
				if (noSpace) System.out.println(" i ne smije sadrzavati razmake. ");
				else System.out.println(". ");
			}
		}
	}
	/*public static String getInput(int length, boolean excactLength, boolean noSpace) {
		String input = null;
		
		while (true) {
			input = scanner.nextLine();
			if (input.length() == length && input.length() <= length && (!noSpace || !input.contains(" "))) return input;
			else {
				System.out.print("Unos mora biti dug " + length + " znakova");
				if (noSpace) System.out.println(" i ne smije sadrzavati razmake. ");
				else System.out.println(". ");
			}
		}
	}*/
	
	
	
	public static User registration() {
		String username;
		String password;
		
		while (true) {
			// unos korisnickog imena i lozinke
			System.out.println("Username: ");
			username = getInput(32, true);
			
			System.out.println("Password: ");
			password = getInput(32, false);

			System.out.println("Confirm Password: ");
			
			if (password.equals(getInput(32, false)) == true) {						// registracija uspjesna
				break;
			}
			else {																	// registracija neuspjesna
				System.out.println("Registracija neuspjesna, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ");
				String choice = getInput(32, false);
				if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {			// izlaz
					return null;
				}
			}
		}
		
		return new User(username, password);
	}
	
	
	
	public static User login() {
		String username;
		String passwordAttempt;
		String EPassword;
		boolean success = false;
		
		while (true) {
			// unos korisnickog imena i lozinke
			System.out.println("Username: ");
			username = getInput(32, true);

			System.out.println("Password: ");
			passwordAttempt = getInput(32, false);
			
			// projvjera ispravnosti unesenih podataka:
			// najprije provjeravamo postoji li korisnik pod imenom 'username', te ako da provjerimo je li unesena lozinka jednaka
			// onoj koja je zapisana u korisnikovoj datoteci
			File testfile = new File(username + ".txt");
			if (testfile.isFile() == true) {
				try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
					EPassword = reader.readLine();
					if (EPassword.equals(Encryption.getMD5(passwordAttempt))) {
						success = true;
					}
				} catch (IOException | NumberFormatException e) {
					System.out.println(e);
				}
			}
			// ako je prijava uspjesna, vrati User objekt sa odgovarajucim podatcima, a ako ne pokusaj ponovo ili izadi iz aplikacije
			if (success == true) break;
			else {
				System.out.println("Korisnicko ime ili lozinka su pogresni, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ");
				String choice = getInput(32, false);
				if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {			// izlaz
					return null;
				}
			}
			
		}
		
		return new User(username);
	}
	
	
	// izbornik za prijavu u aplikaciju
	public static User welcomeLogin() {
		String choice = null;
		User user = null;
		
		// prijava ili registracija u sustav
		System.out.println("r - registracija, p - prijava, e - izlaz");
		while (true) {
			choice = scanner.nextLine();
			
			if (choice.length() <= 0) continue;
			if (choice.charAt(0) == 'r' || choice.charAt(0) == 'R') {				// registracija
				user = registration();
				break;
			}
			else if (choice.charAt(0) == 'p' || choice.charAt(0) == 'P') {			// prijava
				user = login();
				break;
			}
			else if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {			// izlaz
				break;
			}
			else {																	// krivi unos
				System.out.println("Molimo unesite 'r' za registraciju, 'p' za prijavu, ili 'e' za izlaz iz aplikacije. ");
			}
		}
		
		return user;
	}
	
	
	
	public static void main(String[] args)
	{
		// postavi lastNumber(zadnji koristeni broj racuna) na odgovarajucu vrijednost kad se pokrene aplikacija
		try {
			BankAccount.initializeLastNumber();
		} catch (IOException e) {
			System.out.println(e + "\nNe mogu ocitati zadnji broj! ");
			return;
		}
		
		
		System.out.println();
		System.out.println("################################################################");
		System.out.println("                       Online Bankarstvo                        ");
		System.out.println("################################################################");
		System.out.println();
		
		// korisnik se prijavljuje
		User user = App.welcomeLogin();
		if (user == null) {															// korisnik je odabrao izlaz iz programa u nekoj od metoda za prijavu
			cleanUp();
			return;
		} else {																	// uspjesna prijava
			System.out.println("\nDobrodosli, " + user.username + "! \n");
		}

		// glavna infinite petlja - menu aplikacije
		String choice = null;
		while (true) {
			// odabir opcije
			System.out.println("e - odjava, s - stanje racuna, u - uplati/isplati novac, p - placanje, t - popis transakcija");
			choice = getInput(32, false);
			
			if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {				// odjava
				// pitaj korisnika je li siguran
				System.out.println("Jeste li sigurni da se zelite odjaviti od aplikacije? d/n");
				choice = getInput(32, false);
				if (choice.charAt(0) == 'd' || choice.charAt(0) == 'D') {
					break;
				}
			}
			
			else if (choice.charAt(0) == 's' || choice.charAt(0) == 'S') {			// stanje racuna
				// dohvati racun prijavljenog korisnika i ispisi podatke
				BankAccount account = BankAccount.getAccount(user);
				System.out.println("Broj racuna: " + account.accNumber + "\nStanje na racunu: " + account.balance + " " + account.value + " \n");
			}
			
			else if (choice.charAt(0) == 'u' || choice.charAt(0) == 'U') {			// uplata
				// ako je unos ispravan dodaj na racun
				System.out.println("Unesite iznos uplate: ");
				double paymentN = 0;
				// unos
				while (true) {
					String tmp = getInput(32, true);
					try {
						paymentN = Double.parseDouble(tmp);
						break;
					} catch (NumberFormatException e) {
						System.out.println("Neispravan unos, pokusajte ponovo. ");
					}
				}
				// pokusaj uplate/isplate
				BankAccount.payment(user, paymentN);
			}
			
			else if (choice.charAt(0) == 'p' || choice.charAt(0) == 'P') {			// placanje
				// obavi transakciju i zapisi je u povijest transakcija korisnika (ako je dovoljno novca na racunu)
				user.makePayment(user);
			}
			
			else if (choice.charAt(0) == 't' || choice.charAt(0) == 'T') {			// popis transakcija
				System.out.println("Treba napraviti... ");
			}
		}
		
		
		cleanUp();
		System.out.println("Bye! ");
		return;
	}
	
	
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
