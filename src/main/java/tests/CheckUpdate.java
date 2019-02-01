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

        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
            }
        };


       kit.startAsync();
       kit.awaitTerminated();


    }

}
