package btree;

import org.bitcoinj.core.Address;

import java.util.ArrayList;
import java.util.List;


public class BlockchainNode {

    public String leftTx = "";
    public String rightTx = "";
    public List<Address> leftAddresses = new ArrayList<>();
    public List<Address> rightAddresses = new ArrayList<>();
    public List<Address> valueAddress = new ArrayList<>();
    public Address idAddress;

        public int key;
        public String value;
        public BlockchainNode left;
        public BlockchainNode right;
        public BlockchainNode parent;


            public BlockchainNode(int id, String val) {
                key = id;
                value = val;
                parent = null;
                left = null;
                right = null;
            }

            public BlockchainNode findMin() {
                BlockchainNode curr = this;
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


    public void setIdAddress(Address idAddress) {
        this.idAddress = idAddress;
    }

    public void setLeftTx(String leftTx) {
        this.leftTx = leftTx;
    }

    public void setRightTx(String rightTx) {
        this.rightTx = rightTx;
    }

    public void setLeftAddresses(List<Address> leftAddresses) {
        this.leftAddresses = leftAddresses;
    }

    public void setRightAddresses(List<Address> rightAddresses) {
        this.rightAddresses = rightAddresses;
    }

    public void setValueAddress(List<Address> valueAddress) {
        this.valueAddress = valueAddress;
    }

}
