package storage;

import btree.BlockchainNode;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.CoinSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static storage.WalletManager.setupWallet;

public class StoreCredentials {

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("credentials:");
        String credential = scanner.nextLine();
        saveCredential(credential);
    }

    public static void saveCredential(String credential) {
        setupWallet();
        Address addr = GenerateAddress.createAddress(credential);
        Transaction tx = WalletManager.createTransaction(addr);
        String sentTx = WalletManager.send(tx);
        System.out.println("Transaction: " + sentTx);
    }

    public static String saveBinaryTree(BlockchainNode root) {
        List<Address> toSend = new ArrayList<>();

        List<Address> addrValue = format("V:", String.valueOf(root.value), true);
        root.setValueAddress(addrValue);
        toSend.addAll(addrValue);
        Address id = format("id:", String.valueOf(root.key), false).get(0);
        root.setIdAddress(id);
        toSend.add(id);

        if(root.left != null) {
            String leftTransactionHash = saveBinaryTree((BlockchainNode) root.left);
            List<Address> addrs = format("L", leftTransactionHash, true);
            toSend.addAll(addrs);
            root.leftAddresses = addrs;
            root.leftTx = leftTransactionHash;
        }

        if(root.right != null){
            String rightTransactionHash = saveBinaryTree((BlockchainNode) root.right);
            List<Address> addrs = format("R", rightTransactionHash, true);
            toSend.addAll(addrs);
            root.rightAddresses = addrs;
            root.rightTx = rightTransactionHash;
        }

        return sendMultiple(toSend);
    }

    public static String saveUpdatedTree(List<BlockchainNode> lst) {
        assert(lst.size() > 0);
        if(lst.size() == 1) {
            // Update Value
            BlockchainNode bn = lst.get(0);

            List<Address> toSend = new ArrayList<>();
            if(bn.idAddress == null) {
                List<Address> addrValue = format("V:", String.valueOf(bn.value), true);
                bn.setValueAddress(addrValue);
                toSend.addAll(addrValue);
                Address id = format("id:", String.valueOf(bn.key), false).get(0);
                bn.setIdAddress(id);
                toSend.add(id);
            } else {
                toSend.addAll(bn.valueAddress);
                toSend.add(bn.idAddress);
            }

            if(!bn.rightAddresses.isEmpty()) {
                toSend.addAll(bn.rightAddresses);
            }

            if(!bn.leftAddresses.isEmpty()) {
                toSend.addAll(bn.leftAddresses);
            }

            return sendMultiple(toSend);
        } else {
            // Update Transactions
            BlockchainNode bn = lst.get(0);

            List<Address> toSend = new ArrayList<>();

            if(bn.idAddress == null) {
                List<Address> addrValue = format("V:", String.valueOf(bn.value), true);
                bn.setValueAddress(addrValue);
                toSend.addAll(addrValue);
                Address id = format("id:", String.valueOf(bn.key), false).get(0);
                bn.setIdAddress(id);
                toSend.add(id);
            } else {
                toSend.addAll(bn.valueAddress);
                toSend.add(bn.idAddress);
            }


            BlockchainNode nextNode = lst.get(1);

            if(bn.left != null && bn.left.key == nextNode.key) {
                // Add left
                String leftTransactionHash = saveUpdatedTree(lst.subList(1,lst.size()));
                List<Address> addrs = format("L", leftTransactionHash, true);
                bn.leftAddresses = addrs;
            } else if(bn.right != null && bn.right.key == nextNode.key) {
                // Add right
                String rightTransactionHash = saveUpdatedTree(lst.subList(1,lst.size()));
                List<Address> addrs = format("R", rightTransactionHash, true);
                bn.rightAddresses = addrs;
            }

            toSend.addAll(bn.leftAddresses);
            toSend.addAll(bn.rightAddresses);

            return sendMultiple(toSend);
        }
    }

    public static String sendMultiple(List<Address> toSend) {
        CoinSelector c = WalletManager.kit.wallet().getCoinSelector();
        Transaction tx = new Transaction(WalletManager.params);
        Coin coinToSent = Coin.valueOf((long) 600.0);
        c.select(coinToSent, WalletManager.kit.wallet().calculateAllSpendCandidates());
        for(Address sendAddr : toSend) {
            tx.addOutput(coinToSent, sendAddr);
        }
        WalletManager.send(tx);
        return tx.getHashAsString();
    }

    public static List<Address> format(String id, String msg, boolean multAddr) {
        //V:helloworldhellowor
        assert(msg.length() < 170);
        int len = 19 - id.length();
        List<Address> ret = new ArrayList<>();
            String[] tmp = msg.split("(?<=\\G.{" + len + "})");
            int count = 0;
            for (String s : tmp) {
                while (s.length() < 17) {
                    s = s + "x";
                }
                if(multAddr) {
                    s = id + count + s;
                } else {
                    s = id + s;
                }
                ret.add(GenerateAddress.createAddress(s));
                count++;
            }
        return ret;
    }

}
