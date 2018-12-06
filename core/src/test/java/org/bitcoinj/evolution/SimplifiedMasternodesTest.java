package org.bitcoinj.evolution;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.BLSSecretKey;
import org.bitcoinj.params.MainNetParams;
import static org.junit.Assert.*;

import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.FlatDB;
import org.junit.Test;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by hashengineering on 11/26/18.
 */
public class SimplifiedMasternodesTest {

    static {
        MainNetParams MAINPARAMS = MainNetParams.get();
        TestNet3Params TESTPARAMS = TestNet3Params.get();
        Context context = new Context(TESTPARAMS);
    }

    @Test
    public void merkleRoots() throws UnknownHostException
    {
        MainNetParams PARAMS = MainNetParams.get();
        ArrayList<SimplifiedMasternodeListEntry> entries = new ArrayList<SimplifiedMasternodeListEntry>(15);
        for (int i = 0; i < 15; i++) {
            SimplifiedMasternodeListEntry smle = new SimplifiedMasternodeListEntry(PARAMS);
            smle.proRegTxHash = Sha256Hash.wrap(String.format("%064x", i));
            smle.confirmedHash = Sha256Hash.wrap(String.format("%064x", i));

            byte [] ip = {0, 0, 0, (byte)i};
            smle.service = new MasternodeAddress(InetAddress.getByAddress(ip), i);

            byte [] skBuf = new byte[BLSSecretKey.BLS_CURVE_SECKEY_SIZE];
            skBuf[0] = (byte)i;
            BLSSecretKey sk = new BLSSecretKey(skBuf);


            smle.pubKeyOperator = sk.GetPublicKey();
            smle.keyIdVoting = new KeyId(Utils.reverseBytes(Utils.HEX.decode(String.format("%040x", i))));
            smle.isValid = true;

            entries.add(smle);
        }

        String [] expectedHashes = {
                "373b549f6380d8f7b04d7b04d7c58a749c5cbe3bf41536785ba819879c4870f1",
                "3a1010e28226558560e5296bcee6bf0b9b963b73a1514f5aa2885e270f6b90c1",
                "85d3d93b28689128daf3a41d706ae5002f447b9b6372776f0ca9d53b31146884",
                "8930eee6bd2e7971a7090edfb79f74c00a12280e59adfc2cc99d406a01e368f9",
                "dc2e69caa0ef97e8f5cf40a9530641bd4933dd8c9ad533054537728f7e5f58c2",
                "3e4a0e0a0d2ed397fa27221de3047de21f50d17d0ba43738cbdb9fee96c7cb46",
                "eb18476a1496e1cb912b1d4dd93314b78c6a679d83cae8e144a717b967dc4b8c",
                "6c0d01fa40ac11d7b523facd2bf5632c83f7e4df3f60fd1b364ea90f6c852156",
                "c9e3e69d54e6e95b280ae102593fe114cf4620fa89dd88da1a146ada08815d68",
                "1023f67f735e8e9403d5f083e7a17489619b1790feac4f6b133e9dda15999ae6",
                "5d5fc77944f7c72df236a5baf460c7b9a947144d54d0953521f1494c8a2f7aaa",
                "ac7db66820de3c7506f8c6415fd352e36ac5f27c6adbdfb74de3e109d0d277df",
                "cbc25ca965d0fa69a1fdc1d796b8ee2726a0e2137414e92fb9541630e3189901",
                "ac9934c4049ae952d41fb38e7e9659a558a5ce748bdb7fb613741598d1b16a27",
                "a61177eb14450bb8c56e5f0547035e0f3a70fe46f36901351cc568b2e48e29d0",
        };
        ArrayList<String> calculatedHashes = new ArrayList<String>(15);

        for (SimplifiedMasternodeListEntry smle : entries) {
            calculatedHashes.add(smle.getHash().toString());
        }

        assertArrayEquals(expectedHashes, calculatedHashes.toArray());

        SimplifiedMasternodeList sml = new SimplifiedMasternodeList(PARAMS, entries);

        String expectedMerkleRoot = "b2303aca677ae2091c882e44b58f57869fa88a6db1f4e1a5d71975e5387fa195";
        String calculatedMerkleRoot = sml.calculateMerkleRoot().toString();

        assertEquals(expectedMerkleRoot, calculatedMerkleRoot);
    }

    @Test
    public void loadFromFile() throws Exception {
        URL datafile = getClass().getResource("simplifiedmasternodelistmanager.dat");
        FlatDB<SimplifiedMasternodeListManager> db = new FlatDB<SimplifiedMasternodeListManager>(Context.get(), datafile.getFile(), true);

        SimplifiedMasternodeListManager managerDefaultNames = new SimplifiedMasternodeListManager(Context.get());
        assertEquals(db.load(managerDefaultNames), true);

        SimplifiedMasternodeListManager managerSpecific = new SimplifiedMasternodeListManager(Context.get());
        FlatDB<SimplifiedMasternodeListManager> db2 = new FlatDB<SimplifiedMasternodeListManager>(Context.get(), datafile.getFile(), true, managerSpecific.getDefaultMagicMessage(), 1);
        assertEquals(db2.load(managerSpecific), true);

        //check to make sure that they have the same number of masternodes
        assertEquals(managerDefaultNames.mnList.size(), managerSpecific.mnList.size());

        //load a file with version 1, expecting version 2
        SimplifiedMasternodeListManager managerSpecificFail = new SimplifiedMasternodeListManager(Context.get());
        FlatDB<SimplifiedMasternodeListManager> db3 = new FlatDB<SimplifiedMasternodeListManager>(Context.get(), datafile.getFile(), true, managerSpecific.getDefaultMagicMessage(), 2);
        assertEquals(db3.load(managerSpecificFail), false);
    }
}
