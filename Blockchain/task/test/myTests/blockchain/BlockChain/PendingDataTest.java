package myTests.blockchain.BlockChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import blockchain.Key.SignatureChecker;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class PendingDataTest {

  private BlockChain<IBlockData> blockChain;

  @BeforeEach
  void setUp() throws Exception {
    BlockChain.resetInstance();
    blockChain = BlockChain.getInstance(IBlockData.class);
  }

  @Test
  @DisplayName("Pending Data Cleared After Adding Block")
  void testPendingDataCleared() throws Exception {
    try (MockedStatic<SignatureChecker> mockedSignatureChecker = Mockito.mockStatic(
        SignatureChecker.class);
        MockedStatic<BlockchainValidator> mockedValidator = Mockito.mockStatic(
            BlockchainValidator.class);
        MockedStatic<Transaction> mockedTransaction = Mockito.mockStatic(Transaction.class)) {

      mockedSignatureChecker.when(() -> SignatureChecker.check(any(Transaction.class)))
          .thenReturn(true);

      mockedValidator.when(
              () -> BlockchainValidator.isNewBlockValid(any(Block.class), any(BlockChain.class)))
          .thenReturn(true);

      mockedTransaction.when(
              () -> Transaction.generateSignature(any(PublicKey.class), any(PublicKey.class),
                  anyInt(), any(PrivateKey.class)))
          .thenReturn(new byte[]{1, 2, 3});

      KeyPair keyPair = GenerateKeys.generateKeyPair();
      PublicKey senderKey = keyPair.getPublic();
      PrivateKey senderPrivateKey = keyPair.getPrivate();

      byte[] signature = Transaction.generateSignature(senderKey, keyPair.getPublic(), 50,
          senderPrivateKey);
      Transaction tx = new Transaction(senderKey, "Alice", keyPair.getPublic(), "Bob",
          blockChain.getUniqueId(), signature, 50);

      blockChain.addNewData(tx);
      assertEquals(1, blockChain.getData().size());

      Block<IBlockData> block = mock(Block.class);
      when(block.getData()).thenReturn(List.of(tx));
      when(block.getMinerPublicKey()).thenReturn(mock(PublicKey.class));
      when(block.getMinerNumber()).thenReturn(1);
      when(block.getTimeStamp()).thenReturn(System.currentTimeMillis());

      blockChain.tryToAddBlock(block);
      assertEquals(1, blockChain.getNumberOfBlocks());
      assertEquals(1, blockChain.getData().size());
      assertFalse(blockChain.getData().contains(tx));
    }
  }
}
