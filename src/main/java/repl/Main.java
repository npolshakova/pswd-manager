package repl;

import datastruct.BlockchainBinaryTree;
import datastruct.LinkedList;
import datastruct.Storage;
import datastruct.StorageNode;
import storage.WalletManager;
import storage.encryption.AES;
import storage.encryption.IDGenerator;

import java.util.Scanner;

// Open ssl HMAC -> crypto/sec lib

/**
 * REPL to add, delete, search
 */
public class Main {

    static boolean useEncrypt = false;
    public static String txHash = "0d81cdd8fdef11526165c422bb80a137a8d29b1c74c61accf76ccf4c8e076fd1"; // TODO: Load tx from db
    //public static String txHash = null;

    //334234f228eab414ea2395dbe6c34036a25505a76d5d3419153ed8b1f496d10d  // one value unencrypted
    //0cca82a0c92d1ad472c17bbbf731c52c7bfc62d21821dd12db2f75e5d398cd0c // full tree unencrypted
    //0b99d6db5cc29d60a20461f75a4df10f6de41227895909c7a98c0fc5811d6996 // encrypted one val
    //7808eaf0099c407d07f273fbec5dd916dc681c6fdb49a07b600a542fbe8a255c one node

    public static void main(String args[]) throws Exception {

        // Initialize wallet
        WalletManager.setupWallet();

        // Initialize AES
        AES aes = new AES();

        String mode = "tree";

        Storage bt = null;
        if(mode.equals("node")) {
            // Build node
            bt = new StorageNode(txHash);
        } else if(mode.equals("list")) {
            // Build node
            bt = new LinkedList(txHash);
        } else if(mode.equals("tree")) {
            // Build tree
            bt = new BlockchainBinaryTree(txHash);
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
                    txHash= bt.insert(id,val);
                }
            } else if(line.charAt(0) == 'd') {
                String[] inputs = line.split(" ");
                if(inputs.length < 2) {
                    System.out.println("Not enough inputs");
                } else {
                    int id = IDGenerator.generateID(inputs[1]);
                    txHash = bt.delete(id);
                }
            } else if(line.charAt(0) == 's') {
                String[] inputs = line.split(" ");
                if(inputs.length < 2) {
                    System.out.println("Not enough inputs");
                } else {
                    int id = IDGenerator.generateID(inputs[1]);
                    String value = bt.search(id);
                    System.out.println(value);
                }
            }
            System.out.println("Enter credentials (c <id> <input>), search (s <id>) or (q) to Quit:");
            line = in.nextLine();
        }
    }

}
