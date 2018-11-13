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
import java.util.HashMap;
import java.util.Map;

public class TestSearch {

    private static final String apiCode = null; // optional

    public static void main(String args[]) {
        String txHash = "6003c2861f78fc1d040e57df2762029fc139960871c0bba5a3434011d621bc37";
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target("https://testnet.blockchain.info/rawtx/" + txHash);

        Invocation.Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON_TYPE);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            System.out.println("Success! " + response.getStatus());
            String body = response.readEntity(String.class);
            System.out.println(body);
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(body).getAsJsonObject();
            System.out.println(jo.get("inputs"));
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
        }

    }

    private static void tryTestnet() {
        String txHash = "6003c2861f78fc1d040e57df2762029fc139960871c0bba5a3434011d621bc37";
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
