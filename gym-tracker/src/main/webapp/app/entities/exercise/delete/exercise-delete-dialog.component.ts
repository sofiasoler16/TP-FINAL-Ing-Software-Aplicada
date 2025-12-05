import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IExercise } from '../exercise.model';
import { ExerciseService } from '../service/exercise.service';

@Component({
  templateUrl: './exercise-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ExerciseDeleteDialogComponent {
  exercise?: IExercise;

  protected exerciseService = inject(ExerciseService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.exerciseService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
