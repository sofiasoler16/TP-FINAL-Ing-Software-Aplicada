import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ExerciseResolve from './route/exercise-routing-resolve.service';

const exerciseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/exercise.component').then(m => m.ExerciseComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/exercise-detail.component').then(m => m.ExerciseDetailComponent),
    resolve: {
      exercise: ExerciseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/exercise-update.component').then(m => m.ExerciseUpdateComponent),
    resolve: {
      exercise: ExerciseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/exercise-update.component').then(m => m.ExerciseUpdateComponent),
    resolve: {
      exercise: ExerciseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default exerciseRoute;
