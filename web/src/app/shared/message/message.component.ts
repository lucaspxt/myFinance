import { Component, Input, OnInit, ViewChild, ViewContainerRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SocialLinksComponent } from '../social-links/social-links.component';
import { RepeatButtonComponent } from '../repeat-button/repeat-button.component';
import { marked } from 'marked';

export interface MessageData {
  text: string;
  isUser: boolean;
  timestamp: Date;
  // optional type: 'text' (default) or 'component' to render a dynamic component
  type?: 'text' | 'component';
  // Optional dynamic component to render for this message (standalone component class)
  component?: any;
  // Optional inputs to pass into the dynamic component
  componentInputs?: { [key: string]: any };
}

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [CommonModule, SocialLinksComponent, RepeatButtonComponent],
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {
  @Input() message!: MessageData;
  // expose to template for static rendering fallback
  public SocialLinksComponentRef = SocialLinksComponent;
  public RepeatButtonComponentRef = RepeatButtonComponent;
  
  displayText: string = '';
  typingComplete: boolean = false;
  @ViewChild('dynamicHost', { read: ViewContainerRef, static: false }) dynamicHost?: ViewContainerRef;
  private createdComponentRef: any;
  
  ngOnInit() {
    // Iniciar o efeito de digitação apenas para mensagens do sistema
    if (!this.message.isUser && this.message.type !== 'component') {
      // If this is a system text message, start typing effect
      this.startTypingEffect();
    } else {
      // For user messages or component messages, show immediately
      this.displayText = this.message.text;
      this.typingComplete = true;
    }
  }

  ngAfterViewInit() {
    // If message is a dynamic component and not SocialLinks, create it dynamically
    if (this.message.type === 'component' && this.message.component && this.message.component !== this.SocialLinksComponentRef) {
      try {
        const host = this.dynamicHost;
        if (host) {
          host.clear();
          // createComponent supports passing inputs on creation in Ivy
          const compRef = host.createComponent(this.message.component as any) as any;
          // apply inputs if provided
          if (this.message.componentInputs && typeof this.message.componentInputs === 'object') {
            Object.keys(this.message.componentInputs).forEach(key => {
              try { (compRef.instance as any)[key] = this.message.componentInputs![key]; } catch (e) {}
            });
          }
          // detect changes if available
          try { compRef.changeDetectorRef?.detectChanges(); } catch (e) {}
          this.createdComponentRef = compRef;
        }
      } catch (e) {
        console.warn('Could not create dynamic component for message:', e);
      }
    }
  }
  
  startTypingEffect() {
    this.typingComplete = false;

    // Reinicia o texto mostrado
    this.displayText = '';

    // Calcula o intervalo baseado na velocidade e no tamanho da mensagem
    const textLength = this.message.text.length;
    const totalDuration = Math.min(1000, Math.max(500, textLength * 50)); // Entre 1 e 2 segundos, mais lento
    const interval = totalDuration / textLength;

    let currentIndex = 0;

    // Função para adicionar um caractere por vez
    const typeNextChar = () => {
      if (currentIndex < this.message.text.length) {
        // Adiciona o próximo caractere
        this.displayText += this.message.text.charAt(currentIndex);
        currentIndex++;

        // Agenda o próximo caractere
        setTimeout(typeNextChar, interval);
      } else {
        this.typingComplete = true;
      }
    };

    // Inicia a digitação
    setTimeout(typeNextChar, 300); // Pequeno delay inicial, mais lento
  }
  
  // Method to convert basic Markdown to HTML
  convertMarkdownToHtml(text: string): string {
    if (!text) return '';
    return marked.parse(text) as string;
  }
}
