package online_banka.Online_banka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;




public class BankAccount
{
	String IBAN;
	public double balance;
	public String value;
	private static int lastIBAN = 0;
	
	
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
	
	
	
	// metoda za brisanje IBAN-a i korisnickog imena iz datoteke 'userIBANlist.txt' kada korisnik brise svoj racun
	public static void updateUserIBANList(String deletedUsername) {
		ArrayList<String> updatedListIBANs = new ArrayList<String>();
		ArrayList<String> updatedListUsernames = new ArrayList<String>();
		
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
	
	// azuriraj podatke o racunu koristeci samo username i dodaj iznos 'amount' na racun
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
