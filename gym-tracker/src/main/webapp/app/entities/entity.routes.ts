import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'gymTrackerApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'exercise',
    data: { pageTitle: 'gymTrackerApp.exercise.home.title' },
    loadChildren: () => import('./exercise/exercise.routes'),
  },
  {
    path: 'workout',
    data: { pageTitle: 'gymTrackerApp.workout.home.title' },
    loadChildren: () => import('./workout/workout.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
