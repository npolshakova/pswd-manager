package btree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BinaryTree {

    public Node root;

    public BinaryTree(int id, String value) {
        root = new Node(id, value);
    }

    public Node search(Node n, int key) {
        if (n == null || key == n.key) {
            return n;
        }
        if (key < n.key) {
            return search(n.left, key);
        } else {
            return search(n.right, key);
        }
    }

    public List<Node> insert(int key, String value) {
        List<Node> path = new ArrayList<>();
        return insertHelper(root, path, key, value);
    }

    private List<Node> insertHelper(Node current, List<Node> path, int key, String value) {
        if (current == null) {
            current = new Node(key, value);
            if(!path.isEmpty()) {
                current.parent = path.get(path.size() - 1);
            }
            path.add(current);
            return path;
        } else if (key == current.key) {
            // update value
            current.value = value;
            path.add(current);
            return path;
        } else if (key < current.key) {
            path.add(current);
            List<Node> ret = insertHelper(current.left, path, key, value);
            current.left = ret.get(ret.size() - 1);
            return ret;
        } else {
            path.add(current);
            List<Node> ret = insertHelper(current.right, path, key, value);
            current.right = ret.get(ret.size() - 1);
            return ret;
        }
    }


    public List<Node> delete(int key) {
        List<Node> path = new ArrayList<>();
        return deleteHelper(root, path, key);
    }

    private List<Node> deleteHelper(Node current, List<Node> path, int key) {
        if(key < current.key) {
            path.add(current);
            return deleteHelper(current.left, path, key);
        } else if(key > current.key) {
            path.add(current);
            return deleteHelper(current.right, path, key);
        } else {
            if (current.left != null && current.right != null) {
                Node suc = current.right.findMin();
                current.key = suc.key;
                return deleteHelper(suc, path, suc.key);
            } else if (current.left != null) {
                replaceParent(current, current.left);
            } else if(current.right != null) {
                replaceParent(current, current.right);
            } else {
                replaceParent(current, null);
            }
        }
        return null;
    }

    private void replaceParent(Node current, Node newNode) {
        if(current.parent != null) {
            if (current == current.parent.left) {
                current.parent.left = newNode;
            } else if (current == current.parent.right) {
                current.parent.right = newNode;
            }
        }

        if (newNode != null) {
            newNode.parent = current.parent;
        }
    }


    public static void main(String args[]) {
        BinaryTree t = new BinaryTree(1, "hi");
        t.insert(3, "ok");
        System.out.println(t.root);
        t.delete(1);
        System.out.println(t.root);
    }

}


