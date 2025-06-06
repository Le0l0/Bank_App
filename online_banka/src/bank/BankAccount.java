package bank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;




public class BankAccount
{
	public int accNumber;
	public double balance;
	public String value;
	public static int lastNumber = -1;
	
	
	public BankAccount() {
		this.accNumber = 0;
		this.balance = 0;
		this.value = "KN";
	}
	
	public BankAccount(int accNumber, double balance, String value) {
		this.accNumber = accNumber;
		this.balance = balance;
		this.value = value;
	}
	
	
	
	// dohvati zadnji koristeni broj racuna, ili ako se nikad nije koristio stvori datoteku u koju se zapisuje
	public static void initializeLastNumber() throws IOException, NumberFormatException {
		File testfile = new File("lastNumber.txt");
		if (testfile.isFile() == true) {
			try (BufferedReader reader = new BufferedReader(new FileReader("lastNumber.txt"))) {
				lastNumber = Integer.parseInt(reader.readLine());
			}
		}
		else {
			try (FileWriter writer = new FileWriter("lastNumber.txt", false))
			{
				lastNumber = 1;
				writer.write(Integer.toString(lastNumber));
			}
		}
	}
	
	// zapisi zadnji koristeni broj racuna
	public static void writeLastNumber() throws IOException {
		try(FileWriter writer = new FileWriter("lastNumber.txt", false))
		{
			writer.write(Integer.toString(lastNumber));
		}
	}
	
	
	
	// dohvati podatke o racunu
	public static BankAccount getAccount(User user) {
		int accNum = -1;
		double balance = 0;		
		String value = null;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(user.username + ".txt"))) {
			// preskoci lozinku
			reader.readLine();
			// procitaj podatke o racunu
			accNum = Integer.parseInt(reader.readLine());
			balance = Double.parseDouble(reader.readLine());
			value = reader.readLine();
		} catch (IOException | NumberFormatException e) {
			System.out.println(e);
		}
		
		return new BankAccount(accNum, balance, value);
	}
		
	
	
	// azururanje stanja na racunu
	public void updateAccount(User user) throws IOException {
		String ePassword = user.getEPassword();
		try(FileWriter writer = new FileWriter(user.username + ".txt", false)) {
			// zapisi nove podatke
			writer.write(ePassword + "\n");
			writer.write(accNumber + "\n" + balance + "\n" + value + "\n");
		}
	}
		
}
