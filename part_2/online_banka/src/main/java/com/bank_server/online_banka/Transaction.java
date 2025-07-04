package com.bank_server.online_banka;

//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;




class Transaction
{
	// metoda koja ucitava sve korisnikove transakcije u ArrayList i vraca tu listu
	static ArrayList<TransactionM> getTransactionList(String username) {
//		ArrayList<TransactionM> list = new ArrayList<TransactionM>();
//		
//		String payerIBAN = null;
//		String recipientIBAN = null;
//		double amount = 0;
//		LocalDate date = null;
//		
//		// ucitavaj transakcije iz datoteke u 'transactionList'
//		String tmpS = null;
//		try (BufferedReader reader = new BufferedReader(new FileReader(username + "_history.txt"))) {
//			payerIBAN = reader.readLine();
//			while (payerIBAN != null && !payerIBAN.equals("")) {
//				recipientIBAN = reader.readLine();
//				tmpS = reader.readLine();
//				amount = Double.parseDouble(tmpS);
//				tmpS = reader.readLine();
//				date = LocalDate.parse(tmpS);
//				
//				list.add(new TransactionM(payerIBAN, recipientIBAN, amount, date));
//				
//				// preskoci liniju
//				tmpS = reader.readLine();
//				// ucitaj sljedeci IBAN
//				payerIBAN = reader.readLine();
//			}
//		}
//		catch (IOException e) {
//			System.out.println("Greska: " + e.getMessage());
//		}
		
		List<TransactionDB> tmp1 = OnlineBankServer.transactionRepo.findAllByPayer(username);
		List<TransactionDB> tmp2 = OnlineBankServer.transactionRepo.findAllByRecipient(username);
		ArrayList<TransactionM> list = new ArrayList<TransactionM>();
		
		// prebaci sve objekte iz liste 2 u listu 1
		tmp2.forEach(TransactionDB -> tmp1.add(TransactionDB));
		// sve elemente iz liste 1 prebaci u ArrayList koji vracamo
		tmp1.forEach(TransactionDB -> list.add(new TransactionM(TransactionDB.getPayer(), TransactionDB.getRecipient(), TransactionDB.getAmount(), TransactionDB.getDate())));
		
		return list;
	}
}
