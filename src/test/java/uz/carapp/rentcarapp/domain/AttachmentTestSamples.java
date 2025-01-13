package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Attachment getAttachmentSample1() {
        return new Attachment().id(1L).fileName("fileName1").fileSize(1).originalFileName("originalFileName1").path("path1").ext("ext1");
    }

    public static Attachment getAttachmentSample2() {
        return new Attachment().id(2L).fileName("fileName2").fileSize(2).originalFileName("originalFileName2").path("path2").ext("ext2");
    }

    public static Attachment getAttachmentRandomSampleGenerator() {
        return new Attachment()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .fileSize(intCount.incrementAndGet())
            .originalFileName(UUID.randomUUID().toString())
            .path(UUID.randomUUID().toString())
            .ext(UUID.randomUUID().toString());
    }
}
