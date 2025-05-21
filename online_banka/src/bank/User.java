package bank;

// import java.util.Scanner;
import java.io.*;
// import java.security.*;

public class User
{
	String username;
	long balance;
	

	
	public User(String username, String password) {
		this.username = username;
		this.balance = 0;
		try {
			createFiles(this.username, this.balance, password);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public User(String username, long balance) {
		this.username = username;
		this.balance = 0;
	}
	
	
	
	// kreiranje datoteka za spremanje podataka o korisniku i njegovom racunu
	public void createFiles(String username, long balance, String password) throws IOException {
		try(FileWriter writer = new FileWriter(username + ".txt", false))
		{
			writer.write(password + "\n" + balance + "\n");
		}
		try
		{
			File user_history = new File(username + "_history.txt");
			user_history.createNewFile();
		} finally {}
	}

}
