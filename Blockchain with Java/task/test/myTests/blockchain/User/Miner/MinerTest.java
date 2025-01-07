package myTests.blockchain.User.Miner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import blockchain.Block.Block;
import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.Miner.Miner;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MinerTest {

  @Mock
  private BlockChain<IBlockData> mockBlockChain;

  @Test
  @DisplayName("Test Miner creation")
  void testMinerCreation() {
    String minerName = "Miner";
    Miner<IBlockData> miner = new Miner<>(minerName, mockBlockChain);
    assertNotNull(miner);
    assertEquals(minerName,miner.getName());
  }

  @Test
  @DisplayName("Test Miner.run() executes doWork()")
  void testMinerRun() {
    String minerName = "Miner1";

    when(mockBlockChain.getData()).thenReturn(Arrays.asList(mock(IBlockData.class)));
    when(mockBlockChain.getNumberOfBlocks()).thenReturn(5);
    when(mockBlockChain.getLastBlockHashcode()).thenReturn("01");


    Miner<IBlockData> miner = new Miner<>(minerName, mockBlockChain);
    miner.doWork();
    verify(mockBlockChain).tryToAddBlock(any(Block.class));

  }


}