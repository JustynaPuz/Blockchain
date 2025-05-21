package myTests.blockchain.User;

import static org.junit.jupiter.api.Assertions.*;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.User.ChatClient.ChatClient;
import blockchain.User.Miner.Miner;
import blockchain.User.TransactionClient.TransactionClient;
import blockchain.User.User;
import blockchain.User.UserFactory;
import blockchain.User.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {
  @Mock
  BlockChain<IBlockData> mockBlockchain;
  @Test
  void testCreateMiner() {

    String name = "Miner1";

    User user = UserFactory.createUser(UserType.MINER, name, mockBlockchain);

    assertNotNull(user);
    assertTrue(user instanceof Miner);
    assertEquals(name, user.getName());
  }

  @Test
  void testCreateChatClient() {
    String name = "ChatClient1";

    User user = UserFactory.createUser(UserType.CHAT_CLIENT, name, mockBlockchain);

    assertNotNull(user);
    assertTrue(user instanceof ChatClient);
    assertEquals(name, user.getName());
  }

  @Test
  void testCreateTransactionClient() {
    String name = "TransactionClient1";

    User user = UserFactory.createUser(UserType.TRANSACTION_CLIENT, name, mockBlockchain);

    assertNotNull(user);
    assertTrue(user instanceof TransactionClient);
    assertEquals(name, user.getName());
  }

}