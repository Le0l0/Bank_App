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
import java.io.IOException;
import java.net.URISyntaxException;




@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExchangeRateTest
{
	int broj_tecajnice = 100;
	LocalDate datum_primjene = LocalDate.now();
	String drzava = "abece";
	String drzava_iso = "ABC";
	double kupovni_tecaj = 1.0;
	double prodajni_tecaj = 2.0;
	int sifra_valute = 999;
	double srednji_tecaj = 1.5;
	String valuta = "CBA";
	
	

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
	public void test_ExchangeRate() {
		ExchangeRate eRate = new ExchangeRate();
		ExchangeRate eRateFilled = new ExchangeRate(broj_tecajnice, datum_primjene, drzava, drzava_iso, kupovni_tecaj, prodajni_tecaj, sifra_valute, srednji_tecaj, valuta);
		ExchangeRate eRateERate = new ExchangeRate(eRateFilled);
		
		assertAll(
			"ExchangeRate",
			() -> assertEquals(-1, eRate.broj_tecajnice),
			() -> assertEquals(null, eRate.datum_primjene),
			() -> assertEquals(null, eRate.drzava),
			() -> assertEquals(null, eRate.drzava_iso),
			() -> assertEquals(0, eRate.kupovni_tecaj),
			() -> assertEquals(0, eRate.prodajni_tecaj),
			() -> assertEquals(-1, eRate.sifra_valute),
			() -> assertEquals(0, eRate.srednji_tecaj),
			() -> assertEquals(null, eRate.valuta),
			
			() -> assertEquals(broj_tecajnice, eRateFilled.broj_tecajnice),
			() -> assertEquals(datum_primjene, eRateFilled.datum_primjene),
			() -> assertEquals(drzava, eRateFilled.drzava),
			() -> assertEquals(drzava_iso, eRateFilled.drzava_iso),
			() -> assertEquals(kupovni_tecaj, eRateFilled.kupovni_tecaj),
			() -> assertEquals(prodajni_tecaj, eRateFilled.prodajni_tecaj),
			() -> assertEquals(sifra_valute, eRateFilled.sifra_valute),
			() -> assertEquals(srednji_tecaj, eRateFilled.srednji_tecaj),
			() -> assertEquals(valuta, eRateFilled.valuta),
			
			() -> assertEquals(broj_tecajnice, eRateERate.broj_tecajnice),
			() -> assertEquals(datum_primjene, eRateERate.datum_primjene),
			() -> assertEquals(drzava, eRateERate.drzava),
			() -> assertEquals(drzava_iso, eRateERate.drzava_iso),
			() -> assertEquals(kupovni_tecaj, eRateERate.kupovni_tecaj),
			() -> assertEquals(prodajni_tecaj, eRateERate.prodajni_tecaj),
			() -> assertEquals(sifra_valute, eRateERate.sifra_valute),
			() -> assertEquals(srednji_tecaj, eRateERate.srednji_tecaj),
			() -> assertEquals(valuta, eRateERate.valuta)
		);
	}
	
	@Test
	@Order(02)
	public void test_loadExchangeRateList() throws IOException, URISyntaxException {
		ExchangeRate.loadExchangeRateList();
		
		assertEquals(13, ExchangeRate.eRateList.size());
	}

}
