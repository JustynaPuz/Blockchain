package blockchain.User.Client;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.User;
import blockchain.User.UserFactory;
import blockchain.User.UserType;

public class ClientCoordinator<T extends IBlockData> {
  private final ThreadGroup clients = new ThreadGroup("ClientsGroup");
  private final int numberOfClients;
  private final BlockChain<T> blockChain;

  public ClientCoordinator(int numberOfClients, BlockChain<T> chain) {
    this.numberOfClients = numberOfClients;
    this.blockChain = chain;
  }

  public void setup() throws Exception {
    for (int i = 1; i <= numberOfClients; i++) {
      User<T> client = UserFactory.createUser(UserType.CHAT_CLIENT, "Client" + i, blockChain);
      blockChain.addUser(client);
      Thread clientThread = new Thread(clients, client, "Client-" + i);
      clientThread.start();
    }
  }

  public void start() {
    while (true) {
      if (BlockChain.MAX_NUMBER_OF_BLOCKS == blockChain.getNumberOfBlocks()) {
        clients.interrupt();
        break;
      }
    }
  }
}
