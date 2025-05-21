package blockchain.User;

import blockchain.BlockChain.BlockChain;
import blockchain.User.ChatClient.ChatClient;
import blockchain.User.Miner.Miner;
import blockchain.User.TransactionClient.TransactionClient;

public class UserFactory {

  public static User createUser(UserType type, String name, BlockChain blockChain) {
    switch (type) {
      case MINER:
        return new Miner(name, blockChain);
      case CHAT_CLIENT:
        return new ChatClient(name, blockChain);
      case TRANSACTION_CLIENT:
        return new TransactionClient(name, blockChain);
      default:
        throw new IllegalArgumentException("Unknown user type: " + type);
    }
  }
}
