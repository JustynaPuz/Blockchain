package blockchain.User.Client;

import blockchain.BlockChain.BlockChain;

import blockchain.BlockData.IBlockData;
import blockchain.BlockData.Message;
import blockchain.User.User;
import java.util.Random;

public class Client<T extends IBlockData> extends User<T> implements Runnable {

  public Client(String name, BlockChain<T> chain) {
    super(chain,name);
  }

  @Override
  public void run() {
    Random random = new Random();
    while (!Thread.currentThread().isInterrupted()) {
      String text = name + ": Hello " + random.nextInt(100);
      try {
        blockchain.addNewData((T) createMessage(text));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  private Message createMessage(String text) {
    long id = blockchain.getUniqueId();
    byte[] signature;
    try {
      signature = Message.generateSignature(text, id, privateKey);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new Message(text, signature, id, publicKey);
  }
}
