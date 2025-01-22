package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static uz.carapp.rentcarapp.domain.VehicleTestSamples.*;

import org.junit.jupiter.api.Test;
import uz.carapp.rentcarapp.web.rest.TestUtil;

class VehicleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vehicle.class);
        Vehicle vehicle1 = getVehicleSample1();
        Vehicle vehicle2 = new Vehicle();
        assertThat(vehicle1).isNotEqualTo(vehicle2);

        vehicle2.setId(vehicle1.getId());
        assertThat(vehicle1).isEqualTo(vehicle2);

        vehicle2 = getVehicleSample2();
        assertThat(vehicle1).isNotEqualTo(vehicle2);
    }
}
