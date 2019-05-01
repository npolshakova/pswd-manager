package storage;

import btree.btree2;
import btree.BlockchainNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import datastruct.LinkedList;
import datastruct.LinkedNode;
import datastruct.StorageNode;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecoverCredentials {

    public static void main(String args[]) {
        String txHash = "914cec9183ee2343fee2ab7b0d55870dd6a074ed425a1b99cd8f7c63a6ab3e71";

        //bb2661ebd28b9042c7756b0a739db6eaf0a04e0eb71fe1b9d0183b95ae8e0587
        //445edf4f19a1893b59adf7d811aa5bc0874da4755d4239abc656da09735524a0

        List<BlockchainNode> values = new ArrayList<>();
        recoverBinaryTree(txHash, values);

    }

    /**
     * Finds all nodes stored in blockchain
     * @param value Transaction Hash as String
     * @param values Blockchain Nodes stored in blockchain
     */
    public static void recoverBinaryTree(String value, List<BlockchainNode> values) {
        BlockchainNode bn = recoverBinaryTreeNode(value);
        if(bn != null) {
            values.add(bn);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!bn.leftTx.equals("")) {
                recoverBinaryTree(bn.leftTx, values);
            }

            if (!bn.rightTx.equals("")) {
                recoverBinaryTree(bn.rightTx, values);
            }
        }
    }

    /**
     * Recovers one node stored in blockchain
     * @param txHash Transaction Hash
     * @return Blockchain Node stored at hash
     */
    public static BlockchainNode recoverBinaryTreeNode(String txHash) {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("https://api.blockcypher.com/v1/btc/test3/txs/" + txHash);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String body = response.readEntity(String.class);
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(body).getAsJsonObject();
            JsonElement outputs = jo.get("outputs");
            JsonArray jr = outputs.getAsJsonArray();
            List<String> addr = new ArrayList<>();
            for (JsonElement j : jr) {
                String str = j.getAsJsonObject().get("addresses").toString().replaceAll("[\"]", "");
                if(!str.equals("null")) {
                    addr.add(str.replaceAll("\\[", "").replaceAll("\\]", ""));
                }
            }
            BlockchainNode bn = recoverTransactionsBinaryTree(addr);
            return bn;
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
            return null;
        }
    }

    /**
     * Reconstructs transaction from address strings
     * @param addr
     * @return
     */
    private static BlockchainNode recoverTransactionsBinaryTree(List<String> addr) {
        String[] left = {"", "", "", ""};
        String[] right = {"", "", "", ""};
        String id = "";

        List<String> valueStrs = new ArrayList<>();

        for(String s : addr) {
                s = GenerateAddress.decodeAddress(s);
                if (s.substring(1,3).equals("R:")) {
                    int index = Integer.parseInt(String.valueOf(s.charAt(3)));
                    if(s.charAt(3) == '3') {
                        right[index] = s.substring(4,17);
                    } else {
                        right[index] = s.substring(4, 21);
                    }
                } else if (s.substring(1,3).equals("L:")) {
                    int index = Integer.parseInt(String.valueOf(s.charAt(3)));
                    if(s.charAt(3) == '3') {
                        left[index] = s.substring(4,17);
                    } else {
                        left[index] = s.substring(4, 21);
                    }
                } else if (s.substring(1,3).equals("V:")) {
                    int index = Integer.parseInt(String.valueOf(s.charAt(3)));
                    valueStrs.add(index, s.substring(4,21));
                } else if (s.substring(1,4).equals("id:")) {
                    s = s.split("x")[0];
                    id += s.substring(4);
                }
        }

        StringBuilder leftStr = new StringBuilder();
        StringBuilder rightStr = new StringBuilder();

        for(int i = 0; i < 4; i++) {
            leftStr.append(left[i]);
            rightStr.append(right[i]);
        }

        String value = "";
        for(String s : valueStrs) {
            value += value + s;
        }

        BlockchainNode bn = new BlockchainNode(Integer.parseInt(id), value);
        bn.leftTx = leftStr.toString();
        bn.rightTx = rightStr.toString();
        return bn;

    }

    public static StorageNode recoverStorageNode(String txHash) {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("https://api.blockcypher.com/v1/btc/test3/txs/" + txHash);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String body = response.readEntity(String.class);
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(body).getAsJsonObject();
            JsonElement outputs = jo.get("outputs");
            JsonArray jr = outputs.getAsJsonArray();
            List<String> addr = new ArrayList<>();
            for (JsonElement j : jr) {
                String str = j.getAsJsonObject().get("addresses").toString().replaceAll("[\"]", "");
                if(!str.equals("null")) {
                    addr.add(str.replaceAll("\\[", "").replaceAll("\\]", ""));
                }
            }
            StorageNode sn = recoverTransactionsStorageNode(addr);
            return sn;
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
            return null;
        }
    }

    private static StorageNode recoverTransactionsStorageNode(List<String> addr) {
        Map<Integer, String> credentials = new HashMap<>();
        List<Address> addressList = new ArrayList<>();

        Map<Integer, List<String>> assembleCred = new HashMap<>();
        for(String a : addr) {
            addressList.add(Address.fromBase58(WalletManager.params, a));
            String s = GenerateAddress.decodeAddress(a);
            String[] parts = s.split("$");
            int id = Integer.parseInt(parts[0]);
            int count = Integer.parseInt(parts[1]);
            String val = parts[2];
            if(assembleCred.containsKey(id)) {
                assembleCred.get(id).add(count, val);
            } else {
                List<String> vals = new ArrayList<>();
                vals.add(count,val);
                assembleCred.put(id,vals);
            }
        }

        for(Map.Entry<Integer, List<String>> m : assembleCred.entrySet()) {
            String str = "";
            for(String strpart : m.getValue()) {
                str += strpart;
            }
            credentials.put(m.getKey(), str);
        }

        StorageNode sn = new StorageNode(credentials, addressList);

        return sn;

    }

    public static LinkedNode recoverLinkedNode(String txHash) {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("https://api.blockcypher.com/v1/btc/test3/txs/" + txHash);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String body = response.readEntity(String.class);
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(body).getAsJsonObject();
            JsonElement outputs = jo.get("outputs");
            JsonArray jr = outputs.getAsJsonArray();
            List<String> addr = new ArrayList<>();
            for (JsonElement j : jr) {
                String str = j.getAsJsonObject().get("addresses").toString().replaceAll("[\"]", "");
                if(!str.equals("null")) {
                    addr.add(str.replaceAll("\\[", "").replaceAll("\\]", ""));
                }
            }
            LinkedNode sn = recoverTransactionsLinkedNode(addr);
            return sn;
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
            return null;
        }
    }

    private static LinkedNode recoverTransactionsLinkedNode(List<String> addr) {
        Map<Integer, String> credentials = new HashMap<>();
        List<Address> addressList = new ArrayList<>();
        String[] txParts = {"", "", "", ""};

        Map<Integer, List<String>> assembleCred = new HashMap<>();
        for(String a : addr) {
            addressList.add(Address.fromBase58(WalletManager.params, a));
            String s = GenerateAddress.decodeAddress(a);
            if (s.charAt(1) == '$') {
                int index = Integer.parseInt(String.valueOf(s.charAt(2)));
                if(s.substring(13,21).equals("xxxxxxxx")) {
                    txParts[index] = s.substring(3,13);
                } else {
                    txParts[index] = s.substring(3, 21);
                }
            } else {
                String[] parts = s.split("$");
                int id = Integer.parseInt(parts[0]);
                int count = Integer.parseInt(parts[1]);
                String val = parts[2];
                if (assembleCred.containsKey(id)) {
                    assembleCred.get(id).add(count, val);
                } else {
                    List<String> vals = new ArrayList<>();
                    vals.add(count, val);
                    assembleCred.put(id, vals);
                }
            }
        }

        for(Map.Entry<Integer, List<String>> m : assembleCred.entrySet()) {
            String str = "";
            for(String strpart : m.getValue()) {
                str += strpart;
            }
            credentials.put(m.getKey(), str);
        }

        StringBuilder txStr = new StringBuilder();

        for(int i = 0; i < 4; i++) {
            txStr.append(txParts[i]);
        }

        LinkedNode ln = new LinkedNode(credentials, addressList, txStr.toString());

        return ln;
    }

    public static LinkedList recoverLinkedList(String txHash) {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("https://api.blockcypher.com/v1/btc/test3/txs/" + txHash);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String body = response.readEntity(String.class);
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(body).getAsJsonObject();
            JsonElement outputs = jo.get("outputs");
            JsonArray jr = outputs.getAsJsonArray();
            List<String> addr = new ArrayList<>();
            for (JsonElement j : jr) {
                String str = j.getAsJsonObject().get("addresses").toString().replaceAll("[\"]", "");
                if(!str.equals("null")) {
                    addr.add(str.replaceAll("\\[", "").replaceAll("\\]", ""));
                }
            }
            LinkedList lst = recoverTransactionsLinkedList(addr, txHash);
            return lst;
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
            return null;
        }
    }

    private static LinkedList recoverTransactionsLinkedList(List<String> addr, String tx) {
        List<LinkedNode> nodes = new ArrayList<>();
        LinkedNode ln = recoverTransactionsLinkedNode(addr);
        nodes.add(ln);
        while(ln.nextTx != null) {
            ln = recoverLinkedNode(ln.nextTx);
            nodes.add(ln);
        }
        return new LinkedList(nodes, tx);
    }

    public static btree2 recoverBTree(String transaction) {
        btree2 bt = new btree2();
        recoverBTreeNode(bt, transaction);
        if(bt.root != null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(btree2.Node n : bt.root.children) {
                if(n != null)
                recoverBTree(n.tx);
            }
        }
        return bt;
    }

    public static void recoverBTreeNode(btree2 bt, String txHash) {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("https://api.blockcypher.com/v1/btc/test3/txs/" + txHash);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String body = response.readEntity(String.class);
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(body).getAsJsonObject();
            JsonElement outputs = jo.get("outputs");
            JsonArray jr = outputs.getAsJsonArray();
            List<String> addr = new ArrayList<>();
            for (JsonElement j : jr) {
                String str = j.getAsJsonObject().get("addresses").toString().replaceAll("[\"]", "");
                if(!str.equals("null")) {
                    addr.add(str.replaceAll("\\[", "").replaceAll("\\]", ""));
                }
            }
            recoverTransactionsBTree(bt, addr);
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
        }
    }

    private static void recoverTransactionsBTree(btree2 bt, List<String> addr) {
        List<List<String>> children = new ArrayList<>();
        String id = "";

        List<String> valueStrs = new ArrayList<>();

        for(String s : addr) {
            s = GenerateAddress.decodeAddress(s);
            if (s.substring(1,3).equals("C:")) {
                int childIndex = Integer.parseInt(String.valueOf(s.charAt(3)));
                List<String> lst = children.get(childIndex);
                if(lst == null) {
                    lst = new ArrayList<>();
                }
                int index = Integer.parseInt(String.valueOf(s.charAt(5)));
                if(index == 3) {
                    lst.add(index, s.substring(6,17));
                } else {
                    lst.add(index, s.substring(6,21));
                }
            } else if (s.substring(1,3).equals("V:")) {
                int index = Integer.parseInt(String.valueOf(s.charAt(3)));
                valueStrs.add(index, s.substring(4,21));
            } else if (s.substring(1,4).equals("id:")) {
                s = s.split("x")[0];
                id += s.substring(4);
            }
        }

        List<String> transactionPointers = new ArrayList<>();
        for(List<String> child : children) {
            StringBuilder txStr = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                txStr.append(child.get(i));
            }
            transactionPointers.add(txStr.toString());
        }

        String value = "";
        for(String s : valueStrs) {
            value += value + s;
        }

        bt.insert(Integer.parseInt(id), value);
    }
}
