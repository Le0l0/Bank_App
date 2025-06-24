package online_banka_client.bank_client;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
// ostalo
import java.util.ArrayList;
import java.time.LocalDate;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionTest
{
	private static String payerIBAN = "HR123";
	private static String recipientIBAN = "HR321";
	private static double amount = 10.0;
	private static LocalDate date = LocalDate.now();
	private static TransactionM tm1 = new TransactionM("p1", "r1", 1.0, LocalDate.now());
	private static TransactionM tm2 = new TransactionM("p2", "r2", 2.0, LocalDate.now());
	private static ArrayList<TransactionM> tMList = new ArrayList<TransactionM>();

	
	
	@BeforeAll
	public static void TEST_initialize() {
		tMList.add(tm1);
		tMList.add(tm2);
	}
	
	@AfterAll
	public static void TEST_clean_up() {
		// nista
	}
	
	
	
	@Test
	@Order(00)
	// konstruktori
	public void test_Transaction() {
		Transaction transaction = new Transaction();
		Transaction transactionFilled = new Transaction(payerIBAN, recipientIBAN, amount, date);
		
		assertAll(
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
	
	
	
	@Test
	@Order(01)
	public void test_convertM() {		// TODO: cudan test
		ArrayList<Transaction> list = Transaction.convertM(tMList);
		
		for (Transaction el : list) {
			if (el == null || el.payer == null || el.recipient == null || el.amount == 0 || el.date == null) {
				fail("Nesto ne valja u metodi 'convertM'! ");
			}
		}
		
		assertTrue(true);
	}

}
