package tests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import info.blockchain.api.APIException;
import info.blockchain.api.blockexplorer.entity.Transaction;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSearch {

    private static final String apiCode = null; // optional

    public static void main(String args[]) {
        String txHash = "1527fe40163c224762150bdefdc79b223d9810186bc49fa22107c4b241801dd2";
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("https://testnet.blockchain.info/rawtx/" + txHash);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            //System.out.println("Success! " + response.getStatus());
            String body = response.readEntity(String.class);
            //System.out.println(body);
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(body).getAsJsonObject();
            //System.out.println(jo.get("inputs"));
            //System.out.println(jo.get("out"));
            JsonElement outputs = jo.get("out");
            JsonArray jr = outputs.getAsJsonArray();
            List<String> addr = new ArrayList<>();
            for (JsonElement j : jr) {
                //System.out.println(j.getAsJsonObject().get("addr").toString().replaceAll("[\"]", ""));
                addr.add(j.getAsJsonObject().get("addr").toString().replaceAll("[\"]", ""));
            }
            String[] children = recoverTransactions(addr);
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
        }

    }

    private static String[] recoverTransactions(List<String> addr) {
        // TODO worst case
        System.out.println(addr);
        String[] left = {"", "", ""};
        String[] right = {"", "", ""};

        for(String s : addr) {
            if(s.charAt(2) == 'r') {
                int index = Integer.parseInt(String.valueOf(s.charAt(3))) - 2;
                right[index] = s.substring(4,29);
            } else if(s.charAt(2) == 'w') {
                int index = Integer.parseInt(String.valueOf(s.charAt(3))) - 2;
                left[index] = s.substring(4,29);
            }
        }

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        for(int i = 0; i < 3; i++) {
            sb1.append(left[i]);
            sb2.append(right[i]);
        }

        String leftStr = decodeChars(sb1.toString());
        String rightStr = decodeChars(sb2.toString());
        String[] ret = new String[2];
        System.out.println(leftStr);
        System.out.println(rightStr);

        return ret; // TODO

    }


    private static String decodeChars(String str) {
        System.out.println(str);
        str = str.replaceAll("abc","l");
        str = str.replaceAll("def", "1");
        str = str.replaceAll("ghi", "o");
        str = str.replaceAll("jkm", "0");
        System.out.println(str);
        return str;
    }

    private static void tryTestnet() {
        String txHash = "1527fe40163c224762150bdefdc79b223d9810186bc49fa22107c4b241801dd2";
        String response = null;
        try {
            response = HttpTestnetClient.getInstance().get("rawtx/" + txHash, buildBasicRequest());
        } catch (APIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject txJson = new JsonParser().parse(response).getAsJsonObject();
        Transaction tx =  new Transaction(txJson);
        System.out.println(tx.getHash());
        System.out.println(tx.getOutputs());
        System.out.println(tx.getInputs());
    }

    private static Map<String, String> buildBasicRequest() {
        Map<String, String> params = new HashMap<String, String>();

        params.put("format", "json");
        if (apiCode != null) {
            params.put("api_code", apiCode);
        }

        return params;
    }

}
