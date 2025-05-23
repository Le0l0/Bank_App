package bank;

import java.time.LocalDate;




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
