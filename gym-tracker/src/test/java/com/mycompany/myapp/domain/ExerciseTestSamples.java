package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExerciseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Exercise getExerciseSample1() {
        return new Exercise().id(1L).name("name1").description("description1");
    }

    public static Exercise getExerciseSample2() {
        return new Exercise().id(2L).name("name2").description("description2");
    }

    public static Exercise getExerciseRandomSampleGenerator() {
        return new Exercise().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
