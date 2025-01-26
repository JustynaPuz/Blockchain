package blockchain.BlockData;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PendingData<T extends IBlockData> {
  private final ConcurrentLinkedQueue<T> pendingData = new ConcurrentLinkedQueue<>();

  public void addData(T item) {
    pendingData.add(item);
  }

  public List<T> getData() {
    return List.copyOf(pendingData);
  }

  public void clearData() {
    pendingData.clear();
  }

}
