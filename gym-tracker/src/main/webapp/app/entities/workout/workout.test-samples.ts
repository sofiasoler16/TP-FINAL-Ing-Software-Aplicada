import dayjs from 'dayjs/esm';

import { IWorkout, NewWorkout } from './workout.model';

export const sampleWithRequiredData: IWorkout = {
  id: 17220,
  workoutDate: dayjs('2025-11-30'),
};

export const sampleWithPartialData: IWorkout = {
  id: 10093,
  workoutDate: dayjs('2025-11-30'),
};

export const sampleWithFullData: IWorkout = {
  id: 17931,
  workoutDate: dayjs('2025-11-30'),
  durationMinutes: 805,
  notes: 'decriminalize',
};

export const sampleWithNewData: NewWorkout = {
  workoutDate: dayjs('2025-11-30'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
