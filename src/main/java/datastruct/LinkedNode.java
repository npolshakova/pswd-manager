package datastruct;

import org.bitcoinj.core.Address;
import storage.GenerateAddress;
import storage.RecoverCredentials;
import storage.StoreCredentials;

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

    public String insert(int id, String credential, String tx) {
        String newTx = insert(id,credential);
        nextTx = tx;
        return newTx;
    }

    public String update(int id, String credential) {
        String newTx = update(id,credential);
        return newTx;
    }


    public String updateTx(String tx) {
        List<Address> addrs = StoreCredentials.format("$", tx, true);
        List<Address> lst = new ArrayList<>();
        lst.addAll(addressList);
        lst.addAll(addrs);
        String updatedTx = StoreCredentials.sendMultiple(lst);
        return updatedTx;
    }
}
