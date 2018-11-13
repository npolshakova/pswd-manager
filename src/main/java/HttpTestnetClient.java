//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import info.blockchain.api.APIException;
import info.blockchain.api.HttpClientInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpTestnetClient implements HttpClientInterface {
    private static final String BASE_URL = "https://testnet.blockchain.info/";
    public static volatile int TIMEOUT_MS = 10000;
    private static HttpClientInterface instance;

    public HttpTestnetClient() {
    }

    public static synchronized HttpClientInterface getInstance() {
        if (instance == null) {
            Class var0 = HttpTestnetClient.class;
            synchronized(HttpTestnetClient.class) {
                if (instance == null) {
                    instance = new HttpTestnetClient();
                }
            }
        }

        return instance;
    }

    public static void setCustomHttpClient(HttpClientInterface httpClient) {
        instance = httpClient;
    }

    public String get(String resource, Map<String, String> params) throws APIException, IOException {
        return openURL("https://blockchain.info/", resource, params, "GET");
    }

    public String get(String baseURL, String resource, Map<String, String> params) throws APIException, IOException {
        return openURL(baseURL, resource, params, "GET");
    }

    public String post(String resource, Map<String, String> params) throws APIException, IOException {
        return openURL("https://blockchain.info/", resource, params, "POST");
    }

    public String post(String baseURL, String resource, Map<String, String> params) throws APIException, IOException {
        return openURL(baseURL, resource, params, "POST");
    }

    private static String openURL(String baseURL, String resource, Map<String, String> params, String requestMethod) throws APIException, IOException {
        String encodedParams = urlEncodeParams(params);
        URL url = null;
        APIException apiException = null;
        IOException ioException = null;
        String responseStr = null;
        if (requestMethod.equals("GET")) {
            if (encodedParams.isEmpty()) {
                url = new URL(baseURL + resource);
            } else {
                url = new URL(baseURL + resource + '?' + encodedParams);
            }
        } else if (requestMethod.equals("POST")) {
            url = new URL(baseURL + resource);
        }

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(TIMEOUT_MS);
            if (requestMethod.equals("POST")) {
                byte[] postBytes = encodedParams.getBytes("UTF-8");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postBytes.length));
                conn.getOutputStream().write(postBytes);
                conn.getOutputStream().close();
            }

            if (conn.getResponseCode() != 200) {
                apiException = new APIException(inputStreamToString(conn.getErrorStream()));
            } else {
                responseStr = inputStreamToString(conn.getInputStream());
            }
        } catch (IOException var19) {
            ioException = var19;
        } finally {
            try {
                if (apiException != null) {
                    conn.getErrorStream().close();
                }

                conn.getInputStream().close();
            } catch (Exception var18) {
                ;
            }

            if (ioException != null) {
                throw ioException;
            }

            if (apiException != null) {
                throw apiException;
            }

        }

        return responseStr;
    }

    private static String urlEncodeParams(Map<String, String> params) {
        String result = "";
        if (params != null && params.size() > 0) {
            try {
                StringBuilder data = new StringBuilder();
                Iterator var3 = params.entrySet().iterator();

                while(var3.hasNext()) {
                    Entry<String, String> kvp = (Entry)var3.next();
                    if (data.length() > 0) {
                        data.append('&');
                    }

                    data.append(URLEncoder.encode((String)kvp.getKey(), "UTF-8"));
                    data.append('=');
                    data.append(URLEncoder.encode((String)kvp.getValue(), "UTF-8"));
                }

                result = data.toString();
            } catch (UnsupportedEncodingException var5) {
                ;
            }
        }

        return result;
    }

    private static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder responseStringBuilder = new StringBuilder();
        String line = "";

        while((line = reader.readLine()) != null) {
            responseStringBuilder.append(line);
        }

        reader.close();
        return responseStringBuilder.toString();
    }
}
