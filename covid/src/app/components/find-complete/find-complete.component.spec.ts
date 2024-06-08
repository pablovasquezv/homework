import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FindCompleteComponent } from './find-complete.component';

describe('FindCompleteComponent', () => {
  let component: FindCompleteComponent;
  let fixture: ComponentFixture<FindCompleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FindCompleteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FindCompleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
