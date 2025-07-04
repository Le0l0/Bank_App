package com.bank_server.online_banka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;




public class BankDB
{
      static void createTables() throws SQLException {
    	  Connection conn = null;
    	  Statement statement = null;
    	  String query = null;
    		  
    	  conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bankdb", "bank_admin", "pass");
		  System.out.println("OTVORIO BAZU PODATAKA");
		  statement = conn.createStatement();
		 
		  query = "CREATE TABLE IF NOT EXISTS users(" +
				  "iban VARCHAR(34), " +
				  "username VARCHAR(32), " +
				  "epassword VARCHAR(64), " +
				  "balance FLOAT8" +
				  ");";
		  statement.execute(query);
		 
		  query = "CREATE TABLE IF NOT EXISTS transactions(" +
				  "payer VARCHAR(34), " +
				  "recipient VARCHAR(34), " +
				  "amount FLOAT8, " +
				  "date DATE" +
				  ");";
		  statement.execute(query);
		 
		  statement.close();
		  conn.close();
      }

}




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ispod su klase koje reprezentiraju retke u tablicama i interfaceovi za upravljanje tablicama
// kopirano i modificirano sa stranice "https://hackernoon.com/using-postgres-effectively-in-spring-boot-applications"
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@Table(name = "users")
class UserDB
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String username;
	private String ePassword;
	private double balance;
	
	// hibernate expects entities to have a no-arg constructor, though it does not necessarily have to be public
	UserDB() {}
	public UserDB(String username, String ePassword, double balance) {
		this.username = username;
		this.ePassword = ePassword;
		this.balance = balance;
	}

	// getteri
	public Integer getId()			 			{return this.id;}
	public String getUsername() 				{return this.username;}
	public String getEPassword() 				{return this.ePassword;}
	public double getBalance() 					{return this.balance;}
	
	// setteri
	public void setId(Integer id) 				{this.id = id;}
	public void setUsername(String username) 	{this.username = username;}
	public void setEPassword(String ePassword) 	{this.ePassword = ePassword;}
	public void setBalance(double balance) 		{this.balance = balance;}

}

interface UserRepo extends CrudRepository<UserDB, Integer> {
	// napisi deklaracije metoda koje ce nam trebati - kod generira JPA
	boolean existsByUsername(String username);
	UserDB findByUsername(String username);
	void deleteByUsername(String username);
}




@Entity
@Table(name = "transactions")
class TransactionDB
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String payer;
	private String recipient;
	private double amount;
	private LocalDate date;
	
	// hibernate expects entities to have a no-arg constructor, though it does not necessarily have to be public
	TransactionDB() {}
	public TransactionDB(String payer, String recipient, double amount, LocalDate date) {
		this.payer = payer;
		this.recipient = recipient;
		this.amount = amount;
		this.date = date;
	}

	// getteri
	public Integer getId() 			{return this.id;}
	public String getPayer() 		{return this.payer;}
	public String getRecipient() 	{return this.recipient;}
	public double getAmount()	 	{return this.amount;}
	public LocalDate getDate() 		{return this.date;}

}

interface TransactionRepo extends CrudRepository<TransactionDB, Integer> {
	// napisi deklaracije metoda koje ce nam trebati - kod generira JPA
	TransactionDB findByPayer(String payer);
	List<TransactionDB> findAllByPayer(String payer);
	List<TransactionDB> findAllByRecipient(String recipient);
}




@Entity
@Table(name = "accounts")
class BankAccDB
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String owner;
	private String iban;
	private double balance;
	private String value;
	
	// hibernate expects entities to have a no-arg constructor, though it does not necessarily have to be public
	BankAccDB() {}
	public BankAccDB(String owner, String iban, double balance, String value) {
		this.owner = owner;
		this.iban = iban;
		this.balance = balance;
		this.value = value;
	}

	// getteri
	public Integer getId() 			{return this.id;}
	public String getOwner() 		{return this.owner;}
	public String getIban() 		{return this.iban;}
	public double getBalance()	 	{return this.balance;}
	public String getValue()	 	{return this.value;}

}

interface BankAccRepo extends CrudRepository<BankAccDB, Integer> {
	// napisi deklaracije metoda koje ce nam trebati - kod generira JPA
	BankAccDB findByOwner(String owner);
}
