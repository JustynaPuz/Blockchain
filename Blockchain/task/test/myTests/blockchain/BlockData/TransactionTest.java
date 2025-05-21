package myTests.blockchain.BlockData;

import static org.junit.jupiter.api.Assertions.*;

import blockchain.BlockData.Transaction;
import blockchain.Key.GenerateKeys;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransactionTest {

  private static KeyPair senderKeyPair;
  private static KeyPair receiverKeyPair;
  private static final long UNIQUE_ID = 12345L;
  private static final int AMOUNT = 100;

  @BeforeAll
  static void setUpClass() throws Exception {
    senderKeyPair = GenerateKeys.generateKeyPair();
    receiverKeyPair = GenerateKeys.generateKeyPair();
  }

  @BeforeEach

  @Test
  @DisplayName("Test Transaction creation and basic getters")
  void testTransactionCreation() throws Exception {
    PublicKey senderPublicKey = senderKeyPair.getPublic();
    PrivateKey senderPrivateKey = senderKeyPair.getPrivate();
    PublicKey receiverPublicKey = receiverKeyPair.getPublic();
    String senderName = "Alice";
    String receiverName = "Bob";

    byte[] signature = Transaction.generateSignature(senderPublicKey, receiverPublicKey, AMOUNT,
        senderPrivateKey);
    Transaction transaction = new Transaction(senderPublicKey, senderName, receiverPublicKey,
        receiverName, UNIQUE_ID, signature, AMOUNT);
    assertNotNull(transaction);
    assertEquals(senderPublicKey, transaction.getSenderPublicKey());
    assertEquals(receiverPublicKey, transaction.getReceiverPublicKey());
    assertEquals(signature, transaction.getSignature());
    assertEquals(AMOUNT, transaction.getAmount());
    assertEquals(UNIQUE_ID, transaction.getUniqueId());

    assertTrue(transaction.isValid());

  }

  @Test
  @DisplayName("Test invalid transaction: missing signature")
  void testInvalidTransactionMissingSignature() {

    PublicKey senderPublicKey = senderKeyPair.getPublic();
    PublicKey receiverPublicKey = receiverKeyPair.getPublic();
    String senderName = "Alice";
    String receiverName = "Bob";

    byte[] signature = null;

    Transaction transaction = new Transaction(
        senderPublicKey,
        senderName,
        receiverPublicKey,
        receiverName,
        UNIQUE_ID,
        signature,
        AMOUNT
    );

    assertFalse(transaction.isValid());
  }

  @Test
  @DisplayName("Test getDataToSign() returns expected data")
  void testGetDataToSign() {
    //Given
    PublicKey senderPublicKey = senderKeyPair.getPublic();
    PublicKey receiverPublicKey = receiverKeyPair.getPublic();
    String senderName = "Alice";
    String receiverName = "Bob";
    byte[] signature = "fake".getBytes();

    //when
    Transaction transaction = new Transaction(
        senderPublicKey,
        senderName,
        receiverPublicKey,
        receiverName,
        UNIQUE_ID,
        signature,
        AMOUNT
    );

    byte[] dataToSign = transaction.getDataToSign();
    String dataStr = new String(dataToSign);

    //then
    assertTrue(dataStr.contains(senderPublicKey.toString()));
    assertTrue(dataStr.contains(receiverPublicKey.toString()));
    assertTrue(dataStr.contains(String.valueOf(AMOUNT)));
  }

  @Test
  @DisplayName("Test generateSignature() produces correct signature")
  void testGenerateSignature() throws Exception {
    // Given
    PublicKey senderPublicKey = senderKeyPair.getPublic();
    PrivateKey senderPrivateKey = senderKeyPair.getPrivate();
    PublicKey receiverPublicKey = receiverKeyPair.getPublic();

    // When
    byte[] signature = Transaction.generateSignature(senderPublicKey, receiverPublicKey, AMOUNT,
        senderPrivateKey);


    assertNotNull(signature);

    Signature sig = Signature.getInstance("SHA256withRSA");
    sig.initVerify(senderPublicKey);
    String data = senderPublicKey + receiverPublicKey.toString() + AMOUNT;
    sig.update(data.getBytes());
    boolean isSignatureCorrect = sig.verify(signature);

    assertTrue(isSignatureCorrect);
  }


}