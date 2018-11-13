import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;

public class TestRecieve {

    public static void main(String[] args) throws Exception {

        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";

        // Parse the address given as the first parameter.
        final Address forwardingAddress = Address.fromBase58(params, "mwyPZ4i9h3zYFx6BdiH6aKftqCHxr2txtG");

        //mriqZ2eCT2hBmpKyMS4q33MoFEVyR4p1fu
        //Has money: mn9mno1NkVzjgCpNCsPsM1wdDdZM2EXJQt
        // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
                //wallet().importKey(DumpedPrivateKey.fromBase58(params, "mwyPZ4i9h3zYFx6BdiH6aKftqCHxr2txtG").getKey());
            }
        };

        // Download the block chain and wait until it's done.
        kit.startAsync();
        kit.awaitRunning();

        System.out.println(kit.wallet().getBalance());
        System.out.println(kit.wallet().currentReceiveAddress());


        //SEND
        Transaction tx = new Transaction(params);
        Coin coinToSent = Coin.valueOf((long) 30000);
        Coin coinToChange = Coin.valueOf(kit.wallet().getBalance().getValue() - (long) 1.0);
        tx.addOutput(coinToSent, forwardingAddress);

        System.out.println("Created Transaction!");
        System.out.println(tx.getHashAsString());

        SendRequest request = SendRequest.forTx(tx);
        try {
            System.out.println("Complete transaction...");
            kit.wallet().completeTx(request);
            System.out.println("okkk..");
        } catch (InsufficientMoneyException e) {
            System.out.println("SAD");
            e.printStackTrace();
        }
        kit.wallet().commitTx(request.tx);
        kit.peerGroup().broadcastTransaction(request.tx);
        System.out.println("DONE");
        System.out.println(kit.wallet().getBalance());
        System.out.println(tx.getHashAsString());

    }

}
