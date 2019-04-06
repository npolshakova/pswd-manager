package btree;

import org.bitcoinj.core.*;
import org.bitcoinj.wallet.CoinSelector;
import storage.GenerateAddress;
import storage.WalletManager;
import storage.encryption.IDGenerator;

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
        t.insert(IDGenerator.generateID("www.google.com"), "V:0helloworldhellowo");
        t.insert(IDGenerator.generateID("amazon"), "V:0applesauceapplesa");
        t.insert(IDGenerator.generateID("gmail"), "V:0quickbrownquickbr");
        t.insert(IDGenerator.generateID("cs"), "V:0chocolatecakeyumm");
        t.insert(IDGenerator.generateID("ssh"), "V:0carrotcakeyumyumm");
        t.insert(IDGenerator.generateID("website"), "V:0wowowowowowowowow");
        t.insert(IDGenerator.generateID("testing"), "V:0thisisamessagetoy");
        WalletManager.setupWallet();

        blockchainMagic(t.root);

    }

    private static String blockchainMagic(BlockchainNode root) {
        List<Address> toSend = new ArrayList<>();

        Address addrValue = GenerateAddress.createAddress(root.value);
        System.out.println("Address: " + addrValue.toString() + " original: " + root.value);
        toSend.add(addrValue);
        String idStr = "id:" + root.key;
        while(idStr.length() < 20) {
            idStr += "x";
        }
        Address id = GenerateAddress.createAddress( idStr);
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

/*
Current address: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
Address: 2N179oq7ornYzGHrPfphwXrnApGhCxZ14K3 original: V:0helloworldhellowo
Address: 2N179opgzkry4MZbHVvHCRoPyTgL6EwTbwG original: V:0applesauceapplesa
Address: 2N179oqg8VkiTnCviNpECN7A8L43ctVy45Z original: V:0quickbrownquickbr
Balance: 303099
Current addr: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
ID: -1537331589 tx: 417c4150d0cfa54874aceea24e0c86ce3f62007dc1bac54d253d3a05a5f8a07f
Balance: 276199
Current addr: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
ID: -443092682 tx: 65c1a7ee77799911ff47baea62c4439018dcd6feea58dd0095f5f424c4540367
Address: 2N179opp21DynWW5P7PsAwYvDKbkHm3DVJy original: V:0chocolatecakeyumm
Address: 2N179opovMinkixjGWfr8qkaCwDHDq2x2Jk original: V:0carrotcakeyumyumm
Address: 2N179or3SqaALTHrgF7pa1oCygzGJRWUaKR original: V:0wowowowowowowowow
Address: 2N179oqreZj9KxJbXELhSMpzC9j6LZxg4t6 original: V:0thisisamessagetoy
Balance: 234099
Current addr: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
ID: 1434859447 tx: 84b3de3759f3d528454a13a11562dd24de1343d53885c7051adad57d53454a5a
Balance: 207199
Current addr: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
ID: 604272257 tx: be728aeead1c0db266ee2ccb9f795f83e7df90b5d042df09dd458288642ed648
Balance: 165099
Current addr: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
ID: 1617596340 tx: b24a6018a7db520d8b80f7c3f1bdfae0053b53e4febb1a1b3ea7928d0131944b
Balance: 122999
Current addr: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
ID: -274425290 tx: f0abf172f1cd2fae9a3ab361e9aa60b119a6cb2a438ec741e6edc916b578ec10
Balance: 80899
Current addr: mjHCkZAZ6XB117nkCervExMh2gaE4AmLTy
ID: -414563671 tx: 7a8cb75395cca053f92709720b6f675d835a1890767a6b4b45210927925bf4f6
 */

}
