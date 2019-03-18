package storage.encryption;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;

public class IDGenerator {

    public static void main(String args[]) {
        String secret = "ssshhhhhhhhhhh!!!!";
        String message = "Message";

        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
            System.out.println(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
