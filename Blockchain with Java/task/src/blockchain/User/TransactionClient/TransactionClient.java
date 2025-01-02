package blockchain.User.TransactionClient;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.BlockData.Transaction;
import blockchain.User.User;
import java.security.PublicKey;
import java.util.List;
import java.util.Random;

public class TransactionClient<T extends IBlockData> extends User<T> {

  private final List<User<T>> possibleRecipients;
  private final Random random = new Random();

  public TransactionClient(String name, BlockChain<T> chain) {
    super(chain, name);
    this.possibleRecipients = chain.getUsers();
  }

  @Override
  protected void doWork() {
    User<T> recipientUser = pickRandomRecipientUser();
    if (recipientUser == null) {
      return;
    }

    PublicKey recipientKey = recipientUser.getPublicKey();
    if(recipientKey == null) {
      return;
    }
    String recipientName = recipientUser.getName();
    int amount = 1 + random.nextInt(50);
    Transaction tx = createTransaction(publicKey, this.getName(), recipientKey, recipientName,
        amount);

    blockchain.addNewData((T) tx);
  }

  @Override
  protected long getSleepTime() {
    return 300;
  }

  private User<T> pickRandomRecipientUser() {
    if (possibleRecipients.isEmpty()) {
      return null;
    }

    for (int i = 0; i < 10; i++) {
      int index = random.nextInt(possibleRecipients.size());
      User<T> candidateUser = possibleRecipients.get(index);
      PublicKey candidateKey = candidateUser.getPublicKey();
      if (!candidateKey.equals(this.publicKey)) {
        return candidateUser;
      }
    }
    return null;
  }

  private Transaction createTransaction(PublicKey senderKey, String senderName,
      PublicKey receiverKey, String receiverName,
      int amount) {
    long txId = blockchain.getUniqueId();

    byte[] signature;
    try {
      signature = Transaction.generateSignature(publicKey, receiverKey, amount, privateKey);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new Transaction(senderKey, senderName, receiverKey, receiverName, txId, signature,
        amount);
  }

}