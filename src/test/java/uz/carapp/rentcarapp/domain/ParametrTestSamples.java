package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParametrTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Parametr getParametrSample1() {
        return new Parametr().id(1L).name("name1");
    }

    public static Parametr getParametrSample2() {
        return new Parametr().id(2L).name("name2");
    }

    public static Parametr getParametrRandomSampleGenerator() {
        return new Parametr().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
