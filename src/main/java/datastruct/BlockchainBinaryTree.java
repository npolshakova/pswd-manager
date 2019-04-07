package datastruct;

import btree.BinaryTree;
import btree.BlockchainNode;
import storage.RecoverCredentials;
import storage.StoreCredentials;

import java.util.ArrayList;
import java.util.List;

public class BlockchainBinaryTree implements Storage {

    public BinaryTree bt = new BinaryTree();

    public BlockchainBinaryTree(){}

    public BlockchainBinaryTree(String transaction) {
        List<BlockchainNode> values = new ArrayList<>();
        RecoverCredentials.recoverBinaryTree(transaction, values);
        bt.insertAll(values);
    }

    public String search(int id) {
        BlockchainNode b = bt.search(id);
        if(b == null) {
            return null;
        } else {
            return b.value;
        }
    }

    public String update(int id, String credential) {
        return insert(id, credential);
    }

    public String insert(int id, String credential) {
        List<BlockchainNode> lst = bt.insert(id,credential);
        String tx = StoreCredentials.saveUpdatedTree(lst);
        return tx;
    }

    public String delete(int id) {
        List<BlockchainNode> lst = bt.delete(id);
        String tx = StoreCredentials.saveUpdatedTree(lst);
        return tx;
    }


}
