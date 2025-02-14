package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CarParamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CarParam getCarParamSample1() {
        return new CarParam().id(1L).paramItemValue("paramItemValue1").paramValue("paramValue1");
    }

    public static CarParam getCarParamSample2() {
        return new CarParam().id(2L).paramItemValue("paramItemValue2").paramValue("paramValue2");
    }

    public static CarParam getCarParamRandomSampleGenerator() {
        return new CarParam()
            .id(longCount.incrementAndGet())
            .paramItemValue(UUID.randomUUID().toString())
            .paramValue(UUID.randomUUID().toString());
    }
}
