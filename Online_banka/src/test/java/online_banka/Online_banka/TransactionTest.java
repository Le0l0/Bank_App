package online_banka.Online_banka;

// za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// ostalo
import java.time.LocalDate;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionTest
{	
	private static String payerIBAN = "HR123";
	private static String recipientIBAN = "HR321";
	private static double amount = 10.0;
	private static LocalDate date = LocalDate.now();
	
	

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
	// konstruktori
	public void test_Transaction() {
		Transaction transaction = new Transaction();
		Transaction transactionFilled = new Transaction(payerIBAN, recipientIBAN, amount, date);
		
		assertAll(
			"Transaction",
			() -> assertEquals(null, transaction.payer),
			() -> assertEquals(null, transaction.recipient),
			() -> assertEquals(0, transaction.amount),
			() -> assertEquals(null, transaction.date),
			
			() -> assertEquals(payerIBAN, transactionFilled.payer),
			() -> assertEquals(recipientIBAN, transactionFilled.recipient),
			() -> assertEquals(amount, transactionFilled.amount),
			() -> assertEquals(date, transactionFilled.date)
		);
	}

}
