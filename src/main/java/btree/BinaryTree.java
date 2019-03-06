package btree;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T extends  Node> {

    public T root;

    public List<T> insert(int id, String value) {
        List<T> path = new ArrayList<>();
        root = addRecursive(root, id, value, path);
        return path;
    }

    public List<T> insert(T n) {
        List<T> path = new ArrayList<>();
        root = addRecursive(root, n, path);
        return path;
    }

    private T addRecursive(T current, T n, List<T> path) {
        if (current == null) {
            path.add(n);
            return n;
        }

        if (n.key < current.key) {
            path.add(current);
            current.left = addRecursive((T) current.left, n, path);
        } else if (n.key > current.key) {
            path.add(current);
            current.right = addRecursive((T) current.right, n, path);
        } else {
            // value already exists
            return current;
        }

        return current;
    }

    private T addRecursive(T current, int id, String val, List<T> path) {
        if (current == null) {
            T n = (T) new Node(id, val);
            path.add(n);
            return n;
        }

        if (id < current.key) {
            path.add(current);
            current.left = addRecursive((T) current.left, id, val, path);
        } else if (id > current.key) {
            path.add(current);
            current.right = addRecursive((T) current.right, id, val, path);
        } else {
            // value already exists
            return current;
        }

        return current;
    }

    public T search(int id) {
        return containsNodeRecursive(root, id);
    }

    private T containsNodeRecursive(T current, int id) {
        if (current == null) {
            return current;
        }
        if (id == current.key) {
            return current;
        }
        return id < current.key
                ? containsNodeRecursive((T) current.left, id)
                : containsNodeRecursive((T) current.right, id);
    }

    public List<T> delete(int value) {
        List<T> path = new ArrayList<>();
        root = deleteRecursive(root, value, path);
        return path;
    }

    private T deleteRecursive(T current, int id, List<T> path) {
        if (current == null) {
            return null;
        }

        if (id == current.key) {
            if (current.left == null && current.right == null) {
                return null;
            } else if (current.right == null) {
                return (T) current.left;
            } else if (current.left == null) {
                return (T) current.right;
            } else {
                Node smallestValue = findSmallestValue((T) current.right);
                current.key = smallestValue.key;
                current.value = smallestValue.value;
                current.right = deleteRecursive((T) current.right, smallestValue.key, path);
                return current;
            }
        }
        if (id < current.key) {
            current.left = deleteRecursive((T) current.left, id, path);
            path.add(current);
            return current;
        }
        current.right = deleteRecursive((T) current.right, id, path);
        path.add(current);
        return current;
    }

    private T findSmallestValue(T root) {
        return root.left == null ? root : findSmallestValue((T) root.left);
    }

    public void traversePrint(T node) {
        if (node != null) {
            traversePrint((T) node.left);
            System.out.print("ID: " + node.key + " Value: " + node.value + ", ");
            traversePrint((T) node.right);
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

    public void insertAll(List<T> values) {
        for(T v : values) {
            insert(v);
        }
    }

}
