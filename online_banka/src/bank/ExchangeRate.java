package bank;

import java.util.ArrayList;
import java.time.LocalDate;
////////////////////////////////
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;




public class ExchangeRate
{
	int broj_tecajnice;
	LocalDate datum_primjene;
	String drzava;
	String drzava_iso;
	double kupovni_tecaj;
	double prodajni_tecaj;
	int sifra_valute;
	double srednji_tecaj;
	String valuta;
	
	static ArrayList<ExchangeRate> eRateList = new ArrayList<ExchangeRate>();

	
	
	// konstruktori
	public ExchangeRate() {
		this.broj_tecajnice = -1;
		this.datum_primjene = null;
		this.drzava = null;
		this.drzava_iso = null;
		this.kupovni_tecaj = 0;
		this.prodajni_tecaj = 0;
		this.sifra_valute = -1;
		this.srednji_tecaj = 0;
		this.valuta = null;
	}
	
	public ExchangeRate(int broj_tecajnice, LocalDate datum_primjene, String drzava, String drzava_iso, double kupovni_tecaj,
						double prodajni_tecaj, int sifra_valute, double srednji_tecaj, String valuta) {
		this.broj_tecajnice = broj_tecajnice;
		this.datum_primjene = datum_primjene;
		this.drzava = drzava;
		this.drzava_iso = drzava_iso;
		this.kupovni_tecaj = kupovni_tecaj;
		this.prodajni_tecaj = prodajni_tecaj;
		this.sifra_valute = sifra_valute;
		this.srednji_tecaj = srednji_tecaj;
		this.valuta = valuta;
	}
	
	public ExchangeRate(ExchangeRate eRate) {
		this.broj_tecajnice = eRate.broj_tecajnice;
		this.datum_primjene = eRate.datum_primjene;
		this.drzava = eRate.drzava;
		this.drzava_iso = eRate.drzava_iso;
		this.kupovni_tecaj = eRate.kupovni_tecaj;
		this.prodajni_tecaj = eRate.prodajni_tecaj;
		this.sifra_valute = eRate.sifra_valute;
		this.srednji_tecaj = eRate.srednji_tecaj;
		this.valuta = eRate.valuta;
	}
	
	
	
	// dohvati tecajnu listu i zapisi je u "eRateList"
	public static void setExchangerateList() throws IOException {
        // uspostavljanje veze i input streama 
        URL url = new URL("https://api.hnb.hr/tecajn-eur/v3");
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        
        // ucitaj podatke iz json datoteke u ArrayList eRateList
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

        	int i = 0;
        	char[] cBuffer = new char[32];
        	char tmpChar = '0';
        	ExchangeRate rate = new ExchangeRate();
        	
        	while (reader.read() != ']') {
        		
        		// citaj sve segmente i zapisuj u objekt "rate", te na kraju "rate" dodaj u ArrayList
    			reader.skip(19);
    			
    			reader.read(cBuffer, 0, 3);
    			rate.broj_tecajnice = Integer.parseInt(String.valueOf(cBuffer, 0, 3));
    			
    			reader.skip(20);
    			
    			reader.read(cBuffer, 0, 10);
    			rate.datum_primjene = LocalDate.parse(String.valueOf(cBuffer, 0, 10));
    			
    			reader.skip(12);
    			
    			i = 0;
    			for (; (tmpChar = (char)reader.read()) != '"'; i++) {
    				cBuffer[i] = tmpChar;
    			}
    			rate.drzava = String.valueOf(cBuffer, 0, i);
    			
    			reader.skip(15);
    			
    			reader.read(cBuffer, 0, 3);
    			rate.drzava_iso = String.valueOf(cBuffer, 0, 3);
    			
    			reader.skip(19);
    			
    			for (i = 0; (tmpChar = (char)reader.read()) != '"'; i++) {
    				cBuffer[i] = tmpChar;
    			}
    			rate.kupovni_tecaj = Double.parseDouble(String.valueOf(cBuffer, 0, i).replace(',', '.'));
    			
    			reader.skip(19);
    			
    			for (i = 0; (tmpChar = (char)reader.read()) != '"'; i++) {
    				cBuffer[i] = tmpChar;
    			}
    			rate.prodajni_tecaj = Double.parseDouble(String.valueOf(cBuffer, 0, i).replace(',', '.'));
    			
    			reader.skip(17);
    			
    			reader.read(cBuffer, 0, 3);
    			rate.sifra_valute = Integer.parseInt(String.valueOf(cBuffer, 0, 3));
    			
    			reader.skip(19);
    			
    			for (i = 0; (tmpChar = (char)reader.read()) != '"'; i++) {
    				cBuffer[i] = tmpChar;
    			}
    			rate.srednji_tecaj = Double.parseDouble(String.valueOf(cBuffer, 0, i).replace(',', '.'));
    			
    			reader.skip(11);
    			
    			reader.read(cBuffer, 0, 3);
    			rate.valuta = String.valueOf(cBuffer, 0, 3);
    			
    			reader.skip(2);

    			eRateList.add(new ExchangeRate(rate));
        		
        	}
        	
        }   
    }
	
	

	public void printExchangeRate() {
		System.out.println("Valuta: " + valuta + ", drzava: " + drzava + " - " + drzava_iso);
		System.out.println("Datum primjene:\t" + datum_primjene);
		System.out.printf("Kupovni tecaj:\t%.6f\n", kupovni_tecaj);
		System.out.printf("Prodajni tecaj:\t%.6f\n", prodajni_tecaj);
		System.out.printf("Srednji tecaj:\t%.6f\n", srednji_tecaj);
		System.out.printf("Sifra valute:\t%3d\n", sifra_valute);
		System.out.println();
	}
	
}
