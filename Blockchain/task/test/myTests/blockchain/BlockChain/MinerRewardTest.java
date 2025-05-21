package myTests.blockchain.BlockChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import blockchain.Block.Block;
import blockchain.BlockChain.BlockChain;
import blockchain.BlockChain.BlockchainValidator;
import blockchain.BlockData.IBlockData;
import blockchain.BlockData.Transaction;
import blockchain.Key.GenerateKeys;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class MinerRewardTest {

  private BlockChain<IBlockData> blockChain;

  @BeforeEach
  void setUp() throws Exception {
    BlockChain.resetInstance();
    blockChain = BlockChain.getInstance(IBlockData.class);
  }

  @Test
  @DisplayName("Miner Receives Reward After Mining Block")
  void testMinerReward() throws Exception {
    try (MockedStatic<BlockchainValidator> mockedValidator = Mockito.mockStatic(BlockchainValidator.class);
        MockedStatic<Transaction> mockedTransaction = Mockito.mockStatic(Transaction.class)) {

      mockedValidator.when(() -> BlockchainValidator.isNewBlockValid(any(Block.class), any(BlockChain.class)))
          .thenReturn(true);

      mockedTransaction.when(() -> Transaction.generateSignature(any(PublicKey.class), any(PublicKey.class),
              anyInt(), any(PrivateKey.class)))
          .thenReturn(new byte[]{4, 5, 6});

      KeyPair minerKeyPair = GenerateKeys.generateKeyPair();
      PublicKey minerKey = minerKeyPair.getPublic();

      Block<IBlockData> block = mock(Block.class);
      when(block.getMinerPublicKey()).thenReturn(minerKey);
      when(block.getMinerNumber()).thenReturn(1);
      when(block.getData()).thenReturn(blockChain.getData());
      when(block.getTimeStamp()).thenReturn(System.currentTimeMillis());

      int initialBalance = blockChain.getBalance(minerKey);
      assertEquals(100, initialBalance);

      blockChain.tryToAddBlock(block);
      Block<IBlockData> block2 = mock(Block.class);
      when(block2.getMinerPublicKey()).thenReturn(any());
      when(block2.getMinerNumber()).thenReturn(2);
      when(block2.getData()).thenReturn(blockChain.getData());
      when(block2.getTimeStamp()).thenReturn(System.currentTimeMillis());

      int updatedBalance = blockChain.getBalance(minerKey);
      assertEquals(initialBalance + BlockChain.REWARD_FOR_MINING, updatedBalance);

      List<IBlockData> pending = blockChain.getData();
      boolean rewardExists = pending.stream()
          .filter(data -> data instanceof Transaction)
          .map(data -> (Transaction) data)
          .anyMatch(tx -> tx.getReceiverPublicKey().equals(minerKey) &&
              tx.getAmount() == BlockChain.REWARD_FOR_MINING );
      assertTrue(rewardExists);
    }
  }
}
