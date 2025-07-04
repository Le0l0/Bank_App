package com.bank_server.online_banka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;




class BankAccount
{
	private String IBAN;
	private int id;
	double balance;
	private String value;
	private static int lastIBAN = -1;


	// konstruktori
	public BankAccount() {
		this.id = -1;
		this.IBAN = null;
		this.balance = 0;
		this.value = null;
	}

	public BankAccount(int id, String IBAN, double balance, String value) {
		this.id = id;
		this.IBAN = IBAN;
		this.balance = balance;
		this.value = value;
	}
	
	String getIBAN()	{return this.IBAN;}
	String getValue()	{return this.value;}



	// ucitaj zadnji koristeni IBAN iz memeorije u staticku varijablu 'lastIBAN'
	private static void loadLastIBAN() throws IOException, NumberFormatException {
		File testfile = new File("lastIBAN.txt");

		if (testfile.isFile() == true) {
			BufferedReader reader = new BufferedReader(new FileReader(testfile));
			lastIBAN = Integer.parseInt(reader.readLine());
			reader.close();
		}
		else {
			FileWriter writer = new FileWriter(testfile, false);
			lastIBAN = 1;
			writer.write("1");
			writer.close();
		}
	}

	// zapisi zadnji koristeni IBAN u "lastIBAN.txt"
	private static void writeLastIBAN() throws IOException {
		FileWriter writer = new FileWriter("lastIBAN.txt", false);
		writer.write(Integer.toString(lastIBAN));
		writer.close();
	}

	// vraca novi IBAN
	static String getNewIBAN() {
		// ucitaj IBAN u 'lastIBAN' ako vec nije
		if (lastIBAN == -1) {
			try {
				loadLastIBAN();
			} catch (IOException e) {
				lastIBAN = 0;
				System.out.println("Nemoguce ocitati zadnji IBAN! ");
			}
		}

		lastIBAN++;

		// odmah zapisi novi zanji koristeni IBAN
		try {
			writeLastIBAN();
		} catch (IOException e) {
			System.out.println("Nemoguce zapisati zadnji IBAN! ");
		}

		// vrati novi IBAN u obliku Stringa
		return "HR" + String.format("%032d", lastIBAN);
	}



	// dohvati podatke o racunu
	static BankAccount getAccount(String username) throws IOException {
		BankAccDB tmpdbacc = BankDB.findAccByOwner(username);
		return new BankAccount(tmpdbacc.getId(), tmpdbacc.getIban(), tmpdbacc.getBalance(), tmpdbacc.getValue());
	}



	void updateAccount(String username) {
		BankAccDB newAcc = new BankAccDB(username, this.IBAN, this.balance, this.value);
		newAcc.setId(this.id);
		BankDB.updateAcc(newAcc);
	}
	
	
	
	// metoda za zapisivanje podataka o racunu u korisnikovu datoteku
//	void updateAccount(String username) throws IOException {
//		String ePassword = User.getUserEPassword(username);
//		try(FileWriter writer = new FileWriter(username + ".txt", false)) {
//			// zapisi nove podatke
//			writer.write(ePassword + "\n");
//			writer.write(IBAN + "\n" + balance + "\n" + value + "\n");
//		}
//	}

	// azuriraj podatke o racunu koristeci samo username i dodaj iznos 'amount' na racun
	static void updateAccountS(String username, double amount) throws IOException {
		// dohvati racun
		BankAccount bankAcc = getAccount(username);
		// dodaj 'amount'
		bankAcc.balance += amount;
		// zapisi nove podatke
		bankAcc.updateAccount(username);
	}
	
	// isto kao metoda iznad samo sta nije staticka
	void updateAccount(String username, double amount) throws IOException {
		// dodaj 'amount'
		this.balance += amount;
		// zapisi nove podatke
		this.updateAccount(username);
	}

}
