package myTests.blockchain.User.TransactionClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import blockchain.BlockChain.BlockChain;
import blockchain.BlockData.IBlockData;
import blockchain.BlockData.Transaction;
import blockchain.User.TransactionClient.TransactionClient;
import blockchain.User.User;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionClientTest {

  @Mock
  private BlockChain<IBlockData> mockBlockChain;

  @Mock
  private User<IBlockData> mockRecipientUser;

  @Test
  @DisplayName("Test doWork() creates and add a transactions")
  void testDoWork() throws Exception {
    List<User<IBlockData>> mockRecipients = Collections.singletonList(mockRecipientUser);

    PublicKey recipientPublicKey = mock(PublicKey.class);
    when(mockRecipientUser.getPublicKey()).thenReturn(recipientPublicKey);
    when(mockBlockChain.getUsers()).thenReturn(mockRecipients);
    when(mockBlockChain.getUniqueId()).thenReturn(1L);

    TransactionClient<IBlockData> client = new TransactionClient<>("Sender", mockBlockChain);
    client.doWork();

    verify(mockBlockChain, times(1)).addNewData(any(Transaction.class));

  }

  @Test
  @DisplayName("Test doWork() does nothing if no recipients are available")
  void testDoWorkNoRecipients() {

    TransactionClient<IBlockData> client = new TransactionClient<>("Sender", mockBlockChain);
    // When
    client.doWork();
    // Then
    verify(mockBlockChain, never()).addNewData(any());
  }
}