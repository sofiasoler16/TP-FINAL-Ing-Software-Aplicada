import { IExercise, NewExercise } from './exercise.model';

export const sampleWithRequiredData: IExercise = {
  id: 11343,
  name: 'unnaturally violent',
  muscleGroup: 'BICEPS',
  difficulty: 'EASY',
};

export const sampleWithPartialData: IExercise = {
  id: 17877,
  name: 'brr boring',
  muscleGroup: 'FULL_BODY',
  difficulty: 'HARD',
  description: 'truthfully disk woot',
};

export const sampleWithFullData: IExercise = {
  id: 29477,
  name: 'tuber on',
  muscleGroup: 'BACK',
  difficulty: 'MEDIUM',
  description: 'round',
};

export const sampleWithNewData: NewExercise = {
  name: 'freckle concerning improbable',
  muscleGroup: 'CHEST',
  difficulty: 'HARD',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
