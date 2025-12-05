import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IExercise } from 'app/entities/exercise/exercise.model';
import { ExerciseService } from 'app/entities/exercise/service/exercise.service';
import { WorkoutService } from '../service/workout.service';
import { IWorkout } from '../workout.model';
import { WorkoutFormService } from './workout-form.service';

import { WorkoutUpdateComponent } from './workout-update.component';

describe('Workout Management Update Component', () => {
  let comp: WorkoutUpdateComponent;
  let fixture: ComponentFixture<WorkoutUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let workoutFormService: WorkoutFormService;
  let workoutService: WorkoutService;
  let exerciseService: ExerciseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [WorkoutUpdateComponent],
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
      .overrideTemplate(WorkoutUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WorkoutUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    workoutFormService = TestBed.inject(WorkoutFormService);
    workoutService = TestBed.inject(WorkoutService);
    exerciseService = TestBed.inject(ExerciseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Exercise query and add missing value', () => {
      const workout: IWorkout = { id: 4316 };
      const exercises: IExercise[] = [{ id: 19704 }];
      workout.exercises = exercises;

      const exerciseCollection: IExercise[] = [{ id: 19704 }];
      jest.spyOn(exerciseService, 'query').mockReturnValue(of(new HttpResponse({ body: exerciseCollection })));
      const additionalExercises = [...exercises];
      const expectedCollection: IExercise[] = [...additionalExercises, ...exerciseCollection];
      jest.spyOn(exerciseService, 'addExerciseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ workout });
      comp.ngOnInit();

      expect(exerciseService.query).toHaveBeenCalled();
      expect(exerciseService.addExerciseToCollectionIfMissing).toHaveBeenCalledWith(
        exerciseCollection,
        ...additionalExercises.map(expect.objectContaining),
      );
      expect(comp.exercisesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const workout: IWorkout = { id: 4316 };
      const exercises: IExercise = { id: 19704 };
      workout.exercises = [exercises];

      activatedRoute.data = of({ workout });
      comp.ngOnInit();

      expect(comp.exercisesSharedCollection).toContainEqual(exercises);
      expect(comp.workout).toEqual(workout);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkout>>();
      const workout = { id: 25883 };
      jest.spyOn(workoutFormService, 'getWorkout').mockReturnValue(workout);
      jest.spyOn(workoutService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workout });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workout }));
      saveSubject.complete();

      // THEN
      expect(workoutFormService.getWorkout).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(workoutService.update).toHaveBeenCalledWith(expect.objectContaining(workout));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkout>>();
      const workout = { id: 25883 };
      jest.spyOn(workoutFormService, 'getWorkout').mockReturnValue({ id: null });
      jest.spyOn(workoutService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workout: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: workout }));
      saveSubject.complete();

      // THEN
      expect(workoutFormService.getWorkout).toHaveBeenCalled();
      expect(workoutService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWorkout>>();
      const workout = { id: 25883 };
      jest.spyOn(workoutService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ workout });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(workoutService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareExercise', () => {
      it('should forward to exerciseService', () => {
        const entity = { id: 19704 };
        const entity2 = { id: 9508 };
        jest.spyOn(exerciseService, 'compareExercise');
        comp.compareExercise(entity, entity2);
        expect(exerciseService.compareExercise).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });

  describe('Exercise selection helpers', () => {
    it('should return true when exercise is selected', () => {
      const exercise: IExercise = { id: 1, name: 'Sentadilla', muscleGroup: 'LEGS' };

      // Simulamos que el formulario ya tiene ese ejercicio seleccionado
      comp.editForm.patchValue({ exercises: [exercise] });

      expect(comp.isExerciseSelected(exercise)).toBe(true);
    });

    it('should return false when exercise is not selected', () => {
      const exercise: IExercise = { id: 1, name: 'Sentadilla', muscleGroup: 'LEGS' };

      // Form sin ejercicios seleccionados
      comp.editForm.patchValue({ exercises: [] });

      expect(comp.isExerciseSelected(exercise)).toBe(false);
    });

    it('should add exercise when toggled on', () => {
      const exercise: IExercise = { id: 1, name: 'Hip Thrust', muscleGroup: 'GLUTES' };

      comp.editForm.patchValue({ exercises: [] });

      comp.onExerciseToggle(exercise, true);

      const selected: IExercise[] = comp.editForm.get('exercises')!.value ?? [];
      expect(selected.some(e => e.id === exercise.id)).toBe(true);
    });

    it('should remove exercise when toggled off', () => {
      const exercise: IExercise = { id: 1, name: 'Hip Thrust', muscleGroup: 'GLUTES' };

      comp.editForm.patchValue({ exercises: [exercise] });

      comp.onExerciseToggle(exercise, false);

      const selected: IExercise[] = comp.editForm.get('exercises')!.value ?? [];
      expect(selected.some(e => e.id === exercise.id)).toBe(false);
    });
  });
});
