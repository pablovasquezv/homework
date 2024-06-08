import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectTrackingComponent } from './project-tracking.component';

describe('UserListComponent', () => {
  let component: ProjectTrackingComponent;
  let fixture: ComponentFixture<ProjectTrackingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProjectTrackingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectTrackingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
