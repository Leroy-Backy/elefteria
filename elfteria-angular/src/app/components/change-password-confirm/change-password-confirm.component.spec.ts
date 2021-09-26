import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePasswordConfirmComponent } from './change-password-confirm.component';

describe('ChangePasswordConfirmComponent', () => {
  let component: ChangePasswordConfirmComponent;
  let fixture: ComponentFixture<ChangePasswordConfirmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangePasswordConfirmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangePasswordConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
