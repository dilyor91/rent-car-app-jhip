package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Param getParamSample1() {
        return new Param().id(1L).name("name1").description("description1");
    }

    public static Param getParamSample2() {
        return new Param().id(2L).name("name2").description("description2");
    }

    public static Param getParamRandomSampleGenerator() {
        return new Param().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
