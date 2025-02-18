package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CarTemplateParamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CarTemplateParam getCarTemplateParamSample1() {
        return new CarTemplateParam().id(1L).paramVal("paramVal1");
    }

    public static CarTemplateParam getCarTemplateParamSample2() {
        return new CarTemplateParam().id(2L).paramVal("paramVal2");
    }

    public static CarTemplateParam getCarTemplateParamRandomSampleGenerator() {
        return new CarTemplateParam().id(longCount.incrementAndGet()).paramVal(UUID.randomUUID().toString());
    }
}
