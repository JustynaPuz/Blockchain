package blockchain.User.TransactionClient;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.UserCoordinator;
import blockchain.User.UserType;


public class TransactionClientCoordinator<T extends IBlockData> extends UserCoordinator<T> {

  public TransactionClientCoordinator(int numberOfClients, BlockChain<T> chain) {
    super(
        "TransactionsClientsGroup", UserType.TRANSACTION_CLIENT, "Client",
        numberOfClients, chain
    );
  }
}
