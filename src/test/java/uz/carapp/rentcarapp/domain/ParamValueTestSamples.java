package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParamValueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ParamValue getParamValueSample1() {
        return new ParamValue().id(1L).name("name1");
    }

    public static ParamValue getParamValueSample2() {
        return new ParamValue().id(2L).name("name2");
    }

    public static ParamValue getParamValueRandomSampleGenerator() {
        return new ParamValue().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
