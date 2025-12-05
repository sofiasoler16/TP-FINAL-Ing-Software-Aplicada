import { IWorkout } from 'app/entities/workout/workout.model';
import { MuscleGroup } from 'app/entities/enumerations/muscle-group.model';
import { Difficulty } from 'app/entities/enumerations/difficulty.model';

export interface IExercise {
  id: number;
  name?: string | null;
  muscleGroup?: keyof typeof MuscleGroup | null;
  difficulty?: keyof typeof Difficulty | null;
  description?: string | null;
  workouts?: IWorkout[] | null;
}

export type NewExercise = Omit<IExercise, 'id'> & { id: null };
