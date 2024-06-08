import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetPlanContComponent } from './detplan-cont.component';

describe('PlanContComponent', () => {
  let component: DetPlanContComponent;
  let fixture: ComponentFixture<DetPlanContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetPlanContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetPlanContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
