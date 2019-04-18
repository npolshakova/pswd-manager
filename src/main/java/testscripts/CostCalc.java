package testscripts;

import btree.BinaryTree;
import datastruct.*;
import org.bitcoinj.core.Coin;
import storage.WalletManager;
import storage.encryption.IDGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CostCalc {

    // CAP at 19 transactions

    public static void main(String args[]) {

        BlockchainBinaryTree bt = new BlockchainBinaryTree();
        //StorageNode sn = new StorageNode();
        //LinkedList lst = new LinkedList();

        WalletManager.setupWallet();
        String csvFile = "/home/npolshak/pswd-manager/src/main/resources/test4.csv";
        String line = "";
        String cvsSplitBy = ",";

        Coin originalBalance = WalletManager.kit.wallet().getBalance();
        System.out.println(originalBalance);
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[] row = line.split(cvsSplitBy);
                int id = IDGenerator.generateID(row[0]);
                //System.out.println(row[0]);
                String tx = bt.insert(id, row[1]);
                System.out.println("TX: " + tx);
                //sn.insert(id, row[1]);
                //lst.insert(id, row[1]);
                //Thread.sleep(10000);
            }

        } catch (IOException e) {
            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Coin finalBalance = WalletManager.kit.wallet().getBalance();
        System.out.println(finalBalance);
    }

    //1166180
}
