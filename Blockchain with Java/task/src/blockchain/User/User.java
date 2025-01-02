package blockchain.User;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.Key.GenerateKeys;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class User<T extends IBlockData> {

  protected BlockChain<T> blockchain;
  protected PrivateKey privateKey;
  protected PublicKey publicKey;
  private int virtualCoins;
  protected String name;

  public User(BlockChain<T> chain, String name) {
    KeyPair keyPair;
    try {
      keyPair = GenerateKeys.generateKeyPair();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    this.privateKey = keyPair.getPrivate();
    this.publicKey = keyPair.getPublic();
    this.blockchain = chain;
    this.virtualCoins = 100;
    this.name = name;
  }

  public int getVirtualCoins() {
    return virtualCoins;
  }
  public void addCoins(int VC) {
    virtualCoins += VC;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public String getName() {
    return name;
  }
}
