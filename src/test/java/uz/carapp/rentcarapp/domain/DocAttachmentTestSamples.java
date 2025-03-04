package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DocAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DocAttachment getDocAttachmentSample1() {
        return new DocAttachment().id(1L);
    }

    public static DocAttachment getDocAttachmentSample2() {
        return new DocAttachment().id(2L);
    }

    public static DocAttachment getDocAttachmentRandomSampleGenerator() {
        return new DocAttachment().id(longCount.incrementAndGet());
    }
}
