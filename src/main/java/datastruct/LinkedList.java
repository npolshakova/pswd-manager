package datastruct;

import storage.RecoverCredentials;
import storage.StoreCredentials;

import java.util.ArrayList;
import java.util.List;

public class LinkedList implements Storage {

    //public static Map<Integer,String> credentials = new HashMap<>();
    public String txHead = null;
    public List<LinkedNode> nodes = new ArrayList<>();

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

    public String search(int id) {
        for(LinkedNode ln : nodes) {
            String str = ln.search(id);
            if(str != null) {
                return str;
            }
        }
        return null;
    }

    public String insert(int id, String credential) {
        if(nodes.size() > 0) {
            LinkedNode ln = nodes.get(0);
            if(ln.credentials.size() > 10) {
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

    public String update(int id, String credential) {
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

    public String delete(int id) {
        List<LinkedNode> updatePath = new ArrayList<>();
        String tx = "";
        for(LinkedNode ln : nodes) {
            String str = ln.search(id);
            if(str != null) {
                tx = ln.delete(id);
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
