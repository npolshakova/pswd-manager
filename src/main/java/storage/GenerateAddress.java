package storage;


import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import storage.encryption.AES;

public class GenerateAddress {

    /**
     * Given an input credential string, generates an address
     * @param credentials Length must be less than 20
     * @return Bitcoinj Base58 Address
     */
    public static Address createAddress(String credentials) {
            assert(credentials.length() <= 20);
            byte[] hexMsg = asciiToHexArray(credentials);
            String adr = encodeChecked(196, hexMsg);
            Address sendAddress =  Address.fromBase58(WalletManager.params, adr);
            return sendAddress;

    }

    // TODO:
    public static Address createAddressEncrypted(String credentials) {
        AES a = null;
        try {
            a = new AES();
            String encodedCredentials = a.encrypt(credentials);
            return createAddress(encodedCredentials);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] asciiToHexArray(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        byte[] hex = new byte[chars.length];
        for (int i = 0; i < chars.length; i++)
        {
            hex[i] = (byte) chars[i];
        }
        return hex;
    }


    private static String encodeChecked(int version, byte[] payload) {
        // A stringified buffer is:
        // 1 byte version + data bytes + 4 bytes check code (a truncated hash)
        byte[] addressBytes = new byte[1 + payload.length + 4];
        addressBytes[0] = (byte) version;
        System.arraycopy(payload, 0, addressBytes, 1, payload.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, payload.length + 1);
        System.arraycopy(checksum, 0, addressBytes, payload.length + 1, 4);
        return Base58.encode(addressBytes);
    }

    // TODO
    public static String decodeAddressEncrypted(String adr) {
        try {
            AES a = new AES();
            String encoded = decodeAddress(adr);
            return a.decrypt(encoded);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Given an address string, returns the encoded original message
     * @param adr
     * @return
     */
    public static String decodeAddress(String adr) {
        return hexToASCII(javax.xml.bind.DatatypeConverter.printHexBinary(Base58.decodeChecked(adr)));
    }


    private static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }



}
