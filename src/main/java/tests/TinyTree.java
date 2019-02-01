package tests;

import btree.BinaryTree;
import btree.Node;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static tests.MaxTransactionSize.getAddress;

public class TinyTree {

    static NetworkParameters params = TestNet3Params.get();
    static String filePrefix = "forwarding-service-testnet";
    static WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
        @Override
        protected void onSetupCompleted() {
            if (wallet().getKeyChainGroupSize() < 1)
                wallet().importKey(new ECKey());
        }
    };


public static void main(String args[]) throws InterruptedException {

    // Set up tiny tree
    BinaryTree t = new BinaryTree(4, "n3abcxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    t.insert(2, "n3efgxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    t.insert(6, "n3hijxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    //t.insert(1, "n3kmnxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    //t.insert(3, "n3pqrxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    //t.insert(5, "n3stuxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    //t.insert(7, "n3vwxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    setupWallet();
    blockchainMagic(t.root);
    //Thread.sleep(150000);
}

    private static String encodeToAddr(String str) {
        String tmp = "n3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        String match = new String(new char[str.length()]).replace("\0", "x");
        tmp = tmp.replaceFirst(match, str);
        System.out.println(tmp);
        //tmp = tmp.replaceAll("[l10O +]", "x");
        return tmp;
    }

    private static String encodeChars(String str) {
        System.out.println(str);
        str = str.replaceAll("l", "abc");
        str = str.replaceAll("1", "def");
        str = str.replaceAll("o", "ghi");
        str = str.replaceAll("0", "jkm");
        //str = str.replaceAll(" ", "npq");
        System.out.println(str);
        return str;
    }

    private static void setupWallet() {
        kit.startAsync();
        kit.awaitRunning();
        kit.wallet().allowSpendingUnconfirmedTransactions();

        List<TransactionOutput> ti = kit.wallet().calculateAllSpendCandidates();
        for (TransactionOutput t : ti) {
            System.out.println(t.toString());
        }
    }

    private static String blockchainMagic(Node root) {
        List<Address> toSend = new ArrayList<>();

        String addrValue = getAddress(root.value);
        toSend.add(Address.fromBase58(params, addrValue));
        String tmp = encodeToAddr("id" + root.key);
        String addrID = getAddress(tmp);
        toSend.add(Address.fromBase58(params, addrID));

        if(root.left != null) {
            String leftTransactionHash = blockchainMagic(root.left);
            leftTransactionHash = encodeChars(leftTransactionHash);
            String[] tmpAdr = leftTransactionHash.split("(?<=\\G.{"+30+"})");
            int count = 2;
            for(String s : tmpAdr) {
                toSend.add(Address.fromBase58(params, getAddress(encodeToAddr("w" + count + s.replaceAll(" ", "")))));
                count++;
            }
        }

        if(root.right != null){
            String rightTransactionHash = blockchainMagic(root.right);
            rightTransactionHash = encodeChars(rightTransactionHash);
            String[] tmpAdr = rightTransactionHash.split("(?<=\\G.{"+30+"})");
            int count = 2;
            for(String s : tmpAdr) {
                toSend.add(Address.fromBase58(params, getAddress(encodeToAddr("r" + count + s.replaceAll(" ", "")))));
                count++;
            }
        }

        CoinSelector c = kit.wallet().getCoinSelector();
        Transaction tx = new Transaction(params);
        Coin coinToSent = Coin.valueOf((long) 600.0);
        c.select(coinToSent, kit.wallet().calculateAllSpendCandidates());
        for(Address sendAddr : toSend) {
            tx.addOutput(coinToSent, sendAddr);
        }

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
        String txHash = request.tx.toString();
        System.out.println(txHash);

        return  txHash;
    }


}
