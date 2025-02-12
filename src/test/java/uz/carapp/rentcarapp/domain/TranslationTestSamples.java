package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TranslationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Translation getTranslationSample1() {
        return new Translation().id(1L).entityType("entityType1").entityId(1L).value("value1").description("description1");
    }

    public static Translation getTranslationSample2() {
        return new Translation().id(2L).entityType("entityType2").entityId(2L).value("value2").description("description2");
    }

    public static Translation getTranslationRandomSampleGenerator() {
        return new Translation()
            .id(longCount.incrementAndGet())
            .entityType(UUID.randomUUID().toString())
            .entityId(longCount.incrementAndGet())
            .value(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
