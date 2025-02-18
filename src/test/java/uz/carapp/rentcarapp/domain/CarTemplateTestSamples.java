package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CarTemplateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CarTemplate getCarTemplateSample1() {
        return new CarTemplate().id(1L);
    }

    public static CarTemplate getCarTemplateSample2() {
        return new CarTemplate().id(2L);
    }

    public static CarTemplate getCarTemplateRandomSampleGenerator() {
        return new CarTemplate().id(longCount.incrementAndGet());
    }
}
