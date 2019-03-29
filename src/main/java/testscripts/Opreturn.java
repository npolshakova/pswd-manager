package testscripts;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.script.ScriptBuilder;
import storage.WalletManager;

public class Opreturn {

    public static void main(String args[]) {
        WalletManager.setupWallet();

        Address addr = Address.fromBase58(WalletManager.params, "mkHS9ne12qx9pS9VojpwU5xtRd4T7X7ZUt"); // testnet faucet
        Transaction tx = WalletManager.createTransaction(addr);
        tx.addOutput(Coin.ZERO, ScriptBuilder.createOpReturnScript("This is a test".getBytes()));
        String sentTx = WalletManager.send(tx);
        System.out.println("Transaction: " + sentTx);
    }

}
