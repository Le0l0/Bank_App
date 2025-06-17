package online_banka.Online_banka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;




public class BankAccount
{
	String IBAN;
	public double balance;
	public String value;
	private static int lastIBAN = 0;
	
	
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
	
	
	
	// isto kao za lastNumber, samo za IBAN
	public static void initializeIBAN() throws IOException, NumberFormatException {
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
	
	// isto kao za lastNumber, samo za IBAN
	public static void writeLastIBAN() throws IOException {
		FileWriter writer = new FileWriter("lastIBAN.txt", false);
		writer.write(Integer.toString(lastIBAN));
		writer.close();
	}
	
	// vraca novi IBAN
	public static String getNewIBAN() {return "HR" + String.format("%032d", lastIBAN++);}
	
	
	
//	// stvori userIBANlist.txt ako ne postoji
//	public static void createNeccesaryFiles() {
//		File userIBANlist = new File("userIBANlist.txt");
//		
//		try {
//			userIBANlist.createNewFile();
//		} catch (IOException e) {
//			System.out.println("bo");
//		}
//	}
	
	
	
	// dohvati podatke o racunu
	public static BankAccount getAccount(User user) {
		String IBAN = null;
		double balance = 0;		
		String value = null;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(user.username + ".txt"))) {
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
	public void updateAccount(User user) throws IOException {
		String ePassword = user.getEPassword();
		try(FileWriter writer = new FileWriter(user.username + ".txt", false)) {
			// zapisi nove podatke
			writer.write(ePassword + "\n");
			writer.write(IBAN + "\n" + balance + "\n" + value + "\n");
		}
	}
	
	// azuriraj podatke o racunu koristeci samo username
	public static void updateAccount(String username, double amount) throws IOException {
		String IBAN = null;
		double balance = 0;
		String value = null;
		
		// procitaj podatke o racunu
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
		
		// zapisi nove podatke
		String ePassword = User.getEPassword(username);
		try(FileWriter writer = new FileWriter(username + ".txt", false)) {
			// zapisi nove podatke
			writer.write(ePassword + "\n");
			writer.write(IBAN + "\n" + (balance + amount) + "\n" + value + "\n");
		}
	}
		
}
