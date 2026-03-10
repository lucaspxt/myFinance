import { marked } from 'marked';
import { SecurityContext } from '@angular/core';
import { MarkedOptions, MarkedRenderer } from 'ngx-markdown';

export function markedOptionsFactory(): MarkedOptions {
  const renderer = new MarkedRenderer();
  
  // Personalize renderizadores aqui se necessário
  // Por exemplo, você pode personalizar como links são renderizados
  renderer.link = (href: string, title: string | null | undefined, text: string) => {
    return `<a href="${href}" title="${title || ''}" target="_blank" rel="noopener noreferrer">${text}</a>`;
  };

  return {
    renderer,
    gfm: true,
    breaks: true,
    pedantic: false,
    smartLists: true,
    smartypants: false,
  };
}

export const markdownConfig = {
  sanitize: SecurityContext.NONE,
  markedOptions: {
    provide: MarkedOptions,
    useFactory: markedOptionsFactory,
  },
};
