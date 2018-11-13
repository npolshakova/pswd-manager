import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;

public class TestAddress {

    public static void main(String args[]) {
        NetworkParameters params = TestNet3Params.get();
        String msg = "MyPassword";
        Script s = ScriptBuilder.createP2SHOutputScript(Utils.sha256hash160(msg.getBytes()));

        Address addr = Address.fromP2SHScript(params, s);
        System.out.println(addr);

        System.out.println(addr.getHash160());

        ECKey k = ECKey.fromPrivate(msg.getBytes());
        Script s2 = p2pkh(k);
        Address addr2 = s2.getToAddress(params);
        System.out.println(addr2);

        // TODO: recover with signature?
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

}
