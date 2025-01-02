package blockchain.BlockChain;

import blockchain.Block.Block;

import blockchain.BlockData.IBlockData;
import blockchain.BlockData.PendingData;
import blockchain.BlockData.Transaction;
import blockchain.Key.GenerateKeys;
import blockchain.Key.SignatureChecker;
import blockchain.User.User;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class BlockChain<T extends IBlockData> {

  public static final int MAX_NUMBER_OF_BLOCKS = 15;
  public static final int REWARD_FOR_MINING = 100;

  private static BlockChain instance;
  private List<Block<T>> chain;
  private final long creationTime;
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private PendingData<T> pendingData;
  private DifficultyManager difficultyManager;
  private Block<T> lastBlock;
  private static long uniqueId = 1;
  private List<User<T>> users;
  private PublicKey systemPublicKey;
  private PrivateKey systemPrivateKey;

  private BlockChain() throws Exception {
    this.chain = new ArrayList<>();
    this.creationTime = new Date().getTime();
    this.pendingData = new PendingData<>();
    this.difficultyManager = new DifficultyManager();
    this.users = new ArrayList<>();
    KeyPair keyPair = GenerateKeys.generateKeyPair();
    systemPublicKey = keyPair.getPublic();
    systemPrivateKey = keyPair.getPrivate();
  }

  public static <K extends IBlockData> BlockChain<K> getInstance(Class<K> clazz) throws Exception {
    if (Objects.isNull(instance)) {
      instance = new BlockChain<K>();
    }
    return (BlockChain<K>) instance;
  }

  public void tryToAddBlock(Block<T> block) {
    readWriteLock.writeLock().lock();
    try {
      if (!BlockchainValidator.isNewBlockValid(block, this)) {
        return;
      }
      addBlock(block);
      difficultyManager.adjustDifficulty(calculateBlockCreationTime());

      PublicKey minerKey = block.getMinerPublicKey();
      if (minerKey != null) {
        byte[] signature;
        try {
          signature = Transaction.generateSignature(systemPublicKey, minerKey, REWARD_FOR_MINING,
              systemPrivateKey);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        Transaction rewardTx = new Transaction(systemPublicKey, "System", minerKey,
            "Miner" + block.getMinerNumber(), getUniqueId(), signature, REWARD_FOR_MINING);
        pendingData.addData((T) rewardTx);

      }
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  private void addBlock(Block<T> block) {
    chain.add(block);
    lastBlock = block;
    pendingData.clearData();
    System.out.println();
    System.out.println(block.toString());
    long timeOfCreation = calculateBlockCreationTime();
    System.out.println("Block was generating for " + timeOfCreation + " seconds");
  }


  private long calculateBlockCreationTime() {
    if (chain.size() < 2) {
      return (lastBlock.getTimeStamp() - creationTime) / 1000;
    }
    Block secondLastBlock = chain.get(chain.size() - 2);
    return (lastBlock.getTimeStamp() - secondLastBlock.getTimeStamp()) / 1000;
  }

  public synchronized boolean addNewData(T data) {
    readWriteLock.writeLock().lock();
    try {
      if (data instanceof Transaction tx) {
        if (!SignatureChecker.check(tx)) {
          System.out.println("Invalid signature. Rejected: " + tx);
          return false;
        }

        if (tx.isValid()) {
          int senderBalance = getBalance(tx.getSenderPublicKey());
          if (senderBalance < tx.getAmount()) {
            System.out.println("Not enough balance. Tx rejected: " + tx);
            return false;
          }
        }
      }
      pendingData.addData(data);
      return true;
    } finally {
      readWriteLock.writeLock().unlock();
    }

  }

  public synchronized int getBalance(PublicKey address) {
    int balance = 100;
    for (Block<T> b : chain) {
      for (T item : b.getData()) {
        if (item instanceof Transaction tx) {
          if (tx.getSenderPublicKey() != null && tx.getSenderPublicKey().equals(address)) {
            balance -= tx.getAmount();
          }

          if (tx.getReceiverPublicKey() != null && tx.getReceiverPublicKey().equals(address)) {
            balance += tx.getAmount();
          }
        }
      }
    }
    return balance;
  }

  public String getLastBlockHashcode() {
    readWriteLock.readLock().lock();
    try {
      return chain.isEmpty() ? "0" : lastBlock.getHashcode();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  public List<T> getData() {
    readWriteLock.readLock().lock();
    try {
      return pendingData.getData().stream().toList();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  public int getNumberOfBlocks() {
    readWriteLock.readLock().lock();
    try {
      return chain.size();
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  public long getUniqueId() {
    readWriteLock.readLock().lock();
    try {
      return uniqueId++;
    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  public void addUser(User user) {
    users.add(user);

  }

  public Block<T> getLastBlock() {
    return lastBlock;
  }

  public List<Block<T>> getChain() {
    return chain;
  }

  public int getNumberOfZeros() {
    return difficultyManager.getNumberOfZeros();
  }

  public List<User<T>> getUsers() {
    return users;
  }
}
