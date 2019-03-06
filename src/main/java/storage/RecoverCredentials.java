package storage;

import btree.BlockchainNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
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
        recoverAllValues(txHash, values);

    }

    public static void recoverAllValues(String value, List<BlockchainNode> values) {
        BlockchainNode bn = recoverValue(value);
        if(bn != null) {
            values.add(bn);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!bn.leftTx.equals("")) {
                recoverAllValues(bn.leftTx, values);
            }

            if (!bn.rightTx.equals("")) {
                recoverAllValues(bn.rightTx, values);
            }
        }
    }

    public static BlockchainNode recoverValue(String txHash) {
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
            BlockchainNode bn = recoverTransactions(addr);
            return bn;
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
            return null;
        }
    }

    private static BlockchainNode recoverTransactions(List<String> addr) {
        String[] left = {"", "", "", ""};
        String[] right = {"", "", "", ""};
        String id = "";

        List<String> valueStrs = new ArrayList<>();

        for(String s : addr) {
                s = EncryptAddress.decodeAddress(s);
                if (s.charAt(1) == 'R') {
                    int index = Integer.parseInt(String.valueOf(s.charAt(2)));
                    if(s.substring(13,21).equals("xxxxxxxx")) {
                        right[index] = s.substring(3,13);
                    } else {
                        right[index] = s.substring(3, 21);
                    }
                } else if (s.charAt(1) == 'L') {
                    int index = Integer.parseInt(String.valueOf(s.charAt(2)));
                    if(s.substring(13,21).equals("xxxxxxxx")) {
                        left[index] = s.substring(3,13);
                    } else {
                        left[index] = s.substring(3, 21);
                    }
                } else if (s.substring(1,3).equals("V:")) {
                    int index = Integer.parseInt(String.valueOf(s.charAt(3)));
                    valueStrs.add(index, s.substring(4,21));
                } else if (s.substring(1,4).equals("id:")) {
                    id += s.charAt(4);
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

}
