package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MerchantRoleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MerchantRole getMerchantRoleSample1() {
        return new MerchantRole().id(1L);
    }

    public static MerchantRole getMerchantRoleSample2() {
        return new MerchantRole().id(2L);
    }

    public static MerchantRole getMerchantRoleRandomSampleGenerator() {
        return new MerchantRole().id(longCount.incrementAndGet());
    }
}
