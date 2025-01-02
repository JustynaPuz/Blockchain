package blockchain.User.Miner;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;

public class MiningCoordinator<T extends IBlockData> {

  private final ThreadGroup miners = new ThreadGroup("MinersGroup");
  private final int numberOfMiners;
  private final BlockChain<T> blockChain;

  public MiningCoordinator(int numberOfMiners, BlockChain<T> blockChain) {
    this.numberOfMiners = numberOfMiners;
    this.blockChain = blockChain;
  }

  public void setup() {
    for (int i = 1; i <= numberOfMiners; i++) {
      Miner<T> miner = new Miner<>(i, blockChain);
      blockChain.addUser(miner);
      Thread minerThread = new Thread(miners, miner, "Miner-" + i);
      minerThread.start();
    }
  }

  public void start() {
    while (true) {
      if (BlockChain.MAX_NUMBER_OF_BLOCKS == blockChain.getNumberOfBlocks()) {
        miners.interrupt();
        break;
      }
    }
  }
}
