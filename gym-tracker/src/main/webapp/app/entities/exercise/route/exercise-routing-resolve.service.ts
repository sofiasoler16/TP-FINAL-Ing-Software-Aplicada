import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExercise } from '../exercise.model';
import { ExerciseService } from '../service/exercise.service';

const exerciseResolve = (route: ActivatedRouteSnapshot): Observable<null | IExercise> => {
  const id = route.params.id;
  if (id) {
    return inject(ExerciseService)
      .find(id)
      .pipe(
        mergeMap((exercise: HttpResponse<IExercise>) => {
          if (exercise.body) {
            return of(exercise.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default exerciseResolve;
