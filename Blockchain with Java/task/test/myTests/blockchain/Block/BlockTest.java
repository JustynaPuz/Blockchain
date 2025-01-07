package myTests.blockchain.Block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import blockchain.Block.Block;
import blockchain.BlockData.IBlockData;
import blockchain.HashFunction;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlockTest {

  private static PublicKey minerPublicKey;
  @Mock
  private IBlockData data1;
  @Mock
  private IBlockData data2;
  @Mock
  private IBlockData data3;

  @BeforeAll
  static void setUpClass() throws Exception {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    KeyPair keyPair = keyGen.generateKeyPair();
    minerPublicKey = keyPair.getPublic();
  }

  @Test
  @DisplayName("Test block creation with valid data")
  void testBlockCreationWithValidMocks() {
    int blockId = 1;
    String previousHash = "0";
    int minerNumber = 99;
    List<IBlockData> dataItems = Arrays.asList(data1, data2, data3);
    when(data1.getUniqueId()).thenReturn(1L);
    when(data2.getUniqueId()).thenReturn(2L);
    when(data3.getUniqueId()).thenReturn(3L);

    Block<IBlockData> block = new Block<>(blockId, previousHash, minerNumber, dataItems, minerPublicKey);
    assertEquals(blockId, block.getId());
    assertEquals(previousHash, block.getPreviousHashcode());
    assertEquals(minerNumber, block.getMinerNumber());
    assertEquals(minerPublicKey, block.getMinerPublicKey());
    assertEquals(3, block.getData().size());
    assertEquals(3, block.getMaxIdentifierInBlock());

    verify(data1, times(1)).getUniqueId();
    verify(data2, times(1)).getUniqueId();
    verify(data3, times(1)).getUniqueId();

    String expectedStringToHash = block.getStringToHash();
    String expectedHash = HashFunction.applySha256(expectedStringToHash);
    assertEquals(expectedHash, block.getHashcode());

  }

  @Test
  @DisplayName("Test block creation with invalid data")
  void testBlockCreationWithInvalidMocks() {
    int blockId = 1;
    String previousHash = "0";
    int minerNumber = 99;
    List<IBlockData> dataItems = Arrays.asList(data1, data2, data3);
    when(data1.getUniqueId()).thenReturn(1L);
    when(data2.getUniqueId()).thenReturn(2L);
    when(data3.getUniqueId()).thenReturn(2L);

    Block<IBlockData> block = new Block<>(blockId, previousHash, minerNumber, dataItems, minerPublicKey);
    assertEquals(2, block.getData().size());
    assertEquals(2, block.getMaxIdentifierInBlock());

    verify(data1, times(1)).getUniqueId();
    verify(data2, times(1)).getUniqueId();
    verify(data3, times(1)).getUniqueId();
  }
}