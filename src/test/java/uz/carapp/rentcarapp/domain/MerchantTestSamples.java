package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MerchantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Merchant getMerchantSample1() {
        return new Merchant()
            .id(1L)
            .companyName("companyName1")
            .brandName("brandName1")
            .inn("inn1")
            .owner("owner1")
            .phone("phone1")
            .address("address1");
    }

    public static Merchant getMerchantSample2() {
        return new Merchant()
            .id(2L)
            .companyName("companyName2")
            .brandName("brandName2")
            .inn("inn2")
            .owner("owner2")
            .phone("phone2")
            .address("address2");
    }

    public static Merchant getMerchantRandomSampleGenerator() {
        return new Merchant()
            .id(longCount.incrementAndGet())
            .companyName(UUID.randomUUID().toString())
            .brandName(UUID.randomUUID().toString())
            .inn(UUID.randomUUID().toString())
            .owner(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
