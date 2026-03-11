package com.myfinance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myfinance.ai.FinanceAssistant;
import com.myfinance.controller.dto.ChatResponseDTO;
import com.myfinance.model.Message;
import com.myfinance.model.MessageType;
import com.myfinance.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private FinanceAssistant assistant;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void chat_success() {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(assistant.chat(eq(1L), eq("Hello"))).thenReturn("Hi there!");
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        ChatResponseDTO result = chatService.chat("Hello");

        assertEquals("Hi there!", result.getMessage());
        assertEquals("success", result.getStatus());
        // isTransaction is false because no transaction was created (TransactionContext not marked)
        assertEquals(false, result.isTransaction());
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
}
