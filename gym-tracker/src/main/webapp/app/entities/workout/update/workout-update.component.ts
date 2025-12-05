import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IExercise } from 'app/entities/exercise/exercise.model';
import { ExerciseService } from 'app/entities/exercise/service/exercise.service';
import { IWorkout } from '../workout.model';
import { WorkoutService } from '../service/workout.service';
import { WorkoutFormGroup, WorkoutFormService } from './workout-form.service';
import { MuscleGroup } from 'app/entities/enumerations/muscle-group.model';

@Component({
  selector: 'jhi-workout-update',
  templateUrl: './workout-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class WorkoutUpdateComponent implements OnInit {
  isSaving = false;
  workout: IWorkout | null = null;

  exercisesSharedCollection: IExercise[] = [];

  muscleGroups: string[] = Object.values(MuscleGroup);
  exercisesByGroup: Record<string, IExercise[]> = {};

  protected workoutService = inject(WorkoutService);
  protected workoutFormService = inject(WorkoutFormService);
  protected exerciseService = inject(ExerciseService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: WorkoutFormGroup = this.workoutFormService.createWorkoutFormGroup();

  compareExercise = (o1: IExercise | null, o2: IExercise | null): boolean => this.exerciseService.compareExercise(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ workout }) => {
      this.workout = workout;
      if (workout) {
        this.updateForm(workout);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const workout = this.workoutFormService.getWorkout(this.editForm);
    if (workout.id !== null) {
      this.subscribeToSaveResponse(this.workoutService.update(workout));
    } else {
      this.subscribeToSaveResponse(this.workoutService.create(workout));
    }
  }

  isExerciseSelected(exercise: IExercise): boolean {
    const selected: IExercise[] = this.editForm.get('exercises')!.value ?? [];
    return selected.some(e => e.id === exercise.id);
  }

  onExerciseToggle(exercise: IExercise, checked: boolean): void {
    let selected: IExercise[] = this.editForm.get('exercises')!.value ?? [];

    if (checked) {
      // Agrego si no estaba
      if (!selected.some(e => e.id === exercise.id)) {
        selected = [...selected, exercise];
      }
    } else {
      // Saco si estaba
      selected = selected.filter(e => e.id !== exercise.id);
    }

    this.editForm.patchValue({ exercises: selected });
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWorkout>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(workout: IWorkout): void {
    this.workout = workout;
    this.workoutFormService.resetForm(this.editForm, workout);

    this.exercisesSharedCollection = this.exerciseService.addExerciseToCollectionIfMissing<IExercise>(
      this.exercisesSharedCollection,
      ...(workout.exercises ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.exerciseService
      .query()
      .pipe(map((res: HttpResponse<IExercise[]>) => res.body ?? []))
      .pipe(
        map((exercises: IExercise[]) =>
          this.exerciseService.addExerciseToCollectionIfMissing<IExercise>(exercises, ...(this.workout?.exercises ?? [])),
        ),
      )
      .subscribe((exercises: IExercise[]) => {
        this.exercisesSharedCollection = exercises;
        this.groupExercisesByMuscle();
      });
  }

  private groupExercisesByMuscle(): void {
    // Inicializo el objeto con todos los grupos vacíos
    this.exercisesByGroup = {};
    for (const group of this.muscleGroups) {
      this.exercisesByGroup[group] = [];
    }

    // Agrupo los ejercicios por muscleGroup
    for (const ex of this.exercisesSharedCollection) {
      const key = ex.muscleGroup as string | undefined;
      if (key) {
        this.exercisesByGroup[key].push(ex);
      }
    }
  }
}
