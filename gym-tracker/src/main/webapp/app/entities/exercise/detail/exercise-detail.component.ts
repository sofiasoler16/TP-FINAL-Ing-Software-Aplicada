import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IExercise } from '../exercise.model';

@Component({
  selector: 'jhi-exercise-detail',
  templateUrl: './exercise-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ExerciseDetailComponent {
  exercise = input<IExercise | null>(null);

  previousState(): void {
    window.history.back();
  }
}
