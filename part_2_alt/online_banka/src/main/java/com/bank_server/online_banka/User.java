package com.bank_server.online_banka;

import java.time.LocalDate;




class User
{
	String username;
	

	
	User(String username) {
		this.username = username;
	}
	
	
	
	// brisanje korisnika iz baze podataka
	static void deleteUser(String username) {
		BankDB.deleteUserByUsername(username);
		BankAccount.deleteAccs(username);
	}
	
	
	
	// provjera postoji li korisnik s unesenim korisnickim imenom
	public static boolean userExists(String username) {
		return BankDB.existsUserByUsername(username);
	}
	
	
	
	// registracija korisnika
	static boolean registration(String username, String password, char encryption) {
		// ako vec postoji korinsnik sa istim nazivom, nemoj ga opet stvarati
		if (userExists(username)) return false;
		
		BankDB.saveUser(new UserDB(
				username, 
				encryption == 'a' ? Encryption.encryptAES(password) : Encryption.encryptSHA(password)
		));
		BankDB.saveAcc(new BankAccDB(
				username, 
				BankAccount.getNewIBAN(), 
				0.0, 
				"EUR"
		));
		
		return userExists(username);
	}
	
	
	
	// prijava korisnika
	static boolean login(String username, String password) {
		// vrati 'true' ako je prijava uspjesna, u suprotnom vrati 'false'
		return  userExists(username)
				&&
				Encryption.testPassword(password, getUserEPassword(username));
	}
	
	
	
	// dohvati enkriptiranu lozinku
	protected static String getUserEPassword(String username) {
		return BankDB.findUserByUsername(username).getEPassword();
	}
	
	
	
	// uplata ili isplata na racun
	static Integer makePayment(String username, double payment) {
		// dohvati podatke o racunu
		BankAccount tmpAcc = null;
		try {
			tmpAcc = BankAccount.getAccountByOwner(username);
		} catch (Exception e) {
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
		} catch (Exception e) {
			System.out.println(e);
			return 2; // greska
		}

		return 0; // uspjesna uplata/isplata
	}
	
	
	
	// placanje
	static Integer makeTransaction(String payer, String recipient, double amount) {		
		// dohvati podatke o racunu
		BankAccount account = null;
		try {
			account = BankAccount.getAccountByOwner(payer);
		} catch (Exception e) {
			System.out.println("Nije moguce dohvatiti korisnikov racun: " + e.getMessage());
			return 2;
		}
		
		// ako nema dovoljno novca transakciju nije moguce izvrsiti
		if (account.balance < amount || amount <= 0) {
			return 1;
		}
		int retval = 0;
		
		// oduzmi iznos s racuna i spremi promjenu
		account.balance -= amount;
		try {
			account.updateAccount(payer);
		} catch (Exception e) {
			System.out.println("Greska: " + e.getMessage());
			retval = 4;
		}
		
		// ako recipient nije IBAN, znaci da je ime - pronadi ime korisnika koji ima taj racun
		if (recipient.length() < 34) {
			// recipient = BankDB.findAccByOwner(recipient).getIban();
			try {
				recipient = BankAccount.ownerToIban(recipient);
			} catch (Exception e) {/* nista, korisnik unesao niespravan IBAN ili ime korisnika koji nije registriran u nasoj banci */}
		}
		
		// ako je primatelj u nasoj banci dodaj iznos na racun i spremi promjenu
		BankAccount recAcc = null;
		try {
			recAcc = BankAccount.getAccountByIban(recipient);
			if (recAcc == null) recAcc = BankAccount.getAccountByOwner(recipient);
		} catch (Exception e) {
			System.out.println("Nije moguce dohvatiti racun primatelja. ");
		}
		// ?
		if (recAcc != null) {
			recAcc.balance += amount;
			try {
				account.updateAccount(payer);
			} catch (Exception e) {
				System.out.println("Greska: " + e.getMessage());
				retval = 3;
			}
		}
		
		// spremi transakciju u povijest transakcija
		BankDB.saveTransaction(new TransactionDB(account.getIBAN(), recipient, amount, LocalDate.now()));
		
		// transakcija uspjesna
		return retval;
	}

}
