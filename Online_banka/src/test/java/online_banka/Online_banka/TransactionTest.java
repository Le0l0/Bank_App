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
	private static String testUsername = "testUser";
	private static String testPassword = "123";
	private static User user = null;
	private static BankAccount bankAcc = null;
	
	String recipientIBAN = "HR123";
	double amount = 10.0;
	LocalDate date = LocalDate.now();
	
	

	@BeforeAll
	public static void TEST_initialize() {
		user = new User(testUsername, testPassword);
		bankAcc = BankAccount.getAccount(user);
	}
	
	@AfterAll
	public static void TEST_clean_up() {
		user.deleteFiles();
		BankAccount.updateUserIBANList(user.username);
	}
	
	@Test
	@Order(01)
	public void test_Transaction() {
		Transaction transaction = new Transaction();
		Transaction transactionFilled = new Transaction(bankAcc.IBAN, recipientIBAN, amount, date);
		
		assertAll(
			"Transaction",
			() -> assertEquals(null, transaction.payer),
			() -> assertEquals(null, transaction.recipient),
			() -> assertEquals(0, transaction.amount),
			() -> assertEquals(null, transaction.date),
			
			() -> assertEquals(bankAcc.IBAN, transactionFilled.payer),
			() -> assertEquals(recipientIBAN, transactionFilled.recipient),
			() -> assertEquals(amount, transactionFilled.amount),
			() -> assertEquals(date, transactionFilled.date)
		);
	}

}
