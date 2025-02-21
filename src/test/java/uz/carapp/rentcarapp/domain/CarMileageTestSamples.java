package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CarMileageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CarMileage getCarMileageSample1() {
        return new CarMileage().id(1L);
    }

    public static CarMileage getCarMileageSample2() {
        return new CarMileage().id(2L);
    }

    public static CarMileage getCarMileageRandomSampleGenerator() {
        return new CarMileage().id(longCount.incrementAndGet());
    }
}
