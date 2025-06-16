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
	String payerIBAN;
	String recipientIBAN;
	double amount;
	LocalDate date;
	
	
	// konstruktori
	public Transaction() {
		this.payerIBAN = null;
		this.recipientIBAN = null;
		this.amount = 0;
		this.date = null;
	}
	
	public Transaction(String payerIBAN, String recipientIBAN, double amount, LocalDate date) {
		this.payerIBAN = payerIBAN;
		this.recipientIBAN = recipientIBAN;
		this.amount = amount;
		this.date = date;
	}
}
