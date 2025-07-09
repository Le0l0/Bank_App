package com.bank_server.online_banka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.List;




class BankAccount
{
	private Integer id;
	private String IBAN;
	double balance;
	private String value;
	private static int lastIBAN = -1;



	// konstruktori
	public BankAccount(Integer id, String IBAN, double balance, String value) {
		this.id = id;
		this.IBAN = IBAN;
		this.balance = balance;
		this.value = value;
	}
	
	public BankAccount(BankAccDB accDB) {
		this.id = accDB.getId();
		this.IBAN = accDB.getIban();
		this.balance = accDB.getBalance();
		this.value = accDB.getValue();
	}
	
	// getteri
	Integer getId()			{return this.id;}
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



	// dohvati podatke o racunu preko korisnickog imena
	static BankAccount getAccountByOwner(String owner) throws Exception {
		BankAccDB tmpdbacc = BankDB.findAccByOwner(owner);
		return new BankAccount(tmpdbacc);
	}
	
	// dohvati podatke o racunu preko IBAN-a
	static BankAccount getAccountByIban(String iban) throws Exception {
		BankAccDB tmpdbacc = BankDB.findAccByIban(iban);
		return new BankAccount(tmpdbacc);
	}



	// zapisi podatke iz objekta u bazu podataka
	void updateAccount(String owner) {
		BankAccDB newAcc = new BankAccDB(owner, this.IBAN, this.balance, this.value);
		newAcc.setId(this.id);
		BankDB.updateAcc(newAcc);
	}
	
	
	
	// pokusaj dohvatiti IBAN racuna koristeci ime vlasnika, ako ne uspije baca Exception
	static String ownerToIban(String owner) throws Exception {
		owner = BankDB.findAccByOwner(owner).getIban();
		return owner;
	}
	
	
	
	// obrisi racun iz baze podataka
	static void deleteAccs(String owner) {
//		List<BankAccDB> accList = BankDB.findAccsByOwner(owner);
//		accList.forEach(BankAccDB -> BankDB.deleteAcc(BankAccDB));
		BankDB.deleteAcc(BankDB.findAccByOwner(owner));
	}
	
	
	
	BankAccM toMessage() {
		return new BankAccM(this.IBAN, this.balance, this.value);
	}

}
