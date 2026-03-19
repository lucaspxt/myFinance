import { Component, Input, OnInit, ViewChild, ViewContainerRef, AfterViewInit, ChangeDetectorRef } from '@angular/core';
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
  // Flag para controlar se a animação já foi exibida (persiste entre recriações do componente)
  hasAnimated?: boolean;
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
  hasStartedTyping: boolean = false;
  @ViewChild('dynamicHost', { read: ViewContainerRef, static: false }) dynamicHost?: ViewContainerRef;
  private createdComponentRef: any;
  
  ngOnInit() {
    // Se é mensagem do usuário, mostrar completa
    if (this.message.isUser) {
      this.displayText = this.message.text;
      this.typingComplete = true;
      this.hasStartedTyping = true;
    } else if (!this.message.hasAnimated) {
      // Se é mensagem do bot e ainda não foi animada, iniciar efeito
      this.message.hasAnimated = true;
      this.hasStartedTyping = true;
      this.startTypingEffect();
    } else {
      // Mensagem do bot já foi animada, mostrar completa
      this.displayText = this.message.text;
      this.typingComplete = true;
      this.hasStartedTyping = true;
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
    this.displayText = '';

    const textLength = this.message.text.length;
    // Velocidade mais rápida: 10ms por caractere
    const charDelay = 10;
    let currentIndex = 0;

    const typeNextChar = () => {
      if (currentIndex < this.message.text.length) {
        this.displayText += this.message.text.charAt(currentIndex);
        currentIndex++;
        
        setTimeout(typeNextChar, charDelay);
      } else {
        this.typingComplete = true;
      }
    };

    // Pequeno delay antes de começar
    setTimeout(typeNextChar, 50);
  }
  
  // Method to convert basic Markdown to HTML
  convertMarkdownToHtml(text: string): string {
    if (!text) return '';
    return marked.parse(text) as string;
  }
}
