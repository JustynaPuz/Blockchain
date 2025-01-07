package myTests.blockchain.BlockData;

import static org.junit.jupiter.api.Assertions.*;

import blockchain.BlockData.Message;
import blockchain.Key.GenerateKeys;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MessageTest {

  private static KeyPair keyPair;
  private static final long UNIQUE_ID = 999L;
  private static final String SAMPLE_TEXT = "Hello!";

  @BeforeAll
  static void setUp() throws Exception {
    keyPair = GenerateKeys.generateKeyPair();
  }

  @Test
  @DisplayName("Test Message creation and basic getters")
  void testMessageCreation() throws Exception {
    // Given
    PrivateKey privateKey = keyPair.getPrivate();
    PublicKey publicKey = keyPair.getPublic();

    byte[] signature = Message.generateSignature(SAMPLE_TEXT, UNIQUE_ID, privateKey);

    // Act
    Message message = new Message(SAMPLE_TEXT, signature, UNIQUE_ID, publicKey);

    // Then
    assertNotNull(message);
    assertEquals(SAMPLE_TEXT, message.getText());
    assertEquals(signature, message.getSignature());
    assertEquals(UNIQUE_ID, message.getUniqueId());
    assertEquals(publicKey, message.getPublicKey());
  }

  @Test
  @DisplayName("Test isValid() with non-empty text")
  void testIsValid() {
    // Given
    PublicKey publicKey = keyPair.getPublic();
    Message message = new Message("Text", new byte[]{1}, 123L, publicKey);
    // Then
    assertTrue(message.isValid());
  }

  @Test
  @DisplayName("Test isValid() with empty text")
  void testIsValidWithEmptyText() {
    // Given
    PublicKey publicKey = keyPair.getPublic();
    Message message = new Message("", new byte[]{1}, 123L, publicKey);
    // Then
    assertFalse(message.isValid());
  }

  @Test
  @DisplayName("Test getDataToSign() returns expected data")
  void testGetDataToSign() {
    // Given
    PublicKey publicKey = keyPair.getPublic();
    Message message = new Message("Hello", null, 123L, publicKey);

    // When
    byte[] dataToSign = message.getDataToSign();
    String dataStr = new String(dataToSign);

    // Then
    assertTrue(dataStr.contains("Hello"));
    assertTrue(dataStr.contains(String.valueOf(123L)));
  }

  @Test
  @DisplayName("Test generateSignature() and verify signature")
  void testGenerateSignatureAndVerification() throws Exception {
    // Given
    PrivateKey privateKey = keyPair.getPrivate();
    PublicKey publicKey = keyPair.getPublic();
    byte[] signature = Message.generateSignature(SAMPLE_TEXT, UNIQUE_ID, privateKey);

    Message message = new Message(SAMPLE_TEXT, signature, UNIQUE_ID, publicKey);

    // When
    Signature sig = Signature.getInstance("SHA256withRSA");
    sig.initVerify(publicKey);
    sig.update((SAMPLE_TEXT + UNIQUE_ID).getBytes());
    boolean isSignatureValid = sig.verify(signature);

    // Then
    assertNotNull(signature);
    assertTrue(isSignatureValid);
  }



}