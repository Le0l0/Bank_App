package bank;

import java.math.BigInteger;
import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.io.UnsupportedEncodingException;
////////////////////////////////////////////////////////////////
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;




public class Encryption
{
	// za AES
	private static final int KEY_LENGTH = 256;
	private static final int ITERATION_COUNT = 65536;
	private static String secretKey = App.getSecretKey();
	private static String salt = App.getSalt();

	
		
	// AES ////////////////////////////////////////////////////////////////
	// kod kopiran od HowToDoInJava
	public static String encryptAES(String strToEncrypt) {
		try {
	        SecureRandom secureRandom = new SecureRandom();
	        byte[] iv = new byte[16];
	        secureRandom.nextBytes(iv);
	        IvParameterSpec ivspec = new IvParameterSpec(iv);

	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes("UTF-8"), ITERATION_COUNT, KEY_LENGTH);
	        SecretKey tmp = factory.generateSecret(spec);
	        SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");
	
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
	
	        byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
	        byte[] encryptedData = new byte[iv.length + cipherText.length];
	        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
	        System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
	
	        return Base64.getEncoder().encodeToString(encryptedData);
		} catch (Exception e) {
			// Handle the exception properly
		    e.printStackTrace();
		    return null;
		}
	}
	
	public static String decryptAES(String strToDecrypt) {
	    try {
	        byte[] encryptedData = Base64.getDecoder().decode(strToDecrypt);
	        byte[] iv = new byte[16];
	        System.arraycopy(encryptedData, 0, iv, 0, iv.length);
	        IvParameterSpec ivspec = new IvParameterSpec(iv);

	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes("UTF-8"), ITERATION_COUNT, KEY_LENGTH);
	        SecretKey tmp = factory.generateSecret(spec);
	        SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

	        byte[] cipherText = new byte[encryptedData.length - 16];
	        System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.length);

	        byte[] decryptedText = cipher.doFinal(cipherText);
	        
	        return new String(decryptedText, "UTF-8");
	    }
	    // krivi passsword
	    catch (Exception e) {
	        // Handle the exception properly
	        // e.printStackTrace();
	        return null;
	    }
	}
	
	private static boolean testPasswordAES(String encryptedString) {
		//return Encryption.decryptAES(encryptedString, Encryption.encryptMD5(string), Encryption.encryptMD5(string)) == null ? false : true;
		return Encryption.decryptAES(encryptedString) == null ? false : true;
	}
	////////////////////////////////////////////////////////////////
	
	
	
	// SHA ////////////////////////////////////////////////////////////////
	// kopirano od GeeksForGeeks, malo modificirano
	public static String encryptSHA(String input) {
        try {
        	// Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            return no.toString(16);
        }
        catch (Exception e) {
        	System.out.println(e);
        	return null;
        }
    }
	////////////////////////////////////////////////////////////////
	
	
	
	public static boolean testPassword(String password, String ePassword) {
		if (ePassword != null && (Encryption.testPasswordAES(ePassword) || Encryption.encryptSHA(password).equals(ePassword))) return true;
		return false;
	}

}
