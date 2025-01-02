package blockchain.User.Miner;

import blockchain.Block.Block;
import blockchain.BlockChain.BlockChain;

import blockchain.BlockData.IBlockData;
import blockchain.User.User;
import java.util.List;

public class Miner<T extends IBlockData> extends User<T> implements Runnable {
    protected final int minerNumber;

    public Miner(int minerNumber, BlockChain<T> blockChain) {
        super(blockChain, "Miner" + minerNumber);
        this.minerNumber = minerNumber;
    }

    private Block<T> createBlock() {
        List<T> data = blockchain.getData();
        return new Block<>(blockchain.getNumberOfBlocks() + 1,
                blockchain.getLastBlockHashcode(), minerNumber, data, publicKey);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            blockchain.tryToAddBlock(createBlock());
        }
    }
}
