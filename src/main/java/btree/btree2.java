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

        for(int i = 0; i < this.size; i++) {
            if(key < x.values[i].key) {
                Node n = x.children[i];
                String tmp = searchHelper(n, key);
                if (tmp != null) {
                    return tmp;
                }
            }
        }
        return null;
    }

    public List<Node> delete(int key) {
        return insert(key, "N/A");
    }

    private Node insertHelper(Node h, int key, String val, List<Node> l) {
        l.add(h);
        for(int i = 0; i < size; i++) {
            if(h.values[i] == null) {
                if(i != 0) {
                    if (key > h.values[i - 1].key) {
                        h.values[i] = new Entry(key, val);
                        return h;
                    } else {
                        if(h.children[i - 1] == null) {
                            Node n = new Node(key, val);
                            h.children[i - 1] = n;
                            l.add(n);
                            return n;
                        } else {
                            return insertHelper(h.children[i - 1] , key, val, l);
                        }
                    }
                } else {
                    h.values[i] = new Entry(key, val);
                    return h;
                }

            }
        }

        for(int i = 0; i < size; i++) {
            Node n = h.children[i];
            if(n == null) {
                n = new Node();
                h.children[i] = n;
                return insertHelper(n, key, val, l);
            }
        }

        Node n = new Node(key, val);
        h.addChild(n);
        l.add(n);
        return n;
    }



}
