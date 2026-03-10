import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

// MyFinance API Response - the chat endpoint returns a simple string
export interface ChatResponseItem {
  message: string;
  status: string;
}

export interface ChatRequest {
  message: string;
}

export interface ChatMessage {
  id?: number;
  type: 'USER' | 'ASSISTANT';
  content: string;
  userId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  // MyFinance API base URL - adjust if running on different port
  private apiUrl = 'http://localhost:8080/api';
  private userId: number = 1; // Default user ID - should come from auth service

  constructor(private http: HttpClient) {
    // Try to get userId from localStorage if available
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      this.userId = parseInt(storedUserId, 10);
    }
  }

  /**
   * Sets the current user ID
   * @param userId The user ID to set
   */
  setUserId(userId: number): void {
    this.userId = userId;
    localStorage.setItem('userId', userId.toString());
  }

  /**
   * Gets the current user ID
   * @returns The current user ID
   */
  getUserId(): number {
    return this.userId;
  }

  /**
   * Sends a message to the MyFinance AI assistant
   * @param message The text of the message sent by the user
   * @returns Observable with the assistant's response
   */
  sendMessage(message: string): Observable<ChatResponseItem[]> {
    const request: ChatRequest = {
      message: message
    };
    
    // The API returns a plain string with the assistant's response
    return this.http.post(`${this.apiUrl}/chat`, request, { responseType: 'text' })
      .pipe(
        map((response: string) => [
          {
            message: response,
            status: 'success'
          }
        ])
      );
  }

  /**
   * Retrieves the chat history for the current user
   * @returns Observable with the chat history
   */
  getChatHistory(): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/messages/user/${this.userId}`);
  }

  /**
   * Initializes a new chat session (clears local state)
   * @returns Observable with confirmation
   */
  startNewChat(): Observable<{ userId: number }> {
    // In MyFinance, we don't need to create a new chat session
    // All messages are stored per user automatically
    return new Observable(observer => {
      observer.next({ userId: this.userId });
      observer.complete();
    });
  }
}
