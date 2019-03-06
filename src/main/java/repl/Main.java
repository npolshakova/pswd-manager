package repl;

import btree.BinaryTree;
import btree.BlockchainNode;
import btree.Node;
import storage.RecoverCredentials;
import storage.StoreCredentials;
import storage.WalletManager;
import storage.encryption.AES;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static String txHash = "334234f228eab414ea2395dbe6c34036a25505a76d5d3419153ed8b1f496d10d"; // TODO: Load tx from db
    //public static String txHash = null;

    //30413fe6265ab329a02c4ab940b9fac8633f53f03429f1ac3a554b7a6b1a460b  // one value
    //914cec9183ee2343fee2ab7b0d55870dd6a074ed425a1b99cd8f7c63a6ab3e71 // full tree

    public static void main(String args[]) throws Exception {

        // Initialize wallet
        WalletManager.setupWallet();

        // Initialize AES
        AES aes = new AES();

        // Build tree
        BinaryTree<BlockchainNode> bt = new BinaryTree<BlockchainNode>();
        if(txHash != null ) {
            List<BlockchainNode> values = new ArrayList<>();
            RecoverCredentials.recoverAllValues(txHash, values);
            bt.insertAll(values);
        }

        // Update values
        System.out.println("Enter credentials (c <id> <input>), delete (d <id>), search (s <id>) or (q) to Quit:");
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        while(true) {
            System.out.println(txHash);
            if(line.equals("q")) {
                break;
            } else if(line.charAt(0) == 'c') {
                String[] inputs = line.split(" ");
                if(inputs.length < 3) {
                    System.out.println("Not enough inputs");
                } else {
                    int id = Integer.parseInt(inputs[1]);
                    String val = inputs[2];
                    BlockchainNode bn = new BlockchainNode(id, val);
                    List<BlockchainNode> path = bt.insert(bn);
                    String updatedHash = StoreCredentials.saveUpdatedTree(path);
                    txHash = updatedHash;
                }
            } else if(line.charAt(0) == 'd') {
                String[] inputs = line.split(" ");
                if(inputs.length < 2) {
                    System.out.println("Not enough inputs");
                } else {
                    int id = Integer.parseInt(inputs[1]);
                    List<BlockchainNode> path = bt.delete(id);
                    String updatedHash = StoreCredentials.saveUpdatedTree(path);
                    txHash = updatedHash;
                }
            } else if(line.charAt(0) == 's') {
                String[] inputs = line.split(" ");
                if(inputs.length < 2) {
                    System.out.println("Not enough inputs");
                } else {
                    int id = Integer.parseInt(inputs[1]);
                    Node n = bt.search(id);
                    if (n == null) {
                        System.out.println("ID: " + id + " not found");
                    } else {
                        System.out.println("Value: " + n.value);
                    }
                }
            }
            System.out.println("Enter credentials (c <id> <input>), search (s <id>) or (q) to Quit:");
            line = in.nextLine();
        }
    }

}
