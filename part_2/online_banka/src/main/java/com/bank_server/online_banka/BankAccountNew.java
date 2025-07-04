package com.bank_server.online_banka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;




class BankAccountNew
{
	protected String IBAN;
	protected double balance;
	protected String value;
	private static int lastIBAN = -1;


	// konstruktori
	public BankAccountNew() {
		this.IBAN = null;
		this.balance = 0;
		this.value = null;
	}

	public BankAccountNew(String IBAN, double balance, String value) {
		this.IBAN = IBAN;
		this.balance = balance;
		this.value = value;
	}



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
	static BankAccountNew getAccount(String username) {
		BankAccDB tmpacc = OnlineBankServer.bankaccRepo.findByOwner(username);
		return new BankAccountNew(tmpacc.getIban(), tmpacc.getBalance(), tmpacc.getValue());
	}



	// metoda za zapisivanje podataka o racunu u korisnikovu datoteku
	void updateAccount(String username) throws IOException {
		String ePassword = User.getUserEPassword(username);
		try(FileWriter writer = new FileWriter(username + ".txt", false)) {
			// zapisi nove podatke
			writer.write(ePassword + "\n");
			writer.write(IBAN + "\n" + balance + "\n" + value + "\n");
		}
	}

	// azuriraj podatke o racunu koristeci samo username i dodaj iznos 'amount' na racun
	static void updateAccountS(String username, double amount) throws IOException {
		// dohvati racun
		BankAccountNew bankAcc = getAccount(username);
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
