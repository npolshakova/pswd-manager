package btree;

import org.bitcoinj.core.*;
import org.bitcoinj.wallet.CoinSelector;
import storage.GenerateAddress;
import storage.WalletManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for binary tree stored in blockchain
 */
public class TinyTree {

    private static String filler = "xxxxxxxx";

    public static void main(String args[]) throws InterruptedException {

        // Set up tiny tree
        BinaryTree t = new BinaryTree();
        t.insert(4, "V:helloworldhellowor");
        t.insert(2, "V:applesauceapplesau");
        t.insert(6, "V:quickbrownquickbro");
        //t.insert(1, "V:chocolatecakeyummy");
        //t.insert(3, "V:carrotcakeyumyummy");
        //t.insert(5, "V:wowowowowowowowowo");
        //t.insert(7, "V:thisisamessagetoyo");
        WalletManager.setupWallet();

        blockchainMagic(t.root);
        //914cec9183ee2343fee2ab7b0d55870dd6a074ed425a1b99cd8f7c63a6ab3e71

        // TODO: figure out why tx hash changes
//        for(Transaction tx : txs) {
//            WalletManager.send(tx);
//        }

    }

    private static String blockchainMagic(Node root) {
        List<Address> toSend = new ArrayList<>();

        Address addrValue = GenerateAddress.createAddress(root.value);
        System.out.println("Address: " + addrValue.toString() + " original: " + root.value);
        toSend.add(addrValue);
        Address id = GenerateAddress.createAddress("id:" + root.key + "xxxxxxxxxxxxxxxx");
        toSend.add(id);

        if(root.left != null) {
            int count = 0;
            String leftTransactionHash = blockchainMagic(root.left);
            String[] tmpAdr = leftTransactionHash.split("(?<=\\G.{"+18+"})");
            for(String s : tmpAdr) {
                if(s.length() < 18) { // TODO: generalize last case
                    s = s + filler;
                }
                toSend.add(GenerateAddress.createAddress("L" + count + s));
                count++;
            }
        }

        if(root.right != null){
            int count = 0;
            String rightTransactionHash = blockchainMagic(root.right);
            String[] tmpAdr = rightTransactionHash.split("(?<=\\G.{"+18+"})");
            for(String s : tmpAdr) {
                if(s.length() < 18) {
                    s = s + filler;
                }
                toSend.add(GenerateAddress.createAddress("R" + count + s));
                count++;
            }
        }

        CoinSelector c = WalletManager.kit.wallet().getCoinSelector();
        Transaction tx = new Transaction(WalletManager.params);
        Coin coinToSent = Coin.valueOf((long) 600.0);
        c.select(coinToSent, WalletManager.kit.wallet().calculateAllSpendCandidates());
        for(Address sendAddr : toSend) {
            tx.addOutput(coinToSent, sendAddr);
        }

        System.out.println("Balance: " + WalletManager.kit.wallet().getBalance());
        System.out.println("Current addr: " + WalletManager.kit.wallet().currentReceiveAddress());

        WalletManager.send(tx);
        System.out.println("ID: " + root.key + " tx: " + tx.getHashAsString());
        return tx.getHashAsString();
    }


    //68934bde1a984f028bee6c85d9c99f8e823e36ae9cb40814c01f8eab7d1a13ce

}
