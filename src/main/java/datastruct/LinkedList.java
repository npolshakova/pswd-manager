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

    public LinkedList(String tx) {
        LinkedList l = RecoverCredentials.recoverLinkedList(tx);
        this.nodes = l.nodes;
        this.txHead = tx;
    }

    public LinkedList(List<LinkedNode> nodes, String tx) {
        this.nodes = nodes;
        this.txHead = tx;
    }

    public static String search(int id) {
        for(LinkedNode ln : nodes) {
            String str = ln.search(id);
            if(str != null) {
                return str;
            }
        }
        return null;
    }

    public static String insert(int id, String credential) {
        if(nodes.size() > 0) {
            LinkedNode ln = nodes.get(0);
            if(ln.credentials.size() > 30) {
                // new node
                LinkedNode newNode = new LinkedNode();
                newNode.insert(id, credential, txHead);
                txHead = StoreCredentials.sendMultiple(newNode.addressList);
            } else {
                // update head
                String tx = ln.insert(id, credential);
                txHead = tx;
            }
        } else {
            LinkedNode newNode = new LinkedNode();
            newNode.insert(id,credential,null);
            nodes.add(newNode);
        }
        return txHead;
    }

    public static String update(int id, String credential) {
        List<LinkedNode> updatePath = new ArrayList<>();
        String tx = "";
        for(LinkedNode ln : nodes) {
            String str = ln.search(id);
            if(str != null) {
                tx = ln.update(id,credential);
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

    public static String delete(int id, String credential) {
        List<LinkedNode> updatePath = new ArrayList<>();
        String tx = "";
        for(LinkedNode ln : nodes) {
            String str = ln.search(id);
            if(str != null) {
                tx = ln.delete(id,credential);
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
