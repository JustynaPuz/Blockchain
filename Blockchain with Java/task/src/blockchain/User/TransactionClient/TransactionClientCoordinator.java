package blockchain.User.Client;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.User;
import java.util.List;

public class TransactionClientCoordinator<T extends IBlockData> {

  private final ThreadGroup clients = new ThreadGroup("TransactionClientsGroup");
  private final int numberOfClients;
  private final BlockChain<T> blockChain;
  private List<User<T>> recipients;

  public TransactionClientCoordinator(int numberOfClients, BlockChain<T> chain) {
    this.numberOfClients = numberOfClients;
    this.blockChain = chain;
    this.recipients = blockChain.getUsers();
  }

  public void setup() {
    for (int i = 1; i <= numberOfClients; i++) {
      TransactionClient<T> client = new TransactionClient<>("Client" + i, blockChain, recipients);
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
