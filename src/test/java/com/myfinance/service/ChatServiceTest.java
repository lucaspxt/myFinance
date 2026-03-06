package com.myfinance.service;

import com.myfinance.ai.FinanceAssistant;
import com.myfinance.model.Message;
import com.myfinance.model.MessageType;
import com.myfinance.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
}
