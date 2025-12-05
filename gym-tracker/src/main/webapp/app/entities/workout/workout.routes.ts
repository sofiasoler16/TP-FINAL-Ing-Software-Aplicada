import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import WorkoutResolve from './route/workout-routing-resolve.service';

const workoutRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/workout.component').then(m => m.WorkoutComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/workout-detail.component').then(m => m.WorkoutDetailComponent),
    resolve: {
      workout: WorkoutResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/workout-update.component').then(m => m.WorkoutUpdateComponent),
    resolve: {
      workout: WorkoutResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/workout-update.component').then(m => m.WorkoutUpdateComponent),
    resolve: {
      workout: WorkoutResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default workoutRoute;
