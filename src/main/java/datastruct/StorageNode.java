package datastruct;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.CoinSelector;
import storage.GenerateAddress;
import storage.RecoverCredentials;
import storage.StoreCredentials;
import storage.WalletManager;
import storage.encryption.IDGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageNode {

    public static Map<Integer, String> credentials = new HashMap<>();
    public static List<Address> addressList = new ArrayList<>();

    public StorageNode(){}

    public StorageNode(String transaction) {
        StorageNode s = RecoverCredentials.recoverStorageNode(transaction);
        this.credentials = s.credentials;
        this.addressList = s.addressList;
    }

    public StorageNode(Map<Integer, String> credentials, List<Address> addressList) {
        this.credentials = credentials;
        this.addressList = addressList;
    }

    public static void main(String args[]) {
        WalletManager.setupWallet();
        List<Address> toSend = new ArrayList<>();
        String credential1 = IDGenerator.generateID("www.google.com") + "$0$helloworldhellowo";
        Address addrValue = GenerateAddress.createAddress(credential1);
        toSend.add(addrValue);

        CoinSelector c = WalletManager.kit.wallet().getCoinSelector();
        Transaction tx = new Transaction(WalletManager.params);
        Coin coinToSent = Coin.valueOf((long) 600.0);
        c.select(coinToSent, WalletManager.kit.wallet().calculateAllSpendCandidates());
        for(Address sendAddr : toSend) {
            tx.addOutput(coinToSent, sendAddr);
        }

        System.out.println("Transaction: " + tx.getHashAsString());
    }

    public static String search(String domain) {
        return credentials.get(domain);
    }

    public static String update(String domain, String credential) {
        return insert(domain, credential);
    }

    public static String insert(String domain, String credential) {
        credentials.put(IDGenerator.generateID(domain), credential);
        updateAddressList();
        String tx = StoreCredentials.sendMultiple(addressList);
        return tx;
    }

    public static String delete(String domain) {
        credentials.remove(IDGenerator.generateID(domain));
        updateAddressList();
        String tx = StoreCredentials.sendMultiple(addressList);
        return tx;
    }


    private static void updateAddressList() {
        List<Address> toSend = new ArrayList<>();
        for(Map.Entry<Integer,String> m : credentials.entrySet()) {
            String id = m.getKey() + "$";
            int len = 18 - id.length();
            String val = m.getValue();
            String[] tmp = val.split("(?<=\\G.{" + len + "})");
            for(int i = 0; i < tmp.length; i++) {
                String cred = m.getKey() + "$" + i + "$" + tmp[i];
                Address addr = GenerateAddress.createAddress(cred);
                toSend.add(addr);
            }
        }
        addressList = toSend;
    }

}
