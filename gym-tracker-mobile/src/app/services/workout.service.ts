import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

export interface Workout {
  id: number;
  workoutDate: string;
  durationMinutes: number;
  notes?: string;
}

@Injectable({
  providedIn: 'root',
})
export class WorkoutService {
  private apiUrl = 'http://localhost:8080/api/workouts';
  private cacheKey = 'cachedWorkouts';

  constructor(private http: HttpClient) {}

    getWorkouts(): Observable<Workout[]> {
    return this.http.get<Workout[]>(this.apiUrl).pipe(
        tap((data) => {
        console.log('*** Guardando workouts en cache ***', data);
        localStorage.setItem(this.cacheKey, JSON.stringify(data));
        }),
        catchError((err) => {
        const cached = localStorage.getItem(this.cacheKey);
        if (cached) {
            console.warn('*** Devolviendo workouts cacheados ***');
            return of(JSON.parse(cached));
        }
        return throwError(() => err);
        })
    );
    }
}



