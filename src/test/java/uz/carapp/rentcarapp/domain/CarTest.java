package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.CarTestSamples.*;
import static uz.carapp.rentcarapp.domain.MerchantBranchTestSamples.*;
import static uz.carapp.rentcarapp.domain.MerchantTestSamples.*;
import static uz.carapp.rentcarapp.domain.ModelTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class CarTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Car.class);
        Car car1 = getCarSample1();
        Car car2 = new Car();
        assertThat(car1).isNotEqualTo(car2);

        car2.setId(car1.getId());
        assertThat(car1).isEqualTo(car2);

        car2 = getCarSample2();
        assertThat(car1).isNotEqualTo(car2);
    }

    @Test
    void modelTest() {
        Car car = getCarRandomSampleGenerator();
        Model modelBack = getModelRandomSampleGenerator();

        car.setModel(modelBack);
        assertThat(car.getModel()).isEqualTo(modelBack);

        car.model(null);
        assertThat(car.getModel()).isNull();
    }

    @Test
    void merchantTest() {
        Car car = getCarRandomSampleGenerator();
        Merchant merchantBack = getMerchantRandomSampleGenerator();

        car.setMerchant(merchantBack);
        assertThat(car.getMerchant()).isEqualTo(merchantBack);

        car.merchant(null);
        assertThat(car.getMerchant()).isNull();
    }

    @Test
    void merchantBranchTest() {
        Car car = getCarRandomSampleGenerator();
        MerchantBranch merchantBranchBack = getMerchantBranchRandomSampleGenerator();

        car.setMerchantBranch(merchantBranchBack);
        assertThat(car.getMerchantBranch()).isEqualTo(merchantBranchBack);

        car.merchantBranch(null);
        assertThat(car.getMerchantBranch()).isNull();
    }
}
