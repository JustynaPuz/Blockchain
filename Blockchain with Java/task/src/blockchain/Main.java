package blockchain;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockChain.BlockchainValidator;
import blockchain.BlockData.Transaction;
import blockchain.User.Client.TransactionClientCoordinator;
import blockchain.User.Miner.MiningCoordinator;


public class Main {

  public static void main(String[] args) throws Exception {
    BlockChain<Transaction> blockChain = BlockChain.getInstance(Transaction.class);

    MiningCoordinator<Transaction> miningCoordinator = new MiningCoordinator<>(5, blockChain);
    TransactionClientCoordinator<Transaction> coordinator = new TransactionClientCoordinator<>(4, blockChain);
    //ClientCoordinator<Transaction> clientCoordinator = new ClientCoordinator<>(2, blockChain);
    //clientCoordinator.setup();
    miningCoordinator.setup();
    coordinator.setup();


    miningCoordinator.start();
    coordinator.start();
   // clientCoordinator.start();


    System.out.println();
    if (BlockchainValidator.isBlockchainValid(blockChain)) {
      System.out.println("Blockchain is valid");
    }else {
      System.out.println("Blockchain is not valid");
    }

  }
}
