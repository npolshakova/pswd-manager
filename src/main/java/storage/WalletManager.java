package storage;

import com.google.common.util.concurrent.ListenableFuture;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Setups Bitcoin wallet and sends transactions
 */
public class WalletManager {


    public static WalletAppKit kit;
    public static NetworkParameters params = TestNet3Params.get();

    public static void main(String args[]) {
        setupWallet();
    }

    public static void setupWallet() {
        String filePrefix = "forwarding-service-testnet";

        kit = new WalletAppKit(params, new File("../"), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
            }
        };
        kit.startAsync();
        kit.awaitRunning();

        kit.wallet().allowSpendingUnconfirmedTransactions();
        System.out.println("Current address: " + kit.wallet().currentReceiveAddress());

    }

    public static Transaction createTransaction(Address address) {
        CoinSelector c = kit.wallet().getCoinSelector();
        Transaction tx = new Transaction(params);
        Coin coinToSent = Coin.valueOf((long) 600.0);
        c.select(coinToSent, kit.wallet().calculateAllSpendCandidates());
        tx.addOutput(coinToSent, address);
        return tx;
    }

    public static String send(Transaction tx) {
        SendRequest request = SendRequest.forTx(tx);
        try {
            kit.wallet().completeTx(request);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }

        TransactionBroadcast tb = kit.peerGroup().broadcastTransaction(request.tx);
        ListenableFuture listenableFuture = tb.future();
        try {
            listenableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return request.tx.getHashAsString();
    }


}
