package myTests.blockchain.BlockChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

public class TransactionBalanceTest {

  private BlockChain<IBlockData> blockChain;

  @BeforeEach
  void setUp() throws Exception {
    BlockChain.resetInstance();
    blockChain = BlockChain.getInstance(IBlockData.class);
  }

  @Test
  @DisplayName("Single transaction test")
  void testGetBalanceSingleTransaction() {
    try (MockedStatic<SignatureChecker> mockedSignatureChecker = Mockito.mockStatic(
        SignatureChecker.class); MockedStatic<BlockchainValidator> mockedValidator = Mockito.mockStatic(
        BlockchainValidator.class); MockedStatic<Transaction> mockedTransaction = Mockito.mockStatic(
        Transaction.class)) {

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

      KeyPair reciverKeyPair = GenerateKeys.generateKeyPair();
      PublicKey reciverPublicKey = reciverKeyPair.getPublic();


      byte[] signature = Transaction.generateSignature(senderKey, senderKey, 50, senderPrivateKey);
      Transaction tx = new Transaction(senderKey, "Alice", reciverPublicKey, "Bob",
          blockChain.getUniqueId(), signature, 50);

      blockChain.addNewData(tx);
      assertTrue(blockChain.getData().contains(tx));

      int initialBalance = blockChain.getBalance(senderKey);
      assertEquals(100, initialBalance);

      //mock with transaction
      Block<IBlockData> block = mock(Block.class);
      when(block.getData()).thenReturn(List.of(tx));
      when(block.getMinerPublicKey()).thenReturn(mock(PublicKey.class));
      when(block.getMinerNumber()).thenReturn(1);
      when(block.getTimeStamp()).thenReturn(System.currentTimeMillis());
      blockChain.tryToAddBlock(block);

      int updatedBalance = blockChain.getBalance(senderKey);
      assertEquals(1, blockChain.getNumberOfBlocks());
      assertEquals(block, blockChain.getLastBlock());
      assertEquals(50, updatedBalance);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("Verify User Balance after Multiple Transactions")
  void testGetBalanceMultipleTransactions() throws Exception {
    try (MockedStatic<SignatureChecker> mockedSignatureChecker = Mockito.mockStatic(SignatureChecker.class);
        MockedStatic<BlockchainValidator> mockedValidator = Mockito.mockStatic(BlockchainValidator.class);
        MockedStatic<Transaction> mockedTransaction = Mockito.mockStatic(Transaction.class)) {

      mockedSignatureChecker.when(() -> SignatureChecker.check(any(Transaction.class)))
          .thenReturn(true);

      mockedValidator.when(() -> BlockchainValidator.isNewBlockValid(any(Block.class), any(BlockChain.class)))
          .thenReturn(true);

      mockedTransaction.when(() -> Transaction.generateSignature(any(PublicKey.class), any(PublicKey.class),
              anyInt(), any(PrivateKey.class)))
          .thenReturn(new byte[]{1, 2, 3});

      // Create keys
      KeyPair aliceKeyPair1 = GenerateKeys.generateKeyPair();
      PublicKey alicePublicKey = aliceKeyPair1.getPublic();
      PrivateKey alicePrivateKey = aliceKeyPair1.getPrivate();


      KeyPair bobKeyPair = GenerateKeys.generateKeyPair();
      PublicKey bobPublicKey = bobKeyPair.getPublic();
      PrivateKey bobPrivateKey = bobKeyPair.getPrivate();

      KeyPair charlieKeyPair = GenerateKeys.generateKeyPair();
      PublicKey charliePublicKey = charlieKeyPair.getPublic();
      PrivateKey charliePrivateKey = charlieKeyPair.getPrivate();

      byte[] signature1 = Transaction.generateSignature(alicePublicKey, bobPublicKey, 30, alicePrivateKey);
      Transaction tx1 = new Transaction(alicePublicKey, "Alice", bobPublicKey, "Bob",
          blockChain.getUniqueId(), signature1, 30);


      byte[] signature2 = Transaction.generateSignature(alicePublicKey, charliePublicKey, 20, alicePrivateKey);
      Transaction tx2 = new Transaction(alicePublicKey, "Alice", charliePublicKey, "Charlie",
          blockChain.getUniqueId(), signature2, 20);

      byte[] signature3 = Transaction.generateSignature(bobPublicKey, alicePublicKey, 10, bobPrivateKey);
      Transaction tx3 = new Transaction(bobPublicKey, "Bob", alicePublicKey, "Alice",
          blockChain.getUniqueId(), signature3, 10);

      blockChain.addNewData(tx1);
      blockChain.addNewData(tx2);
      blockChain.addNewData(tx3);

      Block<IBlockData> block = mock(Block.class);
      when(block.getData()).thenReturn(List.of(tx1, tx2, tx3));
      when(block.getMinerPublicKey()).thenReturn(mock(PublicKey.class));
      when(block.getMinerNumber()).thenReturn(1);
      when(block.getTimeStamp()).thenReturn(System.currentTimeMillis());

      blockChain.tryToAddBlock(block);

      int balanceAlice = blockChain.getBalance(alicePublicKey);
      int balanceBob = blockChain.getBalance(charliePublicKey);
      int balanceCharlie = blockChain.getBalance(charlieKeyPair.getPublic());

      assertEquals(100 - 30 - 20 + 10, balanceAlice);
      assertEquals(100 - 10 + 30, balanceBob);
      assertEquals(100 + 20, balanceCharlie);
    }
  }


  @Test
  @DisplayName("Reject Transaction with Insufficient Balance")
  void testTransactionInsufficientBalance() throws Exception {
    try (MockedStatic<SignatureChecker> mockedSignatureChecker = Mockito.mockStatic(SignatureChecker.class);
        MockedStatic<BlockchainValidator> mockedValidator = Mockito.mockStatic(BlockchainValidator.class);
        MockedStatic<Transaction> mockedTransaction = Mockito.mockStatic(Transaction.class)) {

      mockedSignatureChecker.when(() -> SignatureChecker.check(any(Transaction.class)))
          .thenReturn(true);

      mockedValidator.when(() -> BlockchainValidator.isNewBlockValid(any(Block.class), any(BlockChain.class)))
          .thenReturn(true);

      mockedTransaction.when(() -> Transaction.generateSignature(any(PublicKey.class), any(PublicKey.class),
              anyInt(), any(PrivateKey.class)))
          .thenReturn(new byte[]{1, 2, 3});

      KeyPair senderKeyPair = GenerateKeys.generateKeyPair();
      KeyPair reciverKeyPair = GenerateKeys.generateKeyPair();

      byte[] signature = Transaction.generateSignature(senderKeyPair.getPublic(), reciverKeyPair.getPublic(), 150, senderKeyPair.getPrivate());
      Transaction tx = new Transaction(senderKeyPair.getPublic(), "Alice", reciverKeyPair.getPublic(), "Bob",
          blockChain.getUniqueId(), signature, 150);

      blockChain.addNewData(tx);
      assertFalse(blockChain.getData().contains(tx));

      mockedValidator.when(() -> BlockchainValidator.isNewBlockValid(any(Block.class), any(BlockChain.class)))
          .thenReturn(false);

      Block<IBlockData> block = mock(Block.class);
      when(block.getData()).thenReturn(List.of(tx));
      when(block.getMinerPublicKey()).thenReturn(mock(PublicKey.class));
      when(block.getMinerNumber()).thenReturn(1);
      when(block.getTimeStamp()).thenReturn(System.currentTimeMillis());

      blockChain.tryToAddBlock(block);

      assertEquals(0, blockChain.getNumberOfBlocks());
      assertFalse(blockChain.getData().contains(tx));
    }
  }

  @Test
  @DisplayName("Reject Transaction with Invalid Signature")
  void testTransactionInvalidSignature() throws Exception {
    try (MockedStatic<SignatureChecker> mockedSignatureChecker = Mockito.mockStatic(SignatureChecker.class)) {
      mockedSignatureChecker.when(() -> SignatureChecker.check(any(Transaction.class)))
          .thenReturn(false);

      KeyPair keyPair = GenerateKeys.generateKeyPair();
      PublicKey senderKey = keyPair.getPublic();

      Transaction tx = new Transaction(senderKey, "Alice", keyPair.getPublic(), "Bob",
          blockChain.getUniqueId(), new byte[]{0, 1, 2}, 50);

      blockChain.addNewData(tx);

      assertFalse(blockChain.getData().contains(tx));
      assertEquals(0, blockChain.getNumberOfBlocks());
    }
  }


}
