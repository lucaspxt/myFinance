import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HeaderComponent } from '../../components/layout/header/header.component';
import { MessageComponent, MessageData } from '../../shared/message/message.component';
import { ChatService } from '../../services/chat.service';
import { BalanceRefreshService } from '../../services/balance-refresh.service';
import { CategoryService, Category } from '../../services/category.service';
import { BankAccountService, BankAccount } from '../../services/bank-account.service';
import { BalanceService, CategoryBalanceDTO, BankAccountBalanceDTO } from '../../services/balance.service';
import { TransactionService, Transaction, TransactionRequest } from '../../services/transaction.service';
import { CurrencyFormatPipe } from '../../shared/pipes/currency-format.pipe';
import { finalize, catchError, takeUntil } from 'rxjs/operators';
import { of, Subject, firstValueFrom, forkJoin } from 'rxjs';
import { Router, NavigationEnd } from '@angular/router';
import { AppService, AppStatus } from '../../services/app.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SocialLinksComponent } from '../../shared/social-links/social-links.component';
import { RepeatButtonComponent } from '../../shared/repeat-button/repeat-button.component';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, HeaderComponent, MessageComponent, FormsModule, ReactiveFormsModule, TranslateModule, CurrencyFormatPipe],
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
  categories: Category[] = [];
  categoryBalances: CategoryBalanceDTO[] = [];
  bankAccounts: BankAccount[] = [];
  bankAccountBalances: BankAccountBalanceDTO[] = [];
  sidebarLoading: boolean = true;
  activeTab: 'categories' | 'accounts' = 'categories';
  activeView: 'chat' | 'transactions' = 'chat';
  showModal: boolean = false;
  modalTitle: string = '';
  modalTransactions: Transaction[] = [];
  modalLoading: boolean = false;
  transactionsTitle: string = '';
  selectedCategoryFilter: number | null = null;
  selectedAccountFilter: number | null = null;
  selectedMonthFilter: number | null = null;
  selectedYearFilter: number | null = null;
  
  // Month and year options
  months = [
    { value: 1, label: 'CHAT.FILTERS.MONTHS.JANUARY' },
    { value: 2, label: 'CHAT.FILTERS.MONTHS.FEBRUARY' },
    { value: 3, label: 'CHAT.FILTERS.MONTHS.MARCH' },
    { value: 4, label: 'CHAT.FILTERS.MONTHS.APRIL' },
    { value: 5, label: 'CHAT.FILTERS.MONTHS.MAY' },
    { value: 6, label: 'CHAT.FILTERS.MONTHS.JUNE' },
    { value: 7, label: 'CHAT.FILTERS.MONTHS.JULY' },
    { value: 8, label: 'CHAT.FILTERS.MONTHS.AUGUST' },
    { value: 9, label: 'CHAT.FILTERS.MONTHS.SEPTEMBER' },
    { value: 10, label: 'CHAT.FILTERS.MONTHS.OCTOBER' },
    { value: 11, label: 'CHAT.FILTERS.MONTHS.NOVEMBER' },
    { value: 12, label: 'CHAT.FILTERS.MONTHS.DECEMBER' }
  ];
  
  years: number[] = [];

  // Edit modal properties
  showEditModal: boolean = false;
  editForm!: FormGroup;
  editingTransaction?: Transaction;
  editModalLoading: boolean = false;

  // Delete confirmation properties
  showDeleteConfirmation: boolean = false;
  transactionToDelete?: Transaction;

  // Dropdown menu state
  openMenuId: number | null = null;

  // Constants
  private readonly MIN_TRANSACTION_VALUE = 0.01;

  // Subject used to signal teardown for active subscriptions (takeUntil)
  private destroy$ = new Subject<void>();

  constructor(
    private chatService: ChatService,
    private appService: AppService,
    private translateService: TranslateService,
    private router: Router,
    private balanceRefreshService: BalanceRefreshService,
    private categoryService: CategoryService,
    private bankAccountService: BankAccountService,
    private balanceService: BalanceService,
    private transactionService: TransactionService,
    private fb: FormBuilder
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

    // Load sidebar data (categories and balances)
    this.loadSidebarData();

    // Subscribe to balance refresh to reload sidebar data
    this.balanceRefreshService.balanceRefresh$.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.loadSidebarData();
      this.loadYears();
    });
    
    // Load years from backend
    this.loadYears();
  }

  private loadYears(): void {
    this.transactionService.getYears().subscribe({
      next: (years) => {
        this.years = years;
      },
      error: (error) => {
        console.error('Error loading years:', error);
        this.years = [];
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
        if (response[0].transaction) {
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

    private loadSidebarData(): void {
      this.sidebarLoading = true;
      forkJoin({
        categories: this.categoryService.getAll(),
        categoryBalances: this.balanceService.getBalanceByCategory(),
        bankAccounts: this.bankAccountService.getAll(),
        bankAccountBalances: this.balanceService.getBalanceByBankAccount()
      }).pipe(
        catchError(error => {
          console.error('Error loading sidebar data:', error);
          return of({ categories: [], categoryBalances: [], bankAccounts: [], bankAccountBalances: [] });
        })
      ).subscribe(({ categories, categoryBalances, bankAccounts, bankAccountBalances }) => {
        this.categories = categories.filter(c => !c.archived);
        this.categoryBalances = categoryBalances;
        this.bankAccounts = bankAccounts.filter(a => !a.archived);
        this.bankAccountBalances = bankAccountBalances;
        this.sidebarLoading = false;
      });
    }

    switchTab(tab: 'categories' | 'accounts'): void {
      this.activeTab = tab;
    }

    getCategoryBalance(categoryId: number): number {
      const balance = this.categoryBalances.find(b => b.categoryId === categoryId);
      return balance ? balance.balance : 0;
    }

    getBalanceClass(categoryId: number): string {
      const balance = this.getCategoryBalance(categoryId);
      if (balance > 0) return 'positive';
      if (balance < 0) return 'negative';
      return 'zero';
    }

    getBankAccountBalance(accountId: number): number {
      const balance = this.bankAccountBalances.find(b => b.bankAccountId === accountId);
      return balance ? balance.balance : 0;
    }

    getAccountBalanceClass(accountId: number): string {
      const balance = this.getBankAccountBalance(accountId);
      if (balance > 0) return 'positive';
      if (balance < 0) return 'negative';
      return 'zero';
    }

    openCategoryModal(category: Category): void {
      this.activeView = 'transactions';
      this.selectedCategoryFilter = category.id;
      this.selectedAccountFilter = null;
      this.loadTransactions();
    }

    openAccountModal(account: BankAccount): void {
      this.activeView = 'transactions';
      this.selectedAccountFilter = account.id;
      this.selectedCategoryFilter = null;
      this.loadTransactions();
    }

    loadTransactions(): void {
      this.modalLoading = true;
      this.modalTransactions = [];

      this.transactionService.getAll(
        this.selectedCategoryFilter,
        this.selectedAccountFilter,
        this.selectedMonthFilter,
        this.selectedYearFilter
      ).subscribe({
        next: (transactions) => {
          this.modalTransactions = transactions;
          this.modalLoading = false;
        },
        error: (error) => {
          console.error('Error loading transactions:', error);
          this.modalLoading = false;
        }
      });
    }

    onCategoryFilterChange(): void {
      this.loadTransactions();
    }

    onAccountFilterChange(): void {
      this.loadTransactions();
    }
    
    onMonthFilterChange(): void {
      this.loadTransactions();
    }
    
    onYearFilterChange(): void {
      this.loadTransactions();
    }

    closeModal(): void {
      this.showModal = false;
      this.modalTransactions = [];
      this.modalTitle = '';
    }

    getTransactionTypeClass(type: string): string {
    return type === 'CREDIT' ? 'positive' : 'negative';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  }

  // Toggle dropdown menu for a transaction
  toggleMenu(transactionId: number, event: Event): void {
    event.stopPropagation();
    this.openMenuId = this.openMenuId === transactionId ? null : transactionId;
  }

  // Close menu when clicking outside
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.transaction-menu')) {
      this.openMenuId = null;
    }
  }

  closeMenu(): void {
    this.openMenuId = null;
  }

  // Open edit modal
  openEditModal(transaction: Transaction, event: Event): void {
    event.stopPropagation();
    this.editingTransaction = transaction;
    this.showEditModal = true;
    this.openMenuId = null;

    // Initialize the form with transaction data
    this.editForm = this.fb.group({
      type: [transaction.type, Validators.required],
      categoryId: [transaction.category.id, Validators.required],
      bankAccountId: [transaction.bankAccount.id, Validators.required],
      value: [transaction.value, [Validators.required, Validators.min(this.MIN_TRANSACTION_VALUE)]],
      description: [transaction.description || ''],
      createdAt: [this.formatDateForInput(transaction.createdAt), Validators.required]
    });
  }

  // Close edit modal
  closeEditModal(): void {
    this.showEditModal = false;
    this.editingTransaction = undefined;
    this.editForm.reset();
  }

  // Save edited transaction
  saveEditedTransaction(): void {
    if (this.editForm.invalid || !this.editingTransaction) {
      return;
    }

    this.editModalLoading = true;
    const formValue = this.editForm.value;
    
    // Convert date string to ISO DateTime format (YYYY-MM-DDTHH:mm:ss)
    // If only date is provided, use the original time from the existing transaction
    let createdAtISO: string;
    if (formValue.createdAt) {
      const originalDateTime = new Date(this.editingTransaction.createdAt);
      const [year, month, day] = formValue.createdAt.split('-');
      const updatedDate = new Date(
        parseInt(year),
        parseInt(month) - 1,
        parseInt(day),
        originalDateTime.getHours(),
        originalDateTime.getMinutes(),
        originalDateTime.getSeconds()
      );
      createdAtISO = updatedDate.toISOString().slice(0, 19);
    } else {
      createdAtISO = new Date().toISOString().slice(0, 19);
    }
    
    const request: TransactionRequest = {
      type: formValue.type,
      categoryId: formValue.categoryId,
      bankAccountId: formValue.bankAccountId,
      value: formValue.value,
      description: formValue.description,
      createdAt: createdAtISO
    };

    this.transactionService.update(this.editingTransaction.id, request).subscribe({
      next: (updatedTransaction) => {
        // Update the transaction in the modal list
        const index = this.modalTransactions.findIndex(t => t.id === updatedTransaction.id);
        if (index !== -1) {
          this.modalTransactions[index] = updatedTransaction;
        }
        this.editModalLoading = false;
        this.closeEditModal();
        // Reload sidebar data to reflect balance changes
        this.loadSidebarData();
      },
      error: (error) => {
        console.error('Error updating transaction:', error);
        this.editModalLoading = false;
        const errorMessage = error?.error?.message || error?.message || 'Erro ao atualizar transação. Tente novamente.';
        alert(errorMessage);
      }
    });
  }

  // Open delete confirmation
  confirmDelete(transaction: Transaction, event: Event): void {
    event.stopPropagation();
    this.transactionToDelete = transaction;
    this.showDeleteConfirmation = true;
    this.openMenuId = null;
  }

  // Close delete confirmation
  closeDeleteConfirmation(): void {
    this.showDeleteConfirmation = false;
    this.transactionToDelete = undefined;
  }

  // Delete transaction
  deleteTransaction(): void {
    if (!this.transactionToDelete) {
      return;
    }

    const transactionId = this.transactionToDelete.id;
    this.transactionService.delete(transactionId).subscribe({
      next: () => {
        // Remove transaction from modal list
        this.modalTransactions = this.modalTransactions.filter(t => t.id !== transactionId);
        this.closeDeleteConfirmation();
        // Reload sidebar data to reflect balance changes
        this.loadSidebarData();
      },
      error: (error) => {
        console.error('Error deleting transaction:', error);
        const errorMessage = error?.error?.message || error?.message || 'Erro ao deletar transação. Tente novamente.';
        alert(errorMessage);
      }
    });
  }

  // Helper to format date for input field (YYYY-MM-DD)
  formatDateForInput(dateString: string): string {
    const date = new Date(dateString);
    const year = date.getUTCFullYear();
    const month = String(date.getUTCMonth() + 1).padStart(2, '0');
    const day = String(date.getUTCDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

    /** Extract a 2-letter language code from the first path segment of a URL. */
    private extractLangFromUrl(url: string): string | null {
      if (!url) return null;
      const m = url.match(/\/([a-z]{2})(?:\/|$)/i);
      return m ? m[1].toLowerCase() : null;
    }
}
