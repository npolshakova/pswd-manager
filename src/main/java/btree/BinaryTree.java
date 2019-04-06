package btree;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree {

    public BlockchainNode root;

    public List<BlockchainNode> insert(int id, String value) {
        List<BlockchainNode> path = new ArrayList<>();
        root = addRecursive(root, id, value, path);
        return path;
    }

    public List<BlockchainNode> insert(BlockchainNode n) {
        List<BlockchainNode> path = new ArrayList<>();
        root = addRecursive(root, n, path);
        return path;
    }

    private BlockchainNode addRecursive(BlockchainNode current, BlockchainNode n, List<BlockchainNode> path) {
        if (current == null) {
            path.add(n);
            return n;
        }

        if (n.key < current.key) {
            path.add(current);
            current.left = addRecursive(current.left, n, path);
        } else if (n.key > current.key) {
            path.add(current);
            current.right = addRecursive(current.right, n, path);
        } else {
            // value already exists
            return current;
        }

        return current;
    }

    private BlockchainNode addRecursive(BlockchainNode current, int id, String val, List<BlockchainNode> path) {
        if (current == null) {
            BlockchainNode n = new BlockchainNode(id, val);
            path.add(n);
            return n;
        }

        if (id < current.key) {
            path.add(current);
            current.left = addRecursive(current.left, id, val, path);
        } else if (id > current.key) {
            path.add(current);
            current.right = addRecursive(current.right, id, val, path);
        } else {
            // value already exists
            return current;
        }

        return current;
    }

    public BlockchainNode search(int id) {
        return containsNodeRecursive(root, id);
    }

    private BlockchainNode containsNodeRecursive(BlockchainNode current, int id) {
        if (current == null) {
            return current;
        }
        if (id == current.key) {
            return current;
        }
        return id < current.key
                ? containsNodeRecursive(current.left, id)
                : containsNodeRecursive(current.right, id);
    }

    public List<BlockchainNode> delete(int value) {
        List<BlockchainNode> path = new ArrayList<>();
        root = deleteRecursive(root, value, path);
        return path;
    }

    private BlockchainNode deleteRecursive(BlockchainNode current, int id, List<BlockchainNode> path) {
        if (current == null) {
            return null;
        }

        if (id == current.key) {
            if (current.left == null && current.right == null) {
                return null;
            } else if (current.right == null) {
                return current.left;
            } else if (current.left == null) {
                return current.right;
            } else {
                BlockchainNode smallestValue = findSmallestValue(current.right);
                current.key = smallestValue.key;
                current.value = smallestValue.value;
                current.right = deleteRecursive(current.right, smallestValue.key, path);
                return current;
            }
        }
        if (id < current.key) {
            current.left = deleteRecursive(current.left, id, path);
            path.add(current);
            return current;
        }
        current.right = deleteRecursive(current.right, id, path);
        path.add(current);
        return current;
    }

    private BlockchainNode findSmallestValue(BlockchainNode root) {
        return root.left == null ? root : findSmallestValue(root.left);
    }

    public void traversePrint(BlockchainNode node) {
        if (node != null) {
            traversePrint(node.left);
            System.out.print("ID: " + node.key + " Value: " + node.value + ", ");
            traversePrint(node.right);
        }
    }

    public static void main(String args[]) {

        BinaryTree bt = new BinaryTree();

        bt.insert(6, "a");
        bt.insert(4, "b");
        bt.insert(8, "c");
        bt.insert(3, "d");
        bt.insert(5, "e");
        bt.insert(7, "f");
        bt.insert(9, "g");

        //bt.traversePrint(bt.root);

    }

    public void insertAll(List<BlockchainNode> values) {
        for(BlockchainNode v : values) {
            insert(v);
        }
    }

}
