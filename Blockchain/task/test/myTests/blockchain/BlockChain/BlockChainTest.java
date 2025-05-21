package myTests.blockchain.BlockChain;

import static org.junit.jupiter.api.Assertions.*;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BlockChainTest {

  private BlockChain<IBlockData> blockChain;

  @BeforeEach
  void setUp() throws Exception {
    blockChain = BlockChain.getInstance(IBlockData.class);
  }

  @Test
  @DisplayName("Test singleton instance")
  void testSingleton() throws Exception {
    BlockChain<IBlockData> anotherBlockchain = BlockChain.getInstance(IBlockData.class);
    assertSame(anotherBlockchain, blockChain);
  }


}