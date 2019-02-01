import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.CoinSelection;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FakeTransaction {

    public static void main(String args[]) {
        //n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";


        //5424c715577803c10f2abc01b8da2fc9c26610bacb6a154ef11213bff992db6c
        String test = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"; //"moccUWfWVngRxQUzjHcwuneiisvrwxXdrJ"; // 32 bytes
        String adr = getAddress(test);
        System.out.println(adr);

        // checksum 4bytes, testnet 4 bytes
        //final Address myAddress = Address.fromBase58(params, "n4n8PBwjMZHdCYu9jJ2rWyce5ukVgu3r2N");
        Address sendAddress1 =  Address.fromBase58(params, adr);

        // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
                }
        };

        // Download the block chain and wait until it's done.
        kit.startAsync();
        kit.awaitRunning();

        // #2 n1MtSNwhekB5KbKqwLB8HTgy78UqxcV1GF
        // #3 mn9mno1NkVzjgCpNCsPsM1wdDdZM2EXJQt
        // #5 mzHmTaZWLCc3DKnnTNsMSxtCAZDvqwmca5


        System.out.println(kit.wallet().getBalance());
        List<Address> l = kit.wallet().getIssuedReceiveAddresses();
        for (Address li : l) {
            System.out.println(li.toString());
        }
        // calculateAllTransaction
        // setCoinSelector
        // kit.wallet().setCoinSelector();
        //kit.wallet().getIssuedReceiveKeys()
        List<TransactionOutput> ti = kit.wallet().calculateAllSpendCandidates();
        for (TransactionOutput t : ti) {
            System.out.println(t.toString());
        }
        List<ECKey> keys = kit.wallet().getIssuedReceiveKeys();
        for(ECKey k : keys) {
            System.out.println(k);
        }
        System.out.println(kit.wallet().getBalance());
        System.out.println(kit.wallet().currentReceiveAddress());
        kit.wallet().allowSpendingUnconfirmedTransactions();

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

    public static String getAddress(String input) {
        byte[] base58dec = Base58.decode(input);
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(base58dec));
        byte[] tmp = new byte[base58dec.length - 4];
        for(int i = 0; i < base58dec.length - 4; i++) {
            tmp[i] = base58dec[i];
        }
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(tmp));
        byte[] sha1 = Sha256Hash.hash(tmp);
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(sha1));
        byte[] sha2 = Sha256Hash.hash(sha1);
        System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(sha2));
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

}
