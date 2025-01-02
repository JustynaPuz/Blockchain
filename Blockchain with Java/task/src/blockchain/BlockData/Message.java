package blockchain.BlockData;

import blockchain.Key.ISignableData;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class Message implements IBlockData, ISignableData {

  private final String text;
  private final byte[] signature;
  private final long uniqueId;
  private final PublicKey publicKey;

  public Message(String text, byte[] signature, long uniqueId, PublicKey publicKey) {
    this.text = text;
    this.signature = signature;
    this.uniqueId = uniqueId;
    this.publicKey = publicKey;
  }


  public byte[] getSignature() {
    return signature;
  }

  @Override
  public byte[] getDataToSign() {
    return (text + uniqueId).getBytes();
  }

  public long getUniqueId() {
    return uniqueId;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }


  public static byte[] generateSignature(String text, long id, PrivateKey privateKey)
      throws Exception {
    Signature privateSignature = Signature.getInstance("SHA256withRSA");
    privateSignature.initSign(privateKey);
    privateSignature.update((text + id).getBytes());
    return privateSignature.sign();
  }

  @Override
  public String toString() {
    return text + "\n";
  }

  @Override
  public boolean isValid() {
    return text != null && !text.isEmpty();
  }
}
