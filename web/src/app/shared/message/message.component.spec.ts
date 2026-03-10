import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MessageComponent } from './message.component';

describe('MessageComponent', () => {
  let component: MessageComponent;
  let fixture: ComponentFixture<MessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageComponent]
    }).compileComponents();
    
    fixture = TestBed.createComponent(MessageComponent);
    component = fixture.componentInstance;
    component.message = {
      text: 'Test message',
      isUser: false,
      timestamp: new Date()
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
