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
import java.time.LocalDateTime;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionTest
{
	private static TransactionM tm1 = new TransactionM("p1", "r1", 1.0, LocalDateTime.now());
	private static TransactionM tm2 = new TransactionM("p2", "r2", 2.0, LocalDateTime.now());
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
	
	
	
	// metoda 'printTransaction()' se ne testira
	
	
	
	@Test
	@Order(01)
	public void test_convertMessage() { // cudan test
		ArrayList<Transaction> list = Transaction.convertMessage(tMList);
		
		for (Transaction el : list) {
			if (el == null || el.payer == null || el.recipient == null || el.amount == 0 || el.dateTime == null) {
				fail("Nesto ne valja u metodi 'convertMessage()'! ");
			}
		}
		
		assertTrue(true);
	}

}
