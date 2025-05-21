package blockchain.User.ChatClient;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.BlockData.Message;
import blockchain.User.User;
import java.util.Random;

public class ChatClient<T extends IBlockData> extends User<T> {

  public ChatClient(String name, BlockChain<T> chain) {
    super(chain, name);
  }

  @Override
  protected void doWork() {
    Random random = new Random();
    String text = name + ": Hello " + random.nextInt(100);
    blockchain.addNewData((T) createMessage(text));
  }

  @Override
  protected long getSleepTime() {
    return 100;
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
