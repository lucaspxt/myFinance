import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe to format numbers in Brazilian currency format (7.909,43)
 */
@Pipe({
  name: 'currencyFormat',
  standalone: true
})
export class CurrencyFormatPipe implements PipeTransform {
  transform(value: number | null | undefined): string {
    if (value === null || value === undefined) {
      return '0,00';
    }

    // Convert to string with 2 decimal places
    const formatted = Math.abs(value).toFixed(2);
    
    // Split integer and decimal parts
    const [integerPart, decimalPart] = formatted.split('.');
    
    // Add thousand separators (dots in Brazilian format)
    const integerWithSeparators = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    
    // Return formatted value with comma as decimal separator
    return `${value < 0 ? '-' : ''}${integerWithSeparators},${decimalPart}`;
  }
}
