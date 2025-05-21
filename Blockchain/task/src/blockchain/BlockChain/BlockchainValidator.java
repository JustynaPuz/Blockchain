package blockchain.BlockChain;

import blockchain.Block.Block;
import blockchain.BlockData.IBlockData;
import blockchain.BlockData.ISignableData;
import blockchain.Key.SignatureChecker;
import java.util.List;
import java.util.Objects;

public class BlockchainValidator {

  public static <T extends IBlockData> boolean isBlockchainValid(BlockChain<T> chain) {
    List<Block<T>> blocks = chain.getChain();

    if (blocks.isEmpty()) {
      return true;
    }
    if (!isFirstBlockValid(blocks.getFirst())) {
      return false;
    }
    for (int i = 1; i < blocks.size(); i++) {
      Block<T> currentBlock = blocks.get(i);
      Block<T> previousBlock = blocks.get(i - 1);
      if (!hasValidPreviousHash(currentBlock, previousBlock)) {
        return false;
      }
      if (hasOneOfTheMessagesAnInvalidId(currentBlock, previousBlock)) {
        return false;
      }
      if (hasOneOfTheMessagesAnInvalidSignature(blocks.get(i))) {
        return false;
      }
    }
    return true;
  }

  public static <T extends IBlockData> boolean isNewBlockValid(Block<T> block,
      BlockChain<T> chain) {

    if (chain.getNumberOfBlocks() == 0) {
      return isFirstBlockValid(block);
    }

    Block<T> lastBlock = chain.getLastBlock();
    if (!hasValidPreviousHash(block, lastBlock)) {
      return false;
    }

    if (!hasValidNumberOfZerosInHash(block, chain)) {
      return false;
    }

    int indexOfLastBlock = chain.getNumberOfBlocks() - 1;
    if (hasOneOfTheMessagesAnInvalidId(block, lastBlock)) {
      return false;
    }

    if (hasOneOfTheMessagesAnInvalidSignature(block)) {
      return false;
    }

    return true;
  }


  private static <T extends IBlockData> boolean isFirstBlockValid(Block<T> block) {
    return "0".equals(block.getPreviousHashcode());
  }

  private static <T extends IBlockData> boolean hasOneOfTheMessagesAnInvalidId(Block<T> block,
      Block<T> previousBlock) {

    long prevMaxId = previousBlock.getMaxIdentifierInBlock();
    for (T t : block.getData()) {
      if (t.getUniqueId() <= prevMaxId) {
        System.err.println("Block rejected: data " + t
            + " has ID <= last block’s max ID of " + prevMaxId);
        return true;
      }
    }
    return false;
  }

  private static <T extends IBlockData> boolean hasValidPreviousHash(Block<T> block,
      Block<T> previousBlock) {
    return Objects.equals(previousBlock.getHashcode(), block.getPreviousHashcode());
  }

  private static <T extends IBlockData> boolean hasOneOfTheMessagesAnInvalidSignature(
      Block<T> block) {
    return block.getData().stream()
        .anyMatch(data -> !SignatureChecker.check((ISignableData) data));
  }

  private static <T extends IBlockData> boolean hasValidNumberOfZerosInHash(Block<T> block,
      BlockChain<T> chain) {
    int numberOfZeros = chain.getNumberOfZeros();
    return block.getHashcode().startsWith("0".repeat(numberOfZeros));
  }

}
