import com.sun.istack.internal.NotNull;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.RedeemData;

public class TestAddress {

    public static void main(String args[]) {
        NetworkParameters params = TestNet3Params.get();
        String msg = "MyPassword";

        // Method 1 P2SH Script:

        Script s = ScriptBuilder.createP2SHOutputScript(Utils.sha256hash160(msg.getBytes()));
        Address addr = Address.fromP2SHScript(params, s);
        System.out.println("P2SH: " + addr);

        // Method 2 P2PKH Script:

        ECKey k = ECKey.fromPrivate(msg.getBytes());
        Script s2 = p2pkh(k);
        Address addr2 = s2.getToAddress(params);
        System.out.println("P2PKH: " + addr2);

        // Method 3 p2pk
        Script s3 = p2pk(k);
        byte[] pk = s3.getPubKey();
        System.out.println("P2PK: " + pk);

        // Method 4 P2SH w/t redeem data:
//        TransactionOutPoint t = new TransactionOutPoint(params);
//        RedeemData rd = t.getConnectedRedeemData();
//        Script s4 = P2SHRedeemData(k);
//        Address addr4 = s4.getToAddress(params);
//        System.out.println("Redeem Data: " + addr4);

        // Method 5 opReturn:
//        Script s5 = opReturn("MyPassword");
//        Address addr5 = s5.getToAddress(params);
//        System.out.println("OpReturn: " + addr5);

        // TODO: recover with signature:

        Transaction tx = new Transaction(params);
        TransactionInput input = tx.getInput(0);
        int index = 0;

        ScriptBuilder sigScript = new ScriptBuilder();
        Sha256Hash hash = tx.hashForSignature(index, s2, Transaction.SigHash.ALL, false);
        ECKey.ECDSASignature ecSig = k.sign(hash);
        TransactionSignature txSig = new TransactionSignature(ecSig,
                Transaction.SigHash.ALL, false);
        sigScript.data(0, txSig.encodeToBitcoin());
        // Add any additional data/pubkeys/etc. to the SigScript here.
        Script scriptWithSig = sigScript.build();
        input.setScriptSig(scriptWithSig);

    }

    private static Script p2pkh(ECKey key) {
        ScriptBuilder script = new ScriptBuilder();
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_DUP, null));
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_HASH160, null));
        script.data(key.getPubKeyHash());
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_EQUALVERIFY,
                null));
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_CHECKSIG, null));
        return script.build();
    }


    private static Script p2pk(ECKey key) {
        ScriptBuilder script = new ScriptBuilder();
        script.data(key.getPubKeyHash());
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_CHECKSIG, null));
        return script.build();
    }

    private static Script P2SHRedeemData(RedeemData redeemScriptHash) {
        Script rsh = redeemScriptHash.redeemScript;
        ScriptBuilder script = new ScriptBuilder();
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_HASH160, null));
        for(ScriptChunk r : rsh.getChunks()) {
            script.addChunk(r);
        }
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_EQUAL, null));
        return script.build();
    }

    private static Script opReturn(String data) {
        ScriptBuilder script = new ScriptBuilder();
        script.addChunk(new ScriptChunk(ScriptOpCodes.OP_RETURN, null));
        script.data(data.getBytes());
        return script.build();
    }
}
