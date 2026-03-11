import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../components/layout/header/header.component';
import { MessageComponent, MessageData } from '../../shared/message/message.component';
import { ChatService } from '../../services/chat.service';
import { BalanceRefreshService } from '../../services/balance-refresh.service';
import { finalize, catchError, takeUntil } from 'rxjs/operators';
import { of, Subject, firstValueFrom } from 'rxjs';
import { Router, NavigationEnd } from '@angular/router';
import { AppService, AppStatus } from '../../services/app.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SocialLinksComponent } from '../../shared/social-links/social-links.component';
import { RepeatButtonComponent } from '../../shared/repeat-button/repeat-button.component';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, HeaderComponent, MessageComponent, FormsModule, TranslateModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('chatMessagesContainer') private messagesContainer!: ElementRef;

  messages: MessageData[] = [];
  newMessage: string = '';
  initialLoading: boolean = true;
  private scrollToBottom: boolean = false;
  private _overlayStart = 0;
  private readonly _overlayMinMs = 1500; // minimum splash duration in ms
  isLoading: boolean = false;
  userId?: number;

  suggestedQuestions: string[] = [];

  // Subject used to signal teardown for active subscriptions (takeUntil)
  private destroy$ = new Subject<void>();

  constructor(
    private chatService: ChatService,
    private appService: AppService,
    private translateService: TranslateService,
    private router: Router,
    private balanceRefreshService: BalanceRefreshService
  ) {}

  ngOnInit(): void {
    // If the app was loaded directly on a language route (e.g. /pt), ensure
    // the translate service switches language before we initialize the chat
    const initialUrl = this.router.url || '';
    let initialLang: string | null = null;
    // detect a two-letter language code in the first segment of the path (e.g. /pt, /en)
    initialLang = this.extractLangFromUrl(initialUrl);

    const startChat = () => {
      // Set suggested questions with translations
      this.updateSuggestedQuestions();
      // Initialize a new chat session
      // mark overlay start and init chat
      this._overlayStart = Date.now();
      this.initChat();
    };

    if (initialLang) {
      // switch language first, then start
      const useSub = this.translateService.use(initialLang).subscribe(() => {
        try { useSub.unsubscribe(); } catch (e) {}
        startChat();
      });
    } else {
      startChat();
    }

    // Subscribe to language changes
    this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.updateSuggestedQuestions();

      // If the first message is a system message, re-translate it as the welcome message
      if (this.messages.length > 0 && !this.messages[0].isUser) {
        this.waitForTranslation('CHAT.WELCOME_MESSAGE').then((msg: string) => {
          this.messages = [];
          this.messages.push({
            text: msg,
            isUser: false,
            type: 'text',
            timestamp: new Date()
          });
        });
      }
    });

    // Also listen to route changes: if user switches URL to /pt directly, retranslate welcome
    this.router.events.pipe(takeUntil(this.destroy$)).subscribe(evt => {
      if (evt instanceof NavigationEnd) {
        const url = evt.urlAfterRedirects || (evt as any).url;
        if (url) {
          // extract a two-letter language code from the path
          const lang = this.extractLangFromUrl(url as string);

          if (lang) {
            // ensure translateService switches language first
            const useSub = this.translateService.use(lang).subscribe(() => {
              try { useSub.unsubscribe(); } catch (e) {}
              if (this.messages.length > 0 && !this.messages[0].isUser) {
                this.waitForTranslation('CHAT.WELCOME_MESSAGE').then((msg: string) => {
                  this.messages[0].text = msg;
                  this.scrollToBottom = true;
                });
              }
            });
          }
        }
      }
    });

    // Subscribe to app status to show social links when done
    this.appService.appStatus$.pipe(takeUntil(this.destroy$)).subscribe(status => {
      if (status === AppStatus.DONE) {
        this.addSocialLinksOnce();
      }
    });
  }

  private updateSuggestedQuestions(): void {
    // Load suggested questions via translate.get to ensure translations are available
    this.translateService.get([
      'CHAT.SUGGESTED_QUESTIONS.QUESTION1',
      'CHAT.SUGGESTED_QUESTIONS.QUESTION2',
      'CHAT.SUGGESTED_QUESTIONS.QUESTION3'
    ]).subscribe((res: any) => {
      this.suggestedQuestions = [
        res['CHAT.SUGGESTED_QUESTIONS.QUESTION1'],
        res['CHAT.SUGGESTED_QUESTIONS.QUESTION2'],
        res['CHAT.SUGGESTED_QUESTIONS.QUESTION3']
      ];
    });
  }

  private initChat(): void {
    this.isLoading = true;
    this.chatService.startNewChat()
      .pipe(
        catchError(error => {
          console.error('Error initializing chat session:', error);
          return of({ userId: 1 });
        })
      )
      .subscribe(response => {
        this.userId = response.userId;
        // Ensure the welcome message translation is loaded before removing the initial overlay
        this.waitForTranslation('CHAT.WELCOME_MESSAGE').then((msg: string) => {
          this.isLoading = false;
          this.addSystemMessage(msg);
          this.hideInitialOverlayWhenReady();
        });
      });
  }

  addSystemMessage(text: string): void {
    this.messages.push({
      text,
      isUser: false,
      type: 'text',
      timestamp: new Date()
    });

    // Activate scroll to bottom when a system message is added
    this.scrollToBottom = true;
  }

  private addSocialLinksOnce(): void {
    const links = [
      { label: 'CHAT.DONE.SITE', url: 'https://quasterai.com/', icon: 'fas fa-globe' },
      { label: 'CHAT.DONE.INSTAGRAM', url: 'https://www.instagram.com/quasterai', icon: 'fab fa-instagram' },
      { label: 'CHAT.DONE.LINKEDIN', url: 'https://www.linkedin.com/company/quasterai', icon: 'fab fa-linkedin' }
    ];

    this.messages.push({
      text: '',
      isUser: false,
      timestamp: new Date(),
      type: 'component',
      component: SocialLinksComponent,
      componentInputs: { links }
    });

    // Ensure scroll to bottom to reveal the buttons
    this.scrollToBottom = true;
  }

  ngAfterViewChecked() {
    if (this.scrollToBottom) {
      this.scrollToBottomOfChat();
      this.scrollToBottom = false;
    }
  }

  private hideInitialOverlayWhenReady(): void {
    const elapsed = Date.now() - this._overlayStart;
    const remaining = Math.max(0, this._overlayMinMs - elapsed);
    setTimeout(() => {
      this.initialLoading = false;
    }, remaining);
  }

  /**
   * Waits for a translation key to be resolved. If the translation service
   * returns the key itself, it will wait for the next language change or until
   * a timeout is reached, then resolve with the best available value.
   */
  private waitForTranslation(key: string, timeoutMs = 2000): Promise<string> {
    return new Promise(async resolve => {
      try {
        const msg = await firstValueFrom(this.translateService.get(key));
        if (msg && msg !== key) {
          resolve(msg);
          return;
        }
      } catch (e) {
        // ignore and fall back to waiting for lang change or timeout
      }

      const langChangePromise = (async () => {
        try {
          await firstValueFrom(this.translateService.onLangChange);
          const m2 = await firstValueFrom(this.translateService.get(key));
          if (m2 && m2 !== key) return m2;
        } catch (e) {
          // ignore
        }
        return this.translateService.instant(key);
      })();

      const timeoutPromise = new Promise<string>(res => setTimeout(() => res(this.translateService.instant(key)), timeoutMs));

      resolve(await Promise.race([langChangePromise, timeoutPromise]));
    });
  }

  private scrollToBottomOfChat(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
    } catch (err) {
      console.error('Error scrolling to bottom of chat:', err);
    }
  }

  shouldShowSuggestedQuestions(): boolean {
    // Show suggestions if there are no messages or if there's only 1 system message
    return this.messages.length === 0 ||
      (this.messages.length === 1 && !this.messages[0].isUser);
  }

  sendMessage(question?: string): void {
  // If a question is passed as a parameter, use it; otherwise use the value of newMessage
  const messageText = question || this.newMessage;
  // Capture the original message text to ensure the repeat button re-sends the exact same text
  const originalMessageText = messageText;

    if (!messageText.trim() || this.isLoading) return;

  // Add the user's message
    const userMessage: MessageData = {
      text: messageText,
      isUser: true,
      type: 'text',
      timestamp: new Date()
    };
    this.messages.push(userMessage);

    // Scroll to bottom
    this.scrollToBottom = true;

    // Clear message input
    this.newMessage = '';

    // Set loading state
    this.isLoading = true;

    // Send message to API
    this.chatService.sendMessage(messageText)
      .pipe(
        catchError(error => {
          console.error('sendMessage catchError:', error);
          // signal a normalized error response so subscribe branch can handle it
          return of([{
            message: this.translateService.instant('CHAT.ERROR_MESSAGE'),
            status: 'error'
          } as any]);
        }),
        finalize(() => {
          this.isLoading = false;
          this.scrollToBottom = true;
        })
      )
      .subscribe(response => {
        // Normalize missing/empty responses into an error path that still shows the retry
        if (!response || response.length === 0) {
          this.appService.setAppStatus('error' as AppStatus);
          this.addSystemMessage(this.translateService.instant('CHAT.ERROR_MESSAGE'));

          // append repeat button so user can retry
          this.appendRepeatButton(originalMessageText);
          return;
        }

        this.appService.setAppStatus(response[0].status as AppStatus);
        // Add the system response
        this.addSystemMessage(response[0].message);

        // Trigger balance refresh if this was a transaction
        if (response[0].isTransaction) {
          this.balanceRefreshService.triggerRefresh();
        }

        // If server flagged error or explicitly requested a repeat, append the repeat button
        const shouldShowRepeat = !!response[0].showRepeat || response[0].status === 'error';
        if (shouldShowRepeat) {
          // Use the captured original text so the callback always re-sends the intended message
          this.appendRepeatButton(originalMessageText);
        }
      });
  }

  /** Append a RepeatButton component message that re-sends the provided text when clicked. */
  private appendRepeatButton(originalMessageText: string): void {
    this.messages.push({
      text: '',
      isUser: false,
      timestamp: new Date(),
      type: 'component',
      component: RepeatButtonComponent,
      componentInputs: {
        label: this.translateService.instant('CHAT.RETRY_BUTTON') || 'Repeat',
        onRepeat: () => this.sendMessage(originalMessageText)
      }
    });
    this.scrollToBottom = true;
  }

    ngOnDestroy(): void {
      this.destroy$.next();
      this.destroy$.complete();
    }

    /** Extract a 2-letter language code from the first path segment of a URL. */
    private extractLangFromUrl(url: string): string | null {
      if (!url) return null;
      const m = url.match(/\/([a-z]{2})(?:\/|$)/i);
      return m ? m[1].toLowerCase() : null;
    }
}
