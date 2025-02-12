package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ModelTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Model getModelSample1() {
        return new Model().id(1L).name("name1");
    }

    public static Model getModelSample2() {
        return new Model().id(2L).name("name2");
    }

    public static Model getModelRandomSampleGenerator() {
        return new Model().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
