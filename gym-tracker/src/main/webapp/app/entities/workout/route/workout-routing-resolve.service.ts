import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWorkout } from '../workout.model';
import { WorkoutService } from '../service/workout.service';

const workoutResolve = (route: ActivatedRouteSnapshot): Observable<null | IWorkout> => {
  const id = route.params.id;
  if (id) {
    return inject(WorkoutService)
      .find(id)
      .pipe(
        mergeMap((workout: HttpResponse<IWorkout>) => {
          if (workout.body) {
            return of(workout.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default workoutResolve;
