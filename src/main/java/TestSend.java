import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

public class TestSend {

    public static void main(String[] args) throws Exception {

        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";

        // Parse the address given as the first parameter.

        String privateKey = "MyPassword";
        byte[] b = privateKey.getBytes();
        ECKey key = ECKey.fromPrivate(b, true);

    }

}
