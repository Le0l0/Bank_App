package online_banka.Online_banka;

import java.time.LocalDate;
import java.util.Comparator;




class TransactionComparator implements Comparator<Transaction>
{
	public int compare(Transaction t1, Transaction t2) {
		return t1.amount > t2.amount ? -1 : 1;
	}
	
}



public class Transaction
{
	protected String payer;
	protected String recipient;
	protected double amount;
	protected LocalDate date;
	
	
	// konstruktori
	public Transaction() {
		this.payer = null;
		this.recipient = null;
		this.amount = 0;
		this.date = null;
	}
	
	public Transaction(String payer, String recipient, double amount, LocalDate date) {
		this.payer = payer;
		this.recipient = recipient;
		this.amount = amount;
		this.date = date;
	}
	
	public void printTransaction(String userIBAN, String username) {
		System.out.println("\nPlatitelj:\t" + payer + (userIBAN.equals(payer) ? " (vi)" : ""));
		System.out.println("Primatelj:\t" + recipient + (userIBAN.equals(recipient) || username.equals(recipient) ? " (vi)" : ""));
		System.out.println("Iznos:\t\t" + amount);
		System.out.println("Datum:\t\t" + date);
	}
}
