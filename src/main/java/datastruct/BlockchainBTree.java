package datastruct;

import btree.BTree;
import btree.BlockchainNode;
import storage.RecoverCredentials;
import storage.StoreCredentials;

import java.util.ArrayList;
import java.util.List;

public class BlockchainBTree {

    public BTree bt = new BTree();

    public BlockchainBTree(){}

    public BlockchainBTree(String transaction) {
        bt = RecoverCredentials.recoverBTree(transaction);
    }

    public String search(int id) {
       return bt.search(id);
    }

    public String update(int id, String credential) {
        return insert(id, credential);
    }

    public String insert(int id, String credential) {
        List<BTree.Node> lst = bt.insert(id,credential);
        String tx = StoreCredentials.saveUpdatedBTree(lst);
        return tx;
    }

    public String delete(int id) {
        List<BTree.Node> lst = bt.delete(id);
        String tx = StoreCredentials.saveUpdatedBTree(lst);
        return tx;
    }

}
