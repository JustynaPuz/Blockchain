package blockchain.User.Miner;

import blockchain.Block.Block;
import blockchain.BlockChain.BlockChain;

import blockchain.BlockData.IBlockData;
import blockchain.User.User;
import java.util.List;

public class Miner<T extends IBlockData> extends User<T> {

    public Miner(String name, BlockChain<T> blockChain) {
        super(blockChain, name);
    }

    private Block<T> createBlock() {
        List<T> data = blockchain.getData();
        return new Block<>(blockchain.getNumberOfBlocks() + 1,
                blockchain.getLastBlockHashcode(), Integer.parseInt(name.substring(5)), data, publicKey);
    }

    @Override
    protected long getSleepTime() {
        return 0;
    }


    @Override
    protected void doWork() {
        blockchain.tryToAddBlock(createBlock());
    }
}
