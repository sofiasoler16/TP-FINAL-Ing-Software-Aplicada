package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ExerciseTestSamples.*;
import static com.mycompany.myapp.domain.WorkoutTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WorkoutTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Workout.class);
        Workout workout1 = getWorkoutSample1();
        Workout workout2 = new Workout();
        assertThat(workout1).isNotEqualTo(workout2);

        workout2.setId(workout1.getId());
        assertThat(workout1).isEqualTo(workout2);

        workout2 = getWorkoutSample2();
        assertThat(workout1).isNotEqualTo(workout2);
    }

    @Test
    void exercisesTest() {
        Workout workout = getWorkoutRandomSampleGenerator();
        Exercise exerciseBack = getExerciseRandomSampleGenerator();

        workout.addExercises(exerciseBack);
        assertThat(workout.getExercises()).containsOnly(exerciseBack);

        workout.removeExercises(exerciseBack);
        assertThat(workout.getExercises()).doesNotContain(exerciseBack);

        workout.exercises(new HashSet<>(Set.of(exerciseBack)));
        assertThat(workout.getExercises()).containsOnly(exerciseBack);

        workout.setExercises(new HashSet<>());
        assertThat(workout.getExercises()).doesNotContain(exerciseBack);
    }
}
