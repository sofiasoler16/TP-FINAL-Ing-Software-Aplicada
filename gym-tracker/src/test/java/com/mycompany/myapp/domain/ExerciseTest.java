package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ExerciseTestSamples.*;
import static com.mycompany.myapp.domain.WorkoutTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExerciseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Exercise.class);
        Exercise exercise1 = getExerciseSample1();
        Exercise exercise2 = new Exercise();
        assertThat(exercise1).isNotEqualTo(exercise2);

        exercise2.setId(exercise1.getId());
        assertThat(exercise1).isEqualTo(exercise2);

        exercise2 = getExerciseSample2();
        assertThat(exercise1).isNotEqualTo(exercise2);
    }

    @Test
    void workoutsTest() {
        Exercise exercise = getExerciseRandomSampleGenerator();
        Workout workoutBack = getWorkoutRandomSampleGenerator();

        exercise.addWorkouts(workoutBack);
        assertThat(exercise.getWorkouts()).containsOnly(workoutBack);
        assertThat(workoutBack.getExercises()).containsOnly(exercise);

        exercise.removeWorkouts(workoutBack);
        assertThat(exercise.getWorkouts()).doesNotContain(workoutBack);
        assertThat(workoutBack.getExercises()).doesNotContain(exercise);

        exercise.workouts(new HashSet<>(Set.of(workoutBack)));
        assertThat(exercise.getWorkouts()).containsOnly(workoutBack);
        assertThat(workoutBack.getExercises()).containsOnly(exercise);

        exercise.setWorkouts(new HashSet<>());
        assertThat(exercise.getWorkouts()).doesNotContain(workoutBack);
        assertThat(workoutBack.getExercises()).doesNotContain(exercise);
    }
}
