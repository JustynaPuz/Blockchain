package blockchain.User;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.Key.GenerateKeys;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class User<T extends IBlockData> implements Runnable {

  protected BlockChain<T> blockchain;
  protected PrivateKey privateKey;
  protected PublicKey publicKey;
  protected String name;

  public User(BlockChain<T> chain, String name) {
    KeyPair keyPair;
    try {
      keyPair = GenerateKeys.generateKeyPair();
      this.privateKey = keyPair.getPrivate();
      this.publicKey = keyPair.getPublic();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    this.blockchain = chain;
    this.name = name;
  }

  @Override
  public final void run() {
    while (!Thread.currentThread().isInterrupted()) {
      doWork();
      try {
        Thread.sleep(getSleepTime());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  protected abstract long getSleepTime();

  protected abstract void doWork();

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public String getName() {
    return name;
  }
}
