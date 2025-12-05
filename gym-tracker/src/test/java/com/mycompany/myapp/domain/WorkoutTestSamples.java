package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WorkoutTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Workout getWorkoutSample1() {
        return new Workout().id(1L).durationMinutes(1).notes("notes1");
    }

    public static Workout getWorkoutSample2() {
        return new Workout().id(2L).durationMinutes(2).notes("notes2");
    }

    public static Workout getWorkoutRandomSampleGenerator() {
        return new Workout()
            .id(longCount.incrementAndGet())
            .durationMinutes(intCount.incrementAndGet())
            .notes(UUID.randomUUID().toString());
    }
}
