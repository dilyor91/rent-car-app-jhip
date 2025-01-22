package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CarClassTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CarClass getCarClassSample1() {
        return new CarClass().id(1L).name("name1");
    }

    public static CarClass getCarClassSample2() {
        return new CarClass().id(2L).name("name2");
    }

    public static CarClass getCarClassRandomSampleGenerator() {
        return new CarClass().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
