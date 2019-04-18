package btree;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add paths to update for insert and delete
 */
public class BTree {

    private static final int M = 4;

    public Node root;
    private int height;
    private int n;

    public void insertAll(List<Node> values) {
        return;
    }

    // helper B-tree node data type
    public static final class Node {
        public int m;
        public Entry[] children = new Entry[M];

        // create a node with k children
        private Node(int k) {
            m = k;
        }
    }

    public static class Entry {
        public int key;
        public String val;
        public String tx;
        public Node next;
        public Entry(int key, String val, Node next) {
            this.key  = key;
            this.val  = val;
            this.next = next;
            this.tx = null;
        }

        public void setTx(String transaction) {
            this.tx = transaction;
        }
    }

    public BTree() {
        root = new Node(0);
    }

    public List<Node> insert(int key, String val) {
        List<Node> l = new ArrayList<>();
        Node u = insertHelper(root, key, val, height, l);
        n++;
        if (u == null) return l;

        // split
        Node t = new Node(2);
        t.children[0] = new Entry(root.children[0].key, null, root);
        t.children[1] = new Entry(u.children[0].key, null, u);
        root = t;
        height++;
        l.add(t);
        return l;
    }

    public String search(int key) {
        return searchHelper(root, key, height);
    }

    private String searchHelper(Node x, int key, int ht) {
        Entry[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                if (key == children[j].key) return children[j].val;
            }
        }

        // internal node
        else {
            for (int j = 0; j < x.m; j++) {
                if (j+1 == x.m || key < children[j+1].key)
                    return searchHelper(children[j].next, key, ht-1);
            }
        }
        return null;
    }

    public List<Node> delete(int key) {
        List<Node> l = new ArrayList<>();
        return insert(key, null);
    }

    private Node insertHelper(Node h, int key, String val, int ht, List<Node> l) {
        int j;
        Entry t = new Entry(key, val, null);

        // external node
        if (ht == 0) {
            l.add(h);
            for (j = 0; j < h.m; j++) {
                if (key < h.children[j].key) {
                    break;
                }
            }
        }

        // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j+1 == h.m) || key < h.children[j+1].key) {
                    Node u = insertHelper(h.children[j++].next, key, val, ht-1, l);
                    if (u == null) return null;
                    t.key = u.children[0].key;
                    t.next = u;
                    break;
                }
            }
            l.add(h);
        }

        for (int i = h.m; i > j; i--)
            h.children[i] = h.children[i-1];
        h.children[j] = t;
        h.m++;
        if (h.m < M) return null;
        else         return split(h);
    }


    private Node split(Node h) {
        Node t = new Node(M/2);
        h.m = M/2;
        for (int j = 0; j < M/2; j++)
            t.children[j] = h.children[M/2+j];
        return t;
    }

}
