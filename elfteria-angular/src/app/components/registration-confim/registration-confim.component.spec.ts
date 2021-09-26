import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationConfimComponent } from './registration-confim.component';

describe('RegistrationConfimComponent', () => {
  let component: RegistrationConfimComponent;
  let fixture: ComponentFixture<RegistrationConfimComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegistrationConfimComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegistrationConfimComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
