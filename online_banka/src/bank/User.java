package bank;

import java.io.*;

public class User
{
	String username;
	long balance;
	

	// registracija novog korisnika
	public User(String username, String password) {
		this.username = username;
		this.balance = 0;
			try {
				createFiles(this.username, this.balance, password);
			} catch (IOException e) {
				System.out.println(e);
			}
	}
	// normalan konstruktor
	public User(String username, long balance) {
		this.username = username;
		this.balance = balance;
	}
	
	
	
	// kreiranje datoteka za spremanje podataka o korisniku i njegovom racunu
	public void createFiles(String username, long balance, String password) throws IOException {
		try(FileWriter writer = new FileWriter(username + ".txt", false))
		{
			writer.write(Encryption.getMD5(password) + "\n" + balance + "\n");
		}
		try
		{
			File user_history = new File(username + "_history.txt");
			user_history.createNewFile();
		} finally {}
	}

}
