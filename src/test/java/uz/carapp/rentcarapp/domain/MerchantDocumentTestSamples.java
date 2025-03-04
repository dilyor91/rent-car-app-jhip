package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MerchantDocumentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MerchantDocument getMerchantDocumentSample1() {
        return new MerchantDocument().id(1L);
    }

    public static MerchantDocument getMerchantDocumentSample2() {
        return new MerchantDocument().id(2L);
    }

    public static MerchantDocument getMerchantDocumentRandomSampleGenerator() {
        return new MerchantDocument().id(longCount.incrementAndGet());
    }
}
