package com.bank_server.online_banka;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.Statement;
//import java.sql.SQLException;
//import java.io.File;
//import java.util.List;
//import org.hibernate.Query;
import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.AnnotationConfiguration;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;




// kopirano sa GeeksForGeeks i modificirano
class BankDB
{
	private static final SessionFactory sessionFactory = buildSessionFactory();
	
	
	
	private static SessionFactory buildSessionFactory() {
		try {
			// We need to create the SessionFactory from
			// hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might
			// be swallowed
			// In case of any exception, it has to be
			// indicated here
			System.out.println("SESSIONFACTORY CREATION FAILED " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	
	
	public static SessionFactory getSessionFactory() {return sessionFactory;}

	public static void shutdown() {getSessionFactory().close();}
	
	
	
	static void saveUser(UserDB user) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		
		session.persist(user);
		
		session.getTransaction().commit();
		session.close();
	}
	
	static UserDB findUserByUsername(String username) {
		Session session = getSessionFactory().openSession();
		
		UserDB user = session.createQuery("FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username).uniqueResult();
		
		session.close();
		return user;
	}
	
	static void deleteUserByUsername(String username) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		
		session.createQuery("DELETE FROM UserDB WHERE username = :username", UserDB.class).setParameter("username", username);
		
		session.getTransaction().commit();
		session.close();
	}
	
	static boolean existsUserByUsername(String username) {
		return findUserByUsername(username) != null ? true : false;
	}
	
	
	static void saveAcc(BankAccDB acc) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		
		session.persist(acc);
		
		session.getTransaction().commit();
		session.close();
	}
	
	static BankAccDB findAccByOwner(String owner) {
		Session session = getSessionFactory().openSession();
		BankAccDB acc = session.createQuery("FROM BankAccDB WHERE owner = :owner", BankAccDB.class).setParameter("owner", owner).uniqueResult();
		session.close();
		return acc;
	}
	
	static void updateAcc(BankAccDB acc) {
		Session session = getSessionFactory().openSession();
		session.beginTransaction();
		
		session.merge(acc);
		//session.createQuery("UPDATE BankAccDB SET balance = :newBalance WHERE id = :id", BankAccDB.class).setParameter("newBalance", newBalance).setParameter("id", id);
		
		session.getTransaction().commit();
		session.close();
	}
	
}




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ispod su klase koje reprezentiraju retke u tablicama
// kopirano i modificirano sa stranice "https://hackernoon.com/using-postgres-effectively-in-spring-boot-applications"
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@Table(name = "users")
class UserDB implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String username;
	private String ePassword;
	
	// hibernate expects entities to have a no-arg constructor, though it does not necessarily have to be public
	UserDB() {}
	public UserDB(String username, String ePassword) {
		this.username = username;
		this.ePassword = ePassword;
	}

	// getteri
	public Integer getId()			 			{return this.id;}
	public String getUsername() 				{return this.username;}
	public String getEPassword() 				{return this.ePassword;}
	
	// setteri
	public void setId(Integer id) 				{this.id = id;}
	public void setUsername(String username) 	{this.username = username;}
	public void setEPassword(String ePassword) 	{this.ePassword = ePassword;}

}


@Entity
@Table(name = "accounts")
class BankAccDB implements Serializable
{
	private static final long serialVersionUID = 1L;
	
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
	public Integer getId() 						{return this.id;}
	public String getOwner() 					{return this.owner;}
	public String getIban() 					{return this.iban;}
	public double getBalance()	 				{return this.balance;}
	public String getValue()	 				{return this.value;}
	
	// setteri
	public void setId(Integer id) 				{this.id = id;}
	public void setOwner(String owner) 			{this.owner = owner;}
	public void setIban(String iban) 			{this.iban = iban;}
	public void setBalance(double balance)	 	{this.balance = balance;}
	public void setValue(String value)	 		{this.value = value;}

}


@Entity
@Table(name = "transactions")
class TransactionDB implements Serializable
{
	private static final long serialVersionUID = 1L;
	
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
	public Integer getId() 						{return this.id;}
	public String getPayer() 					{return this.payer;}
	public String getRecipient() 				{return this.recipient;}
	public double getAmount()	 				{return this.amount;}
	public LocalDate getDate() 					{return this.date;}
	
	// setteri
	public void setId(Integer id) 				{this.id = id;}
	public void setPayer(String payer) 			{this.payer = payer;}
	public void setRecipient(String recipient)	{this.recipient = recipient;}
	public void setAmount(double amount)	 	{this.amount = amount;}
	public void setDate(LocalDate date) 		{this.date = date;}

}
