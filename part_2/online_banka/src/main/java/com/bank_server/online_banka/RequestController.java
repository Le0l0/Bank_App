package com.bank_server.online_banka;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
////////////////////////////////////////////////////////////////
import java.util.ArrayList;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


record UserInfo(String username, String password, char encryption) {}
record BankAccR(String IBAN, double balance, String value) {}
record TransactionInfo(String recipient, double amount) {}
record TransactionM(String payer, String recipient, double amount, LocalDate date) {}




@RestController
public class RequestController
{
	String state = "hello";
	
	@GetMapping("/")
	public String serverState() {
		return state;
	}
	
	
	@GetMapping("/users/{username}/exists")
	public boolean checkUserExists(@PathVariable String username) {
		return User.userExists(username);
	}
	
	@PostMapping("/users/registration")
	public boolean registerUser(@RequestBody UserInfo userInfo) {
		return User.registration(userInfo.username(), userInfo.password(), userInfo.encryption());
	}
	
	@PostMapping("/users/login")
	public boolean loginUser(@RequestBody UserInfo userInfo) {
		return User.login(userInfo.username(), userInfo.password());
	}
	
	@GetMapping("/users/{username}/account-details")
	public BankAccR getAccDetails(@PathVariable String username) {
		BankAccount acc = BankAccount.getAccount(username);
		return new BankAccR(acc.IBAN, acc.balance, acc.value);
	}
	
	@PostMapping("/users/{username}/make-payment")
	public Integer makePayment(@PathVariable String username, @RequestBody double payment) {
		return User.makePayment(username, payment);
	}
	
	@PostMapping("/users/{username}/check-password")
	public boolean checkPassword(@PathVariable String username, @RequestBody String passwordAttempt) {
		return Encryption.testPassword(passwordAttempt, User.getEPassword(username));
	}
	
	@DeleteMapping("/users/{username}")
	public void deleteUser(@PathVariable String username) {
		if (User.deleteFiles(username) == false)
			System.out.println(" !!! DATOTEKE KORISNIKA " + username + " NISU USPJESNO OBRISANE !!! ");
	}
	
	@PostMapping("/users/{username}/make-transaction")
	public Integer makeTransaction(@PathVariable String username, @RequestBody TransactionInfo transactionInfo) {
		return User.makeTransaction(username, transactionInfo.recipient(), transactionInfo.amount());
	}
	
//	@GetMapping("/users/{username}/history")
//	public ArrayList<TransactionM> getAccHistory(@PathVariable String username) {
//		return Transaction.loadTransactionList(username);
//	}
	
	@GetMapping("/users/{username}/history")
	public ResponseEntity<ArrayList<TransactionM>> getAccHistory(@PathVariable String username) {
		return new ResponseEntity<ArrayList<TransactionM>>(Transaction.loadTransactionList(username), HttpStatus.OK);
	}

}
