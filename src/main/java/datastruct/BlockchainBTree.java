package datastruct;

import btree.btree2;
import storage.RecoverCredentials;
import storage.StoreCredentials;

import java.util.List;

public class BlockchainBTree implements Storage {

    public static void main(String args[]) {
        btree2 b = new btree2();
        List<btree2.Node> inst1 =  b.insert(1, "ok");
        System.out.println(inst1.size());
        List<btree2.Node> inst2 =  b.insert(2, "test");
        System.out.println(inst1.size());
        List<btree2.Node> inst3 =  b.insert(3, "work");
        System.out.println(inst1.size());
    }

    public btree2 bt;

    public BlockchainBTree(){
        bt = new btree2();

    }

    public BlockchainBTree(int size){
         bt = new btree2(size);

    }

    public BlockchainBTree(String transaction) {
        if(transaction != null)
        bt = RecoverCredentials.recoverBTree(transaction);
    }

    public String search(int id) {
       return bt.search(id);
    }

    public String update(int id, String credential) {
        return insert(id, credential);
    }

    public String insert(int id, String credential) {
        List<btree2.Node> lst = bt.insert(id,credential);
        String tx = StoreCredentials.saveUpdatedBTree(lst);
        return tx;
    }

    public String delete(int id) {
        List<btree2.Node> lst = bt.delete(id);
        String tx = StoreCredentials.saveUpdatedBTree(lst);
        return tx;
    }

}
