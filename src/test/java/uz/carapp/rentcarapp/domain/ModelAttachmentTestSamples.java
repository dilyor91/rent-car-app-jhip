package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ModelAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ModelAttachment getModelAttachmentSample1() {
        return new ModelAttachment().id(1L);
    }

    public static ModelAttachment getModelAttachmentSample2() {
        return new ModelAttachment().id(2L);
    }

    public static ModelAttachment getModelAttachmentRandomSampleGenerator() {
        return new ModelAttachment().id(longCount.incrementAndGet());
    }
}
