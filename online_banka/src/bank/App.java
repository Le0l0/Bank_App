package bank;

import java.util.Scanner;
import java.io.*;

public class App
{
	public static Scanner scanner = new Scanner(System.in);
	
	
	
	public static String getInput(int maxLength, boolean noSpace) {
		String input = null;
		
		while (true) {
			input = scanner.nextLine();
			if (input.length() > 0 && input.length() <= maxLength && (!noSpace || !input.contains(" "))) return input;
			else {
				System.out.print("Unos nesmije biti dulji od " + maxLength);
				if (noSpace) System.out.println(" i ne smije sadrzavati razmake. ");
				else System.out.println(". ");
			}
		}
	}
	
	
	
	public static User registration() {
		String username;
		String password;
		
		// unos korisnickog imena i lozinke
		while (true) {
			System.out.println("Username: ");
//			while (true) {
				//username = scanner.nextLine();
				username = getInput(32, true);
				
//				if (username.contains(" ") || username.length() > 32) {
//					System.out.println("Korisnicko ime nesmije sadrzavati razmake i biti dulje od 32 znaka! Pokusajte ponovo. ");
//				}
//				else break;
//			}
			System.out.println("Password: ");
//			while (true) {
				//password = scanner.nextLine();
				password = getInput(32, false);
			
//				if (password.length() > 32) {
//					System.out.println("Lozinka nesmije biti dulja od 32 znaka! Pokusajte ponovo. ");
//				}
//				else break;
//			}
			System.out.println("Confirm Password: ");
			if (password.equals(getInput(32, false)) == true) {
				break;
			}
			else {
				System.out.println("Registracija neuspjesna unesite 'e' za izlaz iz aplikacije ili bilo koji drugi znak kako bi ponovo pokusali. ");
				String choice = getInput(32, false);
				if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {	// izlaz
					return null;
				}
			}
		}
		
		return new User(username, password);
	}
	
	
	
	public static User login() {
		String username;
		String passwordAttempt;
		String password;
		long balance = 0;
		boolean success = false;
		
		while (true) {
			// unos korisnickog imena i lozinke
			System.out.println("Username: ");
			while (true) {
				username = scanner.nextLine();
				//username = getInput();
				
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
			// projvjera ispravnosti unesenih podataka
			File testfile = new File(username + ".txt");
			if (testfile.isFile() == true) {
				try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
					password = reader.readLine();
					if (password.equals(Encryption.getMD5(passwordAttempt))) {
						success = true;
					}
					balance = Long.parseLong(reader.readLine());
					System.out.println("PassAttMD5:\t" + Encryption.getMD5(passwordAttempt) + ", PassAtt: " + passwordAttempt);
					System.out.println("Pass MD5:\t" + password);
				} catch (IOException | NumberFormatException e) {
					System.out.println(e);
				}
			}
			
			if (success == true) break;
			else {
				System.out.println("Korisnicko ime ili lozinka su pogresni, pokusajte ponovo, ili unesite 'e' za izlaz iz aplikacije. ");
				String choice = scanner.nextLine();
				if (choice.length() > 0 && (choice.charAt(0) == 'e' || choice.charAt(0) == 'E')) {	// izlaz
					return null;
				}
			}
			
		}
		
		return new User(username, balance);
	}
	
	
	// izbornik za prijavu u aplikaciju
	public static User welcomeLogin() {
		String choice = null;
		User user = null;
		
		// prijava ili registracija u sustav
		System.out.println("r - registracija, p - prijava, e - izlaz");
		while (true) {
			choice = scanner.nextLine();
			
			if (choice.length() <= 0) continue;
			if (choice.charAt(0) == 'r' || choice.charAt(0) == 'R') {		// registracija
				user = registration();
				break;
			}
			else if (choice.charAt(0) == 'p' || choice.charAt(0) == 'P') {	// prijava
				user = login();
				break;
			}
			else if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {	// izlaz
				break;
			}
			else {															// krivi unos
				System.out.println("Molimo unesite 'r' za registraciju, 'p' za prijavu, ili 'e' za izlaz iz aplikacije. ");
			}
		}
		
		return user;
	}
	
	
	
	public static void main(String[] args)
	{
		//App app = new App();
		
		System.out.println();
		System.out.println("################################################################");
		System.out.println("                       Online Bankarstvo                        ");
		System.out.println("################################################################");
		System.out.println();
		
		
		// korisnik se prijavljuje
		User user = App.welcomeLogin();
		if (user != null) {
			System.out.println(user.username + "\n" + user.balance);
		}
		else {								// izlaz iz programa
			System.out.println("Bye! ");
			return;
		}

		// glavna beskonacna petlja - menu aplikacije
		String choice = null;
		while (true) {
			System.out.println("e - odjava");
			choice = scanner.nextLine();
			
			if (choice.length() <= 0) continue;
			if (choice.charAt(0) == 'e' || choice.charAt(0) == 'E') {	// odjava
				System.out.println("Jeste li sigurni da se zelite odjaviti od aplikacije? ");
				choice = scanner.nextLine();
				//if ()
				break;
			}
		}
		
		
		scanner.close();
		return;
	}

}
