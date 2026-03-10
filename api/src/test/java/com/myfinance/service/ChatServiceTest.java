package com.myfinance.service;

import com.myfinance.ai.FinanceAssistant;
import com.myfinance.ai.TransactionExtractionResult;
import com.myfinance.model.BankAccount;
import com.myfinance.model.Category;
import com.myfinance.model.Message;
import com.myfinance.model.MessageType;
import com.myfinance.model.TransactionType;
import com.myfinance.repository.BankAccountRepository;
import com.myfinance.repository.CategoryRepository;
import com.myfinance.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private FinanceAssistant assistant;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void chat_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), eq("Hello"))).thenReturn("Hi there!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = chatService.chat("Hello");

        assertEquals("Hi there!", result);
    }

    @Test
    void chat_savesUserMessage() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), eq("Hello"))).thenReturn("Hi there!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        chatService.chat("Hello");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository, times(2)).save(captor.capture());

        Message userMessage = captor.getAllValues().get(0);
        assertEquals("Hello", userMessage.getMessage());
        assertEquals(MessageType.USER, userMessage.getType());
        assertEquals(1L, userMessage.getMemoryId());
    }

    @Test
    void chat_savesAssistantMessage() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), eq("Hello"))).thenReturn("Hi there!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        chatService.chat("Hello");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository, times(2)).save(captor.capture());

        Message assistantMessage = captor.getAllValues().get(1);
        assertEquals("Hi there!", assistantMessage.getMessage());
        assertEquals(MessageType.ASSISTANT, assistantMessage.getType());
        assertEquals(1L, assistantMessage.getMemoryId());
    }

    @Test
    void chat_createsTransactionWhenExtractionSucceeds() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), any())).thenReturn("Transaction recorded!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionExtractionResult extraction = new TransactionExtractionResult(
                TransactionType.DEBIT, "Food", "Checking", 50.0);
        when(assistant.extractTransaction(eq(1L), any())).thenReturn(extraction);

        Category category = new Category("Food", 1L);
        category.setId(10L);
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(20L);

        when(categoryRepository.findByUserIdAndNameIgnoreCase(1L, "Food"))
                .thenReturn(Optional.of(category));
        when(bankAccountRepository.findByUserIdAndNameIgnoreCase(1L, "Checking"))
                .thenReturn(Optional.of(bankAccount));

        chatService.chat("Spent 50 on food in checking");

        verify(transactionService).create(TransactionType.DEBIT, 10L, 20L, 50.0);
    }

    @Test
    void chat_doesNotCreateTransactionWhenExtractionReturnsNull() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), any())).thenReturn("Hi!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assistant.extractTransaction(eq(1L), any())).thenReturn(null);

        chatService.chat("Hello");

        verify(transactionService, never()).create(any(), any(), any(), any());
    }

    @Test
    void chat_doesNotCreateTransactionWhenExtractionHasMissingFields() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), any())).thenReturn("Hi!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionExtractionResult incomplete = new TransactionExtractionResult(
                TransactionType.DEBIT, null, "Checking", 50.0);
        when(assistant.extractTransaction(eq(1L), any())).thenReturn(incomplete);

        chatService.chat("Something");

        verify(transactionService, never()).create(any(), any(), any(), any());
    }

    @Test
    void chat_doesNotCreateTransactionWhenCategoryNotFound() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), any())).thenReturn("Hi!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionExtractionResult extraction = new TransactionExtractionResult(
                TransactionType.DEBIT, "Unknown", "Checking", 50.0);
        when(assistant.extractTransaction(eq(1L), any())).thenReturn(extraction);
        when(categoryRepository.findByUserIdAndNameIgnoreCase(1L, "Unknown"))
                .thenReturn(Optional.empty());

        chatService.chat("Spent 50 on something");

        verify(transactionService, never()).create(any(), any(), any(), any());
    }

    @Test
    void chat_doesNotCreateTransactionWhenBankAccountNotFound() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), any())).thenReturn("Hi!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionExtractionResult extraction = new TransactionExtractionResult(
                TransactionType.DEBIT, "Food", "Unknown", 50.0);
        when(assistant.extractTransaction(eq(1L), any())).thenReturn(extraction);

        Category category = new Category("Food", 1L);
        category.setId(10L);
        when(categoryRepository.findByUserIdAndNameIgnoreCase(1L, "Food"))
                .thenReturn(Optional.of(category));
        when(bankAccountRepository.findByUserIdAndNameIgnoreCase(1L, "Unknown"))
                .thenReturn(Optional.empty());

        chatService.chat("Spent 50 on food");

        verify(transactionService, never()).create(any(), any(), any(), any());
    }
}
