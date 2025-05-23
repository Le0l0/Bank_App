package bank;

import java.io.*;
import java.time.LocalDate;																// za zapisivanje u popis transakcija




public class User
{
	String username;
	

	// normalan konstruktor
	public User(String username) {
		this.username = username;
		
	}
	
	// registracija novog korisnika
	public User(String username, String password) {
		this.username = username;
			try {
				createFiles(this.username, password);
			} catch (IOException e) {
				System.out.println(e);
			}
	}
	
	
	// kreiranje datoteka za spremanje podataka o korisniku i njegovom racunu
	public void createFiles(String username, String password) throws IOException {
		try(FileWriter writer = new FileWriter(username + ".txt", false)) {
			writer.write(Encryption.getMD5(password) + "\n");							// prva linija je enkriptirana lozinka
			writer.write(BankAccount.lastNumber++ + "\n" + 0 + "\n" + "euro" + "\n");	// dalje su zapisani podatci o stanju racuna: broj racuna, stanje, valuta
		}
		try {
			File user_history = new File(username + "_history.txt");
			user_history.createNewFile();
		} finally {}
	}
	
	
	// dohvati enkriptiranu lozinku
	public String getEPassword() {
		String EPassword = null;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
			EPassword = reader.readLine();
		} catch (IOException e) {
			System.out.println(e);
		}
		
		return EPassword;
	}
	
	
	
	public void makePayment(User user) {
		String IBAN = null;
		double amount = 0;
		
		// unos IBAN-a
		System.out.println("IBAN primatelja: ");
		while (true) {
			IBAN = App.getInput(34, true);
			
			boolean valid = true;
			for (int i = 0; i < IBAN.length(); i++) {
				if (Character.isLetter(IBAN.charAt(i))) {
					System.out.println("IBAN ne smije sadrzavati slova, pokusajte ponovo. ");
					valid = false;
					break;
				}
			}
			
			if (valid) break;
		}
		// unos iznosa placanja
		System.out.println("Iznos: ");
		while (true) {
			String tmp = App.getInput(32, true);
			try {
				amount = Double.parseDouble(tmp);
				if (amount < 0) {
					System.out.println("Neispravan unos, pokusajte ponovo. ");
				}
				break;
			} catch (NumberFormatException e) {
				System.out.println("Neispravan unos, pokusajte ponovo. ");
			}
		}
		// dohvati podatke o racunu
		BankAccount account = BankAccount.getAccount(user);
		// ako nema dovoljno novaca transakciju nije moguce izvrsiti
		if (account.balance < amount) {
			System.out.println("Nedovoljno novca na racunu. ");
			return;
		}
		// ako ima dovoljno novaca, odmah oduzmi s racuna i spremi promjenu
		account.balance -= amount;
		try {
			BankAccount.updateAccount(user, user.getEPassword(), account);
		} catch (IOException e) {
			System.out.println(e);
		}
		// zapisi transakciju u povijest transakcija
		try(FileWriter writer = new FileWriter(username + "_history.txt", true)) {
			writer.write("\nIBAN primatelja:\t" + IBAN + "\n");
			writer.write("Iznos:\t" + amount + "\n");
			writer.write("Datum:\t" + LocalDate.now() + "\n");
		} catch (IOException e) {
			System.out.println(e);
		}
		
		System.out.println("Transakcija uspjesna. ");
	}

}
