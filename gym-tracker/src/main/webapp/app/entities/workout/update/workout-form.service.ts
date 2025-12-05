import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IWorkout, NewWorkout } from '../workout.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWorkout for edit and NewWorkoutFormGroupInput for create.
 */
type WorkoutFormGroupInput = IWorkout | PartialWithRequiredKeyOf<NewWorkout>;

type WorkoutFormDefaults = Pick<NewWorkout, 'id' | 'exercises'>;

type WorkoutFormGroupContent = {
  id: FormControl<IWorkout['id'] | NewWorkout['id']>;
  workoutDate: FormControl<IWorkout['workoutDate']>;
  durationMinutes: FormControl<IWorkout['durationMinutes']>;
  notes: FormControl<IWorkout['notes']>;
  exercises: FormControl<IWorkout['exercises']>;
};

export type WorkoutFormGroup = FormGroup<WorkoutFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WorkoutFormService {
  createWorkoutFormGroup(workout: WorkoutFormGroupInput = { id: null }): WorkoutFormGroup {
    const workoutRawValue = {
      ...this.getFormDefaults(),
      ...workout,
    };
    return new FormGroup<WorkoutFormGroupContent>({
      id: new FormControl(
        { value: workoutRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      workoutDate: new FormControl(workoutRawValue.workoutDate, {
        validators: [Validators.required],
      }),
      durationMinutes: new FormControl(workoutRawValue.durationMinutes, {
        validators: [Validators.min(1)],
      }),
      notes: new FormControl(workoutRawValue.notes, {
        validators: [Validators.maxLength(1000)],
      }),
      exercises: new FormControl(workoutRawValue.exercises ?? []),
    });
  }

  getWorkout(form: WorkoutFormGroup): IWorkout | NewWorkout {
    return form.getRawValue() as IWorkout | NewWorkout;
  }

  resetForm(form: WorkoutFormGroup, workout: WorkoutFormGroupInput): void {
    const workoutRawValue = { ...this.getFormDefaults(), ...workout };
    form.reset(
      {
        ...workoutRawValue,
        id: { value: workoutRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): WorkoutFormDefaults {
    return {
      id: null,
      exercises: [],
    };
  }
}
