package blockchain.User.ChatClient;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.UserCoordinator;
import blockchain.User.UserType;

public class ChatClientCoordinator<T extends IBlockData> extends UserCoordinator<T> {

  public ChatClientCoordinator(int numberOfClients, BlockChain<T> chain) {
    super(
        "ClientsGroup", UserType.CHAT_CLIENT, "Client",
        numberOfClients, chain
    );
  }
}
