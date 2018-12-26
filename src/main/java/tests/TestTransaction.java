package tests;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;

public class TestTransaction {

    public static void main(String args[]) {
        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";
        String msg = "MyPassword";
        ECKey k = ECKey.fromPrivate(msg.getBytes());
        Script s = p2pk(k);

        //mzHmTaZWLCc3DKnnTNsMSxtCAZDvqwmca5
        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
            }
        };

        kit.startAsync();
        kit.awaitRunning();

        System.out.println(kit.wallet().getBalance());
        System.out.println(kit.wallet().currentReceiveAddress());

        //SEND
        Transaction tx = new Transaction(params);
        Coin coinToSent = Coin.valueOf((long) 30000);
        tx.addOutput(coinToSent, s);
        TransactionInput ti = addInputToTransaction(tx, 0, tx.getHashAsString(), s);
        signInput(tx, ti, k, s, 0);

        System.out.println("Created Transaction!");
        System.out.println(tx.getHashAsString());

        SendRequest request = SendRequest.forTx(tx);
        try {
            kit.wallet().completeTx(request);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
        kit.wallet().commitTx(request.tx);
        kit.peerGroup().broadcastTransaction(request.tx);
        System.out.println(kit.wallet().getBalance());
        System.out.println(tx.getHashAsString());
        //2e28caa8f48025570305f32de049588d9ce6123c62e12164e7eff9d7eb352a46

    }

    private static Script p2pk(ECKey key) {
        ScriptBuilder script = new ScriptBuilder();
        script.data(key.getPubKeyHash());
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_CHECKSIG, null));
        return script.build();
    }

    public static TransactionInput addInputToTransaction(Transaction tx,
                                                         long index, String hash, Script scriptPubKey) {
        return tx.addInput(Sha256Hash.wrap(hash), index, scriptPubKey);
    }

    public static void signInput(Transaction tx, TransactionInput input,
                                 ECKey key, Script script, int index) {
        ScriptBuilder sigScript = new ScriptBuilder();
        Sha256Hash hash = tx.hashForSignature(index, script, Transaction.SigHash.ALL, false);
        ECKey.ECDSASignature ecSig = key.sign(hash);
        TransactionSignature txSig = new TransactionSignature(ecSig,
                Transaction.SigHash.ALL, false);
        sigScript.data(0, txSig.encodeToBitcoin());
        // Add any additional data/pubkeys/etc. to the SigScript here.
        Script scriptWithSig = sigScript.build();
        input.setScriptSig(scriptWithSig);
    }

}
