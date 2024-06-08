import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanContComponent } from './plan-cont.component';

describe('PlanContComponent', () => {
  let component: PlanContComponent;
  let fixture: ComponentFixture<PlanContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlanContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlanContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
