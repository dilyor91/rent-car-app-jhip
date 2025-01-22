package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CarBodyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CarBody getCarBodySample1() {
        return new CarBody().id(1L).name("name1");
    }

    public static CarBody getCarBodySample2() {
        return new CarBody().id(2L).name("name2");
    }

    public static CarBody getCarBodyRandomSampleGenerator() {
        return new CarBody().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
