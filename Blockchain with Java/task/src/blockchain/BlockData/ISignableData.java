package blockchain.Key;

import java.security.PublicKey;

public interface ISignableData {

  PublicKey getPublicKey();
  byte[] getSignature();
  byte[] getDataToSign();
}
