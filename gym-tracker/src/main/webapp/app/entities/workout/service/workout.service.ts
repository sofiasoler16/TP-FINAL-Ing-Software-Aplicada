import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWorkout, NewWorkout } from '../workout.model';

export type PartialUpdateWorkout = Partial<IWorkout> & Pick<IWorkout, 'id'>;

type RestOf<T extends IWorkout | NewWorkout> = Omit<T, 'workoutDate'> & {
  workoutDate?: string | null;
};

export type RestWorkout = RestOf<IWorkout>;

export type NewRestWorkout = RestOf<NewWorkout>;

export type PartialUpdateRestWorkout = RestOf<PartialUpdateWorkout>;

export type EntityResponseType = HttpResponse<IWorkout>;
export type EntityArrayResponseType = HttpResponse<IWorkout[]>;

@Injectable({ providedIn: 'root' })
export class WorkoutService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/workouts');

  create(workout: NewWorkout): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workout);
    return this.http
      .post<RestWorkout>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(workout: IWorkout): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workout);
    return this.http
      .put<RestWorkout>(`${this.resourceUrl}/${this.getWorkoutIdentifier(workout)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(workout: PartialUpdateWorkout): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(workout);
    return this.http
      .patch<RestWorkout>(`${this.resourceUrl}/${this.getWorkoutIdentifier(workout)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestWorkout>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWorkout[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getWorkoutIdentifier(workout: Pick<IWorkout, 'id'>): number {
    return workout.id;
  }

  compareWorkout(o1: Pick<IWorkout, 'id'> | null, o2: Pick<IWorkout, 'id'> | null): boolean {
    return o1 && o2 ? this.getWorkoutIdentifier(o1) === this.getWorkoutIdentifier(o2) : o1 === o2;
  }

  addWorkoutToCollectionIfMissing<Type extends Pick<IWorkout, 'id'>>(
    workoutCollection: Type[],
    ...workoutsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const workouts: Type[] = workoutsToCheck.filter(isPresent);
    if (workouts.length > 0) {
      const workoutCollectionIdentifiers = workoutCollection.map(workoutItem => this.getWorkoutIdentifier(workoutItem));
      const workoutsToAdd = workouts.filter(workoutItem => {
        const workoutIdentifier = this.getWorkoutIdentifier(workoutItem);
        if (workoutCollectionIdentifiers.includes(workoutIdentifier)) {
          return false;
        }
        workoutCollectionIdentifiers.push(workoutIdentifier);
        return true;
      });
      return [...workoutsToAdd, ...workoutCollection];
    }
    return workoutCollection;
  }

  protected convertDateFromClient<T extends IWorkout | NewWorkout | PartialUpdateWorkout>(workout: T): RestOf<T> {
    return {
      ...workout,
      workoutDate: workout.workoutDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restWorkout: RestWorkout): IWorkout {
    return {
      ...restWorkout,
      workoutDate: restWorkout.workoutDate ? dayjs(restWorkout.workoutDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestWorkout>): HttpResponse<IWorkout> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestWorkout[]>): HttpResponse<IWorkout[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
