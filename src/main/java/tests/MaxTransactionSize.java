package tests;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;

public class MaxTransactionSize {

    public static void main(String args[]) {
        //n3xxxxxxxxxxxxxxxxxxxxxxxxxxeexxxx

        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";
        String test1 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        String test2 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxy";
        String test3 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxzz";
        String test4 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxwwww";
        String test5 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxeexxxx";
        String test6 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxfffffff";
        String test7 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxnnnnnnn";
        String test8 = "n3xxxxxxxxxxxxxxxxxxxxxxxxxmmmmmmm";
        String adr1 = getAddress(test1);
        String adr2 = getAddress(test2);
        String adr3 = getAddress(test3);
        String adr4 = getAddress(test4);
        String adr5 = getAddress(test5);
        String adr6 = getAddress(test6);
        String adr7 = getAddress(test7);
        String adr8 = getAddress(test8);

        Address sendAddress1 =  Address.fromBase58(params, adr1);
        Address sendAddress2 =  Address.fromBase58(params, adr2);
        Address sendAddress3 =  Address.fromBase58(params, adr3);
        Address sendAddress4 =  Address.fromBase58(params, adr4);
        Address sendAddress5 =  Address.fromBase58(params, adr5);
        Address sendAddress6 =  Address.fromBase58(params, adr6);
        Address sendAddress7 =  Address.fromBase58(params, adr7);
        Address sendAddress8 =  Address.fromBase58(params, adr8);

        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
            }
        };

        kit.startAsync();
        kit.awaitRunning();

        kit.wallet().allowSpendingUnconfirmedTransactions();
        CoinSelector c = kit.wallet().getCoinSelector();
        Transaction tx = new Transaction(params);
        Coin coinToSent = Coin.valueOf((long) 600.0);
        c.select(coinToSent, kit.wallet().calculateAllSpendCandidates());
        tx.addOutput(coinToSent, sendAddress1);
        tx.addOutput(coinToSent, sendAddress2);
        tx.addOutput(coinToSent, sendAddress3);
        tx.addOutput(coinToSent, sendAddress4);
        tx.addOutput(coinToSent, sendAddress5);
        tx.addOutput(coinToSent, sendAddress6);
        tx.addOutput(coinToSent, sendAddress7);
        tx.addOutput(coinToSent, sendAddress8);

        System.out.println(kit.wallet().getBalance());
        System.out.println(kit.wallet().currentReceiveAddress());

        SendRequest request = SendRequest.forTx(tx);
        try {
            kit.wallet().completeTx(request);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
        kit.wallet().commitTx(request.tx);
        kit.peerGroup().broadcastTransaction(request.tx);
    }


    public static String getAddress(String input) {
        byte[] base58dec = Base58.decode(input);
        byte[] tmp = new byte[base58dec.length - 4];
        for(int i = 0; i < base58dec.length - 4; i++) {
            tmp[i] = base58dec[i];
        }
        byte[] sha1 = Sha256Hash.hash(tmp);
        byte[] sha2 = Sha256Hash.hash(sha1);
        byte[] checksum = new byte[4];
        for (int i = 0; i < checksum.length; i++) {
            checksum[i] = sha2[i];
        }
        int count = 0;
        for(int i = 0; i < base58dec.length; i++) {
            if(i >= base58dec.length - 4) {
                base58dec[i] = checksum[count];
                count++;
            }
        }
        String adr = Base58.encode(base58dec);
        System.out.println(adr);
        return adr;
    }
}
