package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.CarMileage;

/**
 * Spring Data JPA repository for the CarMileage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarMileageRepository extends JpaRepository<CarMileage, Long> {}
