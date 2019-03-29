package datastruct;

import org.bitcoinj.core.Transaction;
import storage.RecoverCredentials;
import storage.StoreCredentials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedList {

    //public static Map<Integer,String> credentials = new HashMap<>();
    public static String txHead = null;
    public static List<LinkedNode> nodes = new ArrayList<>();

    public LinkedList() { }

    public LinkedList(Transaction tx) {
        LinkedList l = RecoverCredentials.recoverLinkedList(tx);
        this.nodes = l.nodes;
        this.txHead = tx.getHashAsString();
    }

    public LinkedList(List<LinkedNode> nodes, String tx) {
        this.nodes = nodes;
        this.txHead = tx;
    }

    public static String search(String domain) {
        for(LinkedNode ln : nodes) {
            String str = ln.search(domain);
            if(str != null) {
                return str;
            }
        }
        return null;
    }

    public static String insert(String domain, String credential) {
        if(nodes.size() > 0) {
            LinkedNode ln = nodes.get(0);
            if(ln.credentials.size() > 30) {
                // new node
                LinkedNode newNode = new LinkedNode();
                newNode.insert(domain, credential, txHead);
                txHead = StoreCredentials.sendMultiple(newNode.addressList);
            } else {
                // update head
                String tx = ln.insert(domain, credential);
                txHead = tx;
            }
        } else {
            LinkedNode newNode = new LinkedNode();
            newNode.insert(domain,credential,null);
            nodes.add(newNode);
        }
        return txHead;
    }

    public static String update(String domain, String credential) {
        List<LinkedNode> updatePath = new ArrayList<>();
        String tx = "";
        for(LinkedNode ln : nodes) {
            String str = ln.search(domain);
            if(str != null) {
                tx = ln.update(domain,credential);
                break;
            } else {
                updatePath.add(ln);
            }
        }

        for(int i = updatePath.size() - 1; i >= 0; i++) {
            LinkedNode ln = updatePath.get(i);
            tx = ln.updateTx(tx);
        }

        txHead = tx;

        return txHead;
    }

    public static String delete(String domain, String credential) {
        List<LinkedNode> updatePath = new ArrayList<>();
        String tx = "";
        for(LinkedNode ln : nodes) {
            String str = ln.search(domain);
            if(str != null) {
                tx = ln.delete(domain,credential);
                break;
            } else {
                updatePath.add(ln);
            }
        }

        for(int i = updatePath.size() - 1; i >= 0; i++) {
            LinkedNode ln = updatePath.get(i);
            tx = ln.updateTx(tx);
        }

        txHead = tx;

        return txHead;
    }


}
