package uz.carapp.rentcarapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MerchantBranchTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MerchantBranch getMerchantBranchSample1() {
        return new MerchantBranch().id(1L).name("name1").address("address1").latitude("latitude1").longitude("longitude1").phone("phone1");
    }

    public static MerchantBranch getMerchantBranchSample2() {
        return new MerchantBranch().id(2L).name("name2").address("address2").latitude("latitude2").longitude("longitude2").phone("phone2");
    }

    public static MerchantBranch getMerchantBranchRandomSampleGenerator() {
        return new MerchantBranch()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .latitude(UUID.randomUUID().toString())
            .longitude(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
