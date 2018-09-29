
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;

import java.io.File;

public class PswdManager {

public static void main(String args[]) {
    NetworkParameters params = TestNet3Params.get();
    String filePrefix = "forwarding-service-testnet";

    // Parse the address given as the first parameter.
    Address forwardingAddress = new Address(params, args[0]);

    // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
    WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
        @Override
        protected void onSetupCompleted() {
            // This is called in a background thread after startAndWait is called, as setting up various objects
            // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
            // on the main thread.
            if (wallet().getKeyChainGroupSize() < 1)
                wallet().importKey(new ECKey());
        }
    };

    if (params == RegTestParams.get()) {
        // Regression test mode is designed for testing and development only, so there's no public network for it.
        // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
        kit.connectToLocalHost();
    }

// Download the block chain and wait until it's done.
    kit.startAsync();
    kit.awaitRunning();

}
}
