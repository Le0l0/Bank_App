package bank;

import java.util.Scanner;
import java.io.*;

public class App
{
	public static Scanner scanner = new Scanner(System.in);
	
	
	
	public User registration() {
		String username;
		String password;
		
		System.out.println("Username: ");
		while (true) {
			username = scanner.nextLine();
			
			if (username.contains(" ") || username.length() > 32) {
				System.out.println("Korisnicko ime nesmije sadrzavati razmake i biti dulje od 32 znaka! Pokusajte ponovo. ");
			}
			else break;
		}
		
		System.out.println("Password: ");
		while (true) {
			password = scanner.nextLine();
			
			if (password.length() > 32) {
				System.out.println("Lozinka nesmije biti dulja od 32 znaka! Pokusajte ponovo. ");
			}
			else break;
		}
		
		User new_user = new User(username, password);
		
		return new_user;
	}
	
	
	
	public User login() {
		String username;
		String passwordAttempt;
		String password;
		long balance = 0;
		boolean success = false;
		String tmps = "";
		
		while (true) {
			
			System.out.println("Username: ");
			while (true) {
				username = scanner.nextLine();
				
				if (username.contains(" ") || username.length() > 32) {
					System.out.println("Korisnicko ime nesmije sadrzavati razmake i biti dulje od 32 znaka! Pokusajte ponovo. ");
				}
				else break;
			}
			
			System.out.println("Password: ");
			while (true) {
				passwordAttempt = scanner.nextLine();
				
				if (passwordAttempt.length() > 32) {
					System.out.println("Lozinka nesmije biti dulja od 32 znaka! Pokusajte ponovo. ");
				}
				else break;
			}
			
			File testfile = new File(username + ".txt");
			if (testfile.exists()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
					password = reader.readLine();
					// if (passwords) {success = true;}
					balance = Long.parseLong(reader.readLine());
				} catch (IOException e) {
					System.out.println(e);
				}
			}
			
			if (success = true) break;
			else {
				System.out.println("Korisnicko ime ili lozinka su pogresni, pokusajte ponovo. ");
			}
			
		}
		
		User user = new User(username, balance);
		
		return user;
	}
	
	
	
	public User welcomeLogin() {
		// Scanner scanner = new Scanner(System.in);
		String choice = "";
		User user = null;
		
		System.out.println();
		System.out.println("################################################################");
		System.out.println("                       Online Bankarstvo                        ");
		System.out.println("################################################################");
		System.out.println();
		
		// prijava ili registracija u sustav
		System.out.println("r - registracija, p - prijava");
		while (user == null) {
			choice = scanner.nextLine();
			
			if (choice.charAt(0) == 'r' || choice.charAt(0) == 'R') {		// registracija
				user = registration();
			}
			else if (choice.charAt(0) == 'p' || choice.charAt(0) == 'P') {	//prijava
				// User user = login();
				user = new User("Hrvoje", "12345678");
			}
			else {															// krivi unos
				System.out.println("Molimo unesite 'r' za registraciju ili 'p' za prijavu. ");
			}
		}
		return user;
	}
	
	
	
	public static void main(String[] args)
	{
		App app = new App();
		
		// korisnik se prijavljuje
		User user = app.welcomeLogin();
		System.out.println(user.username + "\n" + user.balance);
		
		
		
		scanner.close();
		return;
	}
}
