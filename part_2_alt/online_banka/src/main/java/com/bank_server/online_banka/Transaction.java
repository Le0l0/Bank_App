package com.bank_server.online_banka;

import java.util.ArrayList;
import java.util.List;




class Transaction
{
	// metoda koja ucitava sve korisnikove transakcije u ArrayList i vraca tu listu
	static ArrayList<TransactionM> getTransactionList(String username) {
		// dohvati IBAN od user-a
		String iban = null;
		try {iban = BankAccount.ownerToIban(username);}
		catch (Exception e) {/* nista */}
		
		// ucitaj transackije u liste i deklariraj ArrayList koji cemo vratiti
		List<TransactionDB> tmp1 = BankDB.findTransByPayer(iban);
		List<TransactionDB> tmp2 = BankDB.findTransByRecipient(iban);
		ArrayList<TransactionM> list = new ArrayList<TransactionM>();
		
		// prebaci sve objekte iz liste 2 u listu 1
		tmp2.forEach(TransactionDB -> tmp1.add(TransactionDB));
		// sve elemente iz liste 1 prebaci u ArrayList koji vracamo
		tmp1.forEach(TransactionDB -> list.add(new TransactionM(TransactionDB.getPayer(), TransactionDB.getRecipient(), TransactionDB.getAmount(), TransactionDB.getDate())));
		
		return list;
	}
}
