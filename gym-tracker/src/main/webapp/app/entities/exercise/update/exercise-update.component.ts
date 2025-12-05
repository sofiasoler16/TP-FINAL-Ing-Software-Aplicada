import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IWorkout } from 'app/entities/workout/workout.model';
import { WorkoutService } from 'app/entities/workout/service/workout.service';
import { MuscleGroup } from 'app/entities/enumerations/muscle-group.model';
import { Difficulty } from 'app/entities/enumerations/difficulty.model';
import { ExerciseService } from '../service/exercise.service';
import { IExercise } from '../exercise.model';
import { ExerciseFormGroup, ExerciseFormService } from './exercise-form.service';

@Component({
  selector: 'jhi-exercise-update',
  templateUrl: './exercise-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ExerciseUpdateComponent implements OnInit {
  isSaving = false;
  exercise: IExercise | null = null;
  muscleGroupValues = Object.keys(MuscleGroup);
  difficultyValues = Object.keys(Difficulty);

  workoutsSharedCollection: IWorkout[] = [];

  protected exerciseService = inject(ExerciseService);
  protected exerciseFormService = inject(ExerciseFormService);
  protected workoutService = inject(WorkoutService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ExerciseFormGroup = this.exerciseFormService.createExerciseFormGroup();

  compareWorkout = (o1: IWorkout | null, o2: IWorkout | null): boolean => this.workoutService.compareWorkout(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ exercise }) => {
      this.exercise = exercise;
      if (exercise) {
        this.updateForm(exercise);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const exercise = this.exerciseFormService.getExercise(this.editForm);
    if (exercise.id !== null) {
      this.subscribeToSaveResponse(this.exerciseService.update(exercise));
    } else {
      this.subscribeToSaveResponse(this.exerciseService.create(exercise));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExercise>>): void {
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

  protected updateForm(exercise: IExercise): void {
    this.exercise = exercise;
    this.exerciseFormService.resetForm(this.editForm, exercise);

    this.workoutsSharedCollection = this.workoutService.addWorkoutToCollectionIfMissing<IWorkout>(
      this.workoutsSharedCollection,
      ...(exercise.workouts ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.workoutService
      .query()
      .pipe(map((res: HttpResponse<IWorkout[]>) => res.body ?? []))
      .pipe(
        map((workouts: IWorkout[]) =>
          this.workoutService.addWorkoutToCollectionIfMissing<IWorkout>(workouts, ...(this.exercise?.workouts ?? [])),
        ),
      )
      .subscribe((workouts: IWorkout[]) => (this.workoutsSharedCollection = workouts));
  }
}
