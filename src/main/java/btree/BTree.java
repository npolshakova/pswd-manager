package btree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTree {

    Node root;

    class Node {

        Node parent;
        Map<Integer, Node> children;
        int key;
        String credentials;
        String transactionID;

        public Node(int key, String credentials, String transactionID) {
            this.key = key;
            this.credentials = credentials;
            this.transactionID = transactionID;
            parent = null;
            children = new HashMap<>();
        }

        public void addChild(Node n) {
            n.parent = this;
            this.children.put(n.key, n);
        }

        public String getEncoding() {
            StringBuilder sb = new StringBuilder();

            // encode ID
            sb.append(this.key);
            // encode credential values
            sb.append(this.credentials);
            // for each child, get transaction ID
            for(Node c : this.children.values()) {
                sb.append(c.transactionID);
            }
            return sb.toString();
        }

    }

    public Node search(Node current, int key) {
        if(current.key == key) {
            return current;
        } else {
            for (Node c : current.children.values()) {
                if(c.key == key) {
                    return c;
                } else if(c.key < key) {
                    return search(c, key);
                }
            }
        }

        return null;
    }


    public List<Node> update(Node oldNode, Node toUpdate) {
        Node oldParent = oldNode.parent;
        List<Node> path = insertParent(oldParent, toUpdate);
        path.add(toUpdate);
        return path;
    }

    // returns list of nodes to retransmit
    public List<Node> insert(Node toInsert) {
        Node parent = getParent(root, toInsert);
        List<Node> path = insertParent(parent, toInsert);
        path.add(toInsert);
        return path;
    }

    private Node getParent(Node current, Node toInsert) {
       if(current.children == null) {
           return current;
       } else {
           int tmp = current.children.get(0).key;
           if (tmp > toInsert.key) {
               return current.children.get(0);
           } else {
               for (Node c : current.children.values()) {
                   if (c.key < toInsert.key) {
                       tmp = c.key;
                   } else if(c.key > toInsert.key) {
                       return getParent(current.children.get(tmp), toInsert);
                   }
               }
               return current;
           }
       }
    }

    public List<Node> insertParent(Node oldParent, Node toInsert) {
        Node newParent = updateChildren(oldParent, toInsert);
        if(oldParent.equals(root)) {
            List<Node> path = new ArrayList<>();
            path.add(newParent);
            return  path;
        } else {
            List<Node> path = insertParent(newParent.parent, toInsert);
            path.add(newParent);
            return path;
        }
    }

    private Node updateChildren(Node oldParent, Node toInsert) {
        oldParent.children.put(toInsert.key, toInsert);
        toInsert.parent = oldParent;
        return  oldParent;
    }

}
