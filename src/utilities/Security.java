package utilities;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Security {
	static Log log = LogFactory.getLog(Security.class);

	private static byte[] key = { 0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41,
			0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79 };// "thisIsASecretKey";

	public static String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			final String encryptedString = Base64.encodeBase64String(cipher
					.doFinal(strToEncrypt.getBytes()));
			return encryptedString;
		} catch (Exception e) {
			log.error("Error while encrypting", e);
		}
		return null;

	}

	public static String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			final String decryptedString = new String(cipher.doFinal(Base64
					.decodeBase64(strToDecrypt)));
			return decryptedString;
		} catch (Exception e) {
			log.error("Error while decrypting", e);

		}
		return null;
	}


	 public static String md5hash(String toHash) {
	 MessageDigest md = null;
	 try {
	 md = MessageDigest.getInstance("SHA1"); /*
	 * choix de l'algo de
	 * hashage, peut etre
	 * MD5
	 */
	 } catch (NoSuchAlgorithmException e) { /* l'algo choisit n'existe pas */
	 return null;
	 }
	 /*
	 * mdp.getBytes() :convertit la chaine mdp en tableau de bytes
	 * md.digest(s.getBytes()) : hash avec l'algo choisit et retourne un
	 * tableau de bytes, byteArrayToHexString() : convertir un tableau de
	 * byte en ne chaine de hexadécimale
	 */
	 return new String(byteArrayToHexString(md.digest(toHash.getBytes())));
	 }

	public static String hash(String password)
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        
      try{   
    	  byte[] salt = md5hash(password).getBytes();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(salt) + toHex(hash);}
      catch (Exception e){
    	  return null;}
    }

	private static String toHex(byte[] array) throws NoSuchAlgorithmException {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}
	/*
	 * Fonction utilisée lors du hashage qui permet de convertir un tableau de
	 * byte en une chaine de hexadécimale
	 */
	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
