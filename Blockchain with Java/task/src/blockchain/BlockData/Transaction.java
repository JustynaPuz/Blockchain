package blockchain.BlockData;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class Transaction implements IBlockData, ISignableData {

  private PublicKey senderPublicKey;
  private PublicKey receiverPublicKey;
  private int amount;
  private long uniqueId;
  private byte[] signature;
  private String from;
  private String to;


  public Transaction(PublicKey senderPublicKey,String from,  PublicKey receiverPublicKey,String to,  long uniqueId,
      byte[] signature, int amount) {
    this.senderPublicKey = senderPublicKey;
    this.from = from;
    this.receiverPublicKey = receiverPublicKey;
    this.to = to;
    this.signature = signature;
    this.amount = amount;
    this.uniqueId = uniqueId;
  }

  @Override
  public String toString() {
    return from + " sent " + amount + " VC to " + to;
  }


  @Override
  public boolean isValid() {
    return senderPublicKey != null && receiverPublicKey != null && signature != null;
  }

  @Override
  public long getUniqueId() {
    return uniqueId;
  }

  @Override
  public PublicKey getPublicKey() {
    return senderPublicKey;
  }

  public int getAmount() {
    return amount;
  }

  public PublicKey getSenderPublicKey() {
    return senderPublicKey;
  }

  public PublicKey getReceiverPublicKey() {
    return receiverPublicKey;
  }

  @Override
  public byte[] getSignature() {
    return signature;
  }


  public static byte[] generateSignature(PublicKey senderPublicKey, PublicKey receiverPublicKey, int amount, PrivateKey privateKey)
      throws Exception {
    Signature privateSignature = Signature.getInstance("SHA256withRSA");
    privateSignature.initSign(privateKey);
    String data = senderPublicKey.toString() + receiverPublicKey.toString() + amount;
    privateSignature.update(data.getBytes());
    return privateSignature.sign();
  }


  @Override
  public byte[] getDataToSign() {
    return (senderPublicKey.toString() + receiverPublicKey.toString() + amount).getBytes();
  }
}


