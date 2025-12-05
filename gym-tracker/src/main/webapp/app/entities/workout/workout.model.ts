import dayjs from 'dayjs/esm';
import { IExercise } from 'app/entities/exercise/exercise.model';

export interface IWorkout {
  id: number;
  workoutDate?: dayjs.Dayjs | null;
  durationMinutes?: number | null;
  notes?: string | null;
  exercises?: IExercise[] | null;
}

export type NewWorkout = Omit<IWorkout, 'id'> & { id: null };
