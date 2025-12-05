import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { WorkoutDetailComponent } from './workout-detail.component';

describe('Workout Management Detail Component', () => {
  let comp: WorkoutDetailComponent;
  let fixture: ComponentFixture<WorkoutDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkoutDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./workout-detail.component').then(m => m.WorkoutDetailComponent),
              resolve: { workout: () => of({ id: 25883 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(WorkoutDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkoutDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load workout on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', WorkoutDetailComponent);

      // THEN
      expect(instance.workout()).toEqual(expect.objectContaining({ id: 25883 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
