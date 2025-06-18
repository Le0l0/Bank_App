package online_banka.Online_banka;

import java.io.*;
import java.time.LocalDate;																// za zapisivanje u popis transakcija
import java.util.ArrayList;




class User
{
	String username;
	final ArrayList<Transaction> transactionList;
	

	// prazan konstruktor
	public User() {
		this.username = "-";
		this.transactionList = null;
	}
	
	// normalan konstruktor - koristen kada se vecpostojeci korisnik ulogira u aplikaciju
	public User(String username) {
		this.username = username;
		this.transactionList = new ArrayList<Transaction>();
	}
	
	// konstruktor - koristen pri registraciji novog korisnika bez specificirane enkripcije lozinke
	public User(String username, String password) {
		this.username = username;
		this.transactionList = new ArrayList<Transaction>();
		try {
			createFiles(password, 's');
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	// konstruktor - koristen pri registraciji novog korisnika
	public User(String username, String password, char encryption) {
		this.username = username;
		this.transactionList = new ArrayList<Transaction>();
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
	boolean deleteFiles() {
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
	static User registration() {
		String username = null;
		String password = null;
		char choice = '0';
		
		while (true) {
			// unos korisnickog imena i lozinke
			username = App.getInput("Username: ", 32, true);
			if (userExists(username) == true) {
				System.out.println("Ovo korisnicko ime je zauzeto, uneste neko drugo. ");
				continue;
			}
			
			password = App.getInput("Password: ", 32, false);
			
			// registracija uspjesna
			if (password.equals(App.getInput("Confirm Password: ", 32, false)) == true) {
				break;
			}
			// registracija neuspjesna
			else {
				choice = App.getChar("Registracija neuspjesna, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ");
				if (choice == 'e') {													// izlaz
					return null;
				}
			}
		}
		
		choice = App.getCharStrict("Odaberite koji tip enkripcije cete koristiti za lozinku. a - AES, s - SHA", "as");
		User user = new User(username, password, choice);
		
		return user;
	}
	
	
	
	// prijava korisnika
	static User login() {
		String username = null;
		String passwordAttempt = null;
		
		while (true) {
			// unos korisnickog imena i lozinke
			username = App.getInput("Username: ", 32, true);
			passwordAttempt = App.getInput("Password: ", 32, false);
			
			// provjerimo je li unesena lozinka jednaka onoj koja je zapisana u korisnikovoj datoteci
			if (userExists(username) == true && Encryption.testPassword(passwordAttempt, getEPassword(username)) == true)
				break;
			// ako prijava nije uspjesna pokusaj ponovo ili izadi iz aplikacije
			else {
				char choice = App.getChar("Korisnicko ime ili lozinka su pogresni, unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ");
				// izlaz
				if (choice == 'e')
					return null;
			}
			
		}
		
		return new User(username);
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
	void makePayment() {
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
		
		// ako ce stanje racuna poslije isplate biti manje od nula, korisnik nema dovoljno novca za isplatu
		BankAccount tmpAcc = BankAccount.getAccount(this.username);
		if (tmpAcc.balance + payment < 0) {
			System.out.println("Isplata neuspjesna - nedovoljno novca na racunu. \n");
			return;
		}
		
		// azururaj racun ako sve valja
		tmpAcc.balance += payment;
		try {
			tmpAcc.updateAccount(this);
		} catch (IOException e) {
			System.out.println("Nije moguce izvrsiti uplatu/isplatu! ");
			return;
		}
		
		System.out.println("Uplata/isplata uspjesna. \n");
	}

	
	
	
	// placanje
	void makeTransaction() {
		String userIBAN = BankAccount.getAccount(this.username).IBAN;
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
		
		// dohvati podatke o racunu
		BankAccount account = BankAccount.getAccount(this.username);
		
		// ako nema dovoljno novaca transakciju nije moguce izvrsiti
		if (account.balance < amount) {
			System.out.println("Nedovoljno novca na racunu. Transakcija neuspjesna. ");
			return;
		}
		
		// oduzmi kolicinu s racuna i spremi promjenu
		account.balance -= amount;
		try {
			account.updateAccount(this);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		// zapisi transakciju u povijest transakcija platitelja
		try(FileWriter writer = new FileWriter(username + "_history.txt", true)) {
			writer.write(userIBAN + "\n");
			writer.write(recipient + "\n");
			writer.write(amount + "\n");
			writer.write(LocalDate.now() + "\n\n");
		} catch (IOException e) {
			System.out.println(e);
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
			System.out.println("Nije moguce citati iz datoteke 'userIBANlist.txt'! ");
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
			}
			
			try {
				BankAccount.updateAccount(recipientUsername, amount);
			} catch (IOException e) {
				System.out.println("Azuriranje racuna primatelja neuspjesno! ");
			}
		}
		
		// ispisi obavijest da je transakcija uspjesna
		System.out.printf("Transakcija uspjesna. Uplaceno %.2f EUR na racun %s. \n", amount, recipient);
	}
	
	
	
	// metoda koja vraca ArrayList u kojoj su sve transakcije korisnika
	void loadTransactionList() {
		String payerIBAN = null;
		String recipientIBAN = null;
		double amount = 0;
		LocalDate date = null;
		
		// ocisti prijasnju listu
		transactionList.clear();
		
		// ucitavaj transakcije iz datoteke u 'transactionList'
		String tmpS = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(username + "_history.txt"))) {
			payerIBAN = reader.readLine();
			while (payerIBAN != null && !payerIBAN.equals("")) {
				recipientIBAN = reader.readLine();
				tmpS = reader.readLine();
				amount = Double.parseDouble(tmpS);
				tmpS = reader.readLine();
				date = LocalDate.parse(tmpS);
				
				transactionList.add(new Transaction(payerIBAN, recipientIBAN, amount, date));
				
				// preskoci liniju
				tmpS = reader.readLine();
				// ucitaj sljedeci IBAN
				payerIBAN = reader.readLine();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}

}
