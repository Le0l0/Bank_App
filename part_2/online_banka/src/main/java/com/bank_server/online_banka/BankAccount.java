package com.bank_server.online_banka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;




class BankAccount
{
	protected String IBAN;
	protected double balance;
	protected String value;
	private static int lastIBAN = -1;


	// konstruktori
	public BankAccount() {
		this.IBAN = null;
		this.balance = 0;
		this.value = "KN";
	}

	public BankAccount(String IBAN, double balance, String value) {
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



	// metoda za brisanje IBAN-a i korisnickog imena iz datoteke "userIBANlist.txt" kada korisnik brise svoj racun
	static void updateUserIBANList(String deletedUsername) {
		// dvije liste: jedna za IBAN-ove korisnika, jedna za korisnicka imena
		ArrayList<String> updatedListIBANs = new ArrayList<String>();
		ArrayList<String> updatedListUsernames = new ArrayList<String>();

		// ucitavaj podatke iz "userIBANlist.txt" samo ako korisnicko ime nije jednako onom koje zelimo izbrisati
		try (BufferedReader reader = new BufferedReader(new FileReader("userIBANlist.txt"))) {
			String tmpUser = null;
			String tmpIBAN = null;

			tmpIBAN = reader.readLine();
			while (tmpIBAN != null && tmpIBAN.equals("") == false) {
				tmpUser = reader.readLine();

				if (tmpUser.equals(deletedUsername) == false) {
					updatedListIBANs.add(tmpIBAN);
					updatedListUsernames.add(tmpUser);
				}

				tmpIBAN = reader.readLine();
			}
		}
		catch (IOException e) {
			System.out.println("Nije moguce citati iz datoteke 'userIBANlist.txt'! Nesto nije dobro u updateUserIBANList! ");
		}

		// zapisi liste u "userIBANlist.txt" koje ne sadrze podatke o izbrisanom korisniku
		try (FileWriter writer = new FileWriter("userIBANlist.txt", false)) {
			for (int i = 0; i < updatedListIBANs.size(); i++) {
				writer.write(updatedListIBANs.get(i) + "\n");
				writer.write(updatedListUsernames.get(i) + "\n");
			}
		}
		catch (IOException e) {
			System.out.println("Nije moguce pisati u datoteku 'userIBANlist.txt'! Nesto nije dobro u updateUserIBANList! ");
		}
	}



	// dohvati podatke o racunu
	static BankAccount getAccount(String username) {
		String IBAN = null;
		double balance = 0;
		String value = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
			// preskoci lozinku
			reader.readLine();
			// procitaj podatke o racunu
			IBAN = reader.readLine();
			balance = Double.parseDouble(reader.readLine());
			value = reader.readLine();
		} catch (IOException | NumberFormatException e) {
			System.out.println(e);
		}

		return new BankAccount(IBAN, balance, value);
	}



	// azururanje stanja na racunu
	void updateAccount(User user) throws IOException {
		String ePassword = user.getEPassword();
		try(FileWriter writer = new FileWriter(user.username + ".txt", false)) {
			// zapisi nove podatke
			writer.write(ePassword + "\n");
			writer.write(IBAN + "\n" + balance + "\n" + value + "\n");
		}
	}

	void updateAccount(String username) throws IOException {
		String ePassword = User.getEPassword(username);
		try(FileWriter writer = new FileWriter(username + ".txt", false)) {
			// zapisi nove podatke
			writer.write(ePassword + "\n");
			writer.write(IBAN + "\n" + balance + "\n" + value + "\n");
		}
	}

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
