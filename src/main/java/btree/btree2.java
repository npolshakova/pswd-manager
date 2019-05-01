package btree;

import java.util.ArrayList;
import java.util.List;

public class btree2 {

    public btree2() {
        root = new Node();
    }

    public  btree2(int size) {
        this.size = size;
        root = new Node();
    }

    public Node root;
    public int size = 3;

    public class Node {
        public Node[] children = new Node[size];
        public Entry[] values = new Entry[size];
        public String tx;
        public int index;

        public Node(int key, String value) {
            Entry e = new Entry(key, value);
            for(int i = 0; i < size; i++) {
                if(values[i] == null) {
                    values[i] = e;
                    break;
                }
            }
        }

        public Node() {

        }

        public void updateTx(String tx) {
            this.tx = tx;
        }

        public void insertEntry(int key, String value) {
            Entry e = new Entry(key, value);
            for(int i = 0; i < size; i++) {
                if(values[i] == null) {
                    values[i] = e;
                    break;
                }
            }
        }

        public  void addChild(Node n) {
            for(int i = 0; i < size; i++) {
                if(children[i] == null) {
                    children[i] = n;
                    n.index = i;
                    break;
                }
            }
        }

        public boolean canInsert() {
            for(int i = 0; i < size; i++) {
                if(children[i] == null) {
                    return true;
                }
            }
            return false;
        }
    }

    public class Entry {
        public int key;
        public String value;

        public Entry(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public List<Node> insert(int key, String val) {
        List<Node> l = new ArrayList<>();
        insertHelper(root, key, val, l);
        return l;
    }

    public String search(int key) {
        return searchHelper(root, key);
    }

    private String searchHelper(Node x, int key) {
        for(Entry e : x.values) {
            if(e.key == key) {
                return e.value;
            }
        }

        for(Node n : x.children) {
            String tmp = searchHelper(n, key);
            if(tmp != null) {
                return tmp;
            }
        }
        return null;
    }

    public List<Node> delete(int key) {
        return insert(key, null);
    }

    private Node insertHelper(Node h, int key, String val, List<Node> l) {
        l.add(h);
        for(int i = 0; i < size; i++) {
            if(h.values[i] == null) {
                h.values[i] = new Entry(key, val);
                return h;
            }
        }

        for(Node n : h.children) {
            if(n.canInsert()) {
                l.add(n);
                return insertHelper(h, key, val, l);
            }
        }

        Node n = new Node(key, val);
        h.addChild(n);
        l.add(n);
        return n;
    }



}
