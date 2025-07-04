package com.bank_server.online_banka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.time.LocalDate;																// za zapisivanje u popis transakcija




class User
{
	String username;
	
	
	
	// konstruktor - koristen pri registraciji novog korisnika - postavlja username i stvara korisnikove datoteke
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
//		writer = new FileWriter("userIBANlist.txt", true);
//		writer.write(BankAccount.getAccount(this.username).IBAN + "\n" + this.username + "\n");
//		writer.close();
		
		// baza podataka test
//		OnlineBankServer.userRepo.save(new UserDB(BankAccount.getNewIBAN(), username, Encryption.encryptSHA(password), 0.0));
	}
	
	// brisanje korisnikovih datoteka - poziva i metodu za brisanje para IBAN-a i korisnickog imena iz datoteke 'userIBANlist.txt'
	static void deleteUser(String username) {
//		File userFile = new File(username + ".txt");
//		File userHistoryFile = new File(username + "_history.txt");
//				
//		boolean ret = userFile.delete() & userHistoryFile.delete();
//		// ako su datoteke uspjesno izbrisane, izbrisi korisnicko ime i IBAN iz liste
//		if (ret == true) BankAccount.updateUserIBANList(username);
//		
//		return ret;
		OnlineBankServer.userRepo.deleteByUsername(username);
	}
	
	
	
	// provjera postoji li korisnik s unesenim korisnickim imenom
	public static boolean userExists(String username) {
//		File userFile = new File(username + ".txt");
//		return userFile.exists();
		return OnlineBankServer.userRepo.existsByUsername(username);
	}
	
	
	
	// registracija korisnika
	static boolean registration(String username, String password, char encryption) {
		// ako vec postoji korinsnik sa istim nazivom, nemoj ga opet stvarati
		if (userExists(username)) return false;
//		if (User.userExists(username)) return false;
//		User user = new User(username, password, encryption);
		OnlineBankServer.userRepo.save(new UserDB(
				username, 
				encryption == 'a' ? Encryption.encryptAES(password) : Encryption.encryptSHA(password), 
				0.0
		));
		return OnlineBankServer.userRepo.existsByUsername(username);
	}
	
	
	
	// prijava korisnika
	static boolean login(String username, String password) {
		// vrati 'true' ako je prijava uspjesna, u suprotnom vrati 'false'
//		return userExists(username) && Encryption.testPassword(password, getEPassword(username));
//		if (userExists(username) == true && Encryption.testPassword(password, getEPassword(username)) == true)
//			return true;
//		return false;
		return  userExists(username)
				&&
				Encryption.testPassword(password, getUserEPassword(username));
	}
	
	
	
	// dohvati enkriptiranu lozinku
	protected static String getUserEPassword(String username) {
		// procitaj samo prvu liniju iz korisnikove datoteke
		try {
			String tmp = OnlineBankServer.userRepo.findByUsername(username).getEPassword();
			return tmp;
		} catch (Exception e) {
			return null;
		}
//		try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
//			return reader.readLine();
//		} catch (IOException e) {
//			System.out.println(e);
//			return null;
//		}
	}
	
	
	
	// uplata ili isplata na racun
	static Integer makePayment(String username, double payment) {
		// dohvati podatke o racunu
		BankAccount tmpAcc = null;
		try {
			tmpAcc = BankAccount.getAccount(username);
		} catch (IOException e) {
			System.out.println("Nije moguce dohvatiti korisnikov racun: " + e.getMessage());
			return 2; // greska
		}
		
		// ako ce stanje racuna poslije isplate biti manje od nula, korisnik nema dovoljno novca za isplatu
		if (tmpAcc.balance + payment < 0) {
			return 1; // nedovoljno novaca na racunu
		}
		
		// azururaj racun ako sve valja
		tmpAcc.balance += payment;
		try {
			tmpAcc.updateAccount(username);
		} catch (IOException e) {
			System.out.println(e);
			return 2; // greska
		}

		return 0; // uspjesna uplata/isplata
	}
	
	
	
	// placanje
	static Integer makeTransaction(String username, String recipient, double amount) {		
		// dohvati podatke o racunu
		BankAccount account = null;
		try {
			account = BankAccount.getAccount(username);
		} catch (IOException e) {
			System.out.println("Nije moguce dohvatiti korisnikov racun: " + e.getMessage());
			return 4;
		}
		
		// ako nema dovoljno novca transakciju nije moguce izvrsiti
		if (account.balance < amount || amount <= 0) {
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
