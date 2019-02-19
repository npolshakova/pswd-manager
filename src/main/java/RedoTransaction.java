import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class RedoTransaction {

    //f96aadf732429df5d7de7f657ff14584fc438b4e4022ab5ee8652ceb2b52e9d2
    //vcF2JF8bkaFZbsu1QD9rR1Vcmr1JbwU6FY6GFLoH9tdpfTgySkFswxUXdHn6Dr9r

    /*
    * 6FF63E246C92311B4FA83EF2E8C878C508A37136C27047DC11
6FF63E246C92311B4FA83EF2E8C878C508A37136C2
B3C863F2C02F20DB564F2E976CAD9141B905F7210931E0D5384F062049DC82C1
650D38287D87468D1790F85E1F5BCD31E5E7A3660A5B9597C8C1A632412B7131
650D3828
6FF63E246C92311B4FA83EF2E8C878C508A37136C2650D3828
n3xxxxxxxxxxxxxxxxxxxxxxxxxxxgKQTh
ADDR: n3xxxxxxxxxxxxxxxxxxxxxxxxxxxgKQTh
    * */


        public static void main(String args[]) {
            //n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

            NetworkParameters params = TestNet3Params.get();
            String filePrefix = "forwarding-service-testnet";


//            String test = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
//            String adr = getAddress(test);

//            byte[] test = javax.xml.bind.DatatypeConverter.parseHexBinary("6FF63E246C92311B4FA83EF2E8C878C508A37136C2");
//            System.out.println(test);
//            String adr = getAddress3(test);

            String msg = "helloworldhelloworld";
            byte[] hexMsg = asciiToHexArray(msg);
            System.out.println("Encoded: " + hexMsg);
            //System.out.println("Decoded: " + hexToASCII(hexMsg));
            String adr = encodeChecked(196, hexMsg);

            System.out.println("ADDR: " + adr);
            String val = javax.xml.bind.DatatypeConverter.printHexBinary(Base58.decodeChecked(adr));
            System.out.println(hexToASCII(val));

            //n3xxxxxxxxxxxxxxxxxxxxxxxxxxxgKQTh
            //JuWWB2ezW2Qw3ihjTptgjZ8XxvLg2uttX
            Address sendAddress1 =  Address.fromBase58(params, adr);

            WalletAppKit kit = new WalletAppKit(params, new File(".."), filePrefix) {
                @Override
                protected void onSetupCompleted() {
                    if (wallet().getKeyChainGroupSize() < 1)
                        wallet().importKey(new ECKey());
                }
            };
            kit.startAsync();
            kit.awaitRunning();

            kit.wallet().allowSpendingUnconfirmedTransactions();
            System.out.println(kit.wallet().currentReceiveAddress());

            //SEND
            CoinSelector c = kit.wallet().getCoinSelector();
            Transaction tx = new Transaction(params);
            Coin coinToSent = Coin.valueOf((long) 600.0);
            c.select(coinToSent, kit.wallet().calculateAllSpendCandidates());
            tx.addOutput(coinToSent, sendAddress1);

            SendRequest request = SendRequest.forTx(tx);
            try {
                kit.wallet().completeTx(request);
            } catch (InsufficientMoneyException e) {
                e.printStackTrace();
            }
            kit.wallet().commitTx(request.tx);
            kit.peerGroup().broadcastTransaction(request.tx);
            System.out.println(tx.getHashAsString());
            System.out.println(request.tx.getHashAsString());

        }

    private static String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
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


    private static final int[] INDEXES = new int[128];
    static {
        Arrays.fill(INDEXES, -1);
        for (int i = 0; i < Base58.ALPHABET.length; i++) {
            INDEXES[Base58.ALPHABET[i]] = i;
        }
    }

        public static byte[] convert(String input) {
            // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
            byte[] input58 = new byte[input.length()];
            for (int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                int digit = c < 128 ? INDEXES[c] : -1;
                input58[i] = (byte) digit;
            }
            return input58;
        }

        public static String getAddress(String input) {
            byte[] base58dec = Base58.decode(input);
            System.out.println(base58dec);
            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(base58dec));
            byte[] tmp = new byte[base58dec.length - 4];
            for(int i = 0; i < base58dec.length - 4; i++) {
                tmp[i] = base58dec[i];
            }
            System.out.println(tmp);

//            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(tmp));
//            byte[] sha1 = Sha256Hash.hash(tmp);
//            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(sha1));
//            byte[] sha2 = Sha256Hash.hash(sha1);
//            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(sha2));

            byte[] sha2 = Sha256Hash.hashTwice(tmp);

            byte[] checksum = new byte[4];
            for (int i = 0; i < checksum.length; i++) {
                checksum[i] = sha2[i];
            }

            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(checksum));

            int count = 0;
            for(int i = 0; i < base58dec.length; i++) {
                if(i >= base58dec.length - 4) {
                    base58dec[i] = checksum[count];
                    count++;
                }
            }

            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(base58dec));
            String adr = Base58.encode(base58dec);
            System.out.println(adr);
            return adr;
        }

    public static String encodeChecked(int version, byte[] payload) {
        // A stringified buffer is:
        // 1 byte version + data bytes + 4 bytes check code (a truncated hash)
        byte[] addressBytes = new byte[1 + payload.length + 4];
        addressBytes[0] = (byte) version;
        System.arraycopy(payload, 0, addressBytes, 1, payload.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, payload.length + 1);
        System.arraycopy(checksum, 0, addressBytes, payload.length + 1, 4);
        return Base58.encode(addressBytes);
    }

        public static String getAddress2(String input) {
            byte[] payload = convert(input);
            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(payload));
            String val = encodeChecked(196, payload); //acceptable versions for network: 1 not in [111, 196]
            System.out.println(val);
            byte[] base58dec = Base58.decodeChecked(val);
            System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(base58dec));
            return val;
        }

    public static String getAddress3(byte[] payload) {
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(payload));
        String val = encodeChecked(196, payload); //acceptable versions for network: 1 not in [111, 196]
        System.out.println(val);
        byte[] base58dec = Base58.decodeChecked(val);
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(base58dec));
        return val;
    }


    //310cda684a1bc8e0c805500a793b5d9da660cb3f6933705932a0f214d21ced7e

}
