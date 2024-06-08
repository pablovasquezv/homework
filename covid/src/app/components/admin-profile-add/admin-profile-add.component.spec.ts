import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminProfileAddComponent } from './admin-profile-add.component';

describe('AdminProfileAddComponent', () => {
  let component: AdminProfileAddComponent;
  let fixture: ComponentFixture<AdminProfileAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminProfileAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminProfileAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
