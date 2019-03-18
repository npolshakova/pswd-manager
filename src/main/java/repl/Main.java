package repl;

import btree.BinaryTree;
import btree.BlockchainNode;
import btree.Node;
import storage.RecoverCredentials;
import storage.StoreCredentials;
import storage.WalletManager;
import storage.encryption.AES;
import storage.encryption.IDGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Open ssl HMAC -> crypto/sec lib

/**
 * REPL to add, delete, search
 */
public class Main {

    static boolean useEncrypt = false;
    public static String txHash = "7a8cb75395cca053f92709720b6f675d835a1890767a6b4b45210927925bf4f6"; // TODO: Load tx from db
    //public static String txHash = null;

    //334234f228eab414ea2395dbe6c34036a25505a76d5d3419153ed8b1f496d10d  // one value unencrypted
    //0cca82a0c92d1ad472c17bbbf731c52c7bfc62d21821dd12db2f75e5d398cd0c // full tree unencrypted
    //0b99d6db5cc29d60a20461f75a4df10f6de41227895909c7a98c0fc5811d6996 // encrypted one val

    public static void main(String args[]) throws Exception {

        // Initialize wallet
        WalletManager.setupWallet();

        // Initialize AES
        AES aes = new AES();

        // Build tree
        BinaryTree<BlockchainNode> bt = new BinaryTree<BlockchainNode>();
        if(txHash != null ) { // replace with cache, hash table
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
                    int id = IDGenerator.generateID(inputs[1]);
                    String val = inputs[2];
                    if(useEncrypt) {
                        val = aes.encrypt(val, false);
                    }
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
                    int id = IDGenerator.generateID(inputs[1]);
                    List<BlockchainNode> path = bt.delete(id);
                    String updatedHash = StoreCredentials.saveUpdatedTree(path);
                    txHash = updatedHash;
                }
            } else if(line.charAt(0) == 's') {
                String[] inputs = line.split(" ");
                if(inputs.length < 2) {
                    System.out.println("Not enough inputs");
                } else {
                    int id = IDGenerator.generateID(inputs[1]);
                    Node n = bt.search(id);
                    if (n == null) {
                        System.out.println("ID: " + id + " not found");
                    } else {
                        String val = n.value;
                        if(useEncrypt) {
                            val = aes.decrypt(val, false);
                        }
                        System.out.println("Value: " + val);
                    }
                }
            }
            System.out.println("Enter credentials (c <id> <input>), search (s <id>) or (q) to Quit:");
            line = in.nextLine();
        }
    }

}
