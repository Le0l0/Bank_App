package bank;

import java.io.*;
import java.time.LocalDate;																// za zapisivanje u popis transakcija
import java.util.ArrayList;




public class User
{
	String username;
	ArrayList<Transaction> transactionList = null;
	

	// normalan konstruktor
	public User(String username) {
		this.username = username;
		this.transactionList = new ArrayList<Transaction>();
	}
	
	// konstruktor koristen pri registraciji novog korisnika
	public User(String username, String password) {
		this.username = username;
		this.transactionList = new ArrayList<Transaction>();
		try {
			createFiles(password);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	
	
	// kreiranje datoteka za spremanje podataka o korisniku i njegovom racunu
	public void createFiles(String password) throws IOException {
		try (FileWriter writer = new FileWriter(username + ".txt", false)) {
//			writer.write(Encryption.encryptMD5(password) + "\n");						// prva linija je enkriptirana lozinka
//			writer.write(Encryption.encryptAES(password, password, password) + "\n");
			writer.write(Encryption.encryptSHA(password) + "\n");
			writer.write(BankAccount.lastNumber++ + "\n" + 0 + "\n" + "euro" + "\n");	// dalje su zapisani podatci o stanju racuna: broj racuna, stanje, valuta
		}
		try {
			File user_history = new File(username + "_history.txt");
			user_history.createNewFile();
		} finally {}
	}
	
	
	
	// registracija korisnika
	public static User registration() {
		String username;
		String password;
		
		while (true) {
			// unos korisnickog imena i lozinke
			username = App.getInput("Username: ", 32, true);
			password = App.getInput("Password: ", 32, false);
			
			// registracija uspjesna
			if (password.equals(App.getInput("Confirm Password: ", 32, false)) == true) {
				break;
			}
			// registracija neuspjesna
			else {
				String choice = App.getInput("Registracija neuspjesna, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ", 32, false);
				if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {				// izlaz
					return null;
				}
			}
		}
		
		return new User(username, password);
	}
	
	
	
	// prijava korisnika
	public static User login() {
		String username;
		String passwordAttempt;
		String EPassword;
		
		while (true) {
			// unos korisnickog imena i lozinke
			username = App.getInput("Username: ", 32, true);
			passwordAttempt = App.getInput("Password: ", 32, false);
			
			// provjerimo je li unesena lozinka jednaka onoj koja je zapisana u korisnikovoj datoteci
			EPassword = getEPassword(username);
//			if (EPassword != null && Encryption.encryptMD5(passwordAttempt).equals(EPassword)) break;
//			if (EPassword != null && Encryption.testPasswordAES(passwordAttempt, EPassword)) break;
			if (EPassword != null && Encryption.encryptSHA(passwordAttempt).equals(EPassword)) break;
			
			// ako prijava nije uspjesna pokusaj ponovo ili izadi iz aplikacije
			else {
				String choice = App.getInput("Korisnicko ime ili lozinka su pogresni, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ", 32, false);
				if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {				// izlaz
					return null;
				}
			}
			
		}
		
		return new User(username);
	}
	
	
	
	// dohvati enkriptiranu lozinku:
	// provjeravamo postoji li korisnik pod imenom 'username', te ako da vracamo enkriptiranu lozinku
	public String getEPassword() {
		String EPassword = null;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(this.username + ".txt"))) {
			EPassword = reader.readLine();
		} catch (IOException e) {
			System.out.println(e);
		}
		
		return EPassword;
	}
	
	public static String getEPassword(String username) {
		String EPassword = null;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
			EPassword = reader.readLine();
		} catch (IOException e) {
			//System.out.println(e);
		}
		
		return EPassword;
	}
	
	
	
	// uplata ili isplata na racun
	public void makePayment(double payment) {
		BankAccount tmpAcc = BankAccount.getAccount(this);
		
		if (tmpAcc.balance + payment < 0) {
			System.out.println("Isplata neuspjesna - nedovoljno novca na racunu. \n");
			return;
		}
		
		tmpAcc.balance += payment;
		try {
			tmpAcc.updateAccount(this);
		} catch (IOException e) {
			System.out.println(e);
			return;
		}
		
		System.out.println("Uplata/isplata uspjesna. \n");
	}
	
	
	
	// placanje
	public void makeTransaction() {
		String IBAN = null;
		double amount = 0;
		
		// unos IBAN-a
		IBAN = App.getInput("IBAN primatelja: ", 34, true);
		
		// unos iznosa placanja
		while (true) {
			String tmp = App.getInput("Iznos: ", 32, true);
			try {
				amount = Double.parseDouble(tmp);
				if (amount < 0) {
					System.out.println("Neispravan unos, pokusajte ponovo. ");
				} else break;
			} catch (NumberFormatException e) {
				System.out.println("Neispravan unos, pokusajte ponovo. ");
			}
		}
		
		// dohvati podatke o racunu
		BankAccount account = BankAccount.getAccount(this);
		
		// ako nema dovoljno novaca transakciju nije moguce izvrsiti
		if (account.balance < amount) {
			System.out.println("Nedovoljno novca na racunu. ");
			return;
		}
		
		// ako ima dovoljno novaca, odmah oduzmi s racuna i spremi promjenu
		account.balance -= amount;
		try {
			account.updateAccount(this);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		// zapisi transakciju u povijest transakcija
		try(FileWriter writer = new FileWriter(username + "_history.txt", true)) {
			writer.write(IBAN + "\n");
			writer.write(amount + "\n");
			writer.write(LocalDate.now() + "\n\n");
		} catch (IOException e) {
			System.out.println(e);
		}
		
		System.out.printf("Transakcija uspjesna. Uplaceno %.2f EUR na racun %s. ", amount, IBAN);
	}
	
	
	
	// metoda koja vraca ArrayList u kojoj su sve transakcije korisnika
	public void loadTransactionList() {
		String IBAN = null;
		double amount = 0;
		LocalDate date = null;
		
		String tmpS = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(username + "_history.txt"))) {
			IBAN = reader.readLine();
			while (IBAN != null && !IBAN.equals("")) {
				tmpS = reader.readLine();
				amount = Double.parseDouble(tmpS);
				tmpS = reader.readLine();
				date = LocalDate.parse(tmpS);
				
				transactionList.add(new Transaction(IBAN, amount, date));
				
				tmpS = reader.readLine();												// preskoci liniju
				IBAN = reader.readLine();												// ucitaj sljedeci IBAN
			}
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
