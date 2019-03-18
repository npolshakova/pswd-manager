package storage.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

// TODO: salt to address, add to original plaintext

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

    public String encrypt(String input, boolean salt) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        if(salt) {
            input = input + generateSalt();
        }
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes("UTF-8")));
    }

    public String decrypt(String input, boolean salt) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String ret = new String(cipher.doFinal(Base64.getDecoder().decode(input)));
        if(salt) {
            ret = ret.substring(0, ret.length() - 4);
        }
        return ret;
    }

    public static String generateSalt() {
        Random r = new SecureRandom();
        byte[] salt = new byte[2];
        r.nextBytes(salt);
        String ret = org.apache.commons.codec.binary.Base64.encodeBase64String(salt);
        System.out.println(ret);
        return ret;
    }

    public static void main(String args[]) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        AES ea = new AES();
        String test = "helloworld";
        String encrypted = ea.encrypt(test, true);
        System.out.println(encrypted);
        String decrypted = ea.decrypt(encrypted, true);
        System.out.println(decrypted);
    }

}
