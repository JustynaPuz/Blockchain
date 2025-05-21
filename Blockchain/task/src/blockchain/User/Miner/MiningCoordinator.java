package blockchain.User.Miner;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.UserCoordinator;
import blockchain.User.UserType;


public class MiningCoordinator<T extends IBlockData> extends UserCoordinator<T> {

  public MiningCoordinator(int numberOfMiners, BlockChain<T> blockChain) {
    super(
        "MinersGroup",    // thread group name
        UserType.MINER,   // user type
        "Miner",          // name prefix
        numberOfMiners,
        blockChain
    );
  }
}
