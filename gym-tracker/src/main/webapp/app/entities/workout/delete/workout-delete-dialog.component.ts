import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IWorkout } from '../workout.model';
import { WorkoutService } from '../service/workout.service';

@Component({
  templateUrl: './workout-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class WorkoutDeleteDialogComponent {
  workout?: IWorkout;

  protected workoutService = inject(WorkoutService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.workoutService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
