package blockchain.User.Client;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.BlockData.Transaction;
import blockchain.User.User;
import java.security.PublicKey;
import java.util.List;
import java.util.Random;

  public class TransactionClient<T extends IBlockData> extends User<T> implements Runnable {
    private final List<User<T>> possibleRecipients;
    private final Random random = new Random();

    public TransactionClient(String name, BlockChain<T> chain, List<User<T>> recipients) {
      super(chain, name);
      this.possibleRecipients = recipients;
    }

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        User<T> recipientUser = pickRandomRecipientUser();
        if (recipientUser == null) {
          break;
        }

        PublicKey recipientKey = recipientUser.getPublicKey();
        String recipientName   = recipientUser.getName();

        if (recipientKey == null) {
          break;
        }
        int amount = 1 + random.nextInt(30);
        Transaction tx = createTransaction(publicKey, this.getName(), recipientKey, recipientName, amount);

        boolean added = blockchain.addNewData((T) tx);
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
  }

    private User<T> pickRandomRecipientUser() {
      if (possibleRecipients.isEmpty()) return null;

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
        int amount)  {
      long txId = blockchain.getUniqueId();

      byte[] signature;
      try {
        signature = Transaction.generateSignature(publicKey, receiverKey,amount,  privateKey);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      return new Transaction(senderKey,senderName,  receiverKey,receiverName  ,txId ,signature,  amount);
    }

  }