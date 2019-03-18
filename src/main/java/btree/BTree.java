package btree;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add paths to update for insert and delete
 */
public class BTree {

    private static final int M = 4;

    private Node root;
    private int height;
    private int n;

    // helper B-tree node data type
    private static final class Node {
        private int m;
        private Entry[] children = new Entry[M];

        // create a node with k children
        private Node(int k) {
            m = k;
        }
    }

    private static class Entry {
        private int key;
        private String val;
        private Node next;
        public Entry(int key, String val, Node next) {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    public BTree() {
        root = new Node(0);
    }

    public List<Node> insert(int key, String val) {
        List<Node> l = new ArrayList<>();
        Node u = insertHelper(root, key, val, height, l);
        n++;
        if (u == null) return null;

        // split
        Node t = new Node(2);
        t.children[0] = new Entry(root.children[0].key, null, root);
        t.children[1] = new Entry(u.children[0].key, null, u);
        root = t;
        height++;
        l.add(t);
        return l;
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
            for (j = 0; j < h.m; j++) {
                if (key < h.children[j].key) {
                    l.add(h);
                    break;
                }
            }
        }

        // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j+1 == h.m) || key < h.children[j+1].key) {
                    l.add(h);
                    Node u = insertHelper(h.children[j++].next, key, val, ht-1, l);
                    if (u == null) return null;
                    t.key = u.children[0].key;
                    t.next = u;
                    break;
                }
            }
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
