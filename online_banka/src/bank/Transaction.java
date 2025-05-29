package bank;

import java.time.LocalDate;
import java.util.Comparator;




public class Transaction
{
	String IBAN;
	double amount;
	LocalDate date;
	
	
	// konstruktori
	public Transaction() {
		this.IBAN = null;
		this.amount = 0;
		this.date = null;
	}
	
	public Transaction(String IBAN, double amount, LocalDate date) {
		this.IBAN = IBAN;
		this.amount = amount;
		this.date = date;
	}
}



class TransactionComparator implements Comparator<Transaction>
{
	
	public int compare(Transaction t1, Transaction t2) {
		return t1.amount > t2.amount ? -1 : 1;
	}
	
}
