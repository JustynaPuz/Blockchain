package blockchain.BlockChain;

public class DifficultyManager {
  private int numberOfZeros = 0;

  public void adjustDifficulty(long creationTime) {
    if(numberOfZeros == 4) {
      numberOfZeros = Math.max(0, numberOfZeros - 1);
      System.out.println("N was decreased by 1");
    }else
     if (creationTime < 1) {
      numberOfZeros++;
      System.out.println("N was increased to " + numberOfZeros);
    } else if (creationTime > 5) {
      numberOfZeros = Math.max(0, numberOfZeros - 1);
      System.out.println("N was decreased by 1");
    } else {
      System.out.println("N stays the same");
    }
  }

  public int getNumberOfZeros() {
    return numberOfZeros;
  }
}

