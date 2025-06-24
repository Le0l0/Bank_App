package com.bank_server.online_banka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;




class Transaction
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
	
	
	
	public String getPayer() {return payer;}
	public String getRecipient() {return recipient;}
	public double getAmount() {return amount;}
	public LocalDate getDate() {return date;}
	
	
	
	// metoda koja vraca ArrayList u kojoj su sve transakcije korisnika
	static ArrayList<TransactionM> loadTransactionList(String username) {
		ArrayList<TransactionM> list = new ArrayList<TransactionM>();
		
		String payerIBAN = null;
		String recipientIBAN = null;
		double amount = 0;
		LocalDate date = null;
		
		// ucitavaj transakcije iz datoteke u 'transactionList'
		String tmpS = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(username + "_history.txt"))) {
			payerIBAN = reader.readLine();
			while (payerIBAN != null && !payerIBAN.equals("")) {
				recipientIBAN = reader.readLine();
				tmpS = reader.readLine();
				amount = Double.parseDouble(tmpS);
				tmpS = reader.readLine();
				date = LocalDate.parse(tmpS);
				
				list.add(new TransactionM(payerIBAN, recipientIBAN, amount, date));
				
				// preskoci liniju
				tmpS = reader.readLine();
				// ucitaj sljedeci IBAN
				payerIBAN = reader.readLine();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
		
		return list;
	}
}
