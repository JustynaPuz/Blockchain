package myTests.blockchain.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.User;
import blockchain.User.UserCoordinator;
import blockchain.User.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCoordinatorTest {

  @Mock
  private BlockChain<IBlockData> mockBlockchain;

  @Test
  @DisplayName("Test UserCoordinator.setup() creates users")
  void testSetupCreatesUsersAndStartsThreads() {
    //Given
    int numberOfUsers = 5;
    String userNamePrefix = "User";
    UserType userType = UserType.CHAT_CLIENT;

    //When
    UserCoordinator<IBlockData> userCoordinator = new UserCoordinator<>("TestGroup", userType, userNamePrefix, numberOfUsers, mockBlockchain);
    userCoordinator.setup();
    int numberOfThreads = userCoordinator.getThreadGroup().activeCount();

    //Then
    assertEquals("TestGroup", userCoordinator.getThreadGroup().getName());
    verify(mockBlockchain, times(numberOfUsers)).addUser(any(User.class));
    assertEquals(numberOfUsers, numberOfThreads);

  }

  @Test
  @DisplayName("Test UserCoordinator.start() interrupts threads at MAX_NUMBER_OF_BLOCKS")
  void testStartInterruptsThreads() {
    // When
    when(mockBlockchain.getNumberOfBlocks()).thenReturn(BlockChain.MAX_NUMBER_OF_BLOCKS);

    UserCoordinator<IBlockData> userCoordinator = new UserCoordinator<>(
        "TestGroup", UserType.MINER, "User", 5, mockBlockchain);

    userCoordinator.start();

    Thread[] threads = new Thread[userCoordinator.getThreadGroup().activeCount()];
    userCoordinator.getThreadGroup().enumerate(threads);

    //Then
    verify(mockBlockchain, atLeastOnce()).getNumberOfBlocks();

    for (Thread thread : threads) {
      assertNotNull(thread);
      assertTrue(thread.isInterrupted());
    }
  }

}