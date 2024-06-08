import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GraphiBarChartComponent } from './graphi-bar-chart.component';

describe('GraphiBarChartComponent', () => {
  let component: GraphiBarChartComponent;
  let fixture: ComponentFixture<GraphiBarChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GraphiBarChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GraphiBarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
