import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWorkout } from 'app/entities/workout/workout.model';
import { WorkoutService } from 'app/entities/workout/service/workout.service';
import { ExerciseService } from '../service/exercise.service';
import { IExercise } from '../exercise.model';
import { ExerciseFormService } from './exercise-form.service';

import { ExerciseUpdateComponent } from './exercise-update.component';

describe('Exercise Management Update Component', () => {
  let comp: ExerciseUpdateComponent;
  let fixture: ComponentFixture<ExerciseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let exerciseFormService: ExerciseFormService;
  let exerciseService: ExerciseService;
  let workoutService: WorkoutService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ExerciseUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ExerciseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExerciseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    exerciseFormService = TestBed.inject(ExerciseFormService);
    exerciseService = TestBed.inject(ExerciseService);
    workoutService = TestBed.inject(WorkoutService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Workout query and add missing value', () => {
      const exercise: IExercise = { id: 9508 };
      const workouts: IWorkout[] = [{ id: 25883 }];
      exercise.workouts = workouts;

      const workoutCollection: IWorkout[] = [{ id: 25883 }];
      jest.spyOn(workoutService, 'query').mockReturnValue(of(new HttpResponse({ body: workoutCollection })));
      const additionalWorkouts = [...workouts];
      const expectedCollection: IWorkout[] = [...additionalWorkouts, ...workoutCollection];
      jest.spyOn(workoutService, 'addWorkoutToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exercise });
      comp.ngOnInit();

      expect(workoutService.query).toHaveBeenCalled();
      expect(workoutService.addWorkoutToCollectionIfMissing).toHaveBeenCalledWith(
        workoutCollection,
        ...additionalWorkouts.map(expect.objectContaining),
      );
      expect(comp.workoutsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const exercise: IExercise = { id: 9508 };
      const workouts: IWorkout = { id: 25883 };
      exercise.workouts = [workouts];

      activatedRoute.data = of({ exercise });
      comp.ngOnInit();

      expect(comp.workoutsSharedCollection).toContainEqual(workouts);
      expect(comp.exercise).toEqual(exercise);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExercise>>();
      const exercise = { id: 19704 };
      jest.spyOn(exerciseFormService, 'getExercise').mockReturnValue(exercise);
      jest.spyOn(exerciseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exercise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: exercise }));
      saveSubject.complete();

      // THEN
      expect(exerciseFormService.getExercise).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(exerciseService.update).toHaveBeenCalledWith(expect.objectContaining(exercise));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExercise>>();
      const exercise = { id: 19704 };
      jest.spyOn(exerciseFormService, 'getExercise').mockReturnValue({ id: null });
      jest.spyOn(exerciseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exercise: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: exercise }));
      saveSubject.complete();

      // THEN
      expect(exerciseFormService.getExercise).toHaveBeenCalled();
      expect(exerciseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExercise>>();
      const exercise = { id: 19704 };
      jest.spyOn(exerciseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exercise });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(exerciseService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareWorkout', () => {
      it('should forward to workoutService', () => {
        const entity = { id: 25883 };
        const entity2 = { id: 4316 };
        jest.spyOn(workoutService, 'compareWorkout');
        comp.compareWorkout(entity, entity2);
        expect(workoutService.compareWorkout).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
