package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CarAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CarAttachment getCarAttachmentSample1() {
        return new CarAttachment().id(1L);
    }

    public static CarAttachment getCarAttachmentSample2() {
        return new CarAttachment().id(2L);
    }

    public static CarAttachment getCarAttachmentRandomSampleGenerator() {
        return new CarAttachment().id(longCount.incrementAndGet());
    }
}
