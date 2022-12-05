import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {GenericErrorComponent} from './genericError.component';

describe('GenericComponent', () => {
  let component: GenericErrorComponent;
  let fixture: ComponentFixture<GenericErrorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ GenericErrorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
