/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.wallet.RedeemData;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PswdManagerClient {

    public static void main(String[] args) throws Exception {
        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";

        // Parse the address given as the first parameter.
        //Address forwardingAddress = new Address(params, args[0]);
        Address myAddress = new Address(params, "mwyPZ4i9h3zYFx6BdiH6aKftqCHxr2txtG");
        Address sendAddress =  new Address(params, "moccUWfWVngRxQUzjHcwuneiisvrwxXdrJ");

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

        System.out.println(kit.wallet().getBalance());
        System.out.println(kit.wallet().currentReceiveAddress());
    }

    /**
     * Encrypts input message
     * @param msg
     * @return
     */
    private static String encrypt(String msg){
        return null;
    }

    /**
     * Decrypts input message
     * @param msg
     * @return
     */
    private static String decrypt(String msg) {
        return null;
    }

    /**
     * Splits input message into max 65 byte shards
     * @param msg
     * @return
     */
    private static String[] createShards(String msg) {
        return null;
    }

    /**
     * Reassemble shards into one message
     * @param shards
     * @return
     */
    private static String reassmebleShards(String[] shards) {
        return null;
    }

    /**
     * To create bitcoin addr: (https://bitcoin.stackexchange.com/questions/67211/public-key-for-testnet-address)
     * 1 - Public ECDSA Key
     * 2 - SHA-256 hash of 1
     * 3 - RIPEMD-160 Hash of 2
     * 4 - Adding network bytes to 3
     * 5 - SHA-256 hash of 4
     * 6 - SHA-256 hash of 5
     * 7 - First four bytes of 6
     * 8 - Adding 7 at the end of 4
     * 9 - Base58 encoding of 8
     *
     * Compression flags (http://royalforkblog.github.io/2014/07/31/address-gen/)
     * Bitcoin =  0x80
     * Testnet = 0xEF
     */

    /**
     * Creates a fake public key transaction using input message
     * 1. sha256
     * 2. ripemd160
     *
     * @param msg
     */
    private static String p2fk(String msg) {
        String addr = Utils.sha256hash160(msg.getBytes()).toString();
        return addr;
    }

    /**
     * Testing: p2pkh from Data Insertion in Bitcoin’s Blockchain
     */
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

    private static Script P2SH(RedeemData redeemScriptHash) {
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

    // Recover Message:
    private static String recover(Transaction tx) {
        int intoutputNum = 0;
        for (TransactionOutput output : tx.getOutputs()) {
            try {
                Script script = output.getScriptPubKey();
                // segment script into OP codes
                List<ScriptChunk> chunks = script.getChunks();

                if (chunks.size() == 2
                        &&chunks.get(0).isPushData()
                        && (chunks.get(0).data.length == 65
                        || chunks.get(0).data.length == 33)
                        &&chunks.get(1).opcode == ScriptOpCodes.OP_CHECKSIG) {
                //P2PK
                } else if (chunks.size() == 5
                        &&chunks.get(0).opcode == ScriptOpCodes.OP_DUP
                        &&chunks.get(1).opcode == ScriptOpCodes.OP_HASH160
                        &&chunks.get(2).isPushData()
                        &&chunks.get(2).data.length == 20
                        &&chunks.get(3).opcode == ScriptOpCodes.OP_EQUALVERIFY
                        &&chunks.get(4).opcode == ScriptOpCodes.OP_CHECKSIG) {
                    //P2PKH
                }

            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return  "";
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

    // DATABASE SETUP

}