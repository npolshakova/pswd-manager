package btree;

import org.bitcoinj.core.Address;

import java.util.ArrayList;
import java.util.List;


public class BlockchainNode extends Node {

    public String leftTx = "";
    public String rightTx = "";
    public List<Address> leftAddresses = new ArrayList<>();
    public List<Address> rightAddresses = new ArrayList<>();
    public List<Address> valueAddress = new ArrayList<>();
    public Address idAddress;

    public BlockchainNode(int id, String val) {
        super(id, val);
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
