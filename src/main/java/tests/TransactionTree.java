package tests;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransactionTree {

    private static NetworkParameters params = TestNet3Params.get();

    public static void main(String args[]) {
        String fakeAddr = "n3xxxxTestingTestingokxxxxxxxxxxxx";
        String adrStr = getAddress(fakeAddr);
        List<Address> listAdr = new ArrayList<>();
        Address adr =  Address.fromBase58(params, adrStr);
        listAdr.add(adr);
        String nextTx1 = sendToAddresses(listAdr);

        List<Address> nextAdr = transactionAddresses(nextTx1);
        String nextTx2 = sendToAddresses(nextAdr);
        System.out.println(nextTx2);
    }

    public static String sendToAddresses(List<Address> addressList) {
        WalletAppKit kit = new WalletAppKit(params, new File("."), "forwarding-service-testnet") {
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
        for(Address a : addressList) {
            tx.addOutput(coinToSent, a);
        }

        SendRequest request = SendRequest.forTx(tx);
        try {
            kit.wallet().completeTx(request);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
        kit.wallet().commitTx(request.tx);
        kit.peerGroup().broadcastTransaction(request.tx);

        String nextTx = request.tx.getHashAsString();
        return nextTx;
    }

    public static List<Address> transactionAddresses(String tx) {
        int resultLen = tx.length() / 16;
        List<Address> ret = new ArrayList<>();
        for(int i = 0; i < resultLen; i++) {
            String pre = "n3xxxx" + tx.substring(i, i + 16) + "xxxxxxxxxxxx";
            String adrStr = getAddress(pre);
            Address adr =  Address.fromBase58(params, adrStr);
            ret.add(adr);
        }
        return ret;
    }

    public static String getAddress(String input) {
        System.out.println(input);
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
