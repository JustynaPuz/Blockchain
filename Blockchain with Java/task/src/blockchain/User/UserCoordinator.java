package blockchain.User;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;

public class UserCoordinator<T extends IBlockData> {

  private final ThreadGroup threadGroup;
  private final int numberOfUsers;
  private final BlockChain<T> blockChain;
  private final UserType userType;
  private final String userNamePrefix;

  public UserCoordinator(String threadGroupName, UserType userType, String userNamePrefix,
      int numberOfUsers, BlockChain<T> blockChain) {
    this.threadGroup = new ThreadGroup(threadGroupName);
    this.userType = userType;
    this.userNamePrefix = userNamePrefix;
    this.numberOfUsers = numberOfUsers;
    this.blockChain = blockChain;
  }

  public void setup() {
    for (int i = 1; i <= numberOfUsers; i++) {
      User<T> user = UserFactory.createUser(userType, userNamePrefix + i, blockChain);

      blockChain.addUser(user);
      Thread minerThread = new Thread(threadGroup, user, userNamePrefix + "-" + i);
      minerThread.start();
    }
  }

  public void start() {
    while (true) {
      if (BlockChain.MAX_NUMBER_OF_BLOCKS == blockChain.getNumberOfBlocks()) {
        threadGroup.interrupt();
        break;
      }
    }
  }

}
