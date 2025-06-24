package com.bank_server.online_banka;

import java.io.*;
import java.time.LocalDate;																// za zapisivanje u popis transakcija




class User
{
	String username;
	
	
	
	// konstruktor - koristen pri registraciji novog korisnika
	public User(String username, String password, char encryption) {
		this.username = username;
		try {
			createFiles(password, encryption);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	
	
	// kreiranje datoteka za spremanje podataka o korisniku i njegovom racunu, te zapisivanje IBAN-a i korisnickog imena u listu
	private void createFiles(String password, char encryption) throws IOException {
		FileWriter writer = null;
		// stvori korisnikovu datoteku
		writer = new FileWriter(username + ".txt", false);
		// prva linija je enkriptirana lozinka
		if (encryption == 'a') writer.write(Encryption.encryptAES(password) + "\n");
		else writer.write(Encryption.encryptSHA(password) + "\n");
		// dalje su zapisani podatci o stanju racuna: broj racuna, stanje, valuta
		writer.write(BankAccount.getNewIBAN() + "\n" + 0 + "\n" + "EUR" + "\n");
		writer.close();
	
		// kreiraj datoteku u koju ce se zapisivati sve transakcije sa ili na korisnikov racun
		File user_history = new File(username + "_history.txt");
		user_history.createNewFile();
		
		// zapisi par korisnickog imena i IBAN-a u datoteku
		writer = new FileWriter("userIBANlist.txt", true);
		writer.write(BankAccount.getAccount(this.username).IBAN + "\n" + this.username + "\n");
		writer.close();
	}
	
	// brisanje podataka o racunu (korisnikovih datoteka)
	static boolean deleteFiles(String username) {
		File userFile = new File(username + ".txt");
		File userHistoryFile = new File(username + "_history.txt");
				
		boolean ret = userFile.delete() & userHistoryFile.delete();
		// ako su datoteke uspjesno izbrisane, izbrisi korisnicko ime i IBAN iz liste
		if (ret == true) BankAccount.updateUserIBANList(username);
		
		return ret;		
	}
	
	
	
	// provjera postoji li korisnik s unesenim korisnickim imenom
	public static boolean userExists(String username) {
		File userFile = new File(username + ".txt");
		return userFile.exists();
	}
	
	
	
	// registracija korisnika
	static boolean registration(String username, String password, char encryption) {
		User user = new User(username, password, encryption);
		
		return user != null;
	}
	
	
	
	// prijava korisnika
	static boolean login(String username, String password) {
		if (userExists(username) == true && Encryption.testPassword(password, getEPassword(username)) == true)
			return true;
		return false;
	}
	
	
	
	// dohvati enkriptiranu lozinku
	protected String getEPassword() {
		String EPassword = null;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(this.username + ".txt"))) {
			EPassword = reader.readLine();
		} catch (IOException e) {
			System.out.println(e);
		}
		
		return EPassword;
	}
	
	protected static String getEPassword(String username) {
		String EPassword = null;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
			EPassword = reader.readLine();
		} catch (IOException e) {
			System.out.println(e);
		}
		
		return EPassword;
	}
	
	
	
	// uplata ili isplata na racun
	static Integer makePayment(String username, double payment) {
		// ako ce stanje racuna poslije isplate biti manje od nula, korisnik nema dovoljno novca za isplatu
		BankAccount tmpAcc = BankAccount.getAccount(username);
		if (tmpAcc.balance + payment < 0) {
			return 1; // nedovoljno novaca na racunu
		}
		
		// azururaj racun ako sve valja
		tmpAcc.balance += payment;
		try {
			tmpAcc.updateAccount(username);
		} catch (IOException e) {
			return 2; // greska
		}

		return 0; // uspjesna uplata/isplata
	}
	
	
	
	// placanje
	static Integer makeTransaction(String username, String recipient, double amount) {		
		// dohvati podatke o racunu
		BankAccount account = BankAccount.getAccount(username);
		
		// ako nema dovoljno novaca transakciju nije moguce izvrsiti
		if (account.balance < amount) {
			return 1;
		}
		int retval = 0;
		
		// oduzmi kolicinu s racuna i spremi promjenu
		account.balance -= amount;
		try {
			account.updateAccount(username);
		} catch (IOException e) {
			System.out.println(e);
			retval = 4;
		}
		
		String userIBAN = account.IBAN;
		// zapisi transakciju u povijest transakcija platitelja
		try(FileWriter writer = new FileWriter(username + "_history.txt", true)) {
			writer.write(userIBAN + "\n");
			writer.write(recipient + "\n");
			writer.write(amount + "\n");
			writer.write(LocalDate.now() + "\n\n");
		} catch (IOException e) {
			System.out.println(e);
			retval = 4;
		}
		
		// provjeri ako u nasoj bazi podataka postoji korisnik sa IBAN-om primatelja
		String recipientUsername = null;
		try (BufferedReader reader = new BufferedReader(new FileReader("userIBANlist.txt"))) {
			String tmpUser = null;
			String tmpIBAN = null;
			
			tmpIBAN = reader.readLine();
			while (tmpIBAN != null && tmpIBAN.equals("") == false) {
				tmpUser = reader.readLine();
				
				if (tmpIBAN.equals(recipient) == true || tmpUser.equals(recipient) == true) {
					recipientUsername = tmpUser;
					break;
				}
				
				tmpIBAN = reader.readLine();
			}
		}
		catch (IOException e) {
			retval = 2;
		}
		
		// ako postoji korisnik sa IBAN-om primatelja zapisi transakciju u njegovu povijest transakcija i dodaj iznos na njegov racun
		if (recipientUsername != null) {
			try(FileWriter writer = new FileWriter(recipientUsername + "_history.txt", true)) {
				writer.write(userIBAN + "\n");
				writer.write(recipient + "\n");
				writer.write(amount + "\n");
				writer.write(LocalDate.now() + "\n\n");
			} catch (IOException e) {
				System.out.println(e);
				retval = 4;
			}
			
			try {
				BankAccount.updateAccountS(recipientUsername, amount);
			} catch (IOException e) {
				retval = 3;
			}
		}
		
		// transakcija uspjesna
		return retval;
	}

}
