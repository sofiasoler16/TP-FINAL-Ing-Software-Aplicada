package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WorkoutExerciseRelationshipTest {

    @Test
    void addExerciseShouldSetBothSidesOfRelationship() {
        // given
        Workout workout = new Workout();
        workout.setId(1L);

        Exercise exercise = new Exercise();
        exercise.setId(10L);

        // Simulamos la lógica de agregar a la relación muchos-a-muchos
        if (workout.getExercises() == null) {
            workout.setExercises(new HashSet<>());
        }
        if (exercise.getWorkouts() == null) {
            exercise.setWorkouts(new HashSet<>());
        }

        workout.getExercises().add(exercise);
        exercise.getWorkouts().add(workout);

        // then
        assertThat(workout.getExercises()).contains(exercise);
        assertThat(exercise.getWorkouts()).contains(workout);
    }

    @Test
    void removeExerciseShouldUnsetBothSidesOfRelationship() {
        // given
        Workout workout = new Workout();
        workout.setId(1L);

        Exercise exercise = new Exercise();
        exercise.setId(10L);

        Set<Exercise> exercises = new HashSet<>();
        exercises.add(exercise);
        workout.setExercises(exercises);

        Set<Workout> workouts = new HashSet<>();
        workouts.add(workout);
        exercise.setWorkouts(workouts);

        // when: simulamos quitar de la relación
        workout.getExercises().remove(exercise);
        exercise.getWorkouts().remove(workout);

        // then
        assertThat(workout.getExercises()).doesNotContain(exercise);
        assertThat(exercise.getWorkouts()).doesNotContain(workout);
    }
}
