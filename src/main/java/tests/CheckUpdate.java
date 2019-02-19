package tests;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;

public class CheckUpdate {

    public static void main(String args[]) {

        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";

        WalletAppKit kit = new WalletAppKit(params, new File(".."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
            }
        };


       kit.startAsync();
       kit.awaitTerminated();


    }

    //https://testnet.blockchain.info/rawtx/a78dc9247981f9345a4a2431ad4b4f029a013cb5db9f44bbd8326b322d7297a9 WWWOOOO!

    //ae50d064c5d8354680455b19ef2c559a64843287e71be956c4eeb0234c436479
  // Received Reject: tx dfc0f6b28d5d659d07cf814a071730df2b229596b67d793b98ae2488033797fa for reason 'non-final'
}
