package org.uzlocoinj.kits;

import org.uzlocoinj.core.NetworkParameters;
import org.uzlocoinj.store.BlockStore;
import org.uzlocoinj.store.BlockStoreException;
import org.uzlocoinj.store.LevelDBBlockStore;
import org.uzlocoinj.store.SPVBlockStore;

import java.io.File;

/**
 * Created by Eric on 2/23/2016.
 */
public class LevelDBWalletAppKit extends WalletAppKit {
    public LevelDBWalletAppKit(NetworkParameters params, File directory, String filePrefix) {
        super(params, directory, filePrefix);
    }

    /**
     * Override this to use a {@link BlockStore} that isn't the default of {@link SPVBlockStore}.
     */
    protected BlockStore provideBlockStore(File file) throws BlockStoreException {
        return new LevelDBBlockStore(context, file);
    }
}
