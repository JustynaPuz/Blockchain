package blockchain.Block;

import blockchain.BlockData.IBlockData;
import blockchain.HashFunction;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Block<T extends IBlockData> {

  private final int id;
  private final String previousHashcode;
  private final long timeStamp;
  private final long magicNumber;
  private final int minerNumber;
  private final PublicKey minerPublicKey;
  protected long maxIdentifierInBlock;
  private final List<T> dataList;

  public Block(int id, String previousHashcode, int minerNumber, List<T> dataList,
      PublicKey minerPublicKey) {
    this.id = id;
    this.previousHashcode = previousHashcode;
    this.timeStamp = new Date().getTime();
    Random random = new Random();
    this.magicNumber = random.nextInt();
    this.minerNumber = minerNumber;
    this.maxIdentifierInBlock = 0;
    this.dataList = validateData(dataList);
    this.minerPublicKey = minerPublicKey;
  }

  private synchronized List<T> validateData(List<T> items) {
    List<T> validatedItems = new ArrayList<>();

    for (T item : items) {
      if (item.getUniqueId() <= maxIdentifierInBlock) {
        System.err.println("Rejected item with ID: " + item.getUniqueId());
      } else {
        validatedItems.add(item);
        if (item.getUniqueId() > maxIdentifierInBlock) {
          maxIdentifierInBlock = item.getUniqueId();
        }
      }
    }
    return validatedItems;
  }

  public String getPreviousHashcode() {
    return previousHashcode;
  }

  public PublicKey getMinerPublicKey() {
    return minerPublicKey;
  }

  public int getMinerNumber() {
    return minerNumber;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public long getMaxIdentifierInBlock() {
    return maxIdentifierInBlock;
  }


  public String getStringToHash() {
    return previousHashcode + magicNumber;
  }

  public String getHashcode() {
    return HashFunction.applySha256(this.getStringToHash());
  }

  public List<T> getData() {
    return dataList;
  }

  public String toString() {
    return "Block: \n" +
        "Created by miner" + minerNumber + "\n" +
        "miner" + minerNumber + " gets 100 VC" + "\n" +
        "Id: " + id + "\n" +
        "Timestamp:" + timeStamp + "\n" +
        "Magic number: " + magicNumber + "\n" +
        "Hash of the previous block:\n" +
        previousHashcode + "\n" +
        "Hash of the block: \n" +
        HashFunction.applySha256(this.getStringToHash()) +
        (dataList.isEmpty()
            ? "\nBlock data: no data"
            : "\nBlock data:\n" + dataList.stream()
                .map(Object::toString)
                .collect(Collectors.joining()));
  }
}
