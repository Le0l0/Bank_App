package online_banka_client.bank_client;

//za JUnit testiranje
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
//ostalo
import java.io.IOException;
import java.net.URISyntaxException;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExchangeRateTest
{
	// varijable i objekti
	
	

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
	public void test_loadExchangeRateList() {
		try {
			ExchangeRate.loadExchangeRateList();
		} catch (URISyntaxException | IOException e) {
			fail(e.getMessage());
		}
		
		assertEquals(13, ExchangeRate.eRateList.size());
	}

}
