import { Component, OnInit } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { NgIf, NgFor } from '@angular/common';
import { WorkoutService, Workout } from '../services/workout.service';

@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss'],
  standalone: true,
  imports: [IonicModule, NgIf, NgFor],
})
export class Tab1Page implements OnInit {
  workouts: Workout[] = [];
  loading = true;
  error?: string;

  constructor(private workoutService: WorkoutService) {}

  ngOnInit() {
    this.loadWorkouts();
  }

  loadWorkouts() {
    this.loading = true;
    this.workoutService.getWorkouts().subscribe({
      next: data => {
        this.workouts = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.error = 'Error cargando workouts';
        this.loading = false;
      },
    });
  }
}
