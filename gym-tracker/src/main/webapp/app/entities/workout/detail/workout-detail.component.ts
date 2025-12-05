import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IWorkout } from '../workout.model';

@Component({
  selector: 'jhi-workout-detail',
  templateUrl: './workout-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class WorkoutDetailComponent {
  workout = input<IWorkout | null>(null);

  previousState(): void {
    window.history.back();
  }
}
