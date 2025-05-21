package myTests.blockchain.BlockChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import blockchain.Block.Block;
import blockchain.BlockChain.BlockChain;
import blockchain.BlockChain.BlockchainValidator;
import blockchain.BlockData.IBlockData;
import java.security.PublicKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class AddBlockTest {

  private BlockChain<IBlockData> blockChain;

  @BeforeEach
  void setUp() throws Exception {
    BlockChain.resetInstance();
    blockChain = BlockChain.getInstance(IBlockData.class);
  }

  @Test
  @DisplayName("Add valid block")
  void testAddValidBlock() {
    try (MockedStatic<BlockchainValidator> mockedValidator = Mockito.mockStatic(
        BlockchainValidator.class)) {
      mockedValidator.when(
              () -> BlockchainValidator.isNewBlockValid(any(Block.class), any(BlockChain.class)))
          .thenReturn(true);

      Block<IBlockData> block = mock(Block.class);
      when(block.getMinerPublicKey()).thenReturn(mock(PublicKey.class));
      when(block.getMinerNumber()).thenReturn(1);
      when(block.getTimeStamp()).thenReturn(System.currentTimeMillis());

      blockChain.tryToAddBlock(block);

      assertEquals(1, blockChain.getNumberOfBlocks());
      assertEquals(block, blockChain.getLastBlock());

    }
  }

  @Test
  @DisplayName("Add invalid block")
  void testAddInvalidBlock() {
    try (MockedStatic<BlockchainValidator> mockedValidator = Mockito.mockStatic(BlockchainValidator.class)) {
      mockedValidator.when(() -> BlockchainValidator.isNewBlockValid(any(Block.class), any(BlockChain.class)))
          .thenReturn(false);

      Block<IBlockData> invalidBlock = mock(Block.class);

      blockChain.tryToAddBlock(invalidBlock);

    }
  }


}
