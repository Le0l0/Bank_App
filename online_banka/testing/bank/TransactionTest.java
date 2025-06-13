package bank;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionTest
{
	String IBAN = "HR123";
	double amount = 10.0;
	LocalDate date = LocalDate.now();
	
	

	@BeforeAll
	public static void TEST_initialize() {
		// nista
	}
	
	@AfterAll
	public static void TEST_clean_up() {
		// nista
	}
	
	@Test
	@Order(01)
	public void test_Transaction() {
		Transaction transaction = new Transaction();
		Transaction transactionFilled = new Transaction(IBAN, amount, date);
		
		assertAll(
			"Transaction",
			() -> assertEquals(null, transaction.IBAN),
			() -> assertEquals(0, transaction.amount),
			() -> assertEquals(null, transaction.date),
			() -> assertEquals(IBAN, transactionFilled.IBAN),
			() -> assertEquals(amount, transactionFilled.amount),
			() -> assertEquals(date, transactionFilled.date)
		);
	}

}
