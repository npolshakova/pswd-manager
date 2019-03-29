package datastruct;

import org.bitcoinj.core.Address;
import storage.GenerateAddress;
import storage.RecoverCredentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinkedNode extends StorageNode{

    public static String nextTx = null;

    public LinkedNode()  {}

    public LinkedNode(Map<Integer, String> credentials, List<Address> addressList, String nextTx) {
        super(credentials, addressList);
        this.nextTx = nextTx;
    }

    public LinkedNode(String transaction) {
        LinkedNode s = RecoverCredentials.recoverLinkedNode(transaction);
        this.credentials = s.credentials;
        this.addressList = s.addressList;
        this.nextTx = s.nextTx;
    }

    public static String insert(String domain, String credential, String tx) {
        String newTx = insert(domain,credential);
        nextTx = tx;
        return newTx;
    }

    public static String update(String domain, String credential) {
        String newTx = update(domain,credential);
        return newTx;
    }

    public static String delete(String domain, String credential) {
        String newTx = delete(domain,credential);
        return newTx;
    }

    // TODO
    public String updateTx(String tx) {
        return null;
    }
}
