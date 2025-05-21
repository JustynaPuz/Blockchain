package blockchain.Key;

import blockchain.BlockData.ISignableData;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

public class SignatureChecker {
  private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

  public static boolean check(ISignableData data) {
    Signature signature;
    try {
      signature = Signature.getInstance(SIGNATURE_ALGORITHM);
      signature.initVerify(data.getPublicKey());
      signature.update(data.getDataToSign());
      return signature.verify(data.getSignature());
    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
      e.printStackTrace();
    }
    return false;
  }

}
