package btree;

import datastruct.BlockchainBinaryTree;
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
        WalletManager.setupWallet();

        // Set up tiny tree
        BlockchainBinaryTree t = new BlockchainBinaryTree();
        t.insert(IDGenerator.generateID("www.google.com"), "helloworldhellowo");
        //t.insert(IDGenerator.generateID("amazon"), "applesauceapplesa");
        //t.insert(IDGenerator.generateID("gmail"), "quickbrownquickbr");
        //t.insert(IDGenerator.generateID("cs"), "chocolatecakeyumm");
        //t.insert(IDGenerator.generateID("ssh"), "carrotcakeyumyumm");
        //t.insert(IDGenerator.generateID("website"), "wowowowowowowowow");
        String tx = t.insert(IDGenerator.generateID("testing"), "thisisamessagetoy");
        System.out.println(tx);

        //060f51fb22b32b9e11a3e3a31ea3eb1165a36792b939c9a36ea614b95054
        //060f51fb22b32b9e11a3e3a31ea3eb1165a36792b939c9a36ea614b9505406b8
        //534069d2988e42cff87e165bb1d361d8fd9965b30e5bcaf198661ed09ac2c317

    }


}
