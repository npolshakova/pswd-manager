package storage.encryption;

import storage.EncryptAddress;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    final String key = "ssshhhhhhhhhhh!!!!";
    private SecretKeySpec secretKey;
    private Cipher cipher;

    public AES() throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] k = key.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        k = sha.digest(k);
        k = Arrays.copyOf(k, 16);
        this.secretKey = new SecretKeySpec(k, "AES");
        this.cipher = Cipher.getInstance("AES");
    }

    public String encrypt(String input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes("UTF-8")));
    }

    public String decrypt(String input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(input)));
    }

    public static void main(String args[]) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        AES ea = new AES();
        String test = "helloworld";
        String encrypted = ea.encrypt(test);
        System.out.println(encrypted);
        String decrypted = ea.decrypt(encrypted);
        System.out.println(decrypted);
    }

}
