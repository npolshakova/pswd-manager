package btree;

public class Node {
    public int key;
    public String value;
    public Node left;
    public Node right;
    public Node parent;

    public Node(int id, String val) {
        key = id;
        value = val;
        parent = null;
        left = null;
        right = null;
    }

    public Node findMin() {
        Node curr = this;
        while(curr.left != null) {
            curr =  curr.left;
        }
        return curr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(" ");
        sb.append(value);
        sb.append("\n");

        if (left != null) {
            sb.append("Left: ");
            sb.append(left.toString());
            sb.append("\n");
        }

        if(right != null) {
            sb.append("Right: ");
            sb.append(right.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
